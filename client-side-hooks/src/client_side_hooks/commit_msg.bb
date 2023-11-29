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
            [cheshire.core     :as json]))



;; version updated by CI pipeline
(def ^:const version "latest")

;; todo changed path for testing
(def ^:const config-file "../../commit-msg.cfg.json")

(def ^:const shell-color-red "\\e[1m\\e[31m")

(def ^:const shell-color-blue "\\e[34m")

(def ^:const shell-color-white "\\e[0m\\e[1m")

(def ^:const shell-color-reset "\\033[0m\\e[0m")


(defn add-up
  [a b]
  (+ a b))

(println "add-up 1 2" (add-up 1 2))

(defn do-on-success
  [fn data]
  (if (:success data)
    (fn data)
    data))


(defn ^:impure run-shell-command
  "Runs commands in 'lines', as either a string or vector of strings, by using 'shell'."
  [lines]
  (if (= (.getSimpleName (type lines)) "String")
    (run-shell-command [lines])
    (dorun (map shell lines))))


(defn apply-display-with-shell
  "Applies 'echo -e' to each line in 'lines', which supports display to the terminal with color coding, and returns the result.  If argument 'lines' is a string, then returns a string; if 'lines' is a vector of strings, then returns a vector of strings."
  [lines]
  (if (= (.getSimpleName (type lines)) "String")
    (str "echo -e " lines)
    (map #(str "echo -e " %) lines)))


(defn generate-shell-newline-characters
  "Generates newline characters understood by the terminal and returns the string result.  Displays one newline without arguments or int 'num' newlines."
  ([]
  (generate-shell-newline-characters 1))
  ([num]
  (vec (repeat num "\n"))))


(defn generate-commit-msg-offending-line-header
  "Generates a header that indicates an offending line that was in error, if 'line-num' is integer 0 or greater; 'line-num' is indexed starting at 0.  Appends the header line to the vector of strings 'lines' and returns the result or, if no header should be generated, returns 'lines' unchanged."
  [lines line-num]
  (if (< line-num 0)
    lines
    (conj lines (str "\"   (offending line # " (inc line-num) " in red) **************\""))))


(defn generate-commit-msg-offending-line-msg-highlight
  "Adds shell color-code formatting for an offending line identified by integer 'line-num' in the vector of strings 'lines'.  Argument 'line-num' is indexed starting at 0.  If 'line-num' is negative, then 'lines' is returned unchanged."
  [lines line-num]
  (if (< line-num 0)
    lines
    (assoc lines line-num (str shell-color-red (nth lines line-num) shell-color-reset))))


(defn generate-commit-msg
  "Generates a formatted commit message, 'msg', with optional call-out to the offending line if the optional integer 'line-num' is non-negative; 'line-num' is indexed starting at 0.  Returns the result as a vector of strings, formatted for shell output with color-coding."
  ([msg]
   (generate-commit-msg msg -1))
  ([msg line-num]
   (let [start-lines-top
         [(str "\"" shell-color-blue "**********************************************\"")
          "\"BEGIN - COMMIT MESSAGE ***********************\""]
         start-line-end
         (str "\"**********************************************" shell-color-reset"\"")
         end-lines
         [(str "\"" shell-color-blue "**********************************************\"")
          "\"END - COMMIT MESSAGE *************************\""
          (str "\"**********************************************" shell-color-reset "\"")]]
     (apply-display-with-shell 
      (into (into (conj (generate-commit-msg-offending-line-header start-lines-top line-num) start-line-end) (generate-commit-msg-offending-line-msg-highlight msg line-num)) end-lines)))))


(defn generate-commit-warn-msg
  "Generates and returns as a string a warning message including the string 'warn-msg', formatting the string for shell output with color-coding."
  [warn-msg]
  (apply-display-with-shell (str "\"" shell-color-blue "WARNING (local): " warn-msg "  Commit proceeding." shell-color-reset "\"")))


(defn generate-commit-err-msg
  "Generates and returns as a vector of strings an error message including the string 'err-msg', formatting the string for shell output with color-coding."
  [err-msg]
  (apply-display-with-shell
   [(str "\"" shell-color-red "[COMMIT REJECTED local]\"")
    (str "\"" shell-color-white "Commit failed: " err-msg "\"")]))


(defn ^:impure exit
  [value]
  (System/exit value))


(defn ^:impure handle-err-exit
  "Generates and displays to the shell an error message, including the string 'err-msg', using color-coding from the shell.  Optionally accepts vector of strings 'commit-msg' which display the original commit message; and optionally accepts the integer 'line-num', indexed at 0, which displays a message about the offending line and highlights it in the commit message.  Exits with return code 1."
  ([err-msg]
   (run-shell-command (generate-commit-err-msg err-msg))
   (exit 1))
  ([err-msg commit-msg]
   (run-shell-command (generate-commit-err-msg err-msg))
   (run-shell-command (generate-commit-msg commit-msg))
   (exit 1))
  ([err-msg commit-msg line-num]
   (run-shell-command (generate-commit-err-msg err-msg))
   (run-shell-command (generate-commit-msg commit-msg line-num))
   (exit 1)))


(defn ^:impure handle-warn-proceed
  "Generates and displays to the terminal a warning message, including the string 'warn-msg', using color-coding from the shell."
  [warn-msg]
  (run-shell-command (generate-commit-warn-msg warn-msg)))


(defn ^:impure get-parse-config-file
  "Reads and parses the JSON config file, global constant 'config-file, and returns a map result.  If successful, ':success' is 'true' and 'result' contains the JSON config as a map.  Else ':success' is 'false' and ':reason' describes the failure."
  [] 
  (let [
        response {:success false}
        result (try 
                 (json/parse-stream-strict (clojure.java.io/reader config-file) true)
                 (catch java.io.FileNotFoundException e
                   (str "Config file '"config-file"' not found. " (.getMessage e)))
                 (catch java.io.IOException e
                   ;; Babashka can't find com.fasterxml.jackson.core.JsonParseException, which is thrown for a JSON parse exception.                   
                   ;;   To differentiate the JsonParseException from a java.io.IOException, attempt to 'getMessage' on the exception.
                   (try
                     (.getMessage e)
                     (str "IO exception when reading config file '"config-file"', but the file was found. " (.getMessage e))
                     (catch clojure.lang.ExceptionInfo ei
                       (str "JSON parse error when reading config file '" config-file "'.")))))]
    (if (= (compare (str (type result)) "class clojure.lang.PersistentArrayMap") 0)
      (assoc (assoc response :result result) :success true)
      (assoc response :reason result))))


(defn string-to-vector
  "Converts and returns the string 'lines' to a vector split on newlines.  A wrapper around 'clojure.string/split-lines'."
  [line]
  (str/split-lines line))


(defn is-string-min-char-compliant?
  "Returns 'true' if 'line' has 'min-chars' characters or more and 'false' otherwise."
  [line min-chars]
  (if (>= (count line) min-chars)
    true
    false))


(defn is-string-max-char-compliant?
  "Returns 'true' if string 'line' has 'max-chars' characters or fewer and 'false' otherwise."
  [line max-chars]
  (if (<= (count line) max-chars)
    true
    false))


(defn validate-config-fail
  "Returns a map with key ':success' with value boolean 'false' and ':reason' set to string 'msg'.  If map 'data' is given, then associates the map values into 'data'."
  ([msg]
   {:success false :reason msg})
  ([msg data]
  (-> data
      (assoc :success false)
      (assoc :reason msg))))


(defn validate-config-length
  "Validates the min and max length fields in map 'data'.  Returns map 'data' with key ':success' set to boolean 'true' oif valid or boolean 'false' and ':reason' set to a post-fix string message."
  [data]
  (if (pos-int? (get-in data [:config :length :titleLine :min]))
    (if (pos-int? (get-in data [:config :length :titleLine :max]))
      (if (>= (get-in data [:config :length :titleLine :max]) (get-in data [:config :length :titleLine :min]))
        (if (pos-int? (get-in data [:config :length :bodyLine :min]))
          (if (pos-int? (get-in data [:config :length :bodyLine :max]))
            (if (>= (get-in data [:config :length :bodyLine :max]) (get-in data[:config :length :bodyLine :min]))
              data
              (validate-config-fail "Maximum length of body line (length.bodyLine.max) must be equal to or greater than minimum length of body line (length.bodyLine.min)." data))
            (validate-config-fail "Maximum length of body line (length.bodyLine.max) must be a positive integer." data))
          (validate-config-fail "Minimum length of body line (length.bodyLine.min) must be a positive integer." data))
        (validate-config-fail "Maximum length of title line (length.titleLine.max) must be equal to or greater than minimum length of title line (length.titleLine.min)." data))
      (validate-config-fail "Maximum length of title line (length.titleLine.max) must be a positive integer." data))
    (validate-config-fail "Minimum length of title line (length.titleLine.min) must be a positive integer." data)))


(defn validate-config-param-string
  "Returns boolean 'true' if the value at vector 'key-path' in map 'data' is a string and 'false' otherwise."
  [data key-path required]
  (if (or required (get-in data key-path))
    (string? (get-in data key-path))
    true))


(defn validate-config-param-array
  "Returns boolean 'true' if for all elements in map 'data' at vector 'key-path' the application of 'fn' to those elements is 'true' and if 'required' is 'true' or if that location is set; 'false' otherwise'."
  [data key-path required fn]
  (if (or required (get-in data key-path))
    (and (vector? (get-in data key-path))
         (> (count (get-in data key-path)) 0)
         (not (.contains (vec (map fn (get-in data key-path))) false)))
    true))


(defn validate-config-project-node
  "Returns a map with key ':success' equal to 'true' if the node is valid else 'false' with a key ':reason' set to a post-fix message for why the node is valid."
  [node]
  (if (validate-config-param-string node [:name] true)
    (if (validate-config-param-string node [:alias] false)
      (if (validate-config-param-array node [:types] true string?)
        (if (validate-config-param-array node [:scopes] false map?)
          {:success :true}
          (validate-config-fail (str "has optional key 'scopes' that must be be an array of objects and contain at least one object.")))
        (validate-config-fail (str "must have key 'types' that must be an array of strings and contain at least one string.")))
      (validate-config-fail (str "project has optional key 'alias' that must have string value.")))
    (validate-config-fail (str "must have key 'name' with string value."))))



;; get scopes and assign then a key path
;; (vec (map #(assoc % :key-path [:config :scope :scopes]) (get-in init-data [:config :scope :scopes])))

(defn validate-config-project
  [data]
  (loop [index-path []
         node (get-in data [:config :scope])
         node-queue []]
    (validate-config-project-node node)))


(defn validate-config-project-temp
  [data]
  {:success false :reason "Due to testing."})


(defn validate-config
  "Performs validation of the config file 'config'.  Returns a map result with key ':success' of 'true' if valid and 'false' otherwise.  If invalid, then returns a key ':reason' with string reason why the validation failed."
  [config]
  (let [data {:success true :config config}]
    (let [result (->> data 
                      ;; 'enabled' was previously evaluated
                      (do-on-success validate-config-length)
                      (do-on-success validate-config-project-temp))]
      (dissoc result :config))))



(defn ^:impure process-commit-attempt
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
      (handle-err-exit (:reason config-response)))))



;; start
;;todo
;;
;; * get path of git editmsg from command line arg
;;    * err w/ exit 1 if not 1 arg
;;
;; * parse config file json into a map [impure]
;;    * err w/ exit 1 if can't read file
;;    * err w/ exit 1 if can't parse json
;;
;; - if enabled=false
;;    - then warning and exit 0 [impure]
;;
;; - read git editmsg from file and keep as string [impure]
;;    - err w/ exit 1 if can't read file
;; - reformat commit message
;; - write commit message [impure]
;;
;; - validate commit message
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
;; - display status and exit [impure]
;;


(defn ^:impure -main
  [& args]
  (if (= (count args) 1)
    (process-commit-attempt (first args))
    (handle-err-exit "Exactly one argument required.")))


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


