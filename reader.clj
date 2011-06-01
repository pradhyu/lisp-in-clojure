(ns reader
  (:require [clojure.string :as str]))

(defn tokenize [exp]
  (remove empty? (-> exp
                     (str/replace "(" " ( ")
                     (str/replace ")" " ) ")
                     (str/replace "'" " ' ")
                     (str/split #"\s+"))))

(declare micro-read read-list)

(defn micro-read [[t & ts]]
  (cond
   (= t "(") (read-list '() ts)
   (= t "'") (let [[new-t new-ts] (micro-read ts)]
	       [(list "quote" new-t) new-ts])
   :else [t ts]))

(defn read-list [list-so-far tokens]
  (let [[t ts] (micro-read tokens)]
    (cond
     (= t ")") [(reverse list-so-far) ts]
     (= t "(") (let [[new-list new-tokens] (read-list '() ts)]
		 (read-list (conj list-so-far new-list) new-tokens))
     :else (read-list (conj list-so-far t) ts))))

(defn read* [exp]
  (first (micro-read (tokenize exp))))
