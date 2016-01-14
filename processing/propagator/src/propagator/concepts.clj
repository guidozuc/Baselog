(ns propagator.concepts
  (:require [clojure.data.json  :as json]
            [clojure.string     :as str]
            [clojure.set        :as set]))

(def stopwords (set (read-string (slurp "resources/stopwords.edn"))))

(defn tokenise
  [string]
  (set/difference (set(str/split (str/lower-case (str/replace string #"(?i)[^\w']+" " ")) #" ")) stopwords))

(defn print-concepts
  ([concepts output-file]
    (spit output-file "")
    (print-concepts concepts output-file 1))
  ([concepts output-file concept-id]
    (cond
      (empty? concepts) true
      :else
        (let [concept (apply str (concat "c_" (str concept-id) ";" (first concepts) "\n"))]
          (spit output-file concept :append true)
          (recur (rest concepts) output-file (inc concept-id))))))

(defn create
  "given a file for input, take all annotated images and create a concept mapping file.
  this file has the format of:
    [conceptId];[concept]"
  [input-file output-file]
  (let [annotations (json/read-str (slurp input-file) :key-fn keyword)]
    (print-concepts (reduce into (map tokenise (map second (map vals annotations)))) output-file)))
    ; (hash-set (mapcat tokenise ))))
