(ns twmerge.core)

(defn create-tailwind-merge
  ([get-config]
   (create-tailwind-merge get-config {}))
  ([get-config override]
   (let [config (merge (get-config) override)]))
  
  
  )
