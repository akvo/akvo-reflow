(ns akvo-reflow.endpoint.status
  (:require [compojure.core :refer :all]
            [hugsql.core :as hugsql]
            [hiccup.core :refer [html]]
            [hiccup.form :refer [form-to submit-button]]))

(hugsql/def-db-fns "akvo_reflow/endpoint/status.sql")

(def status-keys [:instance_id :import_done :export_done :kind :cursor])

(defn import-form [instance-id]
  (form-to [:post (str "/import-instance/" instance-id)]
           (submit-button (str "Import " instance-id))))

(defn export-form [instance-id]
  (form-to [:post (str "/export-instance/" instance-id)]
           (submit-button (str "Export " instance-id))))

(defn status-table [all-instances]
  (html
    [:table
     [:tr
      [:th "Instance name"]
      [:th "Status"]
      [:th "Import"]
      [:th "Export"]
      [:th "Error status"]
      [:th "Error message"]
      [:th "Kind"]
      [:th "Cursor"]]
     (map
       (fn [instance]
         [:tr
          ; Instance name
          [:td (:instance_id instance)]
          ; Status
          [:td (:process_status instance)]
          ; Import link
          [:td (if
                 (:import_done instance)
                 "Complete"
                 (import-form (:instance_id instance)))]
          ; Export link
          [:td (if
                 (not (:import_done instance))
                 "Import first"
                 (if
                   (:export_done instance)
                   "Complete"
                   (export-form (:instance_id instance))))]
          ; Error status
          [:td (:error_status instance)]
          ; Error message
          [:td (:error_message instance)]
          ; Import kind of running import
          [:td (:kind instance)]
          ; Cursor position of running import
          [:td {:title (:cursor instance)} (take 8 (:cursor instance)) "..." (take-last 8 (:cursor instance))]])
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
