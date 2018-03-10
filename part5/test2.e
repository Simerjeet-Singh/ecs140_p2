# check parsing -- valid -- can have 2 declarations
@i
@k
i = 25
# The following code is for loop
# first we give the initial value of i to 1
# then after : the condition ((i-10)<=0) to be true,
# which is true is the expression is non positive
# then after : incrementation assignment
# to increment the variable a every time we run loop
# and in last we have our block where we are
# counting values from 0 to 10 and printing it
{i=1:i-10:i=i+1: !i}
k = 12
!i+k
