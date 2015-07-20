(defproject parser "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :min-lein-version "2.0.0"
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/clojurescript "0.0-2371"]
                 [com.cognitect/transit-cljs "0.8.220"]]
  :plugins [[lein-cljsbuild "1.0.3"]]
  :cljsbuild {
              :builds [{
                        :source-paths ["src-cljs"]
                        :compiler {
                                   :output-to "dist/bundle.js"
                                   :optimizaitons :whitespace
                                   :pretty-print true}}]})
