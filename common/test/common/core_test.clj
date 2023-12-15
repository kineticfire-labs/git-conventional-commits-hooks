;; (c) Copyright 2023 KineticFire. All rights reserved.
;;
;; Licensed under the Apache License, Version 2.0 (the "License");
;; you may not use this file except in compliance with the License.
;; You may obtain a copy of the License at
;;
;;     http://www.apache.org/licenses/LICENSE-2.0
;;
;; Unless required by applicable law or agreed to in writing, software
;; distributed under the License is distributed on an "AS IS" BASIS,
;; WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
;; See the License for the specific language governing permissions and
;; limitations under the License.


;; KineticFire Labs
;;	  Project site:  https://github.com/kineticfire-labs/git-conventional-commits-hooks

(ns common.core-test
  (:require [clojure.test       :refer [deftest is testing]]
            [clojure.string     :as str]
            [babashka.classpath :as cp]
            [common.core        :as common]))

(cp/add-classpath "./")
(require '[common.core :as common])



(defn get-temp-dir
  "Creates a test directory if it doesn't exist and returns a string path to the directory.  The ptah does NOT end with s slash."
  []
  (let [path "gen/test"]
    (.mkdirs (java.io.File. path))
    path))


(deftest do-on-success-test
  (testing "success"
    (let [v (common/do-on-success #(update % :val inc) {:success true :val 1})]
      (is (= "class clojure.lang.PersistentArrayMap" (str (type v))))
      (is (true? (:success v)))
      (is (= 2 (:val v)))))
  (testing "unsuccess"
    (let [v (common/do-on-success #(update % :val inc) {:success false :val 1})]
      (is (= "class clojure.lang.PersistentArrayMap" (str (type v))))
      (is (false? (:success v)))
      (is (= 1 (:val v))))))


(deftest apply-display-with-shell-test
  (testing "string input"
    (let [v (common/apply-display-with-shell "test line")]
      (is (= "class java.lang.String" (str (type v))))
      (is (= "echo -e test line" v))))
  (testing "vector string input"
    (let [v (common/apply-display-with-shell ["test line 1" "test line 2" "test line 3"])]
      (is (= "class clojure.lang.LazySeq" (str (type v))))
      (is (= 3 (count v)))
      (is (= "echo -e test line 1" (first v))))))


(deftest generate-shell-newline-characters-test
  (testing "no arg"
    (let [v (common/generate-shell-newline-characters)]
      (is (= "class java.lang.String" (str (type v))))
      (is (= "\n" v))))
  (testing "arg=1"
    (let [v (common/generate-shell-newline-characters 1)]
      (is (= "class java.lang.String" (str (type v))))
      (is (= "\n" v))))
  (testing "arg=3"
    (let [v (common/generate-shell-newline-characters 3)]
      (is (= "class java.lang.String" (str (type v))))
      (is (= "\n\n\n" v)))))


(deftest generate-commit-msg-offending-line-header-test
  (testing "lines is empty vector"
    (let [v (common/generate-commit-msg-offending-line-header [] -1)]
      (is (= "class clojure.lang.PersistentVector" (str (type v))))
      (is (= 0 (count v)))))
  (testing "lines is empty string"
    (let [v (common/generate-commit-msg-offending-line-header [""] -1)]
      (is (= "class clojure.lang.PersistentVector" (str (type v))))
      (is (= 1 (count v)))
      (is (= "" (first v)))))
  (testing "line-num < 0 (no offending line)"
    (let [v (common/generate-commit-msg-offending-line-header ["Line 1" "Line 2"] -1)]
      (is (= "class clojure.lang.PersistentVector" (str (type v))))
      (is (= 2 (count v)))
      (is (= "Line 1" (first v)))
      (is (= "Line 2" (nth v 1)))))
  (testing "line-num = 0 (first line)"
    (let [v (common/generate-commit-msg-offending-line-header ["Line 1" "Line 2"] 0)]
      (is (= "class clojure.lang.PersistentVector" (str (type v))))
      (is (= 3 (count v)))
      (is (= "Line 1" (first v)))
      (is (= "Line 2" (nth v 1)))
      (is (= "\"   (offending line # 1 in red) **************\"" (nth v 2)))))
  (testing "line-num = 1 (second line)"
    (let [v (common/generate-commit-msg-offending-line-header ["Line 1" "Line 2"] 1)]
      (is (= "class clojure.lang.PersistentVector" (str (type v))))
      (is (= 3 (count v)))
      (is (= "Line 1" (first v)))
      (is (= "Line 2" (nth v 1)))
      (is (= "\"   (offending line # 2 in red) **************\"" (nth v 2))))))


(deftest generate-commit-msg-offending-line-msg-highlight-test
  (testing "lines is empty vector"
    (let [v (common/generate-commit-msg-offending-line-msg-highlight [] -1)]
      (is (= "class clojure.lang.PersistentVector" (str (type v))))
      (is (= 0 (count v)))))
  (testing "lines is empty string"
    (let [v (common/generate-commit-msg-offending-line-msg-highlight [""] -1)]
      (is (= "class clojure.lang.PersistentVector" (str (type v))))
      (is (= 1 (count v)))
      (is (= "" (first v)))))
  (testing "line-num < 0 (no offending line)"
    (let [v (common/generate-commit-msg-offending-line-msg-highlight ["Line 1" "Line 2"] -1)]
      (is (= "class clojure.lang.PersistentVector" (str (type v))))
      (is (= 2 (count v)))
      (is (= "Line 1" (first v)))
      (is (= "Line 2" (nth v 1)))))
  (testing "line-num = 0 (first line)"
    (let [v (common/generate-commit-msg-offending-line-msg-highlight ["Line 1" "Line 2"] 0)]
      (is (= "class clojure.lang.PersistentVector" (str (type v))))
      (is (= 2 (count v)))
      (is (= "\\e[1m\\e[31mLine 1\\033[0m\\e[0m" (first v)))
      (is (= "Line 2" (nth v 1)))))
  (testing "line-num = 1 (second line)"
    (let [v (common/generate-commit-msg-offending-line-msg-highlight ["Line 1" "Line 2"] 1)]
      (is (= "class clojure.lang.PersistentVector" (str (type v))))
      (is (= 2 (count v)))
      (is (= "Line 1" (first v)))
      (is (= "\\e[1m\\e[31mLine 2\\033[0m\\e[0m" (nth v 1))))))


(deftest generate-commit-msg-test
  (testing "lines is empty vector"
    (let [v (common/generate-commit-msg [] -1)]
      (is (= "class clojure.lang.LazySeq" (str (type v))))
      (is (true? (str/includes? (nth v 3) "*************************")))
      (is (= 6 (count v)))))
  (testing "lines is empty string"
    (let [v (common/generate-commit-msg [""] -1)]
      (is (= "class clojure.lang.LazySeq" (str (type v))))
      (is (= 7 (count v)))
      (is (true? (str/includes? (nth v 1) "echo -e \"BEGIN - COMMIT MESSAGE")))
      (is (= "echo -e " (nth v 3)))
      (is (true? (str/includes? (nth v 5) "echo -e \"END - COMMIT MESSAGE")))))
  (testing "line-num < 0 (no offending line)"
    (let [v (common/generate-commit-msg ["Line 1" "Line 2"] -1)]
      (is (= "class clojure.lang.LazySeq" (str (type v))))
      (is (= 8 (count v)))
      (is (true? (str/includes? (nth v 1) "echo -e \"BEGIN - COMMIT MESSAGE")))
      (is (= "echo -e Line 1" (nth v 3)))
      (is (= "echo -e Line 2" (nth v 4)))
      (is (true? (str/includes? (nth v 6) "echo -e \"END - COMMIT MESSAGE")))))
  (testing "line-num = 0 (first line)"
    (let [v (common/generate-commit-msg ["Line 1" "Line 2"] 0)]
      (is (= "class clojure.lang.LazySeq" (str (type v))))
      (is (= 9 (count v)))
      (is (true? (str/includes? (nth v 1) "echo -e \"BEGIN - COMMIT MESSAGE")))
      (is (true? (str/includes? (nth v 2) "echo -e \"   (offending line # 1 in red)")))
      (is (= "echo -e \\e[1m\\e[31mLine 1\\033[0m\\e[0m" (nth v 4))) 
      (is (= "echo -e Line 2" (nth v 5)))
      (is (true? (str/includes? (nth v 7) "echo -e \"END - COMMIT MESSAGE")))))
  (testing "line-num = 1 (second line)"
    (let [v (common/generate-commit-msg ["Line 1" "Line 2"] 1)]
      (is (= "class clojure.lang.LazySeq" (str (type v))))
      (is (= 9 (count v)))
      (is (true? (str/includes? (nth v 1) "echo -e \"BEGIN - COMMIT MESSAGE")))
      (is (true? (str/includes? (nth v 2) "echo -e \"   (offending line # 2 in red)")))
      (is (= "echo -e Line 1" (nth v 4)))
      (is (= "echo -e \\e[1m\\e[31mLine 2\\033[0m\\e[0m" (nth v 5)))
      (is (true? (str/includes? (nth v 7) "echo -e \"END - COMMIT MESSAGE"))))))


(deftest generate-commit-err-msg-test
  (testing "title and err-msg"
    (let [v (common/generate-commit-err-msg "A title." "An error message.")]
      (is (= "class clojure.lang.LazySeq" (str (type v))))
      (is (= 2 (count v)))
      (is (= "echo -e \"\\e[1m\\e[31mCOMMIT REJECTED A title.\"" (first v)))
      (is (= "echo -e \"\\e[1m\\e[31mCommit failed reason: An error message.\\033[0m\\e[0m\"" (nth v 1))))))


(deftest generate-commit-warn-msg-test
  (testing "title and err-msg"
    (let [v (common/generate-commit-warn-msg "A title." "A warning message.")]
      (is (= "class clojure.lang.LazySeq" (str (type v))))
      (is (= 2 (count v)))
      (is (= "echo -e \"\\e[1m\\e[33mCOMMIT WARNING A title.\"" (first v)))
      (is (= "echo -e \"\\e[1m\\e[33mCommit proceeding with warning: A warning message.\\033[0m\\e[0m\"" (nth v 1))))))


(deftest parse-json-file-test
  (testing "file not found"
    (let [v (common/parse-json-file "resources/test/data/does-not-exist.json")]
      (is (= "class clojure.lang.PersistentArrayMap" (str (type v))))
      (is (= "class java.lang.Boolean" (str (type (:success v)))))
      (is (false? (:success v)))
      (is (= "class java.lang.String" (str (type (:reason v)))))
      (is (true? (str/includes? (:reason v) "File 'resources/test/data/does-not-exist.json' not found.")))))
  (testing "parse fail"
    (let [v (common/parse-json-file "resources/test/data/parse-bad.json")]
      (is (= "class clojure.lang.PersistentArrayMap" (str (type v))))
      (is (= "class java.lang.Boolean" (str (type (:success v)))))
      (is (false? (:success v)))
      (is (= "class java.lang.String" (str (type (:reason v)))))
      (is (true? (str/includes? (:reason v) "JSON parse error when reading file 'resources/test/data/parse-bad.json'.")))))
  (testing "parse ok"
    (let [v (common/parse-json-file "resources/test/data/parse-good.json")]
      (is (= "class clojure.lang.PersistentArrayMap" (str (type v))))
      (is (= "class java.lang.Boolean" (str (type (:success v)))))
      (is (true? (:success v)))
      (is (= "class clojure.lang.PersistentArrayMap" (str (type (:result v)))))
      (is (= "hi" (:cb (:c (:result v))))))))


(deftest validate-config-fail-test
  (testing "msg only"
    (let [v (common/validate-config-fail "An error message.")]
      (is (= "class clojure.lang.PersistentArrayMap" (str (type v))))
      (is (string? (:reason v)))
      (is (= "An error message." (:reason v)))
      (is (= "class java.lang.Boolean" (str (type (:success v)))))
      (is (false? (:success v)))))
  (testing "map and msg"
    (let [v (common/validate-config-fail "An error message." {:other "abcd"})]
      (is (= "class clojure.lang.PersistentArrayMap" (str (type v))))
      (is (string? (:reason v)))
      (is (= "An error message." (:reason v)))
      (is (= "class java.lang.Boolean" (str (type (:success v)))))
      (is (false? (:success v)))
      (is (string? (:other v)))
      (is (= "abcd" (:other v))))))


(deftest validate-map-value-test
  (testing "invalid: key sequence not found"
    (let [v (common/validate-map-value {:a {:b 2}} [:a :c] pos-int? (fn[err-msg data](assoc (assoc data :success false) :reason err-msg)) "Was nil." "Not positive int.")]
      (is (boolean? (:success v)))
      (is (false? (:success v)))
      (is (string? (:reason v)))
      (is (= "Was nil." (:reason v)))
      (is (= 2 (get-in v [:a :b])))))
  (testing "invalid: eval fails"
    (let [v (common/validate-map-value {:a {:b -1}} [:a :b] pos-int? (fn [err-msg data] (assoc (assoc data :success false) :reason err-msg)) "Was nil." "Not positive int.")]
      (is (boolean? (:success v)))
      (is (false? (:success v)))
      (is (string? (:reason v)))
      (is (= "Not positive int." (:reason v)))
      (is (= -1 (get-in v [:a :b])))))
  (testing "valid: eval passes"
    (let [v (common/validate-map-value {:a {:b 2}} [:a :b] pos-int? (fn [err-msg data] (assoc (assoc data :success false) :reason err-msg)) "Was nil." "Not positive int.")]
      (is (boolean? (:success v)))
      (is (true? (:success v)))
      (is (false? (contains? v :reason)))
      (is (= 2 (get-in v [:a :b]))))))


(deftest validate-config-msg-enforcement-test
  (testing "enforcement block not defined"
    (let [v (common/validate-config-msg-enforcement {:config {}})]
      (is (boolean? (:success v)))
      (is (false? (:success v)))
      (is (string? (:reason v)))
      (is (= "Commit message enforcement block (commit-msg-enforcement) must be defined." (:reason v)))
      (is (true? (contains? v :config)))))
  (testing "'enabled' not defined"
    (let [v (common/validate-config-msg-enforcement {:config {:commit-msg-enforcement {}}})]
      (is (boolean? (:success v)))
      (is (false? (:success v)))
      (is (string? (:reason v)))
      (is (= "Commit message enforcement must be set as enabled or disabled (commit-msg-enforcement.enabled) with either 'true' or 'false'." (:reason v)))
      (is (true? (contains? v :config)))))
  (testing "'enabled' set to nil"
    (let [v (common/validate-config-msg-enforcement {:config {:commit-msg-enforcement {:enabled nil}}})]
      (is (boolean? (:success v)))
      (is (false? (:success v)))
      (is (string? (:reason v)))
      (is (= "Commit message enforcement must be set as enabled or disabled (commit-msg-enforcement.enabled) with either 'true' or 'false'." (:reason v)))
      (is (true? (contains? v :config)))))
  (testing "'enabled' set to true"
    (let [v (common/validate-config-msg-enforcement {:config {:commit-msg-enforcement {:enabled true}}})]
      (is (boolean? (:success v)))
      (is (true? (:success v)))
      (is (false? (contains? v :reason)))
      (is (true? (contains? v :config)))))
  (testing "'enabled' set to false"
    (let [v (common/validate-config-msg-enforcement {:config {:commit-msg-enforcement {:enabled false}}})]
      (is (boolean? (:success v)))
      (is (true? (:success v)))
      (is (false? (contains? v :reason)))
      (is (true? (contains? v :config))))))


(deftest validate-config-length-test
  ;; keys are defined
  (testing "title-line.min is not defined"
    (let [v (common/validate-config-length {:config {:commit-msg {:length {:title-line {:max 20}
                                                                           :body-line {:min 2
                                                                                       :max 10}}}}})]
      (is (= "class clojure.lang.PersistentArrayMap" (str (type v))))
      (is (= "class java.lang.Boolean" (str (type (:success v)))))
      (is (false? (:success v)))
      (is (= "class java.lang.String" (str (type (:reason v)))))
      (is (true? (= (:reason v) "Minimum length of title line (length.title-line.min) must be defined.")))))
  (testing "title-line.max is not defined"
    (let [v (common/validate-config-length {:config {:commit-msg {:length {:title-line {:min 12}
                                                                           :body-line {:min 2
                                                                                       :max 10}}}}})]
      (is (= "class clojure.lang.PersistentArrayMap" (str (type v))))
      (is (= "class java.lang.Boolean" (str (type (:success v)))))
      (is (false? (:success v)))
      (is (= "class java.lang.String" (str (type (:reason v)))))
      (is (true? (= (:reason v) "Maximum length of title line (length.title-line.max) must be defined.")))))
  (testing "body-line.min is not defined"
    (let [v (common/validate-config-length {:config {:commit-msg {:length {:title-line {:min 12
                                                                                        :max 20}
                                                                           :body-line {:max 10}}}}})]
      (is (= "class clojure.lang.PersistentArrayMap" (str (type v))))
      (is (= "class java.lang.Boolean" (str (type (:success v)))))
      (is (false? (:success v)))
      (is (= "class java.lang.String" (str (type (:reason v)))))
      (is (true? (= (:reason v) "Minimum length of body line (length.body-line.min) must be defined.")))))
  (testing "body-line.max is not defined"
    (let [v (common/validate-config-length {:config {:commit-msg {:length {:title-line {:min 12
                                                                                        :max 20}
                                                                           :body-line {:min 2}}}}})]
      (is (= "class clojure.lang.PersistentArrayMap" (str (type v))))
      (is (= "class java.lang.Boolean" (str (type (:success v)))))
      (is (false? (:success v)))
      (is (= "class java.lang.String" (str (type (:reason v)))))
      (is (true? (= (:reason v) "Maximum length of body line (length.body-line.max) must be defined.")))))
  ;; title-line min/max and relative
  (testing "title-line.min is negative"
    (let [v (common/validate-config-length {:config {:commit-msg {:length {:title-line {:min -1
                                                                                        :max 20}
                                                                           :body-line {:min 2
                                                                                       :max 10}}}}})]
      (is (= "class clojure.lang.PersistentArrayMap" (str (type v))))
      (is (= "class java.lang.Boolean" (str (type (:success v)))))
      (is (false? (:success v)))
      (is (= "class java.lang.String" (str (type (:reason v)))))
      (is (true? (= (:reason v) "Minimum length of title line (length.title-line.min) must be a positive integer.")))))
  (testing "title-line.min is zero"
    (let [v (common/validate-config-length {:config {:commit-msg {:length {:title-line {:min 0
                                                                                        :max 20}
                                                                           :body-line {:min 2
                                                                                       :max 10}}}}})]
      (is (= "class clojure.lang.PersistentArrayMap" (str (type v))))
      (is (= "class java.lang.Boolean" (str (type (:success v)))))
      (is (false? (:success v)))
      (is (= "class java.lang.String" (str (type (:reason v)))))
      (is (true? (= (:reason v) "Minimum length of title line (length.title-line.min) must be a positive integer.")))))
  (testing "title-line.max is negative"
    (let [v (common/validate-config-length {:config {:commit-msg {:length {:title-line {:min 12
                                                                                        :max -1}
                                                                           :body-line {:min 2
                                                                                       :max 10}}}}})]
      (is (= "class clojure.lang.PersistentArrayMap" (str (type v))))
      (is (= "class java.lang.Boolean" (str (type (:success v)))))
      (is (false? (:success v)))
      (is (= "class java.lang.String" (str (type (:reason v)))))
      (is (true? (= (:reason v) "Maximum length of title line (length.title-line.max) must be a positive integer.")))))
  (testing "title-line.max is zero"
    (let [v (common/validate-config-length {:config {:commit-msg {:length {:title-line {:min 12
                                                                                        :max 0}
                                                                           :body-line {:min 2
                                                                                       :max 10}}}}})]
      (is (= "class clojure.lang.PersistentArrayMap" (str (type v))))
      (is (= "class java.lang.Boolean" (str (type (:success v)))))
      (is (false? (:success v)))
      (is (= "class java.lang.String" (str (type (:reason v)))))
      (is (true? (= (:reason v) "Maximum length of title line (length.title-line.max) must be a positive integer.")))))
  (testing "title-line.max is less than title-line.min"
     (let [v (common/validate-config-length {:config {:commit-msg {:length {:title-line {:min 12
                                                                                         :max 11}
                                                                            :body-line {:min 2
                                                                                        :max 10}}}}})]
       (is (= "class clojure.lang.PersistentArrayMap" (str (type v))))
       (is (= "class java.lang.Boolean" (str (type (:success v)))))
       (is (false? (:success v)))
       (is (= "class java.lang.String" (str (type (:reason v)))))
       (is (true? (= (:reason v) "Maximum length of title line (length.title-line.max) must be equal to or greater than minimum length of title line (length.title-line.min).")))))
  ;; body-line min/max and relative)
  (testing "body-line.min is negative"
    (let [v (common/validate-config-length {:config {:commit-msg {:length {:title-line {:min 12
                                                                                        :max 20}
                                                                           :body-line {:min -1
                                                                                       :max 10}}}}})]
      (is (= "class clojure.lang.PersistentArrayMap" (str (type v))))
      (is (= "class java.lang.Boolean" (str (type (:success v)))))
      (is (false? (:success v)))
      (is (= "class java.lang.String" (str (type (:reason v)))))
      (is (true? (= (:reason v) "Minimum length of body line (length.body-line.min) must be a positive integer.")))))
  (testing "body-line.min is zero"
    (let [v (common/validate-config-length {:config {:commit-msg {:length {:title-line {:min 12
                                                                                        :max 20}
                                                                           :body-line {:min 0
                                                                                       :max 10}}}}})]
      (is (= "class clojure.lang.PersistentArrayMap" (str (type v))))
      (is (= "class java.lang.Boolean" (str (type (:success v)))))
      (is (false? (:success v)))
      (is (= "class java.lang.String" (str (type (:reason v)))))
      (is (true? (= (:reason v) "Minimum length of body line (length.body-line.min) must be a positive integer.")))))
  (testing "body-line.max is negative"
    (let [v (common/validate-config-length {:config {:commit-msg {:length {:title-line {:min 12
                                                                                        :max 20}
                                                                           :body-line {:min 2
                                                                                       :max -1}}}}})]
      (is (= "class clojure.lang.PersistentArrayMap" (str (type v))))
      (is (= "class java.lang.Boolean" (str (type (:success v)))))
      (is (false? (:success v)))
      (is (= "class java.lang.String" (str (type (:reason v)))))
      (is (true? (= (:reason v) "Maximum length of body line (length.body-line.max) must be a positive integer.")))))
  (testing "body-line.max is zero"
    (let [v (common/validate-config-length {:config {:commit-msg {:length {:title-line {:min 12
                                                                                        :max 20}
                                                                           :body-line {:min 2
                                                                                       :max 0}}}}})]
      (is (= "class clojure.lang.PersistentArrayMap" (str (type v))))
      (is (= "class java.lang.Boolean" (str (type (:success v)))))
      (is (false? (:success v)))
      (is (= "class java.lang.String" (str (type (:reason v)))))
      (is (true? (= (:reason v) "Maximum length of body line (length.body-line.max) must be a positive integer.")))))
  (testing "title-line.max is less than title-line.min"
    (let [v (common/validate-config-length {:config {:commit-msg {:length {:title-line {:min 12
                                                                                        :max 20}
                                                                           :body-line {:min 2
                                                                                       :max 1}}}}})]
      (is (= "class clojure.lang.PersistentArrayMap" (str (type v))))
      (is (= "class java.lang.Boolean" (str (type (:success v)))))
      (is (false? (:success v)))
      (is (= "class java.lang.String" (str (type (:reason v)))))
      (is (true? (= (:reason v) "Maximum length of body line (length.body-line.max) must be equal to or greater than minimum length of body line (length.body-line.min)."))))))
  

(deftest validate-config-for-root-project-test
  (testing "project valid"
    (let [v (common/validate-config-for-root-project {:config {:project {:a 1 :b 2}}})]
      (is (map? v))
      (is (true? (contains? v :config)))
      (is (true? (:success v)))))
  (testing "project invalid: property not defined"
    (let [v (common/validate-config-for-root-project {:config {:another {:a 1 :b 2}}})]
      (is (map? v))
      (is (true? (contains? v :config)))
      (is (false? (:success v)))
      (is (= (:reason v) "Property 'project' must be defined at the top-level."))))
  (testing "project invalid: project is nil"
    (let [v (common/validate-config-for-root-project {:config {:project nil}})]
      (is (map? v))
      (is (true? (contains? v :config)))
      (is (false? (:success v)))
      (is (= (:reason v) "Property 'project' must be defined at the top-level."))))
  (testing "project invalid: property is a scalar vs a map"
    (let [v (common/validate-config-for-root-project {:config {:project 5}})]
      (is (map? v))
      (is (true? (contains? v :config)))
      (is (false? (:success v)))
      (is (= (:reason v) "Property 'project' must be a map."))))
  (testing "project invalid: property is a vector vs a map"
    (let [v (common/validate-config-for-root-project {:config {:project [5]}})]
      (is (map? v))
      (is (true? (contains? v :config)))
      (is (false? (:success v)))
      (is (= (:reason v) "Property 'project' must be a map.")))))


(deftest validate-config-project-artifact-common-test
  (testing "valid config with all optional properties"
    (let [v (common/validate-config-project-artifact-common :project [:config :project] {:config {:project {:name "Top Project"
                                                                                                            :description "The top project"
                                                                                                            :scope "proj"
                                                                                                            :scope-alias "p"
                                                                                                            :types ["feat", "chore", "refactor"]
                                                                                                            :projects [{:name "Subproject A"
                                                                                                                        :description "The subproject A"
                                                                                                                        :scope "proja"
                                                                                                                        :scope-alias "a"
                                                                                                                        :types ["feat", "chore", "refactor"]}
                                                                                                                       {:name "Subproject B"
                                                                                                                        :description "The subproject B"
                                                                                                                        :scope "projb"
                                                                                                                        :scope-alias "b"
                                                                                                                        :types ["feat", "chore", "refactor"]}]
                                                                                                            :artifacts [{:name "Artifact Y"
                                                                                                                         :description "The artifact Y"
                                                                                                                         :scope "arty"
                                                                                                                         :scope-alias "y"
                                                                                                                         :types ["feat", "chore", "refactor"]}
                                                                                                                        {:name "Artifact Z"
                                                                                                                         :description "The artifact Z"
                                                                                                                         :scope "artz"
                                                                                                                         :scope-alias "z"
                                                                                                                         :types ["feat", "chore", "refactor"]}]}}})]
      (is (map? v))
      (is (true? (:success v)))))
  (testing "valid config without optional properties but with 'projects' and 'artifacts"
    (let [v (common/validate-config-project-artifact-common :project [:config :project] {:config {:project {:name "Top Project"
                                                                                                            :scope "proj"
                                                                                                            :types ["feat", "chore", "refactor"]
                                                                                                            :projects [{:name "Subproject A"
                                                                                                                        :scope "proja"
                                                                                                                        :types ["feat", "chore", "refactor"]}
                                                                                                                       {:name "Subproject B"
                                                                                                                        :scope "projb"
                                                                                                                        :types ["feat", "chore", "refactor"]}]
                                                                                                            :artifacts [{:name "Artifact Y"
                                                                                                                         :scope "arty"
                                                                                                                         :types ["feat", "chore", "refactor"]}
                                                                                                                        {:name "Artifact Z"
                                                                                                                         :scope "artz"
                                                                                                                         :types ["feat", "chore", "refactor"]}]}}})]
      (is (map? v))
      (is (true? (:success v)))))
  (testing "valid config without optional properties and without 'projects'; use 'artifact' node-type"
    (let [v (common/validate-config-project-artifact-common :project [:config :project] {:config {:project {:name "Top Project"
                                                                                                            :scope "proj"
                                                                                                            :types ["feat", "chore", "refactor"]}}})]
      (is (map? v))
      (is (true? (:success v)))))
  (testing "valid config without optional properties and without 'projects'"
    (let [v (common/validate-config-project-artifact-common :project [:config :project] {:config {:project {:name "Top Project"
                                                                                                            :scope "proj"
                                                                                                            :types ["feat", "chore", "refactor"]}}})]
      (is (map? v))
      (is (true? (:success v)))))
  (testing "invalid config: name not defined; use 'artifact' node-type"
    (let [v (common/validate-config-project-artifact-common :artifact [:config :project] {:config {:project {:description "The top project"
                                                                                                             :scope "proj"
                                                                                                             :scope-alias "p"
                                                                                                             :types ["feat", "chore", "refactor"]}}})]
      (is (map? v))
      (is (false? (:success v)))
      (is (= (:reason v) "Artifact required property 'name' at path [:config :project] must be a string."))))
  (testing "invalid config: name not defined"
    (let [v (common/validate-config-project-artifact-common :project [:config :project] {:config {:project {:description "The top project"
                                                                                                            :scope "proj"
                                                                                                            :scope-alias "p"
                                                                                                            :types ["feat", "chore", "refactor"]}}})]
      (is (map? v))
      (is (false? (:success v)))
      (is (= (:reason v) "Project required property 'name' at path [:config :project] must be a string."))))
  (testing "invalid config: name not a string"
    (let [v (common/validate-config-project-artifact-common :project [:config :project] {:config {:project {:name 5
                                                                                                            :description "The top project"
                                                                                                            :scope "proj"
                                                                                                            :scope-alias "p"
                                                                                                            :types ["feat", "chore", "refactor"]}}})]
      (is (map? v))
      (is (false? (:success v)))
      (is (= (:reason v) "Project required property 'name' at path [:config :project] must be a string."))))
  (testing "invalid config: description not a string"
    (let [v (common/validate-config-project-artifact-common :project [:config :project] {:config {:project {:name "Top Project"
                                                                                                            :description 5
                                                                                                            :scope "proj"
                                                                                                            :scope-alias "p"
                                                                                                            :types ["feat", "chore", "refactor"]}}})]
      (is (map? v))
      (is (false? (:success v)))
      (is (= (:reason v) "Project optional property 'description' at name Top Project and path [:config :project] must be a string."))))
  (testing "invalid config: scope not defined"
    (let [v (common/validate-config-project-artifact-common :project [:config :project] {:config {:project {:name "Top Project"
                                                                                                            :description "The top project"
                                                                                                            :scope-alias "p"
                                                                                                            :types ["feat", "chore", "refactor"]}}})]
      (is (map? v))
      (is (false? (:success v)))
      (is (= (:reason v) "Project required property 'scope' at name Top Project and path [:config :project] must be a string."))))
  (testing "invalid config: scope not a string"
    (let [v (common/validate-config-project-artifact-common :project [:config :project] {:config {:project {:name "Top Project"
                                                                                                            :description "The top project"
                                                                                                            :scope 5
                                                                                                            :scope-alias "p"
                                                                                                            :types ["feat", "chore", "refactor"]}}})]
      (is (map? v))
      (is (false? (:success v)))
      (is (= (:reason v) "Project required property 'scope' at name Top Project and path [:config :project] must be a string."))))
  (testing "invalid config: scope-alias not a string"
    (let [v (common/validate-config-project-artifact-common :project [:config :project] {:config {:project {:name "Top Project"
                                                                                                            :description "The top project"
                                                                                                            :scope "proj"
                                                                                                            :scope-alias 5
                                                                                                            :types ["feat", "chore", "refactor"]}}})]
      (is (map? v))
      (is (false? (:success v)))
      (is (= (:reason v) "Project optional property 'scope-alias' at name Top Project and path [:config :project] must be a string."))))
  (testing "invalid config: types not defined"
    (let [v (common/validate-config-project-artifact-common :project [:config :project] {:config {:project {:name "Top Project"
                                                                                                            :description "The top project"
                                                                                                            :scope "proj"
                                                                                                            :scope-alias "p"}}})]
      (is (map? v))
      (is (false? (:success v)))
      (is (= (:reason v) "Project required property 'types' at name Top Project and path [:config :project] must be an array of strings."))))
  (testing "invalid config: types not an array"
    (let [v (common/validate-config-project-artifact-common :project [:config :project] {:config {:project {:name "Top Project"
                                                                                                            :description "The top project"
                                                                                                            :scope "proj"
                                                                                                            :scope-alias "p"
                                                                                                            :types {:object-invalid 5}}}})]
      (is (map? v))
      (is (false? (:success v)))
      (is (= (:reason v) "Project required property 'types' at name Top Project and path [:config :project] must be an array of strings."))))
  (testing "invalid config: can't define property 'project' on non-root project"
    (let [v (common/validate-config-project-artifact-common :project [:config :project] {:config {:project {:name "Top Project"
                                                                                                            :description "The top project"
                                                                                                            :scope "proj"
                                                                                                            :scope-alias "p"
                                                                                                            :project {:name "Invalid Project"}
                                                                                                            :types ["feat", "chore", "refactor"]}}})]
      (is (map? v))
      (is (false? (:success v)))
      (is (= (:reason v) "Project cannot have property 'project' at name Top Project and path [:config :project].")))))


(deftest validate-config-artifact-specific-test
  (testing "valid config with all optional properties"
    (let [v (common/validate-config-artifact-specific [:config :project :artifacts 0] {:config {:project {:name "Top Project"
                                                                                                            :description "The top project"
                                                                                                            :scope "proj"
                                                                                                            :scope-alias "p"
                                                                                                            :types ["feat", "chore", "refactor"]
                                                                                                            :projects [{:name "Subproject A"
                                                                                                                        :description "The subproject A"
                                                                                                                        :scope "proja"
                                                                                                                        :scope-alias "a"
                                                                                                                        :types ["feat", "chore", "refactor"]}
                                                                                                                       {:name "Subproject B"
                                                                                                                        :description "The subproject B"
                                                                                                                        :scope "projb"
                                                                                                                        :scope-alias "b"
                                                                                                                        :types ["feat", "chore", "refactor"]}]
                                                                                                            :artifacts [{:name "Artifact Y"
                                                                                                                         :description "The artifact Y"
                                                                                                                         :scope "arty"
                                                                                                                         :scope-alias "y"
                                                                                                                         :types ["feat", "chore", "refactor"]}
                                                                                                                        {:name "Artifact Z"
                                                                                                                         :description "The artifact Z"
                                                                                                                         :scope "artz"
                                                                                                                         :scope-alias "z"
                                                                                                                         :types ["feat", "chore", "refactor"]}]}}})]
      (is (map? v))
      (is (true? (:success v)))))
  (testing "valid config without optional properties but with 'projects' and 'artifacts"
    (let [v (common/validate-config-artifact-specific [:config :project :artifacts 0] {:config {:project {:name "Top Project"
                                                                                                          :scope "proj"
                                                                                                          :types ["feat", "chore", "refactor"]
                                                                                                          :projects [{:name "Subproject A"
                                                                                                                      :scope "proja"
                                                                                                                      :types ["feat", "chore", "refactor"]}
                                                                                                                     {:name "Subproject B"
                                                                                                                      :scope "projb"
                                                                                                                      :types ["feat", "chore", "refactor"]}]
                                                                                                          :artifacts [{:name "Artifact Y"
                                                                                                                       :scope "arty"
                                                                                                                       :types ["feat", "chore", "refactor"]}
                                                                                                                      {:name "Artifact Z"
                                                                                                                       :scope "artz"
                                                                                                                       :types ["feat", "chore", "refactor"]}]}}})]
      (is (map? v))
      (is (true? (:success v)))))
  (testing "invalid config: artifact can't define 'projects'"
    (let [v (common/validate-config-artifact-specific [:config :project :artifacts 0] {:config {:project {:name "Top Project"
                                                                                                          :scope "proj"
                                                                                                          :types ["feat", "chore", "refactor"]
                                                                                                          :projects [{:name "Subproject A"
                                                                                                                      :scope "proja"
                                                                                                                      :types ["feat", "chore", "refactor"]}
                                                                                                                     {:name "Subproject B"
                                                                                                                      :scope "projb"
                                                                                                                      :types ["feat", "chore", "refactor"]}]
                                                                                                          :artifacts [{:name "Artifact Y"
                                                                                                                       :scope "arty"
                                                                                                                       :types ["feat", "chore", "refactor"]
                                                                                                                       :projects [{:name "a"}]}
                                                                                                                      {:name "Artifact Z"
                                                                                                                       :scope "artz"
                                                                                                                       :types ["feat", "chore", "refactor"]}]}}})]
      (is (map? v))
      (is (false? (:success v)))
      (is (= (:reason v) "Artifact cannot have property 'projects' at name Artifact Y and path [:config :project :artifacts 0]."))))
  (testing "invalid config: artifact can't define 'artifacts'"
    (let [v (common/validate-config-artifact-specific [:config :project :artifacts 0] {:config {:project {:name "Top Project"
                                                                                                          :scope "proj"
                                                                                                          :types ["feat", "chore", "refactor"]
                                                                                                          :projects [{:name "Subproject A"
                                                                                                                      :scope "proja"
                                                                                                                      :types ["feat", "chore", "refactor"]}
                                                                                                                     {:name "Subproject B"
                                                                                                                      :scope "projb"
                                                                                                                      :types ["feat", "chore", "refactor"]}]
                                                                                                          :artifacts [{:name "Artifact Y"
                                                                                                                       :scope "arty"
                                                                                                                       :types ["feat", "chore", "refactor"]
                                                                                                                       :artifacts [{:name "a"}]}
                                                                                                                      {:name "Artifact Z"
                                                                                                                       :scope "artz"
                                                                                                                       :types ["feat", "chore", "refactor"]}]}}})]
      (is (map? v))
      (is (false? (:success v)))
      (is (= (:reason v) "Artifact cannot have property 'artifacts' at name Artifact Y and path [:config :project :artifacts 0].")))))




;;todo
(deftest validate-config-project-specific-test
  (testing "valid config with projects and artifacts"
    (let [v (common/validate-config-project-specific [:config :project] {:config {:project {:name "Top Project"
                                                                                            :scope "proj"
                                                                                            :types ["feat", "chore", "refactor"]
                                                                                            :projects [{:name "Subproject A"
                                                                                                        :scope "proja"
                                                                                                        :types ["feat", "chore", "refactor"]}
                                                                                                       {:name "Subproject B"
                                                                                                        :scope "projb"
                                                                                                        :types ["feat", "chore", "refactor"]}]
                                                                                            :artifacts [{:name "Artifact Y"
                                                                                                         :scope "arty"
                                                                                                         :types ["feat", "chore", "refactor"]}
                                                                                                        {:name "Artifact Z"
                                                                                                         :scope "artz"
                                                                                                         :types ["feat", "chore", "refactor"]}]}}})]
      (is (map? v))
      (is (true? (:success v)))))
  (testing "valid config without projects and artifacts"
    (let [v (common/validate-config-project-specific [:config :project] {:config {:project {:name "Top Project"
                                                                                            :scope "proj"
                                                                                            :types ["feat", "chore", "refactor"]}}})]
      (is (map? v))
      (is (true? (:success v)))))
  (testing "invalid config: projects is not an array of objects"
    (let [v (common/validate-config-project-specific [:config :project] {:config {:project {:name "Top Project"
                                                                                            :scope "proj"
                                                                                            :types ["feat", "chore", "refactor"]
                                                                                            :projects [1 2 3]
                                                                                            :artifacts [{:name "Artifact Y"
                                                                                                         :scope "arty"
                                                                                                         :types ["feat", "chore", "refactor"]}
                                                                                                        {:name "Artifact Z"
                                                                                                         :scope "artz"
                                                                                                         :types ["feat", "chore", "refactor"]}]}}})]
      (is (map? v))
      (is (false? (:success v)))
      (is (= (:reason v) "Project optional property 'projects' at name Top Project and path [:config :project] must be an array of objects."))))
  (testing "invalid config: projects is not an array of objects"
    (let [v (common/validate-config-project-specific [:config :project] {:config {:project {:name "Top Project"
                                                                                            :scope "proj"
                                                                                            :types ["feat", "chore", "refactor"]
                                                                                            :projects [{:name "Subproject A"
                                                                                                        :scope "proja"
                                                                                                        :types ["feat", "chore", "refactor"]}
                                                                                                       {:name "Subproject B"
                                                                                                        :scope "projb"
                                                                                                        :types ["feat", "chore", "refactor"]}]
                                                                                            :artifacts [1 2 3]}}})]
      (is (map? v))
      (is (false? (:success v)))
      (is (= (:reason v) "Project optional property 'artifacts' at name Top Project and path [:config :project] must be an array of objects.")))))





(deftest get-frequency-on-properties-on-array-of-objects-test
  ;; single property
  (testing "empty target, no duplicates"
    (let [v (common/get-frequency-on-properties-on-array-of-objects [] [:name])]
      (is (seq? v))
      (is (= 0 (count v)))))
  (testing "single property, no duplicates"
    (let [v (common/get-frequency-on-properties-on-array-of-objects [{:name "a"} {:name "b"} {:name "c"}][:name])]
      (is (seq? v))
      (is (= 0 (count v)))))
  (testing "single property, 1 duplicate"
    (let [v (common/get-frequency-on-properties-on-array-of-objects [{:name "a"} {:name "b"} {:name "c"} {:name "c"}] [:name])]
      (is (seq? v))
      (is (= 1 (count v)))
      (is (= "c" (first v)))))
  (testing "single property, 2 duplicates"
    (let [v (common/get-frequency-on-properties-on-array-of-objects [{:name "a"} {:name "b"} {:name "c"} {:name "c"} {:name "b"}  {:name "b"}] [:name])]
      (is (seq? v))
      (is (= 2 (count v)))
      (is (some #{"b"} v))
      (is (some #{"c"} v))))
  ;; multiple properties
  (testing "multiple properties, no duplicates"
    (let [v (common/get-frequency-on-properties-on-array-of-objects [{:name "a" :other "1"} {:name "b" :other "2"} {:name "c" :other "3"}] [:name :other])]
      (is (seq? v))
      (is (= 0 (count v)))))
  (testing "multiple properties, 1 duplicate on same property"
    (let [v (common/get-frequency-on-properties-on-array-of-objects [{:name "a" :other "1"} {:name "b" :other "2"} {:name "c" :other "3"} {:name "c" :other "9"}] [:name :other])]
      (is (seq? v))
      (is (= 1 (count v)))
      (is (= "c" (first v)))))
  (testing "multiple properties, 2 duplicates on same properties"
    (let [v (common/get-frequency-on-properties-on-array-of-objects [{:name "a" :other "1"} {:name "b" :other "2"} {:name "c" :other "3"} {:name "c" :other "9"} {:name "b" :other "8"}] [:name :other])]
      (is (seq? v))
      (is (= 2 (count v)))
      (is (some #{"b"} v))
      (is (some #{"c"} v))))
  (testing "multiple properties, 1 duplicate on different property"
    (let [v (common/get-frequency-on-properties-on-array-of-objects [{:name "a" :other "1"} {:name "b" :other "2"} {:name "c" :other "3"} {:name "d" :other "3"}] [:name :other])]
      (is (seq? v))
      (is (= 1 (count v)))
      (is (= "3" (first v)))))
  (testing "multiple properties, 2 duplicates on different properties"
    (let [v (common/get-frequency-on-properties-on-array-of-objects [{:name "a" :other "1"} {:name "b" :other "2"} {:name "c" :other "3"} {:name "c" :other "9"} {:name "z" :other "2"}] [:name :other])]
      (is (seq? v))
      (is (= 2 (count v)))
      (is (some #{"2"} v))
      (is (some #{"c"} v)))))


;; todo
(deftest validate-config-project-artifact-lookahead-test
  (testing "project valid"
    (let [v (common/validate-config-project-artifact-lookahead :project [:config :project :projects] {:config {:project {:name "top"
                                                                                                                         :projects [{:name "a"
                                                                                                                                     :description "Project A"
                                                                                                                                     :scope "alpha"
                                                                                                                                     :scope-alias "a"}
                                                                                                                                    {:name "b"
                                                                                                                                     :description "Project B"
                                                                                                                                     :scope "bravo"
                                                                                                                                     :scope-alias "b"}
                                                                                                                                    {:name "c"
                                                                                                                                     :description "Project C"
                                                                                                                                     :scope "charlie"
                                                                                                                                     :scope-alias "c"}]
                                                                                                                         :artifacts [{:name "Artifact X"
                                                                                                                                      :description "Artifact X"
                                                                                                                                      :scope "artx"
                                                                                                                                      :scope-alias "x"}
                                                                                                                                     {:name "Artifact Y"
                                                                                                                                      :description "Artifact Y"
                                                                                                                                      :scope "arty"
                                                                                                                                      :scope-alias "y"}
                                                                                                                                     {:name "Artifact Z"
                                                                                                                                      :description "Artifact Z"
                                                                                                                                      :scope "artz"
                                                                                                                                      :scope-alias "z"}]}}})]
      (is (map? v))
      (is (true? (:success v)))))
  (testing "artifact valid"
    (let [v (common/validate-config-project-artifact-lookahead :artifact [:config :project :artifacts] {:config {:project {:name "top"
                                                                                                                           :projects [{:name "a"
                                                                                                                                       :description "Project A"
                                                                                                                                       :scope "alpha"
                                                                                                                                       :scope-alias "a"}
                                                                                                                                      {:name "b"
                                                                                                                                       :description "Project B"
                                                                                                                                       :scope "bravo"
                                                                                                                                       :scope-alias "b"}
                                                                                                                                      {:name "c"
                                                                                                                                       :description "Project C"
                                                                                                                                       :scope "charlie"
                                                                                                                                       :scope-alias "c"}]
                                                                                                                           :artifacts [{:name "Artifact X"
                                                                                                                                        :description "Artifact X"
                                                                                                                                        :scope "artx"
                                                                                                                                        :scope-alias "x"}
                                                                                                                                       {:name "Artifact Y"
                                                                                                                                        :description "Artifact Y"
                                                                                                                                        :scope "arty"
                                                                                                                                        :scope-alias "y"}
                                                                                                                                       {:name "Artifact Z"
                                                                                                                                        :description "Artifact Z"
                                                                                                                                        :scope "artz"
                                                                                                                                        :scope-alias "z"}]}}})]
      (is (map? v))
      (is (true? (:success v))))))
     



;; todo
(deftest validate-config-projects-test
  (testing "initial"
    (let [v (common/validate-config-projects {:config {:project {:name "top" :projects [{:name "a" :projects [{:name "a.1"}]} {:name "b" :projects [{:name "b.1"}]}]}}})])))
      ;;(is (map? v))
      ;;(is (= v {}))
      


(deftest config-enabled?-test
  (testing "enabled"
    (let [v (common/config-enabled? {:commit-msg-enforcement {:enabled true}})]
      (is (true? v))
      (is (= "class java.lang.Boolean" (str (type v))))))
  (testing "disabled"
    (let [v (common/config-enabled? {:commit-msg-enforcement {:enabled false}})]
      (is (false? v))
      (is (= "class java.lang.Boolean" (str (type v)))))))


(deftest read-file-test
  (testing "file not found"
    (let [v (common/read-file "resources/test/data/does-not-exist.txt")]
      (is (= "class clojure.lang.PersistentArrayMap" (str (type v))))
      (is (= "class java.lang.Boolean" (str (type (:success v)))))
      (is (false? (:success v)))
      (is (= "class java.lang.String" (str (type (:reason v)))))
      (is (true? (str/includes? (:reason v) "File 'resources/test/data/does-not-exist.txt' not found."))))) 
  (testing "file ok"
    (let [v (common/read-file "resources/test/data/file-to-read.txt")]
      (is (= "class clojure.lang.PersistentArrayMap" (str (type v))))
      (is (= "class java.lang.Boolean" (str (type (:success v)))))
      (is (true? (:success v)))
      (is (= "class java.lang.String" (str (type (:result v)))))
      (is (= "This is a\n\nmulti-line file to read\n" (:result v))))))


(deftest write-file-test
  (let [test-dir (get-temp-dir)]
    (testing "file not found"
      (let [v (common/write-file (str test-dir "/does-not-exist/file.txt") "Line 1\nLine 2\nLine 3")]
        (is (= "class clojure.lang.PersistentArrayMap" (str (type v))))
        (is (= "class java.lang.Boolean" (str (type (:success v)))))
        (is (false? (:success v)))
        (is (= "class java.lang.String" (str (type (:reason v)))))
        (is (true? (str/includes? (:reason v) (str "File '" test-dir "/does-not-exist/file.txt' not found."))))))
    (testing "file ok"
      (let [content "Line 1\nLine 2\nLine 3"
            out-file (str test-dir "/write-file-ok.txt")
            v (common/write-file out-file content)]
        (is (= "class clojure.lang.PersistentArrayMap" (str (type v))))
        (is (= "class java.lang.Boolean" (str (type (:success v)))))
        (is (true? (:success v)))
        (is (= content (slurp out-file)))))))


(deftest split-lines-test
  (testing "empty string"
    (let [v (common/split-lines "")]
      (is (= 1 (count v)))
      (is (= "" (first v)))
      (is (= "class clojure.lang.PersistentVector" (str (type v))))))
  (testing "single line"
    (let [v (common/split-lines "One long line")]
      (is (= 1 (count v)))
      (is (= "One long line" (first v)))
      (is (= "class clojure.lang.PersistentVector" (str (type v))))))
  (testing "multiple lines"
    (let [v (common/split-lines "First line\nSecond line\nThird line")]
      (is (= 3 (count v)))
      (is (= "First line" (first v)))
      (is (= "Second line" (nth v 1)))
      (is (= "Third line" (nth v 2)))
      (is (= "class clojure.lang.PersistentVector" (str (type v)))))))


(deftest format-commit-msg-all-test
  (testing "empty string"
    (let [v (common/format-commit-msg-all "")]
      (is (= "" v))
      (is (= "class java.lang.String" (str (type v))))))
  (testing "replace all lines with comments with empty strings"
    (let [v (common/format-commit-msg-all "#Comment0\nLine1\n#Comment2\nLine3\n #Comment4\nLine5\n#  Comment 6\nLine7")]
      (is (= "Line1\n\nLine3\n\nLine5\n\nLine7" v))
      (is (= "class java.lang.String" (str (type v))))))
  (testing "for a line with spaces only, remove all spaces"
    (let [v (common/format-commit-msg-all "Line1\n \nLine3\n   \nLine5")]
      (is (= "Line1\n\nLine3\n\nLine5" v))
      (is (= "class java.lang.String" (str (type v))))))
  (testing "replace two or more consecutive newlines with a single newline"
    ;; "Line1\n\nLine2" because of regex for "<title>\n\n<body>"
    (let [v (common/format-commit-msg-all "Line1\nLine2\n\nLine3\n\nLine4\nLine5\nLine6\n\n\nLine7\n\n\n\n\n\nLine8")]
      (is (= "Line1\n\nLine2\n\nLine3\n\nLine4\nLine5\nLine6\n\nLine7\n\nLine8" v))
      (is (= "class java.lang.String" (str (type v))))))
  (testing "remove spaces at end of lines (without removing spaces at beginning of lines)"
    ;; "Line1\n\nLine2" because of regex for "<title>\n\n<body>"
    (let [v (common/format-commit-msg-all "Line1\nLine2  \n  Line3  \nLine4\n Line5 ")]
      (is (= "Line1\n\nLine2\n  Line3\nLine4\n Line5" v))
      (is (= "class java.lang.String" (str (type v))))))
  (testing "convert to 'BREAKING CHANGE: <msg>' formatted correctly"
    (let [v (common/format-commit-msg-all "BREAKING CHANGE: a change")]
      (is (= "BREAKING CHANGE: a change" v))
      (is (= "class java.lang.String" (str (type v))))))
  (testing "convert to 'BREAKING CHANGE:<msg>' with spaces"
    (let [v (common/format-commit-msg-all "  BREAKING CHANGE  :   a change  ")]
      (is (= "BREAKING CHANGE: a change" v))
      (is (= "class java.lang.String" (str (type v))))))
  (testing "convert to 'BREAKING CHANGE:<msg>' lowercase"
    (let [v (common/format-commit-msg-all "  breaking change  :   a change  ")]
      (is (= "BREAKING CHANGE: a change" v))
      (is (= "class java.lang.String" (str (type v))))))
  (testing "convert to 'BREAKING CHANGE:<msg>' mixed case"
    (let [v (common/format-commit-msg-all "  BreaKing chANge  :   a change  ")]
      (is (= "BREAKING CHANGE: a change" v))
      (is (= "class java.lang.String" (str (type v))))))
  (testing "convert to 'BREAKING CHANGE:<msg>' separated with underscore"
    (let [v (common/format-commit-msg-all "  breaking_change  :   a change  ")]
      (is (= "BREAKING CHANGE: a change" v))
      (is (= "class java.lang.String" (str (type v))))))
  (testing "convert to 'BREAKING CHANGE:<msg>' separated with dash"
    (let [v (common/format-commit-msg-all "  breaking-change  :   a change  ")]
      (is (= "BREAKING CHANGE: a change" v))
      (is (= "class java.lang.String" (str (type v))))))
  (testing "convert to 'BREAKING CHANGE:<msg>' misspeled braking"
    (let [v (common/format-commit-msg-all "  braking change  :   a change  ")]
      (is (= "BREAKING CHANGE: a change" v))
      (is (= "class java.lang.String" (str (type v))))))
  (testing "convert to 'BREAKING CHANGE:<msg>' misspeled braeking"
    (let [v (common/format-commit-msg-all "  braeking change  :   a change  ")]
      (is (= "BREAKING CHANGE: a change" v))
      (is (= "class java.lang.String" (str (type v))))))
  (testing "remove leading/trailing spaces"
    ;; "Line1\n\nLine2" because of regex for "<title>\n\n<body>"
    (let [v (common/format-commit-msg-all "  Line1\nTest\nLine2  ")]
      (is (= "Line1\n\nTest\nLine2" v))
      (is (= "class java.lang.String" (str (type v))))))
  (testing "remove leading/trailing newlines"
    ;; "Line1\n\nLine2" because of regex for "<title>\n\n<body>"
    (let [v (common/format-commit-msg-all "\nLine1\nTest\nLine2\n")]
      (is (= "Line1\n\nTest\nLine2" v))
      (is (= "class java.lang.String" (str (type v)))))))


(deftest format-commit-msg-first-line-test
  (testing "empty string"
    (let [v (common/format-commit-msg-first-line "")]
      (is (= "" v))
      (is (= "class java.lang.String" (str (type v))))))
  (testing "without exclamation mark"
    (let [v (common/format-commit-msg-first-line "    feat  (  client  )    :      add super neat feature   ")]
      (is (= "feat(client): add super neat feature" v))
      (is (= "class java.lang.String" (str (type v))))))
  (testing "with exclamation mark"
    (let [v (common/format-commit-msg-first-line "    feat  (  client  )  !  :      add super neat feature   ")]
      (is (= "feat(client)!: add super neat feature" v))
      (is (= "class java.lang.String" (str (type v)))))))


(def long-commit-msg
 "
   feat  (  client  )  !  :      add super neat feature   
Support new data with addition of super neat feature

Another line
Directly after line

# Comment line

     # Another comment line

Another line

     This line has 5 spaces before, which is ok

This line has 5 spaces after this     

Line with 4 spaces only below
    
Last real line


breaking change: a big change
BREAKING CHANGE: a big change
BreakinG ChangE: a big change

braking change: a big change
braeking change: a big change

breaking   change: a big change
breaking_change: a big change
breaking-change: a big change

breaking change    :    a big change

# Please enter the commit message for your changes. Lines starting
# with '#' will be ignored, and an empty message aborts the commit.
#
# On branch main
# Your branch is up to date with 'origin/main'.
#
# Changes to be committed:
#	modified:   client-side-hooks/src/commit-msg
#


")


(def long-commit-msg-expected
 "feat(client)!: add super neat feature

Support new data with addition of super neat feature

Another line
Directly after line

Another line

     This line has 5 spaces before, which is ok

This line has 5 spaces after this

Line with 4 spaces only below

Last real line

BREAKING CHANGE: a big change
BREAKING CHANGE: a big change
BREAKING CHANGE: a big change

BREAKING CHANGE: a big change
BREAKING CHANGE: a big change

BREAKING CHANGE: a big change
BREAKING CHANGE: a big change
BREAKING CHANGE: a big change

BREAKING CHANGE: a big change")


(deftest format-commit-msg-test
  (testing "nil string"
    (let [v (common/format-commit-msg nil)]
      (is (= "" v))
      (is (= "class java.lang.String" (str (type v))))))
  (testing "empty string"
    (let [v (common/format-commit-msg "")]
      (is (= "" v))
      (is (= "class java.lang.String" (str (type v))))))
  (testing "one line"
    (let [v (common/format-commit-msg "    feat  (  client  )  !  :      add super neat feature   ")]
      (is (= "feat(client)!: add super neat feature" v))
      (is (= "class java.lang.String" (str (type v))))))
  (testing "one line and newline"
    (let [v (common/format-commit-msg "    feat  (  client  )  !  :      add super neat feature   \n")]
      (is (= "feat(client)!: add super neat feature" v))
      (is (= "class java.lang.String" (str (type v))))))
  (testing "one line and multiple newlines"
    (let [v (common/format-commit-msg "    feat  (  client  )  !  :      add super neat feature   \n\n\n")]
      (is (= "feat(client)!: add super neat feature" v))
      (is (= "class java.lang.String" (str (type v))))))
  (testing "one line and comment"
    (let [v (common/format-commit-msg "    feat  (  client  )  !  :      add super neat feature   \n#Comment here")]
      (is (= "feat(client)!: add super neat feature" v))
      (is (= "class java.lang.String" (str (type v))))))
  (testing "one newline then body"
    (let [v (common/format-commit-msg "    feat  (  client  )  !  :      add super neat feature   \nBody starts here")]
      (is (= "feat(client)!: add super neat feature\n\nBody starts here" v))
      (is (= "class java.lang.String" (str (type v))))))
  (testing "two newlines then body"
    (let [v (common/format-commit-msg "    feat  (  client  )  !  :      add super neat feature   \n\nBody starts here")]
      (is (= "feat(client)!: add super neat feature\n\nBody starts here" v))
      (is (= "class java.lang.String" (str (type v))))))
  (testing "three newlines then body"
    (let [v (common/format-commit-msg "    feat  (  client  )  !  :      add super neat feature   \n\n\nBody starts here")]
      (is (= "feat(client)!: add super neat feature\n\nBody starts here" v))
      (is (= "class java.lang.String" (str (type v))))))
  (testing "long commit message"
    (let [v (common/format-commit-msg long-commit-msg)]
      (is (= long-commit-msg-expected v))
      (is (= "class java.lang.String" (str (type v)))))))


(deftest index-matches-test
  (testing "empty collection"
    (let [v (common/index-matches [] #"z")]
      (is (= "class clojure.lang.LazySeq" (str (type v))))
      (is (= 0 (count v)))))
  (testing "no matches"
    (let [v (common/index-matches ["aqq" "bqq" "cqq" "dqq"] #"z")]
      (is (= "class clojure.lang.LazySeq" (str (type v))))
      (is (= 0 (count v)))))
  (testing "1 match with collection count 1"
    (let [v (common/index-matches ["bqq"] #"b")]
      (is (= "class clojure.lang.LazySeq" (str (type v))))
      (is (= 1 (count v)))))
  (testing "1 match with collection count 5"
    (let [v (common/index-matches ["aqq" "bqq" "cqq" "dqq"] #"a")]
      (is (= "class clojure.lang.LazySeq" (str (type v))))
      (is (= 1 (count v)))))
  (testing "4 matches with collection count 7"
    (let [v (common/index-matches ["aqq" "bqq" "acqq" "dqq" "eqq" "faqq" "gaqq"] #"a")]
      (is (= "class clojure.lang.LazySeq" (str (type v))))
      (is (= 4 (count v))))))


(deftest create-validate-commit-msg-err-test
  (testing "reason without locations"
    (let [v (common/create-validate-commit-msg-err "Reason error occurred")]
      (is (= "class clojure.lang.PersistentArrayMap" (str (type v))))
      (is (= "class java.lang.Boolean" (str (type (:success v)))))
      (is (= "class java.lang.String" (str (type (:reason v)))))
      (is (false? (contains? v :locations)))))
  (testing "reason with locations"
    (let [v (common/create-validate-commit-msg-err "Reason error occurred" '(3 7 9))]
      (is (= "class clojure.lang.PersistentArrayMap" (str (type v))))
      (is (= "class java.lang.Boolean" (str (type (:success v)))))
      (is (= "class java.lang.String" (str (type (:reason v)))))
      (is (= 3 (count (:locations v))))
      (is (= 3 (first (:locations v))))
      (is (= 7 (nth (:locations v) 1)))
      (is (= 9 (nth (:locations v) 2))))))


(deftest validate-commit-msg-title-test
  (let [config {:commit-msg {:length {:title-line {:min 12    ;; 'ab(cd): efgh' = 12 chars
                                                   :max 20}
                                      :body-line {:min 2
                                                  :max 10}}}}]
    (testing "commit msg title line has too few characters"
      (let [v (common/validate-commit-msg "ab(cd): efg\n\nAbcdef" config)]
        (is (= "class clojure.lang.PersistentArrayMap" (str (type v))))
        (is (= "class java.lang.Boolean" (str (type (:success v)))))
        (is (false? (:success v)))
        (is (= "class java.lang.String" (str (type (:reason v)))))
        (is (= (str "Commit message title line must be at least " (:min (:title-line (:length (:commit-msg config)))) " characters.") (:reason v)))
        (is (= "class clojure.lang.LazySeq" (str (type (:locations v)))))
        (is (= 1 (count (:locations v))))
        (is (= 0 (first (:locations v))))))
    (testing "commit msg title line has meets minimum characters"
      (let [v (common/validate-commit-msg "ab(cd): efgh\n\nAbcdef" config)]
        (is (= "class clojure.lang.PersistentArrayMap" (str (type v))))
        (is (= "class java.lang.Boolean" (str (type (:success v)))))
        (is (true? (:success v)))))
    (testing "commit msg title line has too many characters"
      (let [v (common/validate-commit-msg "ab(cd): efghijklmnopq\n\nAbcdef" config)]
        (is (= "class clojure.lang.PersistentArrayMap" (str (type v))))
        (is (= "class java.lang.Boolean" (str (type (:success v)))))
        (is (false? (:success v)))
        (is (= "class java.lang.String" (str (type (:reason v)))))
        (is (= (str "Commit message title line must not contain more than " (:max (:title-line (:length (:commit-msg config)))) " characters.") (:reason v)))
        (is (= "class clojure.lang.LazySeq" (str (type (:locations v)))))
        (is (= 1 (count (:locations v))))
        (is (= 0 (first (:locations v))))))
    (testing "commit msg title line has meets maximum characters"
      (let [v (common/validate-commit-msg "ab(cd): efghijklmnop\n\nAbcdef" config)]
        (is (= "class clojure.lang.PersistentArrayMap" (str (type v))))
        (is (= "class java.lang.Boolean" (str (type (:success v)))))
        (is (true? (:success v)))))))


(deftest validate-commit-msg-body-len-test
  (let [config {:commit-msg {:length {:title-line {:min 3
                                                   :max 8}
                                      :body-line {:min 2
                                                  :max 10}}}}]
    (testing "commit msg body is an empty sequence"
      (let [v (common/validate-commit-msg-body-len [] config)]
        (is (nil? v))))
    (testing "commit msg body line has too few characters, for single element"
      (let [v (common/validate-commit-msg-body-len (common/split-lines "L") config)]
        (is (= "class clojure.lang.PersistentArrayMap" (str (type v))))
        (is (= "class java.lang.Boolean" (str (type (:success v)))))
        (is (false? (:success v)))
        (is (= "class java.lang.String" (str (type (:reason v)))))
        (is (= (str "Commit message body line must be at least " (:min (:body-line (:length (:commit-msg config)))) " characters.") (:reason v)))
        (is (= "class clojure.lang.LazySeq" (str (type (:locations v)))))
        (is (= 1 (count (:locations v))))
        (is (= 0 (first (:locations v))))))
    (testing "commit msg body line has too few characters, for multi element"
      (let [v (common/validate-commit-msg-body-len (common/split-lines "L\nHello\nA\nAnother line\nX") config)]
        (is (= "class clojure.lang.PersistentArrayMap" (str (type v))))
        (is (= "class java.lang.Boolean" (str (type (:success v)))))
        (is (false? (:success v)))
        (is (= "class java.lang.String" (str (type (:reason v)))))
        (is (= (str "Commit message body line must be at least " (:min (:body-line (:length (:commit-msg config)))) " characters.") (:reason v)))
        (is (= "class clojure.lang.LazySeq" (str (type (:locations v)))))
        (is (= 3 (count (:locations v))))
        (is (= 0 (first (:locations v))))
        (is (= 2 (nth (:locations v) 1)))
        (is (= 4 (nth (:locations v) 2)))))
    (testing "commit msg body line has meets minimum characters, for single element"
      (let [v (common/validate-commit-msg-body-len (common/split-lines "Li") config)]
        (is (nil? v))))
    (testing "commit msg body line has meets minimum characters, for multi element"
      (let [v (common/validate-commit-msg-body-len (common/split-lines "Li\nAb\nAbcdef\nAb\nAb") config)]
        (is (nil? v))))
    (testing "commit msg body line has too many characters, for single element"
      (let [v (common/validate-commit-msg-body-len (common/split-lines "Body abcdef") config)]
        (is (= "class clojure.lang.PersistentArrayMap" (str (type v))))
        (is (= "class java.lang.Boolean" (str (type (:success v)))))
        (is (false? (:success v)))
        (is (= "class java.lang.String" (str (type (:reason v)))))
        (is (= (str "Commit message body line must not contain more than " (:max (:body-line (:length (:commit-msg config)))) " characters.") (:reason v)))
        (is (= "class clojure.lang.LazySeq" (str (type (:locations v)))))
        (is (= 1 (count (:locations v))))
        (is (= 0 (first (:locations v))))))
    (testing "commit msg body line has too many characters, for multi element"
      (let [v (common/validate-commit-msg-body-len (common/split-lines "Body abcdef\nAbcd\nBody abcdef\nBody abcdef\nAbcd\nBody abcdef") config)]
        (is (= "class clojure.lang.PersistentArrayMap" (str (type v))))
        (is (= "class java.lang.Boolean" (str (type (:success v)))))
        (is (false? (:success v)))
        (is (= "class java.lang.String" (str (type (:reason v)))))
        (is (= (str "Commit message body line must not contain more than " (:max (:body-line (:length (:commit-msg config)))) " characters.") (:reason v)))
        (is (= "class clojure.lang.LazySeq" (str (type (:locations v)))))
        (is (= 4 (count (:locations v))))
        (is (= 0 (first (:locations v))))
        (is (= 2 (nth (:locations v) 1)))
        (is (= 3 (nth (:locations v) 2)))
        (is (= 5 (nth (:locations v) 3)))))
    (testing "commit msg body line has meets maximum characters, for single element"
      (let [v (common/validate-commit-msg-body-len (common/split-lines "Body abcde") config)]
        (is (nil? v))))
    (testing "commit msg body line has meets maximum characters, for multi element"
      (let [v (common/validate-commit-msg-body-len (common/split-lines "Body abcde\nAb\nBody abcde\nAb\nBody abcde") config)]
        (is (nil? v))))))


(deftest add-string-if-key-empty-test
  (testing "text empty, and collection value not empty"
    (let [v (common/add-string-if-key-empty "" "Added text." :data {:data "non empty"})]
      (is (= "class java.lang.String" (str (type v))))
      (is (= "" v))))
  (testing "text empty, and collection value is not defined so would be empty"
    (let [v (common/add-string-if-key-empty "" "Added text." :other {:data "non empty"})]
      (is (= "class java.lang.String" (str (type v))))
      (is (= "Added text." v))))
  (testing "text empty, and collection value is nil so is empty"
    (let [v (common/add-string-if-key-empty "" "Added text." :data {:data nil})]
      (is (= "class java.lang.String" (str (type v))))
      (is (= "Added text." v))))
  (testing "text not empty, and collection value not empty"
    (let [v (common/add-string-if-key-empty "Original text." "Added text." :data {:data "non empty"})]
      (is (= "class java.lang.String" (str (type v))))
      (is (= "Original text." v))))
  (testing "text not empty, and collection value is not defined so would be empty"
    (let [v (common/add-string-if-key-empty "Original text." "Added text." :other {:data "non empty"})]
      (is (= "class java.lang.String" (str (type v))))
      (is (= "Original text.  Added text." v))))
  (testing "text not empty, and collection value is nil so is empty"
    (let [v (common/add-string-if-key-empty "Original text." "Added text." :data {:data nil})]
      (is (= "class java.lang.String" (str (type v))))
      (is (= "Original text.  Added text." v)))))


(deftest validate-commit-msg-title-scope-type
  (testing "invalid - no type"
    (let [v (common/validate-commit-msg-title-scope-type "(proj): add cool new feature")]
      (is (= "class clojure.lang.PersistentArrayMap" (str (type v))))
      (is (= "class java.lang.Boolean" (str (type (:success v)))))
      (is (false? (:success v)))
      (is (= "class java.lang.String" (str (type (:reason v)))))
      (is (= (:reason v) "Bad form on title.  Could not identify type, scope, or description."))
      (is (false? (contains? v :type)))
      (is (false? (contains? v :scope)))
      (is (false? (contains? v :breaking)))
      (is (false? (contains? v :title-descr)))
      (is (= "class clojure.lang.LazySeq" (str (type (:locations v)))))
      (is (= 1 (count (:locations v))))
      (is (= 0 (first (:locations v))))))
  (testing "invalid - no scope"
    (let [v (common/validate-commit-msg-title-scope-type "feat(): add cool new feature")]
      (is (= "class clojure.lang.PersistentArrayMap" (str (type v))))
      (is (= "class java.lang.Boolean" (str (type (:success v)))))
      (is (false? (:success v)))
      (is (= "class java.lang.String" (str (type (:reason v)))))
      (is (= (:reason v) "Bad form on title.  Could not identify type, scope, or description."))
      (is (false? (contains? v :type)))
      (is (false? (contains? v :scope)))
      (is (false? (contains? v :breaking)))
      (is (false? (contains? v :title-descr)))
      (is (= "class clojure.lang.LazySeq" (str (type (:locations v)))))
      (is (= 1 (count (:locations v))))
      (is (= 0 (first (:locations v))))))
  (testing "invalid - no description"
    (let [v (common/validate-commit-msg-title-scope-type "feat(proj):")]
      (is (= "class clojure.lang.PersistentArrayMap" (str (type v))))
      (is (= "class java.lang.Boolean" (str (type (:success v)))))
      (is (false? (:success v)))
      (is (= "class java.lang.String" (str (type (:reason v)))))
      (is (= (:reason v) "Bad form on title.  Could not identify description."))
      (is (false? (contains? v :type)))
      (is (false? (contains? v :scope)))
      (is (false? (contains? v :breaking)))
      (is (false? (contains? v :title-descr)))
      (is (= "class clojure.lang.LazySeq" (str (type (:locations v)))))
      (is (= 1 (count (:locations v))))
      (is (= 0 (first (:locations v))))))
  (testing "good without exclamation mark"
    (let [v (common/validate-commit-msg-title-scope-type "feat(proj): add cool new feature")]
      (is (= "class clojure.lang.PersistentArrayMap" (str (type v))))
      (is (= "class java.lang.Boolean" (str (type (:success v)))))
      (is (true? (:success v)))
      (is (= "class java.lang.String" (str (type (:type v)))))
      (is (= (:type v) "feat"))
      (is (= "class java.lang.String" (str (type (:scope v)))))
      (is (= (:scope v) "proj"))
      (is (= "class java.lang.Boolean" (str (type (:breaking v)))))
      (is (false? (:breaking v)))
      (is (= "class java.lang.String" (str (type (:title-descr v)))))
      (is (= (:title-descr v) "add cool new feature"))))
  (testing "good with exclamation mark"
    (let [v (common/validate-commit-msg-title-scope-type "feat(proj)!: add cool new feature")]
      (is (= "class clojure.lang.PersistentArrayMap" (str (type v))))
      (is (= "class java.lang.Boolean" (str (type (:success v)))))
      (is (true? (:success v)))
      (is (= "class java.lang.String" (str (type (:type v)))))
      (is (= (:type v) "feat"))
      (is (= "class java.lang.String" (str (type (:scope v)))))
      (is (= (:scope v) "proj"))
      (is (= "class java.lang.Boolean" (str (type (:breaking v)))))
      (is (true? (:breaking v)))
      (is (= "class java.lang.String" (str (type (:title-descr v)))))
      (is (= (:title-descr v) "add cool new feature")))))



(deftest validate-commit-msg-test
  (let [config {:commit-msg {:length {:title-line {:min 12        ;; 'ab(cd): efgh' = 12 chars
                                                   :max 20}
                                      :body-line {:min 2
                                                  :max 10}}}}]
    ;; test commit-msg overall: isn't empty (nil or empty string)
    (testing "commit msg is nil"
      (let [v (common/validate-commit-msg nil config)]
        (is (= "class clojure.lang.PersistentArrayMap" (str (type v))))
        (is (= "class java.lang.Boolean" (str (type (:success v)))))
        (is (false? (:success v)))
        (is (= "class java.lang.String" (str (type (:reason v)))))
        (is (= "Commit message cannot be empty." (:reason v)))
        (is (false? (contains? v :locations)))))
    (testing "commit msg is empty string"
      (let [v (common/validate-commit-msg "" config)]
        (is (= "class clojure.lang.PersistentArrayMap" (str (type v))))
        (is (= "class java.lang.Boolean" (str (type (:success v)))))
        (is (false? (:success v)))
        (is (= "class java.lang.String" (str (type (:reason v)))))
        (is (= "Commit message cannot be empty." (:reason v)))
        (is (false? (contains? v :locations)))))
    ;; test commit-msg overall: doesn't contain tab characters
    (testing "commit msg contains tab on one line"
      (let [v (common/validate-commit-msg "ab(cd): efgh\n\ntabhere	x\nLine 3 ok" config)]
        (is (= "class clojure.lang.PersistentArrayMap" (str (type v))))
        (is (= "class java.lang.Boolean" (str (type (:success v)))))
        (is (false? (:success v)))
        (is (= "class java.lang.String" (str (type (:reason v)))))
        (is (= "Commit message cannot contain tab characters." (:reason v)))
        (is (= "class clojure.lang.LazySeq" (str (type (:locations v)))))
        (is (= 1 (count (:locations v))))
        (is (= 2 (first (:locations v))))))
    (testing "commit msg contains tab on three lines"
      (let [v (common/validate-commit-msg "ab(cd): efgh\n\ntabhere	x\nLine 3 ok\ntabhere	x\nLine 5 ok\ntabhere	x" config)]
        (is (= "class clojure.lang.PersistentArrayMap" (str (type v))))
        (is (= "class java.lang.Boolean" (str (type (:success v)))))
        (is (false? (:success v)))
        (is (= "class java.lang.String" (str (type (:reason v)))))
        (is (= "Commit message cannot contain tab characters." (:reason v)))
        (is (= "class clojure.lang.LazySeq" (str (type (:locations v)))))
        (is (= 3 (count (:locations v))))
        (is (= 2 (first (:locations v))))
        (is (= 4 (nth (:locations v) 1)))
        (is (= 6 (nth (:locations v) 2)))))
    ;; test commit-msg title: min/max characters
    (testing "commit msg title line has too few characters"
      (let [v (common/validate-commit-msg "ab(cd): efg\n\nAbcdef" config)]
        (is (= "class clojure.lang.PersistentArrayMap" (str (type v))))
        (is (= "class java.lang.Boolean" (str (type (:success v)))))
        (is (false? (:success v)))
        (is (= "class java.lang.String" (str (type (:reason v)))))
        (is (= (str "Commit message title line must be at least " (:min (:title-line (:length (:commit-msg config)))) " characters.") (:reason v)))
        (is (= "class clojure.lang.LazySeq" (str (type (:locations v)))))
        (is (= 1 (count (:locations v))))
        (is (= 0 (first (:locations v))))))
    (testing "commit msg title line meets minimum characters" ;;todo-here
      (let [v (common/validate-commit-msg "ab(cd): efgh\n\nAbcdef" config)]
        (is (= "class clojure.lang.PersistentArrayMap" (str (type v))))
        (is (= "class java.lang.Boolean" (str (type (:success v)))))
        (is (true? (:success v)))))
    (testing "commit msg title line has too many characters"
      (let [v (common/validate-commit-msg "ab(cd): efghijklmnopq\n\nAbcdef" config)]
        (is (= "class clojure.lang.PersistentArrayMap" (str (type v))))
        (is (= "class java.lang.Boolean" (str (type (:success v)))))
        (is (false? (:success v)))
        (is (= "class java.lang.String" (str (type (:reason v)))))
        (is (= (str "Commit message title line must not contain more than " (:max (:title-line (:length (:commit-msg config)))) " characters.") (:reason v)))
        (is (= "class clojure.lang.LazySeq" (str (type (:locations v)))))
        (is (= 1 (count (:locations v))))
        (is (= 0 (first (:locations v))))))
    (testing "commit msg title line meets maximum characters"
      (let [v (common/validate-commit-msg "ab(cd): efghijklmnop\n\nAbcdef" config)]
        (is (= "class clojure.lang.PersistentArrayMap" (str (type v))))
        (is (= "class java.lang.Boolean" (str (type (:success v)))))
        (is (true? (:success v)))))
    ;; test commit-msg title/body: title only, no body
    (testing "commit msg consists of title line only without newline (e.g., no body; e.g. body is empty)"
      (let [v (common/validate-commit-msg "ab(cd): efgh" config)]
        (is (= "class clojure.lang.PersistentArrayMap" (str (type v))))
        (is (= "class java.lang.Boolean" (str (type (:success v)))))
        (is (true? (:success v)))))
    (testing "commit msg consists of title line only with newline (e.g., no body; e.g. body is empty)"
      (let [v (common/validate-commit-msg "ab(cd): efgh\n" config)]
        (is (= "class clojure.lang.PersistentArrayMap" (str (type v))))
        (is (= "class java.lang.Boolean" (str (type (:success v)))))
        (is (true? (:success v)))))
    ;; test commit-msg body: min/max characters
    (testing "commit msg body line has too few characters, for single element"
      (let [v (common/validate-commit-msg "ab(cd): efgh\n\nA" config)]
        (is (= "class clojure.lang.PersistentArrayMap" (str (type v))))
        (is (= "class java.lang.Boolean" (str (type (:success v)))))
        (is (false? (:success v)))
        (is (= "class java.lang.String" (str (type (:reason v)))))
        (is (= (str "Commit message body line must be at least " (:min (:body-line (:length (:commit-msg config)))) " characters.") (:reason v)))
        (is (= "class clojure.lang.LazySeq" (str (type (:locations v)))))
        (is (= 1 (count (:locations v))))
        (is (= 0 (first (:locations v))))))
    (testing "commit msg body line has too few characters, for multi element"
      (let [v (common/validate-commit-msg "ab(cd): efgh\n\nA\nAbcd\nA\nAbc\nA" config)]
        (is (= "class clojure.lang.PersistentArrayMap" (str (type v))))
        (is (= "class java.lang.Boolean" (str (type (:success v)))))
        (is (false? (:success v)))
        (is (= "class java.lang.String" (str (type (:reason v)))))
        (is (= (str "Commit message body line must be at least " (:min (:body-line (:length (:commit-msg config)))) " characters.") (:reason v)))
        (is (= "class clojure.lang.LazySeq" (str (type (:locations v)))))
        (is (= 3 (count (:locations v))))
        (is (= 0 (first (:locations v))))
        (is (= 2 (nth (:locations v) 1)))
        (is (= 4 (nth (:locations v) 2)))))
    (testing "commit msg body line has meets minimum characters, for single element"
      (let [v (common/validate-commit-msg "ab(cd): efgh\n\nAb" config)]
        (is (= "class clojure.lang.PersistentArrayMap" (str (type v))))
        (is (= "class java.lang.Boolean" (str (type (:success v)))))
        (is (true? (:success v)))))
    (testing "commit msg body line has meets minimum characters, for multi element"
      (let [v (common/validate-commit-msg "ab(cd): efgh\nAb\nAbcdef\nAb\nAbcd" config)]
        (is (= "class clojure.lang.PersistentArrayMap" (str (type v))))
        (is (= "class java.lang.Boolean" (str (type (:success v)))))
        (is (true? (:success v)))))
    (testing "commit msg body line has too many characters, for single element"
      (let [v (common/validate-commit-msg "ab(cd): efgh\n\nAbcdefghijk" config)]
        (is (= "class clojure.lang.PersistentArrayMap" (str (type v))))
        (is (= "class java.lang.Boolean" (str (type (:success v)))))
        (is (false? (:success v)))
        (is (= "class java.lang.String" (str (type (:reason v)))))
        (is (= (str "Commit message body line must not contain more than " (:max (:body-line (:length (:commit-msg config)))) " characters.") (:reason v)))
        (is (= "class clojure.lang.LazySeq" (str (type (:locations v)))))
        (is (= 1 (count (:locations v))))
        (is (= 0 (first (:locations v))))))
    (testing "commit msg body line has too many characters, for multi element"
      (let [v (common/validate-commit-msg "ab(cd): efgh\n\nAbcdefghijk\nAbc\nAbcdefghijk\nAbcdefghijklmnop\nAbc" config)]
        (is (= "class clojure.lang.PersistentArrayMap" (str (type v))))
        (is (= "class java.lang.Boolean" (str (type (:success v)))))
        (is (false? (:success v)))
        (is (= "class java.lang.String" (str (type (:reason v)))))
        (is (= (str "Commit message body line must not contain more than " (:max (:body-line (:length (:commit-msg config)))) " characters.") (:reason v)))
        (is (= "class clojure.lang.LazySeq" (str (type (:locations v)))))
        (is (= 3 (count (:locations v))))
        (is (= 0 (first (:locations v))))
        (is (= 2 (nth (:locations v) 1)))
        (is (= 3 (nth (:locations v) 2)))))
    (testing "commit msg body line has meets maximum characters, for single element"
      (let [v (common/validate-commit-msg "ab(cd): efgh\n\nAbcdefghij" config)]
        (is (= "class clojure.lang.PersistentArrayMap" (str (type v))))
        (is (= "class java.lang.Boolean" (str (type (:success v)))))
        (is (true? (:success v)))))
    (testing "commit msg body line has meets maximum characters, for multi element"
      (let [v (common/validate-commit-msg "ab(cd): efgh\n\nAbcdefghij\nAbcdef\nAbcdefghij\nAbcd" config)]
        (is (= "class clojure.lang.PersistentArrayMap" (str (type v))))
        (is (= "class java.lang.Boolean" (str (type (:success v)))))
        (is (true? (:success v)))))
    ;; test type/scope,!,descr: format, length, retrieval
    (testing "invalid - no type"
      (let [v (common/validate-commit-msg-title-scope-type "(proj): add cool new feature")]
        (is (= "class clojure.lang.PersistentArrayMap" (str (type v))))
        (is (= "class java.lang.Boolean" (str (type (:success v)))))
        (is (false? (:success v)))
        (is (= "class java.lang.String" (str (type (:reason v)))))
        (is (= (:reason v) "Bad form on title.  Could not identify type, scope, or description."))
        (is (false? (contains? v :type)))
        (is (false? (contains? v :scope)))
        (is (false? (contains? v :breaking)))
        (is (false? (contains? v :title-descr)))
        (is (= "class clojure.lang.LazySeq" (str (type (:locations v)))))
        (is (= 1 (count (:locations v))))
        (is (= 0 (first (:locations v))))))
    (testing "invalid - no scope"
      (let [v (common/validate-commit-msg-title-scope-type "feat(): add cool new feature")]
        (is (= "class clojure.lang.PersistentArrayMap" (str (type v))))
        (is (= "class java.lang.Boolean" (str (type (:success v)))))
        (is (false? (:success v)))
        (is (= "class java.lang.String" (str (type (:reason v)))))
        (is (= (:reason v) "Bad form on title.  Could not identify type, scope, or description."))
        (is (false? (contains? v :type)))
        (is (false? (contains? v :scope)))
        (is (false? (contains? v :breaking)))
        (is (false? (contains? v :title-descr)))
        (is (= "class clojure.lang.LazySeq" (str (type (:locations v)))))
        (is (= 1 (count (:locations v))))
        (is (= 0 (first (:locations v))))))
    (testing "invalid - no description"
      (let [v (common/validate-commit-msg-title-scope-type "ab(cd):")]
        (is (= "class clojure.lang.PersistentArrayMap" (str (type v))))
        (is (= "class java.lang.Boolean" (str (type (:success v)))))
        (is (false? (:success v)))
        (is (= "class java.lang.String" (str (type (:reason v)))))
        (is (= (:reason v) "Bad form on title.  Could not identify description."))
        (is (false? (contains? v :type)))
        (is (false? (contains? v :scope)))
        (is (false? (contains? v :breaking)))
        (is (false? (contains? v :title-descr)))
        (is (= "class clojure.lang.LazySeq" (str (type (:locations v)))))
        (is (= 1 (count (:locations v))))
        (is (= 0 (first (:locations v))))))
    (testing "good without exclamation mark"
      (let [v (common/validate-commit-msg-title-scope-type "feat(proj): add cool new feature")]
        (is (= "class clojure.lang.PersistentArrayMap" (str (type v))))
        (is (= "class java.lang.Boolean" (str (type (:success v)))))
        (is (true? (:success v)))
        (is (= "class java.lang.String" (str (type (:type v)))))
        (is (= (:type v) "feat"))
        (is (= "class java.lang.String" (str (type (:scope v)))))
        (is (= (:scope v) "proj"))
        (is (= "class java.lang.Boolean" (str (type (:breaking v)))))
        (is (false? (:breaking v)))
        (is (= "class java.lang.String" (str (type (:title-descr v)))))
        (is (= (:title-descr v) "add cool new feature"))))
    (testing "good with exclamation mark"
      (let [v (common/validate-commit-msg-title-scope-type "feat(proj)!: add cool new feature")]
        (is (= "class clojure.lang.PersistentArrayMap" (str (type v))))
        (is (= "class java.lang.Boolean" (str (type (:success v)))))
        (is (true? (:success v)))
        (is (= "class java.lang.String" (str (type (:type v)))))
        (is (= (:type v) "feat"))
        (is (= "class java.lang.String" (str (type (:scope v)))))
        (is (= (:scope v) "proj"))
        (is (= "class java.lang.Boolean" (str (type (:breaking v)))))
        (is (true? (:breaking v)))
        (is (= "class java.lang.String" (str (type (:title-descr v)))))
        (is (= (:title-descr v) "add cool new feature"))))))
    ;; todo add tests for new checks
    
