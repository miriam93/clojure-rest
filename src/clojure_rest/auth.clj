(ns clojure-rest.auth
  (:require [ring.util.response :refer :all]
            [clojure-rest.data.users :as users]
            [clojure-rest.util.user-sanitize :as us]
            [pandect.core :refer [sha256-hmac]]
            [environ.core :refer [env]]
            [clojure-rest.data.db :as db]
            [clojure.java.jdbc :as sql]
            [clojure-rest.util.http :as h]
            [clojure-rest.util.utils :refer [time-now
                                             format-time]]))



;; Either<String|nil>
;; Gets the secret key from the environment variable secret-key, returns nil if not found
;; (will throw exception at runtime)
(def ^:private SECRET-KEY
  (when-let [key (env :secret-key)] key))


;; () -> String
;; Generates a random token with username$timestamp$hmac(sha256, username$timestamp)
(defn- generate-session [username date]
  (str username "$" date "$" (sha256-hmac (str username "$" date) SECRET-KEY)))


;; UUID -> String
;; Creates a token and inserts it into the session table, then returns that token
(defn- make-token! [user-id]
  (let [now (time-now)
        token (generate-session user-id now)]
    (sql/with-connection (db/db-connection)
                         (sql/insert-values :sessions [] [token
                                                          user-id
                                                          (format-time now)]))
    token))


;; String, String -> [{}?, Error?]
;; Checks if the given user / pass combination is correct, returns an error tuple
(defn- validate-user-pass [username password]
  (if (users/pass-matches? username password) 200 401))


;; [{}?, Error?] -> Response [:body nil :status Either<200|401|Error>]
(defn- bind-validate [[val err]]
  (if (nil? err)
    [nil (validate-user-pass (val :username) (val :password))]
    [nil err]))


;; {} -> Response [:body nil :status Natural]
;; Destructures the given content into username and password for validation ingestion
(defn auth-handler [content]
  (->> content
       clojure.walk/keywordize-keys
       us/sanitize-auth
       bind-validate
       h/wrap-response))
