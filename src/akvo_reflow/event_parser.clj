(ns akvo-reflow.event-parser
  (:require
    [cheshire.core :as json]
    [clojure.set :refer [difference rename-keys]]
    [clojure.string :refer [blank?]]))


(def project-types
  {"PROJECT" "SURVEY"
   "PROJECT_FOLDER" "FOLDER"})

(def deprecated-props
  {"SurveyGroup" #{"createUserId" "lastUpdateUserId" "path"}
   "Survey" #{"createUserId" "lastUpdateUserId" "sector" "path"}
   "QuestionGroup" #{"createUserId" "lastUpdateUserId"}
   "Question" #{"createUserId" "lastUpdateUserId" "path"}
   "DeviceFiles" #{"createUserId" "lastUpdateUserId"}
   "SurveyedLocale" #{"createUserId" "lastUpdateUserId"}
   "SurveyInstance" #{"createUserId" "lastUpdateUserId"}
   "QuestionAnswerStore" #{"createUserId" "lastUpdateUserId" "strength" "scoredValue" }})


(defn parse [json]
  (json/parse-string json))

(defn event-properties [data]
  (get-in data ["entity" "properties"]))

(defn kind [data]
  (get-in data ["entity" "kind"]))

(defn drop-deprecated-props
  "Remove deprecated properties from the event"
  [kind event-properties]
  (select-keys event-properties (difference
                                  (set (keys event-properties))
                                  (get deprecated-props kind ))))

(defn get-string-with-default
  [m k default]
  (let [val (get m k)]
    (if (blank? val)
      default
      val)))

;;;;;; Events special transformations;;;;;;

(defn survey
  "Transformations for GAE SurveyGroup to Unilog Survey"
  [properties]
  (->
    properties
    (assoc  "name" (get-string-with-default properties "name" "<name missing>"))
    (assoc
      "projectType"
      (get-string-with-default project-types (get properties "projectType") "<project type missing>"))
    (assoc "privacyLevel" (= (get properties "privacyLevel") "PUBLIC"))
    (rename-keys {"projectType" "surveyGroupType" "privacyLevel" "public"})))

(defn form
  "Transformations for GAE Survey to Unilog Form"
  [properties]
    (rename-keys properties {"desc" "description" "surveyGroupId" "surveyId"}))

(defn question-group
  "Transformations for GAE QuestionGroup to Unilog QuestionGroup"
  [properties]
  (->
    properties
    (assoc  "name" (get-string-with-default properties "name" "<name missing>"))
    (rename-keys {"surveyId" "formId"})))

(defn question
  "Transformations for GAE Question to Unilog Question"
  [properties]
  (rename-keys properties {"text" "displayText"
                           "questionId" "identifier"
                           "surveyId" "formId"
                           "type" "questionType"}))

(defn transform-event [kind properties]
  (case kind
    "SurveyGroup" (survey properties)
    "Survey" (form properties)
    "QuestionGroup" (question-group properties)
    "Question" (question properties)
    properties))