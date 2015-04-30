(DATA  a)
(DATA  b)
(FUNCTION  addThem  []
  (BB 3
    (OPER 4 Func_Entry []  [])
  )
  (BB 4
    (OPER 5 Add_Q [(s e)]  [(s d)(s e)])
    (OPER 6 Mov [(r 1)]  [(r 2)])
    (OPER 7 Func_Exit []  [])
  )
)
