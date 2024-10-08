(ns twmerge.validators
  (:require
   [clojure.string :as str]))

(def string-lengths #{"px" "full" "screen"})
(def image-labels #{"image" "url"})
(def size-labels #{"length" "size" "percentage"})
(def tshirt-unit-regex #"^(\d+(\.\d+)?)?(xs|sm|md|lg|xl)$")
(def fraction-regex #?(:clj #"^\d+\/\d+$"
                       :cljs #"^\d+/\d+$"))
(def arbitrary-value-regex #"(?i)^\[(?:([a-z-]+):)?(.+)\]$")
(def shadow-regex #"^(inset_)?-?((\d+)?\.?(\d+)[a-z]+|0)_-?((\d+)?\.?(\d+)[a-z]+|0)")
(def length-unit-regex
  #"(?:\d*\.)?\d+(%|px|r?em|[sdl]?v([hwib]|min|max)|pt|pc|in|cm|mm|cap|ch|ex|r?lh|cq(w|h|i|b|min|max))|\b(calc|min|max|clamp)\(.+\)|^0$")
(def color-function-regex #"^(rgba?|hsla?|hwb|(ok)?(lab|lch))\(.+\)$")
(def image-regex
  #"^(url|image|image-set|cross-fade|element|(repeating-)?(linear|radial|conic)-gradient)\(.+\)$")

(defn- parse-float
  "Parse a string to a float."
  [v]
  (try
    #?(:clj (Float/parseFloat v)
       :cljs (js/parseFloat v))
    (catch #?(:clj Throwable :cljs :default) _
      nil)))

(defn- parse-int
  "Parse a string to an integer."
  [v]
  (try
    #?(:clj (Integer/parseInt v)
       :cljs (js/parseInt v))
    (catch #?(:clj Throwable :cljs :default) _
      nil)))

(defn- remove-percent
  [s]
  (str/replace s #"%" ""))

(defn tw-number?
  "Number or string number"
  [v]
  (if (number? v)
    true
    (let [parsed (parse-float v)]
      #?(:clj (number? parsed)
         :cljs (and (number? parsed)
                    (not (NaN? parsed)))))))

(defn tw-integer?
  [v]
  (int? (parse-int v)))

(defn percent?
  [v]
  (and (tw-number? (remove-percent v))
       (str/ends-with? v "%")))

(defn length?
  [value]
  (boolean (or (tw-number? value)
               (string-lengths value)
               (re-matches fraction-regex value))))

(defn never? [_v] false)

(comment
  (length? "screen")
  (length? "1/5")
  (length? 6)
  (length? "25"))

(defn make-arbitrary-value-checker
  [label test-value-fn]
  (fn [value]
    (if-let [[_ label-match value-match] (re-matches arbitrary-value-regex value)]
      (if label-match
        (cond
          (string? label) (= label-match label)
          (set? label) (contains? label label-match))
        (test-value-fn value-match))
      false)))

(defn regex-matcher
  [regex]
  (fn [v] (boolean (re-find regex v))))

(def tshirt-size? (regex-matcher tshirt-unit-regex))
(def image? (regex-matcher image-regex))
(def arbitrary-value? (regex-matcher arbitrary-value-regex))
(def length-unit? (regex-matcher length-unit-regex))
(def color-function? (regex-matcher color-function-regex))
(def shadow? (regex-matcher shadow-regex))

(defn length-only?
  "`color-function?` check is necessary because color functions can
  have percentages in them which which would be incorrectly classified
  as lengths. For example, `hsl(0 0% 0%)` would be classified as a
  length without this check."
  [v]
  (and (length-unit? v)
       (not (color-function? v))))

(def arbitrary-size? (make-arbitrary-value-checker size-labels never?))
(def arbitrary-image? (make-arbitrary-value-checker image-labels image?))
(def arbitrary-length? (make-arbitrary-value-checker "length" length-only?))
(def arbitrary-number? (make-arbitrary-value-checker "number" tw-number?))
(def arbitrary-position? (make-arbitrary-value-checker "position" never?))
(def arbitrary-shadow? (make-arbitrary-value-checker "" shadow?))


