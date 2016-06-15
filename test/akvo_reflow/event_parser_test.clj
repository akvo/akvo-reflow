(ns akvo-reflow.event-parser-test
  (:require
    [akvo-reflow.event-parser :refer
     [drop-deprecated-props event-properties kind parse transform-event]]
    [clojure.test :refer :all]))

(def survey-group-sample-1
  (slurp "resources/akvo_reflow/gae_json/survey_group_sample_1.json"))

(def survey-group-sample-2
  (slurp "resources/akvo_reflow/gae_json/survey_group_sample_2.json"))

(def survey-sample-1
  (slurp "resources/akvo_reflow/gae_json/survey_sample_1.json"))

(def question-group-1
  (slurp "resources/akvo_reflow/gae_json/question_group_1.json"))

(deftest events
  []
  (testing "SurveyGroup"
    (let [data (parse survey-group-sample-1)
          event-properties (event-properties data)
          kind (kind data)]
      (is (=
            (transform-event kind (drop-deprecated-props kind event-properties))
            {"parentId" 0,
             "newLocaleSurveyId" nil,
             "defaultLanguageCode" "en",
             "surveyGroupType" "FOLDER",
             "lastUpdateDateTime" 1462883547507,
             "createdDateTime" 1462883536260,
             "name" "One two three",
             "published" false,
             "ancestorIds" [0],
             "public" false,
             "monitoringGroup" false,
             "code" "One two three",
             "description" ""})))
    (let [data (parse survey-group-sample-2)
          event-properties (event-properties data)
          kind (kind data)]
      (is (=
            (transform-event kind (drop-deprecated-props kind event-properties))
            {"parentId" 0,
             "newLocaleSurveyId" nil,
             "defaultLanguageCode" "en",
             "surveyGroupType" "<project type missing>",
             "lastUpdateDateTime" 1462883547507,
             "createdDateTime" 1462883536260,
             "name" "<name missing>",
             "published" false,
             "ancestorIds" [0],
             "public" true,
             "monitoringGroup" false,
             "code" "One two three",
             "description" ""}))))
  (testing "Survey"
    (let [data (parse survey-sample-1)
          event-properties (event-properties data)
          kind (kind data)]
      (is (=
            (transform-event kind (drop-deprecated-props kind event-properties))
            {"ancestorIds"         [0
                                    42063003
                                    42083002]
             "code"                "Some new form"
             "createdDateTime"     1465912272798
             "defaultLanguageCode" "en"
             "description"         "Some description"
             "lastUpdateDateTime"  1465912289086
             "name"                "Some new form"
             "pointType"           nil
             "requireApproval"     false
             "status"              "NOT_PUBLISHED"
             "surveyId"            42083002
             "version"             1.0}))))
  (testing "QuestionGroup"
    (let [data (parse question-group-1)
          event-properties (event-properties data)
          kind (kind data)]
      (is (=
            (transform-event kind (drop-deprecated-props kind event-properties))
            {"ancestorIds"        nil
             "code"               "New group - please change name"
             "createdDateTime"    1465912291758
             "desc"               nil
             "lastUpdateDateTime" 1465912291758
             "name"               "New group - please change name"
             "order"              1
             "path"               "My survey 1/Some new form"
             "repeatable"         false
             "status"             "READY"
             "surveyId"           40993004})))))
