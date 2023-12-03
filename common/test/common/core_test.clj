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
            [babashka.classpath :as cp]))

(cp/add-classpath "./")
(require '[common.core :as common])


(deftest config-enabled?-test
  (testing "enabled"
    (let [v (common/config-enabled? {:commit-msg-enforcement {:enabled true}})]
      (is (true? v))
      (is (= "class java.lang.Boolean" (str (type v))))))
  (testing "disabled"
    (let [v (common/config-enabled? {:commit-msg-enforcement {:enabled false}})]
      (is (false? v))
      (is (= "class java.lang.Boolean" (str (type v)))))))


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