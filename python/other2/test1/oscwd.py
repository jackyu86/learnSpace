#!/usr/bin/env python
import sys
import os as aaaa
import script1
from imp import reload
print (sys.platform + '00000000')
print(2**1)
str='abcdef gh'
str.isalpha()
print (aaaa.getcwd())

D={'a':1,'b':2,'c':3,'d':[1,2,3,4,5,6,7,7,8,9,9],'e':{'aa':1,'bb':2}}
#for
newlist = D['d']
type(newlist)
newmap=D['e']
type(newmap)

#元组 不可变
tuplea = (1,2,3,4)
print(tuplea + (5,6))
tuplea.__contains__(4)


#file 文件

file1 = open('data.txt','w')
file1.write('write/n')
file1.write('2write/n')
file1.close()


file2 = open('data.txt')
txt = file2.read();
print (txt)

reload(script1)
class A:
    a = 1

    def __init__(self):
        print self

