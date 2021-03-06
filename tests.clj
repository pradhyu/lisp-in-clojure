(ns tests
  (:require [env :as env]
            [eval :as eval]
            [reader :as reader])
  (:use [clojure.test]))

(deftest eval*
  (let [env (env/make-env)]
    (are [exp answer] (= (eval/eval* (reader/read* exp) env) answer)

         "(quote a)"
         "a"

         "'a"
         "a"

         "(quote (a b c))"
         '("a" "b" "c")

         "(atom 'a)"
         "t"

         "(atom '(a b c))"
         '()

         "(atom '())"
         "t"

         "(atom (atom 'a))"
         "t"

         "(atom '(atom 'a))"
         '()

         "(eq 'a 'a)"
         "t"

         "(eq 'a 'b)"
         '()

         "(eq '() '())"
         "t"

         "(car '(a b c))"
         "a"

         "(cdr '(a b c))"
         '("b" "c")

         "(cons 'a '(b c))"
         '("a" "b" "c")

         "(cons 'a (cons 'b (cons 'c '())))"
         '("a" "b" "c")

         "(car (cons 'a '(b c)))"
         "a"

         "(cdr (cons 'a '(b c)))"
         '("b" "c")

         (str "(cond ((eq 'a 'b) 'first)"
              "      ((atom 'a)  'second))")
         "second"

         "((lambda (x) (cons x '(b))) 'a)"
         '("a" "b")

         (str "((lambda (x y) (cons x (cdr y)))"
              " 'z"
              " '(a b c))")
         '("z" "b" "c")

         (str "((lambda (f) (f '(b c)))"
              " '(lambda (x) (cons 'a x)))")
         '("a" "b" "c")

         (str "((label subst (lambda (x y z)"
              "                (cond ((atom z)"
              "                       (cond ((eq z y) x)"
              "                             ('t z)))"
              "                      ('t (cons (subst x y (car z))"
              "                                (subst x y (cdr z)))))))"
              " 'm 'b '(a b (a b c) d))")
         '("a" "m" ("a" "m" "c") "d")

         "(subst 'm 'b '(a b (a b c) d))"
         '("a" "m" ("a" "m" "c") "d")

         "(cadr '((a b) (c d) e))"
         '("c" "d")

         "(caddr '((a b) (c d) e))"
         "e"

         "(cdar '((a b) (c d) e))"
         '("b")

         "(null 'a)"
         '()

         "(null '())"
         "t"

         "(and (atom 'a) (eq 'a 'a))"
         "t"

         "(and (atom 'a) (eq 'a 'b))"
         '()

         "(not (eq 'a 'a))"
         '()

         "(not (eq 'a 'b))"
         "t"

         "(append '(a b) '(c d))"
         '("a" "b" "c" "d")

         "(append '() '(c d))"
         '("c" "d")

         "(pair '(x y z) '(a b c))"
         '(("x" "a") ("y" "b") ("z" "c"))

         "(assoc 'x '((x a) (y b)))"
         "a"

         "(assoc 'x '((x new) (x a) (y b)))"
         "new"

         "(eval 'x '((x a) (y b)))"
         "a"

         "(eval '(eq 'a 'a) '())"
         "t"

         (str "(eval '(cons x '(b c))"
              "      '((x a) (y b)))")
         '("a" "b" "c")

         (str "(eval '(cond ((atom x) 'atom)"
              "             ('t 'list))"
              "      '((x '(a b))))")
         "list"

         (str "(eval '(f '(b c))"
              "      '((f (lambda (x) (cons 'a x)))))")
         '("a" "b" "c")

         (str "(eval '((label firstatom (lambda (x)"
              "                           (cond ((atom x) x)"
              "                                 ('t (firstatom (car x))))))"
              "        y)"
              "      '((y ((a b) (c d)))))")
         "a"

         (str "(eval '((lambda (x y) (cons x (cdr y)))"
              "        'a"
              "        '(b c d))"
              "      '())")
         '("a" "c" "d")

         "(defun test (xs) (car xs))"
         "'test' defined!"

         "(test '(1 2 3))"
         "1"

         "(defun test (xs) (cdr xs))"
         "'test' defined!"

         "(test '(1 2 3))"
         '("2" "3"))))

(run-tests)
