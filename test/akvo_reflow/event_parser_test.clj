(ns akvo-reflow.event-parser-test
  (:require
    [akvo-reflow.event-parser :refer
     [drop-deprecated-props event-properties kind parse transform-event]]
    [clojure.test :refer :all]))

(def event-samples
  [{:event-type "SurveyGroup"
    :samples [{:file-name "survey_group_sample_1.json"
               :expected-result {"parentId" 0,
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
                                 "description" ""}}
              {:file-name "survey_group_sample_2.json"
               :expected-result {"parentId" 0,
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
                                 "description" ""}}]}
   {:event-type "Survey"
    :samples [{:file-name "survey_sample_1.json"
               :expected-result {"ancestorIds" [0
                                                42063003
                                                42083002]
                                 "code" "Some new form"
                                 "createdDateTime" 1465912272798
                                 "defaultLanguageCode" "en"
                                 "description" "Some description"
                                 "lastUpdateDateTime" 1465912289086
                                 "name" "Some new form"
                                 "pointType" nil
                                 "requireApproval" false
                                 "status" "NOT_PUBLISHED"
                                 "surveyId" 42083002
                                 "version" 1.0}}]}
   {:event-type "QuestionGroup"
    :samples [{:file-name "question_group_1.json"
               :expected-result {"ancestorIds" nil
                                 "code" "New group - please change name"
                                 "createdDateTime" 1465912291758
                                 "desc" nil
                                 "lastUpdateDateTime" 1465912291758
                                 "name" "New group - please change name"
                                 "order" 1
                                 "path" "My survey 1/Some new form"
                                 "repeatable" false
                                 "status" "READY"
                                 "formId" 40993004}}]}
   {:event-type "Question"
    :samples [{:file-name "question_1.json"
               :expected-result {"allowDecimal" false
                                 "allowExternalSources" false
                                 "allowLine" true
                                 "allowMultipleFlag" false
                                 "allowOtherFlag" false
                                 "allowPoints" true
                                 "allowPolygon" true
                                 "allowSign" false
                                 "ancestorIds" nil
                                 "cascadeResourceId" nil
                                 "collapseable" false
                                 "createdDateTime" 1465912334587
                                 "dependentFlag" false
                                 "dependentQuestionAnswer" nil
                                 "dependentQuestionId" nil
                                 "geoLocked" false
                                 "immutable" false
                                 "isName" false
                                 "lastUpdateDateTime" 1465912334587
                                 "localeLocationFlag" false
                                 "localeNameFlag" false
                                 "mandatoryFlag" true
                                 "maxVal" nil
                                 "metricId" nil
                                 "minVal" nil
                                 "order" 2
                                 "questionGroupId" 44793003
                                 "identifier" nil
                                 "referenceId" nil
                                 "requireDoubleEntry" false
                                 "sourceQuestionId" nil
                                 "formId" 40993004
                                 "displayText" "New question - please change name"
                                 "tip" nil
                                 "questionType" "FREE_TEXT"}}]}])

(defn transform-sample
  [sample]
  (let [file (str "resources/akvo_reflow/gae_json/" sample)
        data (parse (slurp file))
        event-properties (event-properties data)
        kind (kind data)]
          (transform-event kind (drop-deprecated-props kind event-properties))))

(deftest events
  []
  (doseq [event event-samples]
    (testing (:event-type event)
      (doseq [sample (:samples event)]
        (is (=
          (transform-sample (:file-name sample))
          (:expected-result sample)
          ))))))
