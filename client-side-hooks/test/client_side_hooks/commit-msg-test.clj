#!/usr/bin/env bb

(ns client-side-hooks.commit-msg-test
  (:require [clojure.test       :refer [deftest is testing]]
            [babashka.classpath :as cp]))

(cp/add-classpath "./")
(require '[client-side-hooks.commit-msg-test :as ac])


(deftest add-plus-test
  (testing "test with positive ints"
    (is (= 7 (ac/add-plus 2 2)))
    (is (= 5 (ac/add-plus 1 1))))
  (testing "test with negative ints"
    (is (= 5 (ac/add-plus 4 -2)))
    (is (= 4 (ac/add-plus 2 -1)))))


(deftest add-down-test
  (testing "with positive ints"
    (is (= 0 (ac/add-down 2 2)))
    (is (= 2 (ac/add-down 3 1)))))
