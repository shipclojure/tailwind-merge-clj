(ns shipclojure.utils
  (:require
   [clojure.string :as str]
   [clojure.walk :as walk]
   [shipclojure.default-config :refer [get-default-config]]
   [shipclojure.validators :as v]))

(defn theme-getter?
  [f]
  (boolean (:theme-getter? (meta f))))

(def arbitrary-property-regex #"^\[(.+)\]$")

(defrecord ClassTreeNode [class-group-id children validators])

(defrecord ClassValidatorObject [class-group-id validator])

(def simple [["aspect" [{"aspect" ["auto" "square" "video" v/arbitrary-value?]}]]])

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
  (let [current-node (volatile! v-node)]
    (doseq [path-part (str/split path (re-pattern class-part-separator))]
      (let [child-node (volatile! (map->ClassTreeNode {:class-group-id nil
                                                       :children {}
                                                       :validators []}))]
        (vswap! v-node assoc-in [:children path-part]
                (or (get-in @v-node [:children path-part]) child-node))
        (vreset! current-node child-node)))
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
  (create-class-map (get-default-config)))

(defn get-class-group-recursive
  "cpo - ClassPartObject record"
  [class-parts cpo]
  (if (empty? class-parts)
    (:class-group-id cpo)
    (let [current-class-part (first class-parts)
          next-cpo (get-in cpo [:next-part current-class-part])
          next-cpo-class-group (when next-cpo (get-class-group-recursive (rest class-parts) next-cpo))]
      (cond
        (string? next-cpo-class-group) next-cpo-class-group
        (empty? (:validators cpo)) nil
        :else (let [class-rest (str/join class-parts class-part-separator)
                    {:keys [validators]} cpo]
                (some #(when ((:validator %) class-rest) (:class-group-id %)) validators))))))

(defn get-group-id-for-arbitrary-property [class-name]
  (when-let [[_ arbitrary-property-class-name] (re-find arbitrary-property-regex class-name)]
    (when-let [property (second (str/split arbitrary-property-class-name #":"))]
      ;; I use two dots here because one dot is used as prefix for class groups in plugins
      (str "arbitrary.." property))))

(defn create-class-group-utils [config]
  (let [class-map (create-class-map config)
        {:keys [conflicting-class-groups conflicting-class-group-modifiers]} config]
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
                (if (and has-postfix-modifier (get conflicting-class-group-modifiers class-group-id))
                  (into conflicts (get conflicting-class-group-modifiers class-group-id))
                  conflicts)))]

      {:get-class-group-id get-class-group-id
       :get-conflicting-class-group-ids get-conflicting-class-group-ids})))




