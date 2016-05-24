(ns propagator.topics
  (:require [clojure.string       :as str]
            [propagator.concepts  :as concepts]
            [propagator.util      :as util]))

(defn- read-concepts
  [concepts-file]
  (apply hash-map (reduce into (map #(str/split % #";") (str/split-lines (slurp concepts-file))))))

(defn- tf
  [concept concepts]
  (cond
    (zero? (count concepts)) 0.0
    :else
      (double (/ (count (filter #{concept} concepts)) (count (set concepts))))))

(defn- process-annotated-image
  [annotated-image concepts]
  (println (key annotated-image))
  (let [image-concepts (concepts/tokenise (val annotated-image))]
    (cons (key annotated-image) (util/pmapcat #(vector (tf % image-concepts)) concepts))))

(defn create
  [annotations-file concepts-file output-file]
  (let [annotations (read-string (slurp annotations-file))
        concepts (read-concepts concepts-file)]
    (spit output-file "image_path,")
    (spit output-file (str/join #"," (keys concepts)) :append true)
    (doseq [annotated-image annotations]
      (spit output-file (apply str (concat (str/join #"," (process-annotated-image annotated-image (vals concepts))) "\n")) :append true))))
