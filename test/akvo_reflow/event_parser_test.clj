(ns akvo-reflow.event-parser-test
  (:require
    [akvo-reflow.event-parser :refer [survey properties parse]]
    [clojure.test :refer :all]))

(def survey-group-sample
  (slurp "test/akvo_reflow/survey_group_sample.json"))

(deftest events
  []
  (testing "SurveyGroup -> Survey"
    (is (=
          (count (parse survey-group-sample))
          4))
    (is (=
          (count (properties (parse survey-group-sample)))
           16))
    (is (=
          (survey (properties (parse survey-group-sample)))
          {"description"     ""
           "name"            "One two three"
           "parentId"        0
           "public"          false
           "surveyGroupType" "FOLDER"}))))
