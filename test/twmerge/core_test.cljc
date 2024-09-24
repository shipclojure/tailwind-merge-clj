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

(deftest class-group-conflicts
  (testing "Merge classes from the same group correctly"
    (is (= (tw-merge "overflow-x-auto overflow-x-hidden") "overflow-x-hidden"))
    (is (= (tw-merge "basis-full basis-auto") "basis-auto"))
    (is (= (tw-merge "w-full w-fit") "w-fit"))
    (is (= (tw-merge "overflow-x-auto overflow-x-hidden overflow-x-scroll") "overflow-x-scroll"))
    (is (= (tw-merge "overflow-x-auto hover:overflow-x-hidden overflow-x-scroll") "hover:overflow-x-hidden overflow-x-scroll"))
    (is (= (tw-merge "overflow-x-auto hover:overflow-x-hidden hover:overflow-x-auto overflow-x-scroll") "hover:overflow-x-auto overflow-x-scroll"))
    (is (= (tw-merge "col-span-1 col-span-full") "col-span-full")))

  (testing "Merge classes from Font Variant Numeric section correctly"

    (is (= (tw-merge "lining-nums tabular-nums diagonal-fractions")
           "lining-nums tabular-nums diagonal-fractions"))
    (is (= (tw-merge "normal-nums tabular-nums diagonal-fractions")
           "tabular-nums diagonal-fractions"))
    (is (= (tw-merge "tabular-nums diagonal-fractions normal-nums") "normal-nums"))
    (is (= (tw-merge "tabular-nums proportional-nums") "proportional-nums"))))

(deftest colors
  (testing "Handles color conflicts properly "
    (is (= (tw-merge "bg-grey-5 bg-hotpink") "bg-hotpink"))
    (is (= (tw-merge "hover:bg-grey-5 hover:bg-hotpink") "hover:bg-hotpink"))
    (is (= (tw-merge "stroke-[hsl(350_80%_0%)] stroke-[10px]") "stroke-[hsl(350_80%_0%)] stroke-[10px]"))))

(deftest conflicts-across-class-groups
  (testing "handles conflicts across class groups correctly"
    (is (= (tw-merge "inset-1 inset-x-1") "inset-1 inset-x-1"))
    (is (= (tw-merge "inset-x-1 inset-1") "inset-1"))
    (is (= (tw-merge "inset-x-1 left-1 inset-1") "inset-1"))
    (is (= (tw-merge "inset-x-1 inset-1 left-1") "inset-1 left-1"))
    (is (= (tw-merge "inset-x-1 right-1 inset-1") "inset-1"))
    (is (= (tw-merge "inset-x-1 right-1 inset-x-1") "inset-x-1"))
    (is (= (tw-merge "inset-x-1 right-1 inset-y-1") "inset-x-1 right-1 inset-y-1"))
    (is (= (tw-merge "right-1 inset-x-1 inset-y-1") "inset-x-1 inset-y-1"))
    (is (= (tw-merge "inset-x-1 hover:left-1 inset-1") "hover:left-1 inset-1")))

  (testing "ring and shadow classes do not create conflict"
    (is (= (tw-merge "ring shadow") "ring shadow"))
    (is (= (tw-merge "ring-2 shadow-md") "ring-2 shadow-md"))
    (is (= (tw-merge "shadow ring") "shadow ring"))
    (is (= (tw-merge "shadow-md ring-2") "shadow-md ring-2")))

  (testing "touch classes do create conflicts correctly"
    (is (= (tw-merge "touch-pan-x touch-pan-right") "touch-pan-right"))
    (is (= (tw-merge "touch-none touch-pan-x") "touch-pan-x"))
    (is (= (tw-merge "touch-pan-x touch-none") "touch-none"))
    (is (= (tw-merge "touch-pan-x touch-pan-y touch-pinch-zoom") "touch-pan-x touch-pan-y touch-pinch-zoom"))
    (is (= (tw-merge "touch-manipulation touch-pan-x touch-pan-y touch-pinch-zoom") "touch-pan-x touch-pan-y touch-pinch-zoom"))
    (is (= (tw-merge "touch-pan-x touch-pan-y touch-pinch-zoom touch-auto") "touch-auto")))

  (testing "line-clamp classes do create conflicts correctly"
    (is (= (tw-merge "overflow-auto inline line-clamp-1") "line-clamp-1"))
    (is (= (tw-merge "line-clamp-1 overflow-auto inline") "line-clamp-1 overflow-auto inline"))))

(deftest arbitrary-properties
  (testing "handles arbitrary property conflicts correctly"
    (is (= (tw-merge "[paint-order:markers] [paint-order:normal]") "[paint-order:normal]"))
    (is (= (tw-merge "[paint-order:markers] [--my-var:2rem] [paint-order:normal] [--my-var:4px]"),
           "[paint-order:normal] [--my-var:4px]")))

  (testing "handles arbitrary property conflicts with modifiers correctly"
    (is (= (tw-merge "[paint-order:markers] hover:[paint-order:normal]") "[paint-order:markers] hover:[paint-order:normal]"))
    (is (= (tw-merge "hover:[paint-order:markers] hover:[paint-order:normal]") "hover:[paint-order:normal]"))
    (is (= (tw-merge "hover:focus:[paint-order:markers] focus:hover:[paint-order:normal]") "focus:hover:[paint-order:normal]"))
    (is (= (tw-merge "[paint-order:markers] [paint-order:normal] [--my-var:2rem] lg:[--my-var:4px]"),
           "[paint-order:normal] [--my-var:2rem] lg:[--my-var:4px]"))
    (is (= (tw-merge "bg-[#B91C1C] bg-opacity-[0.56] bg-opacity-[48%]") "bg-[#B91C1C] bg-opacity-[48%]")))

  (testing "handles complex arbitrary property conflicts correctly"
    (is (= (tw-merge "[-unknown-prop:::123:::] [-unknown-prop:url(https://hi.com)]") "[-unknown-prop:url(https://hi.com)]")))

  (testing "handles important modifier correctly"
    (is (= (tw-merge "![some:prop] [some:other]") "![some:prop] [some:other]"))
    (is (= (tw-merge "![some:prop] [some:other] [some:one] ![some:another]") "[some:one] ![some:another]"))))
