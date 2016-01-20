(ns propagator.util)

(defn pmapcat [f batches]
  (->> batches
       (pmap f)
       (apply concat)
       doall))
