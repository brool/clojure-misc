Miscellaneous Clojure Routines
==============================

Pattern Matching
----------------

Literal values match against the same value, while _ matches
against any non-nil value.  Additionally, :when clauses can be used
for conditional checks::

    ;; return "zero" "positive" or "negative" for a number
    (defn signum [x]
      (match x 
        (0 0)
        (n :when (< n 0) -1)
        (_ 1)))

If the same variable occurs in multiple locations in the parameter
list, it will be checked for equality::

    ;; count identical elements in the same location in two lists:
    (defn count= [ lst1 lst2 ]
      (loop [ a lst1 b lst2 count 0 ]
        (match [a b]
          ( [[e & at] [e & bt]]  (recur at bt (inc count)) )
          ( [[_ & at] [_ & bt]]  (recur at bt count) )
          ( _                    count))))



