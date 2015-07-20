(ns parser.core)

(defn json-parse
  "Returns ClojureScript data for the given JSON string."
  [line]
  (js->clj (.parse js/JSON line) :keywordize-keys true))

(defn test-cases []
  (json-parse "[{\"bits\":\"000111111111111111111111111111111110000001111100000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000001111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111\",\"data\":\"a\",\"correct_bits\":\"1100001\"}, {\"bits\":\"001111111111011111111111111111111100000000111111100000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111\",\"data\":\"a\",\"correct_bits\":\"1100001\"}, {\"bits\":\"0011111111110111111000000000001111111000000000000000000000000000000000000000000000000000000000000000000\",\"data\":\"z\",\"correct_bits\":\"1111010\"}, {\"bits\":\"1111111111111111100000000000111111100000000000000000000000000000000000000000000000000000000000000000001\",\"data\":\"z\",\"correct_bits\":\"1111010\"}, {\"bits\":\"1111111111101111100000000000011111110000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000001111111111111111111111111111111111111111\",\"data\":\"z\",\"correct_bits\":\"1111010\"}, {\"bits\":\"00000000000000000000000111111111111111111111111111111111111111100111111111000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111\",\"data\":\" \",\"correct_bits\":\"100000\"}, {\"bits\":\"111111111111111111111111111111111000111111110000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000001111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111\",\"data\":\" \",\"correct_bits\":\"100000\"}, {\"bits\":\"000011111111111111111111111111000000011111111111000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111110\",\"data\":\"1\",\"correct_bits\":\"110001\"}, {\"bits\":\"0000111111111101111111111111111000000001111111111100000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000111111111111111111111111111111111111111111111111111111111111111111111111111111111\",\"data\":\"1\",\"correct_bits\":\"110001\"}, {\"bits\":\"011111111111110111111111111111000000000111111111100000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000011111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111\",\"data\":\"1\",\"correct_bits\":\"110001\"}]"))

;; ^:export exposes this as a function that js acn call from the global context
;; this is to prevent google closure compiler from mucking it up
(defn ^:export init[])

(defn test-case [n]
  (let [testcase (nth (test-cases) n)
        observed-bits (freq-map (preprocess (:bits testcase)))
        correct-bits (freq-map (:correct_bits testcase))]
    (relate correct-bits observed-bits)))


;; parsing stuff

(defn freq-map [coll]
  (map
   #(vector (count %) (first %))
   (partition-by identity coll)))

(defn greater-than-three [coll]
  (filter #(> (count %) 3) coll))

(defn preprocess [bits]
 (->> bits
       (partition-by identity)
       (greater-than-three)
       (flatten)))

(defn relate [correct observed]
  (map
   #(vector (/ (first %2) (first %1))
             (= (second %2) (second %1)))
   correct observed))


(test-case 9)