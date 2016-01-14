(ns propagator.propagate
  "To generate the annotations file, the following SQL query can be used:
  SELECT images.name, annotated_images.annotation FROM images, annotated_images WHERE images.id = annotated_images.image_id"
  (:require [clojure.data.json :as json]))

(defn invert-mapping
  ([mapping] (invert-mapping mapping (hash-map)))
  ([mapping inverted-mapping]
    (println (count mapping) (count inverted-mapping))
    (cond
      (empty? mapping) inverted-mapping
      :else
        (recur (rest mapping) (into
          (for [image (second (first mapping))
            :let [map {(keyword (first (second (first mapping)))) (keyword (first (first mapping)))}]] map)
          inverted-mapping)))))

(defn simple
  "use simple propagation which propagates annotations to only images inside the same cluster"
  [mapping-file inverted-mapping-file annotations-file output-file]
  (let [mapping (json/read-str (slurp annotations-file) :key-fn keyword)
        inverted-mapping (read-string (slurp inverted-mapping-file))
        annotations (json/read-str (slurp annotations-file) :key-fn keyword)]
    ()
    (println (reduce conj mapping))))

(defn distance
  "use distance propagation which propagates annotations to images which are similar in distance to each other"
  [mapping-file annotations-file output-file])
