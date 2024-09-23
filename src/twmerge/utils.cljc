(ns twmerge.utils
  (:require
   [clojure.string :as str]
   [clojure.walk :as walk]
   [twmerge.default-config :as c :refer [get-default-config]]))

(defn theme-getter?
  [f]
  (boolean (:theme-getter? (meta f))))

(def arbitrary-property-regex #"^\[(.+)\]$")

(defrecord ClassTreeNode [class-group-id children validators])

(defrecord ClassValidatorObject [class-group-id validator])

(def small-config (merge (get-default-config) {:class-groups c/small-class-group}))

(def class-part-separator "-")

(defn deep-deref
  [maybe-volatile]
  (walk/prewalk (fn [x]
                  (if (volatile? x)
                    (deref x)
                    x))
                maybe-volatile))

(defn prefixed-class-entries [class-group-entries prefix]
  (if-not prefix
    class-group-entries
    (map (fn [[class-group-id class-group]]
           [class-group-id
            (map (fn [class-definition]
                   (cond
                     (string? class-definition) (str prefix class-definition)
                     (map? class-definition) (into {} (map (fn [[k v]] [(str prefix k) v]) class-definition))
                     :else class-definition))
                 class-group)])
         class-group-entries)))

(defn get-or-create-child! [v-node path]
  (let [current-node (volatile! v-node)
        path-parts (str/split path (re-pattern class-part-separator))]
    (doseq [path-part path-parts]
      (let [child-node (volatile! (map->ClassTreeNode {:class-group-id nil
                                                       :children {}
                                                       :validators []}))]
        (when-not (get-in @@current-node [:children path-part])
          (when-let [val @current-node]
            (vswap! val assoc-in [:children path-part]
                    child-node)))
        (vreset! current-node (get-in @@current-node [:children path-part]))))
    @current-node))

(comment
  (def v-node (volatile! (map->ClassTreeNode {:class-group-id nil :children {} :validators []}))))

(defn process-classes-recursively! [class-group v-tree-node class-group-id theme]
  (doseq [class-definition class-group]
    (cond
      (string? class-definition)
      (let [class-part-object-to-edit (if (empty? class-definition)
                                        v-tree-node
                                        (get-or-create-child! v-tree-node class-definition))]
        (vswap! class-part-object-to-edit assoc :class-group-id class-group-id))

      (fn? class-definition)
      (if (theme-getter? class-definition)
        (process-classes-recursively! (class-definition theme) v-tree-node class-group-id theme)
        (vswap!  v-tree-node update-in [:validators] conj
                 (map->ClassValidatorObject {:class-group-id class-group-id
                                             :validator class-definition})))

      :else ;; when the entry is a map
      (doseq [[key sub-class-group] class-definition]
        (process-classes-recursively! sub-class-group
                                      (get-or-create-child! v-tree-node key)
                                      class-group-id
                                      theme)))))

(defn create-class-map [config]
  (let [{:keys [theme prefix]} config
        class-map (volatile! (map->ClassTreeNode {:class-group-id nil
                                                  :children {}
                                                  :validators []}))
        class-group-entries (prefixed-class-entries
                             (seq (:class-groups config))
                             prefix)]
    (doseq [[class-group-id class-group] class-group-entries]
      (process-classes-recursively! class-group class-map class-group-id theme))
    ;; Return just the persistent tree
    (deep-deref class-map)))

(comment

  (-> (get  (:children (create-class-map small-config)) "table")
      :children
      keys))

(comment
  (create-class-map (get-default-config)))

(defn get-class-group-recursive
  "cpo - ClassPartObject record"
  [class-parts cpo]
  (if (empty? class-parts)
    (:class-group-id cpo)
    (let [current-class-part (first class-parts)
          next-cpo (get-in cpo [:children current-class-part])
          next-cpo-class-group (when next-cpo (get-class-group-recursive (rest class-parts) next-cpo))]
      (cond
        (string? next-cpo-class-group) next-cpo-class-group
        (empty? (:validators cpo)) nil
        :else (let [class-rest (str/join  class-part-separator class-parts)
                    {:keys [validators]} cpo]
                (some #(when ((:validator %) class-rest) (:class-group-id %)) validators))))))

(defn get-group-id-for-arbitrary-property [class-name]
  (when-let [[_ arbitrary-property-class-name] (re-find arbitrary-property-regex class-name)]
    (when-let [property (second (str/split arbitrary-property-class-name #":"))]
      ;; I use two dots here because one dot is used as prefix for class groups in plugins
      (str "arbitrary.." property))))

(defn create-class-group-utils [config]
  (let [class-map (create-class-map config)
        {:keys [conflicting-class-groups conflicting-class-groups-modifiers]} config]
    (letfn [(get-class-group-id [class-name]
              (let [class-parts-result (str/split class-name (re-pattern class-part-separator))
                    ;; Classes like `-inset-1` produce an empty string
                    ;; as first classPart. We assume that classes for
                    ;; negative values are used correctly and remove
                    ;; it from class-parts.
                    inset-class? (and (= (first class-parts-result) "") (> (count class-parts-result) 1))
                    class-parts (if inset-class? (rest class-parts-result) class-parts-result)]
                (or (get-class-group-recursive class-parts class-map)
                    (get-group-id-for-arbitrary-property class-name))))

            (get-conflicting-class-group-ids [class-group-id has-postfix-modifier]
              (let [conflicts (get conflicting-class-groups class-group-id [])]
                (if (and has-postfix-modifier (get conflicting-class-groups-modifiers class-group-id))
                  (into conflicts (get conflicting-class-groups-modifiers class-group-id))
                  conflicts)))]

      {:get-class-group-id get-class-group-id
       :get-conflicting-class-group-ids get-conflicting-class-group-ids})))

(defn- traverse-class-name
  [class-name separator]
  (let [sep-length (count separator)
        separator-single-character? (= sep-length 1)
        first-separator-char (get separator 0)]
    (loop [modifiers []
           modifier-start 0
           bracket-depth 0
           postfix-modifier-position nil
           idx 0]
      (if (= idx (count class-name))
        {:modifiers modifiers
         :modifiers-start modifier-start
         :postfix-modifier-position postfix-modifier-position}
        (let [current-char (get class-name idx)]
          (cond
            (and (= bracket-depth 0)
                 (= current-char first-separator-char)
                 (or separator-single-character?
                     (= (subs class-name idx (+ idx sep-length)) separator)))
            (recur (conj modifiers (subs class-name modifier-start idx))
                   (+ idx sep-length)
                   bracket-depth
                   postfix-modifier-position
                   (+ idx sep-length))

            (and (= bracket-depth 0)
                 (= current-char \/))
            (recur modifiers
                   modifier-start
                   bracket-depth
                   idx  ;; set postfix-position to current idx
                   (inc idx))

            (= current-char \[)
            (recur modifiers
                   modifier-start
                   (inc bracket-depth)
                   postfix-modifier-position
                   (inc idx))

            (= current-char \])
            (recur modifiers
                   modifier-start
                   (dec bracket-depth)
                   postfix-modifier-position
                   (inc idx))

            :else
            (recur modifiers
                   modifier-start
                   bracket-depth
                   postfix-modifier-position
                   (inc idx))))))))

(def important-modifier "!")

(defn make-parse-class
  [config]
  (let [{:keys [separator experimental-parse-class]} config
        parse-class
        (fn [class-name]
          (let [{:keys [modifiers modifiers-start postfix-modifier-position]}
                (traverse-class-name class-name separator)
                base-class-with-important (if (empty? modifiers) class-name (subs class-name modifiers-start))
                has-important-modifier? (str/starts-with? base-class-with-important important-modifier)
                base-class (if has-important-modifier? (subs base-class-with-important 1) base-class-with-important)
                maybe-postfix-modifier-position (when (and postfix-modifier-position
                                                           (> postfix-modifier-position modifiers-start))
                                                  (- postfix-modifier-position modifiers-start))]
            {:modifiers modifiers
             :has-important-modifier? has-important-modifier?
             :base-class base-class
             :maybe-postfix-modifier-position maybe-postfix-modifier-position}))]
    (if experimental-parse-class
      (fn [class] (experimental-parse-class {:class class :parse-class parse-class}))
      parse-class)))

(defn sort-modifiers
  "Sorts modifiers according to following schema:
  - Predefined modifiers are sorted alphabetically
  - When an arbitrary variant appears, it must be preserved which modifiers are before and after it
  - modifiers - array of strings"
  [modifiers]
  (if (< (count modifiers) 1)
    modifiers
    (let [sorted-modifiers (volatile! [])
          unsorted-modifiers (volatile! [])]
      (doseq [modifier modifiers]
        (let [arbitrary-variant? (= (first modifier) \[)]
          (if arbitrary-variant?
            (do (vreset! sorted-modifiers (-> (into @sorted-modifiers (sort @unsorted-modifiers))
                                              (conj modifier)))
                (vreset! unsorted-modifiers []))
            (vswap! unsorted-modifiers conj modifier))))
      (vreset! sorted-modifiers (into @sorted-modifiers (sort @unsorted-modifiers)))
      @sorted-modifiers)))

(def split-classes-regex #"\s+")

(defn create-config-utils
  [config]
  (merge {:parse-class-name (make-parse-class config)}
         (create-class-group-utils config)))

(defn merge-class-list [config-utils class-list]
  (let [{:keys [parse-class-name get-class-group-id get-conflicting-class-group-ids]} config-utils]
    (loop [class-names (str/split (str/trim class-list) split-classes-regex)
           class-groups-in-conflict #{}
           result []]
      (if (empty? class-names)
        (str/join " " (reverse result))
        (let [original-class-name (last class-names)
              {:keys [modifiers
                      has-important-modifier?
                      base-class
                      maybe-postfix-modifier-position]} (parse-class-name original-class-name)

              has-postfix-modifier? (boolean maybe-postfix-modifier-position)
              class-group-id (get-class-group-id (if has-postfix-modifier?
                                                   (subs base-class 0 maybe-postfix-modifier-position)
                                                   base-class))]

          (if (not class-group-id)
            (if (not has-postfix-modifier?)
              ;; not a tailwind class
              (recur (rest class-names)
                     class-groups-in-conflict
                     (conj result original-class-name))
              (let [class-group-id (get-class-group-id base-class)]
                (if (not class-group-id)
                  ;; not a tailwind class
                  (recur (rest class-names)
                         class-groups-in-conflict
                         (conj result original-class-name))
                  (let [has-postfix-modifier? false
                        variant-modifier (str/join ":" (sort-modifiers modifiers))
                        modifier-id (if has-important-modifier?
                                      (str variant-modifier important-modifier)
                                      variant-modifier)
                        class-id (str modifier-id class-group-id)]
                    (if (contains? class-groups-in-conflict class-id)
                      ;; tailwind class omitted due to conflict
                      (recur (rest class-names) class-groups-in-conflict result)
                      (let [new-conflicts (into class-groups-in-conflict
                                                (map #(str modifier-id %)
                                                     (get-conflicting-class-group-ids
                                                      class-group-id
                                                      has-postfix-modifier?)))]
                        (recur (rest class-names)
                               (conj new-conflicts class-id)
                               (conj result original-class-name))))))))
            (let [variant-modifier (str/join ":" (sort-modifiers modifiers))
                  modifier-id (if has-important-modifier?
                                (str variant-modifier important-modifier)
                                variant-modifier)
                  class-id (str modifier-id class-group-id)]
              (if (contains? class-groups-in-conflict class-id)
                (recur (rest class-names) class-groups-in-conflict result)
                (let [new-conflicts (into class-groups-in-conflict
                                          (map #(str modifier-id %)
                                               (get-conflicting-class-group-ids class-group-id has-postfix-modifier?)))]
                  (recur (butlast class-names)
                         (conj new-conflicts class-id)
                         (conj result original-class-name)))))))))))



