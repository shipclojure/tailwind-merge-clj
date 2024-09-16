(ns shipclojure.default-config
  (:require [shipclojure.validators :as v]))

(defn from-theme
  [key]
  (fn [theme]
    (get theme key [])))

(def colors (from-theme "colors"))
(def spacing (from-theme "spacing"))
(def blur (from-theme "blur"))
(def brightness (from-theme "brightness"))
(def border-color (from-theme "borderColor"))
(def border-radius (from-theme "borderRadius"))
(def border-spacing (from-theme "borderSpacing"))
(def border-width (from-theme "borderWidth"))
(def contrast (from-theme "contrast"))
(def grayscale (from-theme "grayscale"))
(def hue-rotate (from-theme "hueRotate"))
(def invert (from-theme "invert"))
(def gap (from-theme "gap"))
(def gradient-color-stops (from-theme "gradientColorStops"))
(def gradient-color-stop-positions (from-theme "gradientColorStopPositions"))
(def inset (from-theme "inset"))
(def margin (from-theme "margin"))
(def opacity (from-theme "opacity"))
(def padding (from-theme "padding"))
(def saturate (from-theme "saturate"))
(def scale (from-theme "scale"))
(def sepia (from-theme "sepia"))
(def skew (from-theme "skew"))
(def space (from-theme "space"))
(def translate (from-theme "translate"))

(defn get-overscroll []
  ["auto" "contain" "none"])

(defn get-overflow []
  ["auto" "hidden" "clip" "visible" "scroll"])

(defn get-spacing-with-auto-and-arbitrary []
  ["auto" v/arbitrary-value?])

(defn get-spacing-with-arbitrary []
  [v/arbitrary-value? spacing])

(defn get-length-with-empty-and-arbitrary []
  ["" v/length? v/arbitrary-length?])

(defn get-number-with-auto-and-arbitrary []
  ["auto" v/tw-number? v/arbitrary-value?])

(defn get-positions []
  ["bottom" "center" "left" "left-bottom" "left-top" "right" "right-bottom" "right-top" "top"])

(defn get-line-styles []
  ["solid" "dashed" "dotted" "double" "none"])

(defn get-blend-modes []
  ["normal" "multiply" "screen" "overlay" "darken" "lighten" "color-dodge" "color-burn"
   "hard-light" "soft-light" "difference" "exclusion" "hue" "saturation" "color" "luminosity"])

(defn get-align []
  ["start" "end" "center" "between" "around" "evenly" "stretch"])

(defn get-zero-and-empty []
  ["" "0" v/arbitrary-value?])

(defn get-breaks []
  ["auto" "avoid" "all" "avoid-page" "page" "left" "right" "column"])

(defn get-number-and-arbitrary []
  [v/tw-number? v/arbitrary-value?])

(defn get-default-config []
  {"cache-size" 500
   "separator" ":"
   "theme" {"colors" [v/any?]
            "spacing" [v/length? v/arbitrary-length?]
            "blur" ["none" "" v/tshirt-unit? v/arbitrary-value?]
            "brightness" (get-number-and-arbitrary)
            "borderColor" [colors]
            "borderRadius" ["none" "" "full" v/tshirt-unit? v/arbitrary-value?]
            "borderSpacing" (get-spacing-with-arbitrary)
            "borderWidth" (get-length-with-empty-and-arbitrary)
            "contrast" (get-number-and-arbitrary)
            "grayscale" (get-zero-and-empty)
            "hueRotate" (get-number-and-arbitrary)
            "invert" (get-zero-and-empty)
            "gap" (get-spacing-with-arbitrary)
            "gradientColorStops" [colors]
            "gradientColorStopPositions" [v/percent? v/arbitrary-length?]
            "inset" (get-spacing-with-auto-and-arbitrary)
            "margin" (get-spacing-with-auto-and-arbitrary)
            "opacity" (get-number-and-arbitrary)
            "padding" (get-spacing-with-arbitrary)
            "saturate" (get-number-and-arbitrary)
            "scale" (get-number-and-arbitrary)
            "sepia" (get-zero-and-empty)
            "skew" (get-number-and-arbitrary)
            "space" (get-spacing-with-arbitrary)
            "translate" (get-spacing-with-arbitrary)}
   "class-groups" {"aspect" [{"aspect" ["auto" "square" "video" v/arbitrary-value?]}]
                   "container" ["container"]
                   "columns" [{"columns" [v/tshirt-unit?]}]
                   "break-after" [{"break-after" (get-breaks)}]
                   "break-before" [{"break-before" (get-breaks)}]
                   "break-inside" [{"break-inside" ["auto" "avoid" "avoid-page" "avoid-column"]}]
                   "box-decoration" [{"box-decoration" ["slice" "clone"]}]
                   "box" [{"box" ["border" "content"]}]
                   "display" ["block" "inline-block" "inline" "flex" "inline-flex" "table" "inline-table"
                              "table-caption" "table-cell" "table-column" "table-column-group"
                              "table-footer-group" "table-header-group" "table-row-group" "table-row"
                              "flow-root" "grid" "inline-grid" "contents" "list-item" "hidden"]
                   "float" [{"float" ["right" "left" "none" "start" "end"]}]
                   "clear" [{"clear" ["left" "right" "both" "none" "start" "end"]}]
                   "isolation" ["isolate" "isolation-auto"]
                   "object-fit" [{"object" ["contain" "cover" "fill" "none" "scale-down"]}]
                   "object-position" [{"object" (into (get-positions) [v/arbitrary-value?])}]
                   "overflow" [{"overflow" (get-overflow)}]
                   "overflow-x" [{"overflow-x" (get-overflow)}]
                   "overflow-y" [{"overflow-y" (get-overflow)}]
                   "overscroll" [{"overscroll" (get-overscroll)}]
                   "overscroll-x" [{"overscroll-x" (get-overscroll)}]
                   "overscroll-y" [{"overscroll-y" (get-overscroll)}]
                   "position" ["static" "fixed" "absolute" "relative" "sticky"]
                   "inset" [{"inset" [inset]}]
                   "inset-x" [{"inset-x" [inset]}]
                   "inset-y" [{"inset-y" [inset]}]
                   "start" [{"start" [inset]}]
                   "end" [{"end" [inset]}]
                   "top" [{"top" [inset]}]
                   "bottom" [{"bottom" [inset]}]
                   "left" [{"left" [inset]}]
                   "right" [{"right" [inset]}]
                   "visibility" ["visible" "invisible" "collapse"]
                   "z" [{"z" ["auto" v/tw-integer? v/arbitrary-value?]}]
                   "basis" [{"basis" (get-spacing-with-auto-and-arbitrary)}]
                   "flex-direction" [{"flex" ["row" "row-reverse" "col" "col-reverse"]}]
                   "flex-wrap" [{"flex" ["wrap" "wrap-reverse" "nowrap"]}]
                   "flex" [{"flex" ["1" "auto" "initial" "none" v/arbitrary-value?]}]
                   "grow" [{"grow" (get-zero-and-empty)}]
                   "shrink" [{"shrink" (get-zero-and-empty)}]
                   "order" [{"order" ["first" "last" "none" v/tw-integer? v/arbitrary-value?]}]
                   "grid-cols" [{"grid-cols" [v/any?]}]
                   "col-start-end" [{"col" ["auto" {"span" ["full" v/tw-integer? v/arbitrary-value?]} v/arbitrary-value?]}]
                   "col-start" [{"col-start" (get-number-with-auto-and-arbitrary)}]
                   "col-end" [{"col-end" (get-number-with-auto-and-arbitrary)}]
                   "grid-rows" [{"grid-rows" [v/any?]}]
                   "row-start-end" [{"row" ["auto" {"span" [v/tw-integer? v/arbitrary-value?]} v/arbitrary-value?]}]
                   "row-start" [{"row-start" (get-number-with-auto-and-arbitrary)}]
                   "row-end" [{"row-end" (get-number-with-auto-and-arbitrary)}]
                   "grid-flow" [{"grid-flow" ["row" "col" "dense" "row-dense" "col-dense"]}]
                   "auto-cols" [{"auto-cols" ["auto" "min" "max" "fr" v/arbitrary-value?]}]
                   "auto-rows" [{"auto-rows" ["auto" "min" "max" "fr" v/arbitrary-value?]}]
                   "gap" [{"gap" [gap]}]
                   "gap-x" [{"gap-x" [gap]}]
                   "gap-y" [{"gap-y" [gap]}]
                   "justify-content" [{"justify:" (into ["normal"] (get-align))}]
                   "justify-items" [{"justify-items" ["start", "end", "center", "stretch"]}]
                   "justify-self" [{"justify-self" ["auto", "start", "end", "center", "stretch"]}],
                   "align-content" [{"content" (into ["normal" "baseline"] (get-align))}],
                   "align-items" [{"items" ["start", "end", "center", "baseline", "stretch"]}],
                   "align-self" [{"self" ["auto", "start", "end", "center", "stretch", "baseline"]}],
                   "place-content" [{"place-content" (into (get-align) ["baseline"])}],
                   "place-items" [{"place-items" ["start", "end", "center", "baseline", "stretch"]}],
                   "place-self" [{"place-self" ["auto", "start", "end", "center", "stretch"]}]
                   "p" [{"p" [padding]}],
                   "px" [{"px" [padding]}],
                   "py" [{"py" [padding]}],
                   "ps" [{"ps" [padding]}],
                   "pe" [{"pe" [padding]}],
                   "pt" [{"pt" [padding]}],
                   "pb" [{"pb" [padding]}],
                   "pr" [{"pr" [padding]}],
                   "pl" [{"pl" [padding]}],
                   "m" [{"m" [margin]}],
                   "mx" [{"mx" [margin]}],
                   "my" [{"my" [margin]}],
                   "ms" [{"ms" [margin]}],
                   "me" [{"me" [margin]}],
                   "mt" [{"mt" [margin]}],
                   "mr" [{"mr" [margin]}],
                   "mb" [{"mb" [margin]}],
                   "ml" [{"ml" [margin]}]
                   "space-x" [{"space-x" [space]}],
                   "space-x-reverse" ["space-x-reverse"],
                   "space-y" [{"space-y" [space]}],
                   "space-y-reverse" ["space-y-reverse"]
                   "w" [{"w" ["auto",
                              "min",
                              "max",
                              "fit",
                              "svw",
                              "lvw",
                              "dvw",
                              v/arbitrary-value?,
                              spacing]}]
                   "min-w" [{"min-w" [v/arbitrary-value?, spacing, "min", "max", "fit"]}]

                   "max-w" [{"max-w" [v/arbitrary-value?,
                                      spacing,
                                      "none",
                                      "full",
                                      "min",
                                      "max",
                                      "fit",
                                      "prose",
                                      {"screen" [v/tshirt-unit?]},
                                      v/tshirt-unit?]}]

                   "h" [{"h" ["isArbitraryValue"
                              "spacing"
                              "auto"
                              "min"
                              "max"
                              "fit"
                              "svh"
                              "lvh"
                              "dvh"]}]
                   "min-h" [{"min-h" ["isArbitraryValue"
                                      "spacing"
                                      "min"
                                      "max"
                                      "fit"
                                      "svh"
                                      "lvh"
                                      "dvh"]}]
                   "max-h" [{"max-h" ["isArbitraryValue"
                                      "spacing"
                                      "min"
                                      "max"
                                      "fit"
                                      "svh"
                                      "lvh"
                                      "dvh"]}]
                   "size" [{"size" ["isArbitraryValue" "spacing" "auto" "min" "max" "fit"]}]
                   "font-size" [{"text" ["base" "isTshirtSize" "isArbitraryLength"]}]
                   "font-smoothing" ["antialiased" "subpixel-antialiased"]
                   "font-style" ["italic" "not-italic"]
                   "font-weight" [{"font" ["thin"
                                           "extralight"
                                           "light"
                                           "normal"
                                           "medium"
                                           "semibold"
                                           "bold"
                                           "extrabold"
                                           "black"
                                           "isArbitraryNumber"]}]
                   "font-family" [{"font" ["isAny"]}]
                   "fvn-normal" ["normal-nums"]
                   "fvn-ordinal" ["ordinal"]
                   "fvn-slashed-zero" ["slashed-zero"]
                   "fvn-figure" ["lining-nums" "oldstyle-nums"]
                   "fvn-spacing" ["proportional-nums" "tabular-nums"]
                   "fvn-fraction" ["diagonal-fractions" "stacked-fractons"]
                   "tracking" [{"tracking" ["tighter"
                                            "tight"
                                            "normal"
                                            "wide"
                                            "wider"
                                            "widest"
                                            "isArbitraryValue"]}]
                   "line-clamp" [{"line-clamp" ["none" "isNumber" "isArbitraryNumber"]}]
                   "leading" [{"leading" ["none"
                                          "tight"
                                          "snug"
                                          "normal"
                                          "relaxed"
                                          "loose"
                                          "isLength"
                                          "isArbitraryValue"]}]
                   "list-image" [{"list-image" ["none" "isArbitraryValue"]}]
                   "list-style-type" [{"list" ["none" "disc" "decimal" "isArbitraryValue"]}]
                   "list-style-position" [{"list" ["inside" "outside"]}]
                   "placeholder-color" [{"placeholder" ["colors"]}]
                   "placeholder-opacity" [{"placeholder-opacity" ["opacity"]}]
                   "text-alignment" [{"text" ["left" "center" "right" "justify" "start" "end"]}]
                   "text-color" [{"text" ["colors"]}]
                   "text-opacity" [{"text-opacity" ["opacity"]}]
                   "text-decoration" ["underline" "overline" "line-through" "no-underline"]
                   "text-decoration-style" [{"decoration" ["getLineStyles" "wavy"]}]
                   "text-decoration-thickness" [{"decoration" ["auto" "from-font" "isLength" "isArbitraryLength"]}]
                   "underline-offset" [{"underline-offset" ["auto" "isLength" "isArbitraryValue"]}]
                   "text-decoration-color" [{"decoration" ["colors"]}]}})

(into ["normal"] (get-align))
