(ns twmerge.core-test
  (:require
   [clojure.test :refer [deftest testing are is]]
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

(deftest arbitrary-values
  (testing "handles simple conflicts with arbitrary values correctly"
    (is (= (tw-merge "m-[2px] m-[10px]") "m-[10px]"))
    (is (= (tw-merge "m-[2px] m-[11svmin] m-[12in] m-[13lvi] m-[14vb] m-[15vmax] m-[16mm] m-[17%] m-[18em] m-[19px] m-[10dvh]") "m-[10dvh]"))
    (is (= (tw-merge "h-[10px] h-[11cqw] h-[12cqh] h-[13cqi] h-[14cqb] h-[15cqmin] h-[16cqmax]") "h-[16cqmax]"))
    (is (= (tw-merge "z-20 z-[99]") "z-[99]"))
    (is (= (tw-merge "my-[2px] m-[10rem]") "m-[10rem]"))
    (is (= (tw-merge "cursor-pointer cursor-[grab]") "cursor-[grab]"))
    (is (= (tw-merge "m-[2px] m-[calc(100%-var(--arbitrary))]") "m-[calc(100%-var(--arbitrary))]"))
    (is (= (tw-merge "m-[2px] m-[length:var(--mystery-var)]") "m-[length:var(--mystery-var)]"))
    (is (= (tw-merge "opacity-10 opacity-[0.025]") "opacity-[0.025]"))
    (is (= (tw-merge "scale-75 scale-[1.7]") "scale-[1.7]"))
    (is (= (tw-merge "brightness-90 brightness-[1.75]") "brightness-[1.75]"))

    (is (= (tw-merge "min-h-[0.5px] min-h-[0]") "min-h-[0]"))
    (is (= (tw-merge "text-[0.5px] text-[color:0]") "text-[0.5px] text-[color:0]"))
    (is (= (tw-merge "text-[0.5px] text-[--my-0]") "text-[0.5px] text-[--my-0]")))

  (testing "handles arbitrary length conflicts with labels and modifiers correctly"
    (is (= (tw-merge "hover:m-[2px] hover:m-[length:var(--c)]") "hover:m-[length:var(--c)]"))
    (is (= (tw-merge "hover:focus:m-[2px] focus:hover:m-[length:var(--c)]") "focus:hover:m-[length:var(--c)]"))
    (is (= (tw-merge "border-b border-[color:rgb(var(--color-gray-500-rgb)/50%))]") "border-b border-[color:rgb(var(--color-gray-500-rgb)/50%))]"))
    (is (= (tw-merge "border-[color:rgb(var(--color-gray-500-rgb)/50%))] border-b") "border-[color:rgb(var(--color-gray-500-rgb)/50%))] border-b"))
    (is (= (tw-merge "border-b border-[color:rgb(var(--color-gray-500-rgb)/50%))] border-some-coloooor") "border-b border-some-coloooor")))

  (testing "handles complex arbitrary value conflicts correctly"
    (is (= (tw-merge "grid-rows-[1fr,auto] grid-rows-2") "grid-rows-2"))
    (is (= (tw-merge "grid-rows-[repeat(20,minmax(0,1fr))] grid-rows-3") "grid-rows-3")))

  (testing "handles ambiguous arbitrary values correctly"
    (is (= (tw-merge "mt-2 mt-[calc(theme(fontSize.4xl)/1.125)]") "mt-[calc(theme(fontSize.4xl)/1.125)]"))
    (is (= (tw-merge "p-2 p-[calc(theme(fontSize.4xl)/1.125)_10px]") "p-[calc(theme(fontSize.4xl)/1.125)_10px]"))
    (is (= (tw-merge "mt-2 mt-[length:theme(someScale.someValue)]") "mt-[length:theme(someScale.someValue)]"))
    (is (= (tw-merge "mt-2 mt-[theme(someScale.someValue)]") "mt-[theme(someScale.someValue)]"))
    (is (= (tw-merge "text-2xl text-[length:theme(someScale.someValue)]") "text-[length:theme(someScale.someValue)]"))
    (is (= (tw-merge "text-2xl text-[calc(theme(fontSize.4xl)/1.125)]") "text-[calc(theme(fontSize.4xl)/1.125)]"))
    (is (= (tw-merge "bg-cover bg-[percentage:30%] bg-[length:200px_100px]") "bg-[length:200px_100px]"))
    (is (= (tw-merge "bg-none bg-[url(.)] bg-[image:.] bg-[url:.] bg-[linear-gradient(.)] bg-gradient-to-r") "bg-gradient-to-r"))))

(deftest arbitrary-variants
  (testing "basic arbitrary variants"
    (is (= (tw-merge "[&>*]:underline [&>*]:line-through") "[&>*]:line-through"))
    (is (= (tw-merge "[&>*]:underline [&>*]:line-through [&_div]:line-through") "[&>*]:line-through [&_div]:line-through"))
    (is (= (tw-merge "supports-[display:grid]:flex supports-[display:grid]:grid") "supports-[display:grid]:grid")))

  (testing "arbitrary variants with modifiers"
    (is (= (tw-merge "dark:lg:hover:[&>*]:underline dark:lg:hover:[&>*]:line-through") "dark:lg:hover:[&>*]:line-through"))
    (is (= (tw-merge "dark:lg:hover:[&>*]:underline dark:hover:lg:[&>*]:line-through") "dark:hover:lg:[&>*]:line-through"))
    (is (= (tw-merge "hover:[&>*]:underline [&>*]:hover:line-through") "hover:[&>*]:underline [&>*]:hover:line-through"))
    (is (= (tw-merge "hover:dark:[&>*]:underline dark:hover:[&>*]:underline dark:[&>*]:hover:line-through"),,
           "dark:hover:[&>*]:underline dark:[&>*]:hover:line-through")))

  (testing "arbitrary variants with complex syntax in them"
    (is (= (tw-merge "[@media_screen{@media(hover:hover)}]:underline [@media_screen{@media(hover:hover)}]:line-through"),,
           "[@media_screen{@media(hover:hover)}]:line-through"))
    (is (= (tw-merge "hover:[@media_screen{@media(hover:hover)}]:underline hover:[@media_screen{@media(hover:hover)}]:line-through"),,
           "hover:[@media_screen{@media(hover:hover)}]:line-through")))

  (testing "arbitrary variants with attribute selectors"
    (is (= (tw-merge "[&[data-open]]:underline [&[data-open]]:line-through") "[&[data-open]]:line-through")))

  (testing "arbitrary variants with multiple attribute selectors"
    (is (= (tw-merge "[&[data-foo][data-bar]:not([data-baz])]:underline [&[data-foo][data-bar]:not([data-baz])]:line-through"),,
           "[&[data-foo][data-bar]:not([data-baz])]:line-through")))

  (testing "multiple arbitrary variants"
    (is (= (tw-merge "[&>*]:[&_div]:underline [&>*]:[&_div]:line-through") "[&>*]:[&_div]:line-through"))
    (is (= (tw-merge "[&>*]:[&_div]:underline [&_div]:[&>*]:line-through") "[&>*]:[&_div]:underline [&_div]:[&>*]:line-through"))
    (is (= (tw-merge "hover:dark:[&>*]:focus:disabled:[&_div]:underline dark:hover:[&>*]:disabled:focus:[&_div]:line-through"),,
           "dark:hover:[&>*]:disabled:focus:[&_div]:line-through"))
    (is (= (tw-merge "hover:dark:[&>*]:focus:[&_div]:disabled:underline dark:hover:[&>*]:disabled:focus:[&_div]:line-through"),,
           "hover:dark:[&>*]:focus:[&_div]:disabled:underline dark:hover:[&>*]:disabled:focus:[&_div]:line-through")))

  (testing "arbitrary variants with arbitrary properties"
    (is (= (tw-merge "[&>*]:[color:red] [&>*]:[color:blue]") "[&>*]:[color:blue]"))
    (is (= (tw-merge "[&[data-foo][data-bar]:not([data-baz])]:nod:noa:[color:red] [&[data-foo][data-bar]:not([data-baz])]:noa:nod:[color:blue]"),,
           "[&[data-foo][data-bar]:not([data-baz])]:noa:nod:[color:blue]"))))

(deftest content-utilities
  (testing "merges content utilities correctly"
    (is (= (tw-merge "content-['hello'] content-[attr(data-content)]") "content-[attr(data-content)]"))))

(deftest important-modifier
  (testing "merges tailwind classes with important modifier correctly"
    (is (= (tw-merge "!font-medium !font-bold") "!font-bold"))
    (is (= (tw-merge "!font-medium !font-bold font-thin") "!font-bold font-thin"))
    (is (= (tw-merge "!right-2 !-inset-x-px") "!-inset-x-px"))
    (is (= (tw-merge "focus:!inline focus:!block") "focus:!block"))))

(deftest modifiers
  (testing "conflicts across prefix modifiers"
    (is (= (tw-merge "hover:block hover:inline") "hover:inline"))
    (is (= (tw-merge "hover:block hover:focus:inline") "hover:block hover:focus:inline"))
    (is (= (tw-merge "hover:block hover:focus:inline focus:hover:inline") "hover:block focus:hover:inline"))
    (is (= (tw-merge "focus-within:inline focus-within:block") "focus-within:block")))

  (testing "conflicts across postfix modifiers"
    (is (= (tw-merge "text-lg/7 text-lg/8") "text-lg/8"))
    (is (= (tw-merge "text-lg/none leading-9") "text-lg/none leading-9"))
    (is (= (tw-merge "leading-9 text-lg/none") "text-lg/none"))
    (is (= (tw-merge "w-full w-1/2") "w-1/2"))))

(deftest negative-values
  (testing "handles negative value conflicts correctly"
    (is (= (tw-merge "-m-2 -m-5") "-m-5"))
    (is (= (tw-merge "-top-12 -top-2000") "-top-2000")))

  (testing "handles conflicts between positive and negative values correctly"
    (is (= (tw-merge "-m-2 m-auto") "m-auto"))
    (is (= (tw-merge "top-12 -top-69") "-top-69")))

  (testing "handles conflicts across groups with negative values correctly",
    (is (= (tw-merge "-right-1 inset-x-1") "inset-x-1"))
    (is (= (tw-merge "hover:focus:-right-1 focus:hover:inset-x-1") "focus:hover:inset-x-1"))))

(deftest non-conflicting-classes
  (testing "merges non-conflicting classes correctly"
    (is (= (tw-merge "border-t border-white/10") "border-t border-white/10"))
    (is (= (tw-merge "border-t border-white") "border-t border-white"))
    (is (= (tw-merge "text-3.5xl text-black") "text-3.5xl text-black"))))

(deftest non-tailwind-classes
  (testing "does not alter non-tailwind classes"
    (is (= (tw-merge "non-tailwind-class inline block") "non-tailwind-class block"))
    (is (= (tw-merge "inline block inline-1") "block inline-1"))
    (is (= (tw-merge "inline block i-inline") "block i-inline"))
    (is (= (tw-merge "focus:inline focus:block focus:inline-1") "focus:block focus:inline-1"))))

(deftest per-side-border-color

  (testing "merges classes with per-side border colors correctly"
    (is (= (tw-merge "border-t-some-blue border-t-other-blue") "border-t-other-blue"))
    (is (= (tw-merge "border-t-some-blue border-some-blue") "border-some-blue"))))

#_(deftest prefixes
;; TODO support prefixes
  (testing "prefix working correctly"
    (is (= (tw-merge "tw-block tw-hidden") "tw-hidden"))
    (is (= (tw-merge "block hidden") "block hidden"))
    (is (= (tw-merge "tw-p-3 tw-p-2") "tw-p-2"))
    (is (= (tw-merge "p-3 p-2") "p-3 p-2"))
    (is (= (tw-merge "!tw-right-0 !tw-inset-0") "!tw-inset-0"))
    (is (= (tw-merge "hover:focus:!tw-right-0 focus:hover:!tw-inset-0") "focus:hover:!tw-inset-0"))))

(deftest pseudo-variants

  (testing "handles pseudo variants conflicts properly"
    (is (= (tw-merge "empty:p-2 empty:p-3") "empty:p-3"))
    (is (= (tw-merge "hover:empty:p-2 hover:empty:p-3") "hover:empty:p-3"))
    (is (= (tw-merge "read-only:p-2 read-only:p-3") "read-only:p-3")))

  (testing "handles pseudo variant group conflicts properly"
    (is (= (tw-merge "group-empty:p-2 group-empty:p-3") "group-empty:p-3"))
    (is (= (tw-merge "peer-empty:p-2 peer-empty:p-3") "peer-empty:p-3"))
    (is (= (tw-merge "group-empty:p-2 peer-empty:p-3") "group-empty:p-2 peer-empty:p-3"))
    (is (= (tw-merge "hover:group-empty:p-2 hover:group-empty:p-3") "hover:group-empty:p-3"))
    (is (= (tw-merge "group-read-only:p-2 group-read-only:p-3") "group-read-only:p-3"))))

(deftest standalone-classes

  (testing "merges standalone classes from same group correctly"
    (is (= (tw-merge "inline block") "block"))
    (is (= (tw-merge "hover:block hover:inline") "hover:inline"))
    (is (= (tw-merge "hover:block hover:block") "hover:block"))
    (is (= (tw-merge "inline hover:inline focus:inline hover:block hover:focus:block") "inline focus:inline hover:block hover:focus:block"))
    (is (= (tw-merge "underline line-through") "line-through"))
    (is (= (tw-merge "line-through no-underline") "no-underline"))))

(deftest tailwind-css-versions

  (testing "supports Tailwind CSS v3.3 features"
    (is (= (tw-merge "text-red text-lg/7 text-lg/8") "text-red text-lg/8"))
    (is (= (tw-merge "start-0 start-1 end-0 end-1 ps-0 ps-1 pe-0 pe-1 ms-0 ms-1 me-0 me-1 rounded-s-sm rounded-s-md rounded-e-sm rounded-e-md rounded-ss-sm rounded-ss-md rounded-ee-sm rounded-ee-md")
           "start-1 end-1 ps-1 pe-1 ms-1 me-1 rounded-s-md rounded-e-md rounded-ss-md rounded-ee-md"))
    (is (= (tw-merge "start-0 end-0 inset-0 ps-0 pe-0 p-0 ms-0 me-0 m-0 rounded-ss rounded-es rounded-s"),,
           "inset-0 p-0 m-0 rounded-s"))
    (is (= (tw-merge "hyphens-auto hyphens-manual") "hyphens-manual"))
    (is (= (tw-merge "from-0% from-10% from-[12.5%] via-0% via-10% via-[12.5%] to-0% to-10% to-[12.5%]"),
           "from-[12.5%] via-[12.5%] to-[12.5%]"))
    (is (= (tw-merge "from-0% from-red") "from-0% from-red"))
    (is (= (tw-merge "list-image-none list-image-[url(./my-image.png)] list-image-[var(--value)]"),
           "list-image-[var(--value)]"))
    (is (= (tw-merge "caption-top caption-bottom") "caption-bottom"))
    (is (= (tw-merge "line-clamp-2 line-clamp-none line-clamp-[10]") "line-clamp-[10]"))
    (is (= (tw-merge "delay-150 delay-0 duration-150 duration-0") "delay-0 duration-0"))
    (is (= (tw-merge "justify-normal justify-center justify-stretch") "justify-stretch"))
    (is (= (tw-merge "content-normal content-center content-stretch") "content-stretch"))
    (is (= (tw-merge "whitespace-nowrap whitespace-break-spaces") "whitespace-break-spaces")))

  (testing "supports Tailwind CSS v3.4 features"
    (is (= (tw-merge "h-svh h-dvh w-svw w-dvw") "h-dvh w-dvw"))
    (is (= (tw-merge "has-[[data-potato]]:p-1 has-[[data-potato]]:p-2 group-has-[:checked]:grid group-has-[:checked]:flex"),,
           "has-[[data-potato]]:p-2 group-has-[:checked]:flex"))
    (is (= (tw-merge "text-wrap text-pretty") "text-pretty"))
    (is (= (tw-merge "w-5 h-3 size-10 w-12") "size-10 w-12"))
    (is (= (tw-merge "grid-cols-2 grid-cols-subgrid grid-rows-5 grid-rows-subgrid") "grid-cols-subgrid grid-rows-subgrid"))
    (is (= (tw-merge "min-w-0 min-w-50 min-w-px max-w-0 max-w-50 max-w-px") "min-w-px max-w-px"))
    (is (= (tw-merge "forced-color-adjust-none forced-color-adjust-auto") "forced-color-adjust-auto"))
    (is (= (tw-merge "appearance-none appearance-auto") "appearance-auto"))
    (is (= (tw-merge "float-start float-end clear-start clear-end") "float-end clear-end"))
    (is (= (tw-merge "*:p-10 *:p-20 hover:*:p-10 hover:*:p-20") "*:p-20 hover:*:p-20"))))

(deftest wonky-inputs
  (testing "handles wonky inputs"
    (is (= (tw-merge " block") "block"))
    (is (= (tw-merge "block ") "block"))
    (is (= (tw-merge " block ") "block"))
    (is (= (tw-merge "  block  px-2     py-4  ") "block px-2 py-4"))
    (is (= (tw-merge "  block  px-2"), "block px-2"))
    (is (= (tw-merge "block\npx-2") "block px-2"))
    (is (= (tw-merge "\nblock\npx-2\n") "block px-2"))
    (is (= (tw-merge "  block\n        \n        px-2   \n          py-4  ") "block px-2 py-4"))
    (is (= (tw-merge "\r  block\n\r        \n        px-2   \n          py-4  ") "block px-2 py-4"))))
