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



(ns client-side-hooks.commit-msg
  (:require [clojure.string    :as str]
            [babashka.cli      :as cli]
            [babashka.process  :refer [shell process exec]]
            [clojure.java.io   :as io]
            [cheshire.core     :as json]
            [common.core       :as common]))



;; version updated by CI pipeline
(def ^:const version "latest")

;; todo changed path for testing
(def ^:const config-file "../resources/project-small.def.json")

(def ^:const title "by local commit-msg hook.")



;; todo for testing tests
(defn add-up
  [a b]
  (+ a b))
;;(println "add-up 1 2" (add-up 1 2))

;; todo for testing inclusion of 'common' project
;;(defn current-add-two
;;  [x]
;;  (common/add-two x))
;;(println "current-add-two 5" (current-add-two 5))








;;todo
;;
;; - read git editmsg from file and keep as string
;;    - err w/ exit 1 if can't read file
;; - reformat commit message (common)
;; - write commit message
;;
;; - validate commit message (common)
;;    - check if empty, then exit 1
;;    - convert editmsg string to vector of strings split on newline
;;    - group
;;       - check if contains tabs, then get line num and exit 1
;;       - check for (1) one line only or (2) line, newline, rest
;;       - check for min/max chars (first line min/max diff from others)
;;    - subject
;;       - x
;;    - body
;;       - x
;;
;; - display status and exit
;;




;; - parse JSON config file
;;    - exit 1 if
;;      - file doesn't exist or can't read file
;;      - JSON file fails to parse
;; - validate config file
;;    - exit 0 if
;;      - disabled
;;    - exit 1 if
;;      - config file invalid
;; - retrieve git edit message file
;;    - exit 1 if
;;      - file doesn't exist or can't read file
;;
(defn ^:impure -main
  [& args]
  (if (= (count args) 1)
    (let [config-response (common/parse-json-file config-file)]
      (if (:success config-response)
        (let [config-validation-response (common/validate-config (:config config-response))]
          (if (:success config-validation-response)
            (if (common/config-enabled? (:config config-response))
              (let [commit-msg-response (common/get-commit-msg-from-file (first args))]
                (if (:success commit-msg-response)
                  (println (common/format-commit-msg (:result commit-msg-response)))
                  (common/handle-err-exit title (str "Error reading git commit edit message file '" (first args) "'. " (:reason commit-msg-response)))))
              (common/handle-warn-proceed title "Commit message enforcement disabled."))
            (common/handle-err-exit title (str "Error in config file '" config-file "'. " (:reason config-validation-response)))))
        (common/handle-err-exit title (:reason config-response))))
    (common/handle-err-exit title "Exactly one argument required.  Usage:  commit-msg <path to git edit message>")))


(when (= *file* (System/getProperty "babashka.file"))
  (apply -main *command-line-args*))
