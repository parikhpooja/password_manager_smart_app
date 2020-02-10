(ns rest-demo.core
  (:require [org.httpkit.server :as server]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer :all]
            [clojure.pprint :as pp]
            [clojure.string :as str]
            [clojure.data.json :as json])
  (:gen-class))

; my people-collection mutable collection vector
  (def people-collection (atom []))

;Collection Helper functions to add a new person
  (defn addperson [firstname surname]
  (swap! people-collection conj
         {:firstname (str/capitalize firstname)
          :surname (str/capitalize surname)}))

; Example JSON objects
  (addperson "Pooja" "Parikh")
  (addperson "Andreas" "Freund")
  (addperson "Emma" "Grasmeder")

; Return List of People
  (defn people-handler [req]
    {:status  200
    :headers {"Content-Type" "text/json"}
    :body    (str (json/write-str @people-collection))})

  ; Simple Body Page
  (defn simple-body-page [req]
        {:status  200
         :headers {"Content-Type" "text/html"}
         :body    "Hello World"})

  ; request-example
  (defn request-example [req]
        {:status  200
         :headers {"Content-Type" "text/html"}
         :body    (->>
                    (pp/pprint req)
                    (str "Request Object: " req))})

  (defn hello-name [req]
    {:status 200
     :headers {"Content-Type" "text/html"}
     :body (->
             (pp/pprint req)
             (str "Hello" (:name (:params req))))})


  (defroutes app-routes
             (GET "/hello/world" [] simple-body-page)
             (POST "/request" [] request-example)
             (GET "/hello" [] hello-name)
             (GET "/people" [] people-handler)
             (route/not-found "Error, page not found!"))

  (defn -main
        "This is our main entry point"
        [& args]
        (let [port (Integer/parseInt (or (System/getenv "PORT") "3001"))]
          ; Run the server with Ring.defaults middleware
          ;(server/run-server (wrap-defaults #'app-routes site-defaults) {:port port})
          ; Run the server without ring defaults
          (server/run-server #'app-routes {:port port})
          (println (str "Running webserver at http:/127.0.0.1:" port "/hello"))))
