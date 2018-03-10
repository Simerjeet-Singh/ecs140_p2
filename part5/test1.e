# test scoping. i.e., that correct a is used.
@ a
a = 999
!a+a
[a:!1111 % @a a=8888 !a]
!a
[ a:!1111
  % !2222
    !a
    [0-2: @a a = 3 !a]
    !a
    # The following code is for loop
    # first we give the initial value of a to 1
    # then after : the condition ((a-10)<=0) to be true,
    # which is true is the expression is non positive
    # then after : incrementation assignment
    # to increment the variable a every time we run loop
    # and in last we have our block where we are
    # first declaring an integer p and assigning it value 10
    # and then we are printing the value of p 10 times
  	{a=1:a-10:a=a+1: @p p=10 !p}
]
!a

[ a:!1111
  % @a a = 4
    !5
    !a
    [0: @a a = 6 !a]
    !a
]
!a
