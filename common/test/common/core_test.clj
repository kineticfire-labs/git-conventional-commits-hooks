#!/usr/bin/env bb

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


(deftest add-two-test
  (testing "test with positive ints"
    (is (= 4 (common/add-two 2)))
    (is (= 3 (common/add-two 1))))
  (testing "test with negative ints"
    (is (= -2 (common/add-two -4)))
    (is (= 0 (common/add-two -2)))))