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
(defn current-add-two
  [x]
  (common/add-two x))
;;(println "current-add-two 5" (current-add-two 5))



(comment (defn ^:impure process-commit-attempt
  [edit-commit-msg-file]
  (let [config-response (get-parse-config-file)]
    (if (:success config-response)
      (let [config (:result config-response)]
        (if (contains? config :enabled) ;; early check if config is enabled prior to validating entire 'config' structure
          (if (:enabled config)
            (let [config-validation-response (validate-config config)]
              (if (:success config-validation-response)
                (println "config is valid")
                (handle-err-exit (str "Error in config file '" config-file "'. " (:reason config-validation-response)))))
            (handle-warn-proceed (str "This commit-msg script is disabled, per the config file '" config-file "'.")))
          (handle-err-exit (str "The config file '" config-file "' does not contain the key 'enabled'."))))
      (handle-err-exit (:reason config-response))))))


;; parse-config-file
;; validate-config-file
;; is-enabled
(defn ^:impure process-commit-attempt 
  [edit-commit-msg-file]
  (println "got commit msg file of " edit-commit-msg-file))




;;todo
;;
;; - get path of git editmsg from command line arg
;;    - err w/ exit 1 if not 1 arg
;;
;; - parse config file json into a map
;;    - err w/ exit 1 if can't read file
;;    - err w/ exit 1 if can't parse json
;;
;; - validate config file (common)
;;    - exit 1 if invalid
;;
;; - warning and proceeed (exit 0) if enabled=false
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


;; parse-config-file
;; validate-config-file
;; is-enabled

;; get edit message: (process-commit-attempt (first args))

(defn ^:impure -main
  [& args]
  (if (= (count args) 1)
    (let [config-response (common/parse-json-file config-file)]
      (if (:success config-response)
        (let [config-validation-response (common/validate-config (:config config-response))]
          (if (:success config-validation-response)
            (println "config valid 2")
            (common/handle-err-exit title (str "Error in config file '" config-file "'. " (:reason config-validation-response)))))
        (common/handle-err-exit title (:reason config-response))))
    (common/handle-err-exit title "Exactly one argument required.")))


(when (= *file* (System/getProperty "babashka.file"))
  (apply -main *command-line-args*))


;; ------------------------------------------------------------------
;; ------------------------------------------------------------------
;; ------------------------------------------------------------------


(defn get-commit-msg
  [filename]
  (slurp filename))


;;(handle-err-exit "A big error occured." ["feat(client): added new feature" "A really big commit message" "would span multiple lines" "much line this one does"] 2)


;;(let [commit-msg-cfg "commit-msg.cfg.json"])
;;(".git/COMMIT_EDITMSG")


