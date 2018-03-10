# test parser -- this is another valid program

@ a

# The following code is for loop
# This will through an error, since we are expecting
# a then ':' symbol after the condition but we are getting
# an assignment, therefore it will through an mustbe error

{a=1:a-10 a=a+1: @p p=10 !p}
