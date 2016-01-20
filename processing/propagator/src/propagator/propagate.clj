(ns propagator.propagate
  "To generate the annotations file, the following SQL query can be used:
  SELECT images.name, annotated_images.annotation FROM images, annotated_images WHERE images.id = annotated_images.image_id"
  (:require [clojure.data.json    :as json]
            [clojure.set          :as set]
            [clojure.string       :as str]
            [propagator.util      :as util]))

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

(defn- intracluster
  "propogate annotations to images inside a cluster which already have one image annotated in them
  this function returns a list of propagated clusters"
  ([annotations mapping inverted-mapping] (intracluster annotations mapping inverted-mapping []))
  ([annotations mapping inverted-mapping propagated-images]
    (cond
      (empty? annotations) (apply hash-map propagated-images)
      :else
        (let [cluster (get inverted-mapping (keyword (first (first annotations))))
              images (get mapping cluster)
              annotation (val (first annotations))
              annotated-images (apply hash-map (reduce into (mapcat #(apply hash-map (vector % annotation)) images)))]
          (recur (rest annotations) mapping inverted-mapping (reduce into propagated-images annotated-images))))))

(defn- extract-annotated-clusters
  "takes the inter-cluster annotated images and returns a list of those clusters which have been annotated"
  ([inverted-mapping annotated-images] (extract-annotated-clusters inverted-mapping annotated-images []))
  ([inverted-mapping annotated-images clusters]
    (cond
      (empty? annotated-images) clusters
      :else
        (let [image (keyword (key (first annotated-images)))
              cluster (get inverted-mapping image)]
              (cond
                (not (nil? cluster)) (recur inverted-mapping (rest annotated-images) (conj clusters cluster))
                :else
                  (recur inverted-mapping (rest annotated-images) clusters))))))

(defn- can-annotate-cluster?
  "predicate to test if a cluster has any nearby clusters which have been annotated"
  [cluster cluster-similarity annotated-clusters]
  (let [similar-clusters (keys (get cluster-similarity cluster))]
    (cond
      (empty? similar-clusters) false
      (empty? (set/intersection (set annotated-clusters) (set similar-clusters))) false
      :else true)))

(defn- annotations-for-cluster
  [cluster cluster-similarity annotated-clusters mapping annotated-images]
  (let [similar-clusters (keys (get cluster-similarity cluster))
        clusters (set/intersection (set annotated-clusters) (set similar-clusters))
        images (get mapping cluster)
        annotating-images (mapcat #(get mapping %) clusters)
        propagated-annotations (mapcat #(vector (get annotated-images %)) annotating-images)]
      (apply hash-map (reduce into (util/pmapcat #(apply hash-map (vector % (str/join #" " propagated-annotations))) images)))))

(defn- intercluster
  "propagate annotations to images which have not yet been annotated inside clusters which have not yet recieved an annotated image
  this function takes a list of annotated-images from running the intercluster function and propagates to the remaining images"
  ([mapping inverted-mapping annotated-images cluster-similarity]
    (intercluster mapping inverted-mapping annotated-images cluster-similarity (extract-annotated-clusters inverted-mapping annotated-images)))
  ([mapping inverted-mapping annotated-images cluster-similarity annotated-clusters]
    (let [mapping-keys (into [] (keys mapping))
          unannotated-clusters (set/difference (set mapping-keys) (set annotated-clusters))
          annotatable-clusters (filter #(can-annotate-cluster? % cluster-similarity annotated-clusters) unannotated-clusters)]
      (println (count annotated-clusters) (count unannotated-clusters) (count annotatable-clusters) (count mapping))
      (cond
        (empty? unannotated-clusters) annotated-images
        (empty? annotatable-clusters) annotated-images
        :else
            (recur
              mapping
              inverted-mapping
              (merge annotated-images (merge (util/pmapcat #(annotations-for-cluster % cluster-similarity annotated-clusters mapping annotated-images) annotatable-clusters)))
              cluster-similarity
              (into annotatable-clusters annotated-clusters))))))

(defn distance
  "use distance propagation which propagates annotations to images which are similar in distance to each other"
  [mapping-file inverted-mapping-file annotations-file cluster-similarity-file output-file]
  (let [mapping (apply hash-map (reduce into (json/read-str (slurp mapping-file) :key-fn keyword)))
        inverted-mapping (read-string (slurp inverted-mapping-file))
        annotation-mapping (json/read-str (slurp annotations-file) :key-fn keyword)
        cluster-similarity (json/read-str (slurp cluster-similarity-file) :key-fn keyword)
        annotations (apply hash-map (interleave (take-nth 2 (reverse (mapcat first annotation-mapping))) (take-nth 2 (reverse (mapcat second annotation-mapping)))))
        annotated-images (intracluster annotations mapping inverted-mapping)
        propagated-annotations (intercluster mapping inverted-mapping annotated-images cluster-similarity)]
        (spit output-file propagated-annotations)))
