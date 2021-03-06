(ns clojure-rest.routes.user-routes
  (:require [compojure.core :refer :all]
            [clojure-rest.util.http :as http]
            [clojure-rest.data.users :as users]))


(defn- user-add-routes [username]
  (routes
    (GET "/" [] (users/get-user-contacts username))
    (POST "/" {body :body} (users/add-contact username body))
    (DELETE "/" [] (http/not-implemented))
    (OPTIONS "/" [] (http/options [:options :post :get]))
    (ANY "/" [] (http/method-not-allowed [:options :post :get]))))


(defn- user-id-routes [username]
  (routes
    (GET "/" [] (users/get-user username))
    (PUT "/" {body :body} (users/update-user username body))
    (DELETE "/" [] (users/delete-user username))
    (OPTIONS "/" [] (http/options [:options :get :put :delete]))
    (ANY "/" [] (http/method-not-allowed [:options :get :put :delete]))
    (context "/contacts" [] (user-add-routes username))))


(defn- user-search-routes [query]
  (routes
    (GET "/" [] (users/search-users query))
    (OPTIONS "/" [] (http/options [:options :get]))
    (ANY "/" [] (http/method-not-allowed [:options :get]))))


(defroutes user-routes
  (GET "/" [] (users/get-all-users))
  (POST "/" {body :body} (users/create-new-user body))
  (OPTIONS "/" [] (http/options [:options :get :post]))
  (ANY "/" [] (http/method-not-allowed [:options :get :post]))
  (context "/:username" [username] (user-id-routes username))
  (context "/search/:query" [query] (user-search-routes query)))
