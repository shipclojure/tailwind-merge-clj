(ns twmerge.core
  (:require [twmerge.utils :as u]
            [twmerge.default-config :as c]))

(defn fixed-size-memoize
  "Like memoize but clears the chache if a certain cache size has been hit"
  ([f]
   (fixed-size-memoize f 500))
  ([f cache-size]
   (let [mem (atom {})]
     (fn [& args]
       (if-let [e (find @mem args)]
         (val e)
         (let [ret (apply f args)]
           (when (= (count (keys @mem)) cache-size)
             (reset! mem {}))
           (swap! mem assoc args ret)
           ret))))))

(defn create-tw-merge
  [{:keys [cache-size]
    :or {cache-size 500} :as config}]
  (let [cu (u/create-config-utils config)]
    (fixed-size-memoize (partial u/merge-class-list cu) cache-size)))

(def tw-merge (create-tw-merge (c/get-default-config)))


