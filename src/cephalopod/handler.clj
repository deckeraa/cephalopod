(ns cephalopod.handler
  (:use [compojure.core]
        [clojure.test :as test])
  (:require [compojure.handler :as handler]
            [compojure.route :as route]
            [hiccup.core :as html]
            [me.raynes.conch :as conch]))





;; (read-string "(+ 2 3)")

;; returns a list with a map of post attributes as the first element
;; and a string with the post converted to html
;; (the newlines in the post are replaced by html breaks)
;; (defmacro defpost [map & post]
;;   `(let [processed-post# (str '~post)]
;;      (list ~map processed-post#)))
(defmacro defpost [map & post]
  `(let [str-post# (str '~post)
        stringified-post# (subs str-post# 1 (count str-post#))
         processed-post# (exec-literate-prog stringified-post#)]
     (list
      ~map
      stringified-post#)))
;; (load-file "./resources/public/foo.clj")
;; (eval (read-string (slurp "./resources/public/foo.clj")))
;; (load-string (slurp "./resources/public/foo.clj"))
;; (defmacro defpost [map & post]
;;   (list map (str 'post)))
;; (eval (read-string (slurp "./resources/public/foo.clj")))
;; (eval (macroexpand '(defpost {:title "bar"} asdf qwer))
;;       )

;; (exec-literate-prog (slurp "./resources/public/foo.clj"))
;; (macroexpand '(defpost {:title "bar"}  asdf foo \n qwer
;;                ))
;; (load-file "./resources/public/foo.clj")
;; (read-string "#=(clojure.java.shell/sh \"echo\" \"hi\")")

;; (exec-literate-prog (slurp "./resources/public/foo.clj"))

(defn read-post-from-string [post]
  (let [lines (clojure.string/split-lines post)
        metadata (read-string (first lines))
        post (rest lines)
        htmlified-post (clojure.string/join "<br/>\n" post)]
    (assoc metadata :text htmlified-post)))

(deftest test-read-post-from-string
  (is (= (read-post-from-string "{:title foo}\nHello World\nLine Two")
         '{:title foo :text "Hello World<br/>\nLine Two"})))



;; read-literate-string
;; Takes a string a reads it.
;; If the string does not start with ";>"
;; the string is returned.
;; Otherwise the subsequent form after ";>" is
;; read and evaluted, and the return valued is
;; stringified and returned.
;; (defn read-literate-string [string]
;;   (if (= (subs string 0 2) ";>")
;;     (pr-str (eval (read-string (subs string 2))))
;;     (if (= (subs string 0 1) ";")
;;       nil
;;       string)))

(defn read-literate-string [string]
  (cond
   (< (count string) 1) nil ;empty string
   (< (count string) 2) (if (= (subs string 0 1) ";") nil string)
   (= (subs string 0 2) ";>") (pr-str (eval (read-string (subs string 2))))
   :else (if (= (subs string 0 1) ";") nil string)
   ))

(deftest test-read-literate-string
  (is (= (read-literate-string "foo") "foo"))
  (is (= (read-literate-string "(foo)") "(foo)"))
  (is (= (read-literate-string ";>(+ 2 3)") "5"))
  (is (= (read-literate-string ";foo") nil))
  (is (= (read-literate-string ";") nil))
  (is (= (read-literate-string "a") "a"))
  (is (= (read-literate-string ";a") nil))
  (is (= (read-literate-string "") nil)))

;; literate
;; Takes a literal Clojure program as one big string and
;; executes the lines marked for execution by starting with ;>
;;
(defn exec-literate-prog [prog]
  (let [lines (clojure.string/split-lines prog)]
    (clojure.string/join "\n" (remove nil? (map read-literate-string lines)))))

(deftest test-exec-literate-prog
  (is (= (exec-literate-prog "foo\n;>(+ 4 3)\nbar") "foo\n7\nbar"))
  (is (= (exec-literate-prog "foo\n;>(+ 4 3)\n;(+ 4 5)\n;another comment\nbar") "foo\n7\nbar")))

(defn post-head [post]
  [:head
   [:meta {:charset "utf-8"}]
   [:title (:title post)]
   [:link {:rel "stylesheet" :type "text/css" :href "/styles/css/bootstrap.css"}]
   [:link {:rel "stylesheet" :type "text/css" :href "/styles/css/blog.css"}]
   [:meta {:name "viewport" :content "width=device-width, initial-scale=1.0"}]
   [:meta {:name "generator" :content "cephalopod"}]
   ])

(defn post-masthead [post]
  [:div {:class "blog-masthead"}
   [:div {:class "container"}
    [:nav {:class "blog-nav"}
     [:a {:class "blog-nav-item active" :href "#"} "Home"]
     [:a {:class "blog-nav-item " :href "#"} "Blog"]]]])

(defn post-title-area [post]
  [:div {:class "blog-header"}
   [:h1 {:class "blog-title"} (:title post)]
   [:p {:class "lead blog-description"} (:lead-description post)]])

(defn post-body [post]
  [:div {:class "blog-post"}
   [:h2 {:class "blog-post-title"} (:title post)]
   [:p {:class "blog-post-meta"} (:date post) " by " (:author post)]
   (:text post)])

(defn add-newlines-to-html [html]
  (clojure.string/replace html #"</?(html|head|body|title|link|meta|div)[^>]*>" #(str (first %1) "\n")))

(deftest test-add-newlines-to-html
  (is (= (add-newlines-to-html "<html><body><p>Foo</p></body></html>") "<html>\n<body>\n<p>Foo</p></body>\n</html>\n")))

(defn htmlify-post [post-title]
  (let [post (read-post-from-string
              (slurp (str "./resources/public/" post-title)))]
    (add-newlines-to-html
     (html/html [:html {:lang "en"}
                 (post-head post)
                 [:body
                  (post-masthead post)
                  [:div {:class "container"}
                   (post-title-area post)
                   [:div {:class "row"}]
                   (post-body post)]]
                 ]))))

(defn clj-tags-namespace-testing-function []
  "clj-tags-namespace-testing-function was evaluated successfully")

(def ^:dynamic pagemap (transient {}))
;; (read-string (slurp "./resources/public/bar.clj"))
;; eval-clj-tags
;; Runs through a String of html and replaces all <clj src="(some_expression)"/>
;; tags with the evaluated expression for that tag.
;; Yes, I know that technically html cannot be parsed using regular expressions.
;; However, I don't need this to work on every html file, just on the ones I make,
;; which I can guarantee to be non-pathological.
(defn eval-clj-tags [html]
  (binding [pagemap (transient {})]
    (clojure.string/replace
     html
     #"<\s*clj\s*src=\"(.*)\"\s*/\s*>" ; parse the clj tag
     (fn [[_ expr]]
       (clojure.string/replace
        (pr-str (load-string expr))
        #"\"(.*)\"" ; remove outermost quotes if present
        #(second %1)))
     )))

(deftest test-eval-clj-tags
  (is (= (eval-clj-tags "<html><clj src=\"(+ 3 2)\"/></html>") "<html>5</html>"))
;  (is (= (eval-clj-tags "<head>asdf< clj     src=\"(pr-str (pr-str (+ 3 2)))\"    /  >") "<head>asdf\"5\""))
;  (is (= (eval-clj-tags "<clj src=\"(foo)\"/>") "7"))
  (is (= (eval-clj-tags "<clj src=\"(load-string \"(+ 3 2)\")\"/>") "5"))
;  (is (= (eval-clj-tags "<clj src=\"(pr-str \"I should have quotes around me\")\"/>") "\"I shoudl have quotes around me\""))
  )

(defroutes app-routes
  ;; (GET "/" [] (html/html [:html
  ;;                         [:h1 "Hello World"]
  ;;                         [:p "This is a test paragraph"]]))
  (GET "/ioctopus/:foo" [foo] (eval-clj-tags (slurp "./resources/public/templates/blog.html")))
;  (route/files "/" {:root "resources"})
  (route/files "/styles/" {:root "resources/public/styles/"})
  (route/not-found "Not Found"))

(def app
  (handler/site app-routes))
