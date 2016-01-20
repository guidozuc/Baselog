(ns propagator.core
  (:gen-class)
  (:require [propagator.propagate :as propagate]
            [propagator.concepts  :as concepts]
            [clojure.data.json    :as json]))

(defn help []
  (println "Usage:")
  (println "\tpropagator [action] [& options]")
  (println "Available options:")
  (println "\tpropagator invert-mapping image_cluster_mapping.json output.edn")
  (println "\tpropagator propagate image_cluster_mapping.json inverted_image_cluster_mapping.edn annotations.json cluster_similarity_mapping.json output.json")
  (println "\tpropagator concepts propagated_annotations output.txt"))

(defn display-error
  ([message]
    (println "The propagator program has encountered an error:")
    (println "\t" message))
  ([message usage]
    (println "There was a problem with the command:\t")
    (println "\t" message)
    (println "Proper usage:")
    (println "\t" usage)
    (println "Run 'propagator help' for more help")))

(defn -main
  "entry point to image propagator"
  [& args]
  (cond
    ; if the argument count doesn't match just stop
    (< (count args) 2) (help)
    :else
      (let [action (first args)]
        (cond
          ; determine which action path to take based on the first argument
          (= action "help") (help)
          (= action "propagate") (propagate/distance (nth args 1) (nth args 2) (nth args 3) (nth args 4) (nth args 5))
          (= action "concepts") (concepts/create (nth args 1) (nth args 2))
          (= action "invert-mapping") (spit (nth args 2) (pr-str (reduce conj (propagate/invert-mapping (json/read-str (slurp (nth args 1)) :key-fn keyword)))))
          :else
            (display-error "Could not parse command" "propagator [action] [option]")))))
