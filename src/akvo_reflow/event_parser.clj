(ns akvo-reflow.event-parser
  (:require
    [cheshire.core :as json]))

(defn parse [json]
  (json/parse-string json))

(defn properties [data]
  (->
    data
    (get "entity")
    (get "properties")))

;;;;;; Events ;;;;;;

(defn survey
  "GAE SurveyGroup"
  [properties]
  (let [project-types {"PROJECT" "SURVEY"
                       "PROJECT_FOLDER" "FOLDER"}]
  {"name" (get properties "name" "<name missing>")
   "parentId" (get properties "parentId")
   "surveyGroupType" (get project-types (get properties "projectType") "<project type missing>")
   "description" (get properties "description")
   "public" (= (get properties "privacyLevel") "PUBLIC")
   }))
