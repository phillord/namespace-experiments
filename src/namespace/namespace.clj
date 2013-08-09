(ns namespace.namespace)
;; refer
;; :exclude list-of-symbols
;; :only list-of-symbols
;; :rename map-of-fromsymbol-tosymbol
;;
;; refer iterates through every symbol in a namespace to do it's work. It
;; bottoms out in a call on the *ns* object. Use bottoms out here.

;; require
;;  :as takes a symbol as its argument and makes that symbol an alias to the
;;    lib's namespace in the current namespace.
;;  :refer takes a list of symbols to refer from the namespace or the :all
;;    keyword to bring in all public vars. All :refer function comes from refer

;; alias
;;
;; Defines an alias from one namespace to another. This bottoms out in a call
;; on the *ns* object.

;; so, everything is going to work on a "require spec". This will be a vector
;; with the first element a namespace symbol. Having this in the same place
;; will make generic functions easier. At the end, we will have a function
;; which operates over all of these.
;; [symbol] -- require the namespace
;; [symbol :as x] -- add an alias to the given namespace
;; [symbol :refer x y z] -- from symbol refer to x y z
;; [symbol :rename {x x1 y y2}] from symbol rename and refer to
;; [symbol :import] -- symbol is a java class, so import it
;;
;; [symbol] -->
;; (require 'symbol)
;; [symbol :as x] -->
;; (alias 'x 'symbol)
;; [symbol :refer x y z] -->
;; (refer 'symbol :only '(x y z))
;; [symbol :all] -->
;; (refer 'symbol :all)
;; [symbol :exclude '(x y z)] -->
;; (refer 'symbol :exclude '(x y z))
;; [symbol :rename {x x1 y y1}] -->
;; (refer 'symbol :rename {x x1 y y1}
(defn lq [arg]
  (list 'quote arg))

(defn spec-expand [spec]
  (println "spec" spec)
  (if
      (= 1 (count spec))
    (list 'require (lq (spec 0)))
    (case
        (spec 1)
      :as
      (list 'alias (lq (spec 2)) (lq (spec 0)))
      :refer
      (list 'refer (lq (spec 0)) :only (lq (nthrest spec 2)))
      :all
      (list 'refer (lq (spec 0)) :refer :all)
      :exclude
      (list 'refer (lq (spec 0)) :exclude (lq (nthrest spec 2)))
      :rename
      (list 'refer (lq (spec 0)) :rename (lq (spec 2)))
      :import
      (list 'import ~(spec 0)))))

(defn namespace-process [list]
  (doall (map (comp eval spec-expand) list)))

(defn namespace-test [list]
  (doall (map spec-expand list)))

(defn namespace-process-n [& list]
  (doall (map namespace-process list)))

(defn namespace-test-n [& list]
  (doall (map namespace-test list)))

(defmacro req [& symbols]
  `(list
    ~@(for [n# symbols]
        `[(quote ~n#)])))

(defn as [alias spec-list]
  (conj
   (vec spec-list)
   (vec (list (ffirst spec-list)
              :as alias))))

(defn symb [& args]
  (symbol
   (apply str
          (map name args))))

(defn prefix [prefix & spec-list]
  (for [[n & args] (apply concat spec-list)]
    (vec
     (list*
      (symb prefix '. n)
      args))))

(defn use-all [spec-list]
  (interleave
   spec-list
   (for
       [[n] spec-list]
     [n :all])))

;; this blizes existing spec -- it needs to interleave
(defn use-only [spec-list & only-list]
  ;; spec-list should be one
  (conj
   (vec spec-list)
   (vec (list* (ffirst spec-list)
                :refer only-list))))

(defn use-exclude [spec-list & exclude-list]
  (conj
   (vec spec-list)
   (vec (list* (ffirst spec-list)
               :exclude exclude-list))))


(defn use-rename [spec-list rename-map]
  (conj
   (vec spec-list)
   (vec (list (ffirst spec-list) :rename rename-map))))


(defmacro newnamespace [ns & args]
  `(do
     ;; currently commented out because it's a pain for testing
     ;;(in-ns '~ns)
     (use 'namespace.namespace)
     (namespace-process-n ~@args)
     (refer 'namespace.namespace :only '())))

(defmacro newnamespacet [ns & args]
  ;; ignore the namespace
  `(apply concat
         (namespace-test-n ~@args)))
