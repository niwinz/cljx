(ns cljx.rules
  (:refer-clojure :exclude [==])
  (:use [clojure.core.logic :only [matche conde pred lvar == firsto]]))

(defn- meta-guard [key]
  #(-> % meta key (= true)))

(defn remove-marked [key]
  [#(matche [%]
            ([[_ var . _]]
               (pred var (meta-guard key)))
            ([x]
               (pred x (meta-guard key))))
   #(== % :cljx.core/exclude)])

(def cljs-protocols
  (let [x (lvar)]
    [#(conde ;; matche has some problems here; you need to match (symbol "clojure.lang.IFn"), so it doesn't really save space...
       ((== % 'clojure.lang.IFn)  (== x 'IFn))
       ;;other protocol renaming goes here
       )
     #(== % x)]))

(def remove-defmacro
  [#(firsto % 'defmacro)
   #(== % :cljx.core/exclude)])



(def cljs-rules [cljs-protocols
                 (remove-marked :clj)
                 remove-defmacro])

(def clj-rules [(remove-marked :cljs)])
