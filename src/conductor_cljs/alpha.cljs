(ns conductor-cljs.alpha
  (:require
   [cljs.spec.alpha :as s]
   [cljs.spec.test.alpha :as stest]
   [cljs.test :as test])
  (:require-macros [conductor-cljs.alpha]))

(def ^:private enabled (atom false))

(def ^:private original-formatter (atom nil))
(def ^:private original-assert (atom nil))
(def ^:private original-explain (atom nil))

(defn test-formatter [v]
  (if (instance? js/Error v)
    (.-message v)
    v))

