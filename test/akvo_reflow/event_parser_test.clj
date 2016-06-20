(ns akvo-reflow.event-parser-test
  (:require
    [akvo-reflow.event-parser :refer
     [drop-deprecated-props event-properties kind parse transform-event]]
    [clojure.java.io :as io]
    [clojure.test :refer :all]))

(def event-samples
  [{:event-type "SurveyGroup"
    :samples [{:file-name "survey_group_1.json"
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
              {:file-name "survey_group_2.json"
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
    :samples [{:file-name "survey_1.json"
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
                                 "questionType" "FREE_TEXT"}}]}
   {:event-type "DeviceFiles"
    :samples [{:file-name "device_files_1.json"
               :expected-result {"uri" "https://akvoflowsandbox.s3.amazonaws.com/devicezip/d3f64886-9004-4bbd-afea-98776ff33bff.zip"
                                 "ancestorIds" nil
                                 "androidId" "4586599e2859c369"
                                 "checksum" nil
                                 "createdDateTime" 1465912477091
                                 "imei" "358848047655824"
                                 "lastUpdateDateTime" 1465912477091
                                 "phoneNumber" ""
                                 "processDate" "2016_06_14_13:54:35"
                                 "processedStatus" "PROCESSED_NO_ERRORS"
                                 "processingMessageText" nil
                                 "status" nil
                                 "surveyInstanceId" 40283002
                                 "uploadDateTime" 1465912475756}}]}
   {:event-type "SurveyedLocale"
    :samples [{:file-name "surveyed_locale_1.json"
               :expected-result {"ambiguous" false
                                 "ancestorIds" nil
                                 "countryCode" nil
                                 "createdDateTime" 1465912476087
                                 "creationSurveyId" nil
                                 "currentStatus" nil
                                 "name" nil
                                 "geocells" nil
                                 "identifier" "42v1-jyvd-he9s"
                                 "lastSurveyalInstanceId" nil
                                 "lastSurveyedDate" nil
                                 "lastUpdateDateTime" 1465912476087
                                 "lat" nil
                                 "localeType" "PUBLIC"
                                 "lon" nil
                                 "organization" "Akvo"
                                 "sublevel1" nil
                                 "sublevel2" nil
                                 "sublevel3" nil
                                 "sublevel4" nil
                                 "sublevel5" nil
                                 "sublevel6" nil
                                 "surveyId" 42083002
                                 "surveyInstanceContrib" nil
                                 "systemIdentifier" nil}}]}
   {:event-type "SurveyInstance"
    :samples [{:file-name "survey_instance_1.json"
               :expected-result {"ancestorIds" nil
                                 "approvedFlag" nil
                                 "approximateLocationFlag" nil
                                 "collectionDate" 1465912468220
                                 "community" nil
                                 "countryCode" nil
                                 "createdDateTime" 1465912476278
                                 "deviceFileId" nil
                                 "deviceIdentifier" "ipdroid"
                                 "lastUpdateDateTime" 1465912478274
                                 "localeGeoLocation" nil
                                 "sublevel1" nil
                                 "sublevel2" nil
                                 "sublevel3" nil
                                 "sublevel4" nil
                                 "sublevel5" nil
                                 "sublevel6" nil
                                 "submitterName" "ivanp"
                                 "formId" 40993004
                                 "surveyalTime" 11
                                 "surveyedLocaleDisplayName" nil
                                 "dataPointId" 37663003
                                 "surveyedLocaleIdentifier" "42v1-jyvd-he9s"
                                 "userID" 1
                                 "uuid" "d3f64886-9004-4bbd-afea-98776ff33bff"}}]}
   {:event-type "QuestionAnswerStore"
    :samples [{:file-name "question_answer_store_1.json"
               :expected-result {"questionID" 42953002
                                 "lastUpdateDateTime" 1465912476556
                                 "createdDateTime" 1465912476556
                                 "value" "[{\"text\":\"Option 2\",\"code\":\"02\"}]"
                                 "collectionDate" 1465912468220
                                 "iteration" 0
                                 "answerType" "OPTION"
                                 "formInstanceId" 40283002
                                 "ancestorIds" nil
                                 "formId" 40993004
                                 "arbitratyNumber" nil}}]}])

(defn transform-sample
  [sample]
  (let [file (io/resource (str "akvo_reflow/gae_json_samples/" sample))
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
          (:expected-result sample)))))))
