def tester1(num):
    state=num
    def indef(lable):
        nonlocal state
        print (lable,state)
        state+=1
    return indef

f=tester1(1)
f('aaa1')
f('aaa2')
f('aaa3')


def f(b):
    b=99
    print(b)

b=88
f(b)
print(b)



def func(a,b,c,d):print(1,2,3,4)

#解包参数
#解包元组和字典
func(*(1,2),**{'c':3,'d':4})
#解包元组
func(*(1,2),c=3,d=4)

#keyword only
#1
def olargs1(a,*,b,c):print(a,b,c)
#2 invalid syntax 参数不能出现在**参数后
#def olargs2(a,**b,c):print(a,b,c)

olargs1((1,2,),b=3,c=4)

def olargs3(*,a,b,c):print(a,b,c)

olargs3(a=1,b=(2,3),c=4)

#参数默认值
def defaultParm(*,a,b=3,c='aaa'):print(a,b,c)



