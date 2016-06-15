(ns akvo-reflow.event-parser-test
  (:require
    [akvo-reflow.event-parser :refer
     [drop-deprecated-props event-properties kind parse transform-event]]
    [clojure.test :refer :all]))

(def survey-group-sample
  (slurp "resources/akvo_reflow/gae_json/survey_group_sample.json"))

(def survey-sample-1
  (slurp "resources/akvo_reflow/gae_json/survey_sample_1.json"))

(deftest events
  []
  (testing "SurveyGroup"
    (let [data (parse survey-group-sample)
          event-properties (event-properties data)
          kind (kind data)]
      (is (=
            (transform-event kind (drop-deprecated-props kind event-properties))
            {
             "parentId" 0,
             "newLocaleSurveyId" nil,
             "defaultLanguageCode" "en",
             "surveyGroupType" "FOLDER",
             "lastUpdateDateTime" 1462883547507,
             "createdDateTime" 1462883536260,
             "name" "One two three",
             "published" false,
             "createUserId" nil,
             "ancestorIds" [0],
             "public" false,
             "monitoringGroup" false,
             "code" "One two three",
             "description" "",
             "lastUpdateUserId" nil})))))
