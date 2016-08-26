(ns akvo-reflow.endpoint.status
  (:require [compojure.core :refer :all]
            [hugsql.core :as hugsql]
            [hiccup.core :refer [html]]
            [hiccup.form :refer [form-to submit-button]]))

(hugsql/def-db-fns "akvo_reflow/endpoint/status.sql")

(def status-keys [:instance_id :import_done :export_done :kind :cursor])

(defn import-form [instance-id]
  (form-to [:post (str "/import-instance/" instance-id)]
            (submit-button (str "No, import " instance-id))))

(defn status-table [all-instances]
  (html
    [:table
     [:tr
      [:th "Instance name"]
      [:th "Import done?"]
      [:th "Export done?"]
      [:th "Kind"]
      [:th "Cursor"]]
     (map
       (fn [instance]
         [:tr
          [:td (:instance_id instance)]
          [:td (if
                 (:import_done instance)
                 "Yes!"
                 (import-form (:instance_id instance)))]
          [:td (:export_done instance)]
          [:td (:kind instance)]
          [:td (:cursor instance)]])
       (sort-by :import_done #(and (= %1 true) (= %2 false)) (sort-by :instance_id all-instances)))]))

(defn status-html [all-instances]
  (html
    [:html
     [:head
      [:title "Reflow status"]
      [:style
       "body {
          font-family: arial,sans-serif;
        }
        form {
          margin: 0!important;
        }
        td, th {
          text-align: left;
          padding-left: 12px;
          vertical-align: baseline;
        }
        input[type=submit] {
          background: none!important;
          border: none;
          padding: 0!important;
          color: #069;
          cursor: pointer;
          font-size: 100%;
          }
          input[type=submit]:hover {
            text-decoration: underline;
          }"]]
     [:body
      [:h1 "Reflow status"]
      [:p (form-to [:post (str "/reload")]
               (submit-button (str "Reload Flow instances")))]
      (status-table all-instances)
      ]]))

(defn endpoint [{:keys [config flow-config db] :as system}]
  (context "/status" []
    (GET "/" []
      (let [ds (select-keys (-> db :spec) [:datasource])]
        (let [all-instances (all-instances-status ds)]
          {:status 200
           :headers {"Content-Type" "text/html"}
           :body (status-html all-instances)})))))
