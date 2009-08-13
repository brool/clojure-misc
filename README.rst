Miscellaneous Clojure Routines
==============================

Pattern Matching
----------------

Literal values match against the same value, while _ matches against
any non-nil value.  Additionally, :when clauses can be used for
conditional checks::

    ; simple recursive evaluator
    (defn arithmetic [lst]
      (match lst
        v  :when (number? v)  v
        [ _ "error" _]     "error"
        [ _ _ "error"]     "error"
        [ "print" a ]      (do (println "Output:" a) a)
        [ "add" a b ]      (+ (arithmetic a) (arithmetic b))
        [ "sub" a b ]      (- (arithmetic a) (arithmetic b))
        [ "mul" a b ]      (* (arithmetic a) (arithmetic b))
        [ "div" a b ]      (/ (arithmetic a) (arithmetic b))
        [ "squared" a ]    (arithmetic ["mul" (arithmetic a) (arithmetic a)])
        _                  "error" ))

Both collections and single values can be used::

    ;; return "zero" "positive" or "negative" for a number
    (defn signum [x]
      (match x 
        0 0
        n :when (< n 0) -1
        _ 1))

The pattern matching is stricter than the typical destructure;  whereas [ a b ] will destructure against a list of any number of elements, [ a b ] will pattern match only against a list of two elements.

::

    (match x 
        []    "empty"
        [_]   "one element"
        [a a] "two identical elements"
        [_ _] "two elements"
        _     "three or more")

If the same variable occurs in multiple locations in the parameter
list, it will be checked for equality::

    ;; count identical elements in the same location in two lists:
    (defn count= [ lst1 lst2 ]
      (loop [ a lst1 b lst2 count 0 ]
        (match [a b]
          [[e & at] [e & bt]]  (recur at bt (inc count))
          [[_ & at] [_ & bt]]  (recur at bt count)
          _                    count)))

Note that this is slightly more flexible than Haskell / ML, in that a variable of the same name can be multiple places in the pattern.

Defining
--------

You can use the defnp macro to define a function that is pattern
matched; it defines a function that takes one argument and has an
implicit match statement.  For example, the signum function can be
written:
         
::
        
    (defnp signum
       0 0
       n :when (< n 0) -1
       _ 1)

(Thanks to `Tom Faulhaber`_ for suggesting this)

.. _Tom Faulhaber: http://infolace.blogspot.com/

Gotchas
-------

The Clojure destructuring will cause an exception if you try to destructure a collection type with a value.

::

    (let [[a b] 10] a)
    java.lang.UnsupportedOperationException: nth not supported on this type: Integer (NO_SOURCE_FILE:0)

... so be sure to check such cases early in your match statement, if they are possible.

How It Works
------------

The pattern matcher uses the built-in Clojure destructuring as the main mechanism, but adorns it so that the pattern can be verified.  For example, the code::

    (match x [a a] "two identical")

turns into essentially the following::

    (let [ [ a g0001 & g0002 ] x ] 
         (if (and (not (nil? a)) (= g0001 a) (nil? g0002)) "two identical" nil))

That is, the destructuring is done, but then the two variables are checked to make sure that they are equal, and the list is checked to make sure it is only two elements long.

