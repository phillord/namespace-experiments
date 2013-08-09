(ns namespace.examples
  (:use [namespace.namespace]))

;; each of these defines a new namespace and adds requires and the like.
;; In practice, you don't want to do this --- just macroexpand each form. They
;; all expand at compile time to the primitive functions in clojure.core

(newnamespacet
  bob
  ;; the simplest use -- require two namespaces
  (req
   clojure.set
   clojure.data)
  ;; and a third -- we could have added this earlier, if we wanted.
  (req tawny.owl))

(newnamespacet
 bob
 ;; typing "clojure" all the time is a pain in the ass.
 ;; This requires clojure.set clojure.walk clojure.data
 ;; currently, only "req" is a macro, so some quoting is inevitable. This
 ;; could probably be smoothed out somewhat.
 (prefix 'clojure
         (req set walk data)))

(newnamespacet
 bob
 ;; sometimes we want to alias namespaces as well as requiring them.
 ;; Only one can be namespace can be aliased at a time (since having two
 ;; namespaces with the same alias makes no sense).
 ;;
 ;; The prefix command can be added anywhere, including outside the as calls.
 (prefix 'clojure
         (as 's (req walk))
         (as 's (req set))))


(newnamespacet
 bob
 ;; we can prefix at any point -- in this case add clojure to both require and
 ;; exclude statements
 (prefix
  'clojure
  ;; a simple req
  (req xml)
  ;; we provide a set of use functions
  ;; require all of set but use only union
  (use-only (req set) 'union)
  ;; use all of walk
  (use-all (req walk))
  ;; use all of data, except equality partition
  (use-exclude (req data) 'equality-partition)
  ;; all children should be called Sean
  (use-rename (req zip) '{children Sean})))


(newnamespacet
 bob
 ;; we can rename and exclude
 (use-rename
  (use-exclude (req clojure.data) 'a)
  '{b c}))

(newnamespacet
 bob
 ;; either way around
 (use-exclude
  (use-rename (req data) '{b c})
  'a))

;; The interface is fully programmatic -- we can add new functionality as we choose.
;; In this case, we define in namespace.imports some standard "profiles" which
;; people might use. First we require this namespace, then we use one of the
;; functions in it.

;; when running this with newnamespacet, we have to run the require
;; externally, because the newnamespacet doesn't actually do the requiring, so
;; the function won't be available. When run with newnamespace this isn't
;; necessary.
(require 'namespace.imports)

(newnamespacet
 bob
 (req namespace.imports)
 (namespace.imports/tawny-read))


;; importing is possible also
(newnamespacet
 bob
 (imp java.util.Set))

;; or several at once
(newnamespacet
 bob
 (imp java.util.Set
      java.util.List))

(newnamespacet
 bob
 ;; prefixes work as expected
 (prefix
  'java.util
  (imp Set List Collection)))
