(ns cephalopod.handler
  (:use [compojure.core])
  (:require [compojure.handler :as handler]
            [compojure.route :as route]
            [hiccup.core :as html]
            [me.raynes.conch :as conch]))

(conch/programs pwd)

(defroutes app-routes
  (GET "/" [] (html/html [:html
                          [:h1 "Hello World"]
                          [:p "This is a test paragraph"]]))
  (GET "/ioctopus/:foo" [foo] (htmlify-post foo))
  (route/resources "/")
  (route/not-found "Not Found"))

(def app
  (handler/site app-routes))

(defn htmlify-post [post-title]
  (html/html [:html
              (pwd)
              "<br/>"
              (slurp (str "./resources/public/" post-title))
              [:p post-title]])
  )


(read-string "(+ 2 3)")

;; returns a list with a map of post attributes as the first element
;; and a string with the post converted to html
;; (the newlines in the post are replaced by html breaks)
(defmacro defpost [map & post]
  `(let [processed-post# (str '~post)]
     (list ~map processed-post#)))
(defmacro defpost [map & post]
  `(let [processed-post# (str '~post)]
     (list
      ~map
      processed-post#)))
(defmacro defpost [map & post]
  (list map (str 'post)))
(eval (read-string (slurp "./resources/public/foo.clj")))
(eval (macroexpand '(defpost {:title "bar"} asdf qwer))
      )
(@ ('foo))

(defn literate [prog]
  (let [lines (clojure.string/split-lines prog)]
    (clojure.string/join "\n" (map read-literate-string lines))))

(literate "foo\n;>(+ 4 3)\nbar")
(clojure.string/join "" '( "1" "2"))
(defn read-literate-string [string]
  (if (= (subs string 0 2) ";>")
    (pr-str (eval (read-string (subs string 2))))
    string))

(def string ";>(+ 2 3)")
(read-literate-string ";>(+ 2 3)")
(= (subs ";>(+ 2 3" 0 2) ";>")
(list {:title "foo"})
(str (map quote '(asdf brfk)))
(str '(asdf brfk))
(defpost {:title "bar"} asdf qwer)
(defmacro dbg [code]
  `(do (print "Executing " '~code)
       ~code))

(macroexpand '(dbg (* 3 2)))
(dbg (* 3 2))

(defn forloop-fn-6 [i end & code]
  `(let [finish# ~end]
     (loop [~i 0]
       (when (< ~i finish#)
         ~@code
         (recur (inc ~i))))))

(eval (forloop-fn-6 'i 10 '(print i) '(print (* i i))))
