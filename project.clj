(defproject conductor "0.1.2"
  :description "Automatic instrumentation for clojure.spec"
  :url "https://github.com/rads/conductor"
  :license {:name "MIT License"
            :url "https://opensource.org/licenses/MIT"}
  :dependencies [[expound "0.3.1"]
                 [orchestra "2017.08.13"]]
  :profiles {:dev {:dependencies [[org.clojure/clojure "1.9.0-beta4"]
                                  [org.clojure/clojurescript "1.9.946"]]}})
