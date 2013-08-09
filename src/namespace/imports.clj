(ns namespace.imports
  (:use namespace.namespace))

(defn tawny-read []
  (use-only
   (req tawny.read)
   'defread))

(defn tawny-dev []
  (prefix
   (use-all (req owl))
   (req (repl lookup))
   (as 'r (req reasoner))))
