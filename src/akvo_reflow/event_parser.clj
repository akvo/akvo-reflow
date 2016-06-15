(ns akvo-reflow.event-parser
  (:require
    [cheshire.core :as json]
    [clojure.set :refer [difference]]
    [clojure.string :refer [blank?]]))


(def project-types
  {"PROJECT" "SURVEY"
   "PROJECT_FOLDER" "FOLDER"})

(def deprecated-props
  {"SurveyGroup" #{"path"}
   "Survey" #{"translationMap" "sector" "quesitonGroupMap" "instanceCount" "path"}
   "QuestionGroup" #{"questionMap" "translationMap"}
   "Question" #{"translationMap" "questionOptionMap" "questionHelpMediaMap" "path" "scoringRules"}
   "DeviceFiles" #{}
   "SurveyedLocale" #{"ambiguos" "surveyalValues"}
   "SurveyInstance" #{"questionAnswerStore" "approximateLocationFlag"}
   "QuestionAnswerStore" #{"strength" "scoredValue" }})


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
      "surveyGroupType"
      (get-string-with-default project-types (get properties "projectType") "<project type missing>"))
    (dissoc "projectType")
    (assoc "public" (= (get properties "privacyLevel") "PUBLIC"))
    (dissoc "privacyLevel")))

(defn transform-event [kind properties]
  (case kind
    "SurveyGroup" (survey properties)
    "default" properties))