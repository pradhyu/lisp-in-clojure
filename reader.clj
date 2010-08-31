(ns reader
  (:require [clojure.string :as str]))

(defn tokenize [line]
  (let [tokens (-> line
		   (str/replace "(" " ( ")
		   (str/replace ")" " ) ")
		   (str/split #"\s"))]
    (filter (comp not empty?) tokens)))

(defn vectorize
  ([tokens] (vectorize '() tokens))
  ([acc tokens]
     (let [head (first tokens)
	   tail (rest tokens)]
       (cond
	(empty? tokens) (first acc)
	(= head ")") [acc tail]
	(= head "(") (let [[v new-tokens] (vectorize tail)]
		       (vectorize (concat acc (list v)) new-tokens))
	:else (vectorize (concat acc (list head)) tail)))))

(defn _read [line] (vectorize (tokenize line)))
