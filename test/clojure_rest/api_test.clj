(ns clojure-rest.api-test
  (:require [clojure.test :refer :all]
            [ring.mock.request :as mock]
            [clojure-rest.handler :refer :all]))

(deftest test-api
    ;; A GET to an available api method should return a 200 OK response
  (testing "content found"
    (let [response (app (mock/request :get "/api/events"))]
      (is (= (:status response) 200))
      (is (= (get-in response [:headers "Content-Type"]) "application/json; charset=utf-8"))))

  ;; A request to a non-existing api route should return a 404 response
  (testing "not-found asset"
    (let [response (app (mock/request :get "/api/foo"))]
      (is (= (:status response) 404)))))