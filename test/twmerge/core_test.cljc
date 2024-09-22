(ns twmerge.core-test
  (:require
   [clojure.test :refer :all]
   [twmerge.core :refer [tw-merge]]))

(deftest simple
  (testing "tw-merge"
    (are [input-classes output] (= (tw-merge input-classes) output)
      "mix-blend-normal mix-blend-multiply" "mix-blend-multiply"
      "h-10 h-min" "h-min"
      "stroke-black stroke-1" "stroke-black stroke-1"
      "stroke-2 stroke-[3]" "stroke-[3]"
      "outline-black outline-1" "outline-black outline-1"
      "grayscale-0 grayscale-[50%]" "grayscale-[50%]"
      "grow grow-[2]" "grow-[2]")))


