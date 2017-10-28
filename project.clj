(defproject conductor "0.1.1-SNAPSHOT"
  :description "Automatic instrumentation for clojure.spec"
  :url "https://github.com/rads/conductor"
  :license {:name "MIT License"
            :url "https://opensource.org/licenses/MIT"}
  :dependencies [[expound "0.3.1"]
                 [orchestra "2017.08.13"]]
  :profiles {:dev {:dependencies [[org.clojure/clojure "1.9.0-beta2"]
                                  [org.clojure/clojurescript "1.9.946"]]}})
