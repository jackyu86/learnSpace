from tkinter import  Button,mainloop
from functools import reduce
import sys
lfunc1=lambda x,y: x if x>y else y
lfunc1(1,2)
#嵌套的lambda
#1 def方式

def action(x):
        return (lambda y:x+y)
#2 嵌套lambda
lambda x:(lambda y:x+y)

(lambda x:(lambda y:x*y)(99))(2)

x = Button(text='Press me',command=(lambda :sys.stdout.write("Spam\n")))
x.pack()
mainloop()


# map filter reduce

list(filter((lambda x : x>0),range(-5,5)))
reduce((lambda x,y:x+y),[1,2,3,4])

#列表解析
#1 迭代

res =[]

for a in 'spam':
        res.append(ord(a))

print('first ',res)


#2 map
print('second',list(map(ord,'spam')))

#列表解析

list2=[ord(aax) for aax in 'spam']

# 列表实现
[x for x in range(5) if x % 2 ==0]
#2 类似嵌套for循环  迭代外层(第一个for列表) 和内层(第二个for列表)
[(x,y) for x in range(5) if x % 2==0 for y in range(5) if y % 2 ==1]
#lambda 实现

list(filter((lambda x : x%2==0),range(5)))




#列表解析和矩阵

#3*3矩阵

M=[[0,1,2],[3,4,5],[6,7,8]]
L=[[6,7,8],[3,4,5],[0,1,2]]
[M[row][1] for row in (0,1,2)]
[(M[row][1],L[row2][1]) for row in (0,1,2) for row2 in (0,1,2)]


