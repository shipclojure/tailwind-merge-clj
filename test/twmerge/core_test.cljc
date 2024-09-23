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
      "grow grow-[2]" "grow-[2]"))

  (testing "Merge classes from the same group correctly"
    (is (= (tw-merge "overflow-x-auto overflow-x-hidden") "overflow-x-hidden"))
    (is (= (tw-merge "basis-full basis-auto") "basis-auto"))
    (is (= (tw-merge "w-full w-fit") "w-fit"))
    (is (= (tw-merge "overflow-x-auto overflow-x-hidden overflow-x-scroll") "overflow-x-scroll"))
    (is (= (tw-merge "overflow-x-auto hover:overflow-x-hidden overflow-x-scroll") "hover:overflow-x-hidden overflow-x-scroll"))
    (is (= (tw-merge "overflow-x-auto hover:overflow-x-hidden hover:overflow-x-auto overflow-x-scroll") "hover:overflow-x-auto overflow-x-scroll" ))
    (is (= (tw-merge "col-span-1 col-span-full") "col-span-full"))))


