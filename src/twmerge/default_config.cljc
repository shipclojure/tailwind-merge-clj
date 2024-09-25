(ns twmerge.default-config
  (:require
   [twmerge.validators :as v]
   [flatland.ordered.map :as o]))

(defn from-theme
  [key]
  (with-meta
    (fn [theme]
      (get theme key []))
    {:theme-getter? true}))

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
  ["auto" v/arbitrary-value? spacing])

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
  {:cache-size 500
   :separator ":"
   :theme {"colors" [any?]
           "spacing" [v/length? v/arbitrary-length?]
           "blur" ["none" "" v/tshirt-size? v/arbitrary-value?]
           "brightness" (get-number-and-arbitrary)
           "borderColor" [colors]
           "borderRadius" ["none" "" "full" v/tshirt-size? v/arbitrary-value?]
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
   ;; We use ordered map as insertion order must be preserved when
   ;; iterating over the keys
   :class-groups (o/ordered-map
                  "aspect" [{"aspect" ["auto" "square" "video" v/arbitrary-value?]}]
                  "container" ["container"]
                  "columns" [{"columns" [v/tshirt-size?]}]
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
                  "grid-cols" [{"grid-cols" [any?]}]
                  "col-start-end" [{"col" ["auto" {"span" ["full" v/tw-integer? v/arbitrary-value?]} v/arbitrary-value?]}]
                  "col-start" [{"col-start" (get-number-with-auto-and-arbitrary)}]
                  "col-end" [{"col-end" (get-number-with-auto-and-arbitrary)}]
                  "grid-rows" [{"grid-rows" [any?]}]
                  "row-start-end" [{"row" ["auto" {"span" [v/tw-integer? v/arbitrary-value?]} v/arbitrary-value?]}]
                  "row-start" [{"row-start" (get-number-with-auto-and-arbitrary)}]
                  "row-end" [{"row-end" (get-number-with-auto-and-arbitrary)}]
                  "grid-flow" [{"grid-flow" ["row" "col" "dense" "row-dense" "col-dense"]}]
                  "auto-cols" [{"auto-cols" ["auto" "min" "max" "fr" v/arbitrary-value?]}]
                  "auto-rows" [{"auto-rows" ["auto" "min" "max" "fr" v/arbitrary-value?]}]
                  "gap" [{"gap" [gap]}]
                  "gap-x" [{"gap-x" [gap]}]
                  "gap-y" [{"gap-y" [gap]}]
                  "justify-content" [{"justify" (into ["normal"] (get-align))}]
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
                                     {"screen" [v/tshirt-size?]},
                                     v/tshirt-size?]}]

                  "h" [{"h" [v/arbitrary-value?
                             spacing
                             "auto"
                             "min"
                             "max"
                             "fit"
                             "svh"
                             "lvh"
                             "dvh"]}]
                  "min-h" [{"min-h" [v/arbitrary-value?
                                     spacing
                                     "min"
                                     "max"
                                     "fit"
                                     "svh"
                                     "lvh"
                                     "dvh"]}]
                  "max-h" [{"max-h" [v/arbitrary-value?
                                     spacing
                                     "min"
                                     "max"
                                     "fit"
                                     "svh"
                                     "lvh"
                                     "dvh"]}]
                  "size" [{"size" [v/arbitrary-value? spacing "auto" "min" "max" "fit"]}]
                  "font-size" [{"text" ["base" v/tshirt-size? v/arbitrary-length?]}]
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
                                          v/arbitrary-number?]}]
                  "font-family" [{"font" [any?]}]
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
                                           v/arbitrary-value?]}]
                  "line-clamp" [{"line-clamp" ["none" v/tw-number? v/arbitrary-number?]}]
                  "leading" [{"leading" ["none"
                                         "tight"
                                         "snug"
                                         "normal"
                                         "relaxed"
                                         "loose"
                                         v/length?
                                         v/arbitrary-value?]}]
                  "list-image" [{"list-image" ["none" v/arbitrary-value?]}]
                  "list-style-type" [{"list" ["none" "disc" "decimal" v/arbitrary-value?]}]
                  "list-style-position" [{"list" ["inside" "outside"]}]
                  "placeholder-color" [{"placeholder" [colors]}]
                  "placeholder-opacity" [{"placeholder-opacity" ["opacity"]}]
                  "text-alignment" [{"text" ["left" "center" "right" "justify" "start" "end"]}]
                  "text-color" [{"text" [colors]}]
                  "text-opacity" [{"text-opacity" ["opacity"]}]
                  "text-decoration" ["underline" "overline" "line-through" "no-underline"]
                  "text-decoration-style" [{"decoration" (into (get-line-styles) ["wavy"])}]
                  "text-decoration-thickness" [{"decoration" ["auto" "from-font" v/length? v/arbitrary-length?]}]
                  "underline-offset" [{"underline-offset" ["auto" v/length? v/arbitrary-value?]}]
                  "text-decoration-color" [{"decoration" [colors]}]
                  "text-transform" ["uppercase" "lowercase" "capitalize" "normal-case"]
                  "text-overflow" ["truncate" "text-ellipsis" "text-clip"]
                  "text-wrap" [{"text" ["wrap" "nowrap" "balance" "pretty"]}]
                  "indent" [{"indent" (get-spacing-with-arbitrary)}]
                  "vertical-align" [{"align" ["baseline" "top" "middle" "bottom" "text-top" "text-bottom" "sub" "super" v/arbitrary-value?]}]

                  "whitespace" [{"whitespace" ["normal" "nowrap" "pre" "pre-line" "pre-wrap" "break-spaces"]}]
                  "break" [{"break" ["normal" "words" "all" "keep"]}]
                  "hyphens" [{"hyphens" ["none" "manual" "auto"]}]
                  "content" [{"content" ["none" v/arbitrary-value?]}]
                  "bg-attachment" [{"bg" ["fixed" "local" "scroll"]}]
                  "bg-clip" [{"bg-clip" ["border" "padding" "content" "text"]}]
                  "bg-opacity" [{"bg-opacity" [opacity]}]
                  "bg-origin" [{"bg-origin" ["border" "padding" "content"]}]
                  "bg-position" [{"bg" (into (get-positions) [v/arbitrary-position?])}]
                  "bg-repeat" [{"bg" ["no-repeat" {"repeat" ["" "x" "y" "round" "space"]}]}]
                  "bg-size" [{"bg" ["auto" "cover" "contain" v/arbitrary-size?]}]
                  "bg-image" [{"bg" ["none"
                                     {"gradient-to" ["t" "tr" "r" "br" "b" "bl" "l" "tl"]}
                                     v/arbitrary-image?]}]

                  "bg-color" [{"bg" [colors]}]
                  "gradient-from-pos" [{"from" [gradient-color-stop-positions]}]
                  "gradient-via-pos" [{"via" [gradient-color-stop-positions]}]
                  "gradient-to-pos" [{"to" [gradient-color-stop-positions]}]
                  "gradient-from" [{"from" [gradient-color-stops]}]
                  "gradient-via" [{"via" [gradient-color-stops]}]
                  "gradient-to" [{"to" [gradient-color-stops]}]
                  "rounded" [{"rounded" [border-radius]}]
                  "rounded-s" [{"rounded-s" [border-radius]}]
                  "rounded-e" [{"rounded-e" [border-radius]}]
                  "rounded-t" [{"rounded-t" [border-radius]}]
                  "rounded-r" [{"rounded-r" [border-radius]}]
                  "rounded-b" [{"rounded-b" [border-radius]}]
                  "rounded-l" [{"rounded-l" [border-radius]}]
                  "rounded-ss" [{"rounded-ss" [border-radius]}]
                  "rounded-se" [{"rounded-se" [border-radius]}]
                  "rounded-ee" [{"rounded-ee" [border-radius]}]
                  "rounded-es" [{"rounded-es" [border-radius]}]
                  "rounded-tl" [{"rounded-tl" [border-radius]}]
                  "rounded-tr" [{"rounded-tr" [border-radius]}]
                  "rounded-br" [{"rounded-br" [border-radius]}]
                  "rounded-bl" [{"rounded-bl" [border-radius]}]
                  "border-w" [{"border" [border-width]}]
                  "border-w-x" [{"border-x" [border-width]}]
                  "border-w-y" [{"border-y" [border-width]}]
                  "border-w-s" [{"border-s" [border-width]}]
                  "border-w-e" [{"border-e" [border-width]}]
                  "border-w-t" [{"border-t" [border-width]}]
                  "border-w-r" [{"border-r" [border-width]}]
                  "border-w-b" [{"border-b" [border-width]}]
                  "border-w-l" [{"border-l" [border-width]}]
                  "border-opacity" [{"border-opacity" [opacity]}]
                  "border-style" [{"border" (into (get-line-styles) ["hidden"])}]

                  "divide-x" [{"divide-x" [border-width]}]
                  "divide-x-reverse" ["divide-x-reverse"]
                  "divide-y" [{"divide-y" [border-width]}]
                  "divide-y-reverse" ["divide-y-reverse"]
                  "divide-opacity" [{"divide-opacity" [opacity]}]
                  "divide-style" [{"divide" (get-line-styles)}]
                  "border-color" [{"border" [border-color]}]
                  "border-color-x" [{"border-x" [border-color]}]
                  "border-color-y" [{"border-y" [border-color]}]
                  "border-color-t" [{"border-t" [border-color]}]
                  "border-color-r" [{"border-r" [border-color]}]
                  "border-color-b" [{"border-b" [border-color]}]
                  "border-color-l" [{"border-l" [border-color]}]
                  "divide-color" [{"divide" [border-color]}]
                  "outline-style" [{"outline" (into [""] (get-line-styles))}]
                  "outline-offset" [{"outline-offset" [v/length? v/arbitrary-value?]}]
                  "outline-w" [{"outline" [v/length? v/arbitrary-length?]}]
                  "outline-color" [{"outline" [colors]}]
                  "ring-w" [{"ring" (get-length-with-empty-and-arbitrary)}]
                  "ring-w-inset" ["ring-inset"]
                  "ring-color" [{"ring" [colors]}]
                  "ring-opacity" [{"ring-opacity" [opacity]}]
                  "ring-offset-w" [{"ring-offset" [v/length? v/arbitrary-length?]}]
                  "ring-offset-color" [{"ring-offset" [colors]}]
                  "shadow" [{"shadow" ["" "inner" "none" v/tshirt-size? v/arbitrary-shadow?]}]
                  "shadow-color" [{"shadow" [any?]}]
                  "opacity" [{"opacity" [opacity]}]
                  "mix-blend" [{"mix-blend" (into (get-blend-modes) ["plus-lighter" "plus-darker"])}]
                  "bg-blend" [{"bg-blend" (get-blend-modes)}]
                  "filter" [{"filter" ["" "none"]}]
                  "blur" [{"blur" [blur]}]
                  "brightness" [{"brightness" [brightness]}]
                  "contrast" [{"contrast" [contrast]}]
                  "drop-shadow" [{"drop-shadow" ["" "none" v/tshirt-size? v/arbitrary-value?]}]
                  "grayscale" [{"grayscale" [grayscale]}]
                  "hue-rotate" [{"hue-rotate" [hue-rotate]}]
                  "invert" [{"invert" [invert]}]
                  "saturate" [{"saturate" [saturate]}]
                  "sepia" [{"sepia" [sepia]}]
                  "backdrop-filter" [{"backdrop-filter" ["" "none"]}]
                  "backdrop-blur" [{"backdrop-blur" [blur]}]
                  "backdrop-brightness" [{"backdrop-brightness" [brightness]}]
                  "backdrop-contrast" [{"backdrop-contrast" [contrast]}]
                  "backdrop-grayscale" [{"backdrop-grayscale" [grayscale]}]
                  "backdrop-hue-rotate" [{"backdrop-hue-rotate" [hue-rotate]}]
                  "backdrop-invert" [{"backdrop-invert" [invert]}]
                  "backdrop-opacity" [{"backdrop-opacity" [opacity]}]
                  "backdrop-saturate" [{"backdrop-saturate" [saturate]}]
                  "backdrop-sepia" [{"backdrop-sepia" [sepia]}]
                  "border-collapse" [{"border" ["collapse" "separate"]}]

                  "border-spacing" [{"border-spacing" [border-spacing]}]
                  "border-spacing-x" [{"border-spacing-x" [border-spacing]}]
                  "border-spacing-y" [{"border-spacing-y" [border-spacing]}]
                  "table-layout" [{"table" ["auto" "fixed"]}]
                  "caption" [{"caption" ["top" "bottom"]}]
                  "transition" [{"transition" ["none" "all" "" "colors" "opacity" "shadow" "transform" v/arbitrary-value?]}]
                  "duration" [{"duration" (get-number-and-arbitrary)}]
                  "ease" [{"ease" ["linear" "in" "out" "in-out" v/arbitrary-value?]}]
                  "delay" [{"delay" (get-number-and-arbitrary)}]
                  "animate" [{"animate" ["none" "spin" "ping" "pulse" "bounce" v/arbitrary-value?]}]
                  "transform" [{"transform" ["" "gpu" "none"]}]
                  "scale" [{"scale" [scale]}]
                  "scale-x" [{"scale-x" [scale]}]
                  "scale-y" [{"scale-y" [scale]}]
                  "rotate" [{"rotate" [v/tw-integer? v/arbitrary-value?]}]
                  "translate-x" [{"translate-x" [translate]}]
                  "translate-y" [{"translate-y" [translate]}]
                  "skew-x" [{"skew-x" [skew]}]
                  "skew-y" [{"skew-y" [skew]}]
                  "transform-origin" [{"origin" ["center" "top" "top-right" "right" "bottom-right" "bottom" "bottom-left" "left" "top-left" v/arbitrary-value?]}]
                  "accent" [{"accent" ["auto" colors]}]
                  "appearance" [{"appearance" ["none" "auto"]}]
                  "cursor" [{"cursor" ["auto" "default" "pointer" "wait" "text" "move" "help" "not-allowed" "none" "context-menu" "progress" "cell" "crosshair" "vertical-text" "alias" "copy" "no-drop" "grab" "grabbing" "all-scroll" "col-resize" "row-resize" "n-resize" "e-resize" "s-resize" "w-resize" "ne-resize" "nw-resize" "se-resize" "sw-resize" "ew-resize" "ns-resize" "nesw-resize" "nwse-resize" "zoom-in" "zoom-out" v/arbitrary-value?]}]
                  "caret-color" [{"caret" [colors]}]
                  "pointer-events" [{"pointer-events" ["none" "auto"]}]
                  "resize" [{"resize" ["none" "y" "x" ""]}]
                  "scroll-behavior" [{"scroll" ["auto" "smooth"]}]
                  "scroll-m" [{"scroll-m" (get-spacing-with-arbitrary)}]
                  "scroll-mx" [{"scroll-mx" (get-spacing-with-arbitrary)}]
                  "scroll-my" [{"scroll-my" (get-spacing-with-arbitrary)}]
                  "scroll-ms" [{"scroll-ms" (get-spacing-with-arbitrary)}]
                  "scroll-me" [{"scroll-me" (get-spacing-with-arbitrary)}]
                  "scroll-mt" [{"scroll-mt" (get-spacing-with-arbitrary)}]
                  "scroll-mr" [{"scroll-mr" (get-spacing-with-arbitrary)}]
                  "scroll-mb" [{"scroll-mb" (get-spacing-with-arbitrary)}]
                  "scroll-ml" [{"scroll-ml" (get-spacing-with-arbitrary)}]
                  "scroll-p" [{"scroll-p" (get-spacing-with-arbitrary)}]
                  "scroll-px" [{"scroll-px" (get-spacing-with-arbitrary)}]
                  "scroll-py" [{"scroll-py" (get-spacing-with-arbitrary)}]
                  "scroll-ps" [{"scroll-ps" (get-spacing-with-arbitrary)}]
                  "scroll-pe" [{"scroll-pe" (get-spacing-with-arbitrary)}]
                  "scroll-pt" [{"scroll-pt" (get-spacing-with-arbitrary)}]
                  "scroll-pr" [{"scroll-pr" (get-spacing-with-arbitrary)}]
                  "scroll-pb" [{"scroll-pb" (get-spacing-with-arbitrary)}]
                  "scroll-pl" [{"scroll-pl" (get-spacing-with-arbitrary)}]
                  "snap-align" [{"snap" ["start" "end" "center" "align-none"]}]
                  "snap-stop" [{"snap" ["normal" "always"]}]
                  "snap-type" [{"snap" ["none" "x" "y" "both"]}]
                  "snap-strictness" [{"snap" ["mandatory" "proximity"]}]
                  "touch" [{"touch" ["auto" "none" "manipulation"]}]
                  "touch-x" [{"touch-pan" ["x" "left" "right"]}]
                  "touch-y" [{"touch-pan" ["y" "up" "down"]}]
                  "touch-pz" ["touch-pinch-zoom"]
                  "select" [{"select" ["none" "text" "all" "auto"]}]
                  "will-change" [{"will-change" ["auto" "scroll" "contents" "transform" v/arbitrary-value?]}]
                  "fill" [{"fill"  [colors "none"]}]
                  "stroke-w" [{"stroke" [v/length? v/arbitrary-length? v/arbitrary-number?]}]
                  "stroke" [{"stroke" [colors "none"]}]
                  "sr" ["sr-only" "not-sr-only"]
                  "forced-color-adjust" [{"forced-color-adjust" ["auto" "none"]}])

   :conflicting-class-groups {"overflow" ["overflow-x" "overflow-y"]
                              "overscroll" ["overscroll-x" "overscroll-y"]
                              "inset" ["inset-x" "inset-y" "start" "end" "top" "right" "bottom" "left"]
                              "inset-x" ["right" "left"]
                              "inset-y" ["top" "bottom"]
                              "flex" ["basis" "grow" "shrink"]
                              "gap" ["gap-x" "gap-y"]
                              "p" ["px" "py" "ps" "pe" "pt" "pr" "pb" "pl"]
                              "px" ["pr" "pl"]
                              "py" ["pt" "pb"]
                              "m" ["mx" "my" "ms" "me" "mt" "mr" "mb" "ml"]
                              "mx" ["mr" "ml"]
                              "my" ["mt" "mb"]
                              "size" ["w" "h"]
                              "font-size" ["leading"]
                              "fvn-normal" ["fvn-ordinal" "fvn-slashed-zero" "fvn-figure" "fvn-spacing" "fvn-fraction"]
                              "fvn-ordinal" ["fvn-normal"]
                              "fvn-slashed-zero" ["fvn-normal"]
                              "fvn-figure" ["fvn-normal"]
                              "fvn-spacing" ["fvn-normal"]
                              "fvn-fraction" ["fvn-normal"]
                              "line-clamp" ["display" "overflow"]
                              "rounded" ["rounded-s" "rounded-e" "rounded-t" "rounded-r" "rounded-b" "rounded-l"
                                         "rounded-ss" "rounded-se" "rounded-ee" "rounded-es"
                                         "rounded-tl" "rounded-tr" "rounded-br" "rounded-bl"]
                              "rounded-s" ["rounded-ss" "rounded-es"]
                              "rounded-e" ["rounded-se" "rounded-ee"]
                              "rounded-t" ["rounded-tl" "rounded-tr"]
                              "rounded-r" ["rounded-tr" "rounded-br"]
                              "rounded-b" ["rounded-br" "rounded-bl"]
                              "rounded-l" ["rounded-tl" "rounded-bl"]
                              "border-spacing" ["border-spacing-x" "border-spacing-y"]
                              "border-w" ["border-w-s" "border-w-e" "border-w-t" "border-w-r" "border-w-b" "border-w-l"]
                              "border-w-x" ["border-w-r" "border-w-l"]
                              "border-w-y" ["border-w-t" "border-w-b"]
                              "border-color" ["border-color-t" "border-color-r" "border-color-b" "border-color-l"]
                              "border-color-x" ["border-color-r" "border-color-l"]
                              "border-color-y" ["border-color-t" "border-color-b"]
                              "scroll-m" ["scroll-mx" "scroll-my" "scroll-ms" "scroll-me"
                                          "scroll-mt" "scroll-mr" "scroll-mb" "scroll-ml"]
                              "scroll-mx" ["scroll-mr" "scroll-ml"]
                              "scroll-my" ["scroll-mt" "scroll-mb"]
                              "scroll-p" ["scroll-px" "scroll-py" "scroll-ps" "scroll-pe"
                                          "scroll-pt" "scroll-pr" "scroll-pb" "scroll-pl"]
                              "scroll-px" ["scroll-pr" "scroll-pl"]
                              "scroll-py" ["scroll-pt" "scroll-pb"]
                              "touch" ["touch-x" "touch-y" "touch-pz"]
                              "touch-x" ["touch"]
                              "touch-y" ["touch"]
                              "touch-pz" ["touch"]}
   :conflicting-class-groups-modifiers {"font-size" ["leading"]}})

