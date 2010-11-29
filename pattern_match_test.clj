;;
;; tests for pattern-match
;;

(ns pattern-match-test
    (:use pattern-match)
    (:use clojure.contrib.test-is))

(deftest single-values 
  (testing "Single value patterns"
           (let [f (fn [x] (match x nil "nil" n :when (< n 10) "<10" _ ">10"))]
             (are (= _1 _2) 
              "nil" (f nil)
              "<10" (f -20)
              "<10" (f 0)
              ">10" (f 20))
)))

(deftest symbol-and-keyword-values 
  (testing "Single value patterns"
    (let [f (fn [x] (match x 'a "a" 'b "b" :c ":c" :d ":d" _ "other"))]
             (are (= _1 _2) 
              "a" (f 'a)
              "b" (f 'b)
              ":c" (f :c)
              ":d" (f :d)
              "other" (f 1))
)))

(deftest basic-list-matching
  (testing "Basic list matching + fallthrough should go to nil"
           (let [f (fn [x] (match x [] "empty" [_] "one" [_ _] "two"))]
             (are (= _1 _2) 
                "empty" (f [])
                "one" (f [1])
                "two" (f [1 2]))
             (is (nil? (f [1 2 3])))
)))

(deftest head-tail
  (testing "Head/tail matching"
           (let [f (fn [x] (match x [a] "a" [a & rest] "b"))]
             (is (nil? (f [])))
             (is (= "a" (f [1])))
             (is (= "b" (f [1 2])))))
  (testing "& body should match nil"
           (let [f (fn [x] (match x [a & rest] "a"))]
             (is (= "a" (f [1])))
             (is (= "a" (f [1 2])))))
)

(deftest equality-checks
  (testing "Matching symbols should be equal"
           (let [f (fn [x] (match x [a a] "equal" [a b] :when (< a b) "less than" _ "greater than"))]
             (are (= _1 _2)
                  "less than" (f [1 2])
                  "greater than" (f [2 1])
                  "equal"     (f [1 1]))))

  (testing "Matching & body forms should work as well"
           (let [f (fn [x] (match x [[_ & rest] [_ & rest]] "true" _ "false"))]
             (are (= _1 _2)
                  "false" (f [[1 2] [1 3]])
                  "false" (f [[1] [1 3]])
                  "false" (f [[1 'a] [1 'b]])
                  "true"  (f [[1 'a] [1 'a]])
                  "true"  (f [[1 2] [1 2]])
                  "true"  (f [[0 2] [1 2]])
                  "true"  (f [[0 2 3] [9 2 3]])
                  "false" (f [[0 2 3] [9 4 2]]))))
)

(deftest nil-check
  (testing "_ should not match nil"
           (let [f (fn [x] (match x _ "non-nil" nil "nil"))]
             (are (= _1 _2)
                  "non-nil"  (f 10)
                  "non-nil"  (f f)
                  "nil"      (f nil)
                  "non-nil"  (f "nil"))))

  (testing "_ should not match nil in lists"
           (let [f (fn [x] (match x [_] "non-nil" [_ & _] "1+ non-nil" _ "nil"))]
             (are (= _1 _2)
                  "non-nil"     (f [10])
                  "1+ non-nil"  (f [10 20])
                  "1+ non-nil"  (f [10 nil])
                  "nil"         (f [nil]))))
)

(deftest testnp
  (defnp np-signum
    0 0
    n :when (< n 0) -1
    _ 1)

  (testing "Testing defnp"
           (are (= _1 _2)
                -1     (np-signum -10)
                0      (np-signum 0)
                1      (np-signum 10)))
) 