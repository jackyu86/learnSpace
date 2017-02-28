#yield

"""
和python中迭代协议的关系
生成器是单迭代对象
"""
def generor():
    for i in range(10):
        x=yield i+1
        print(x)


def generor2():
    for i in range(10):
        print(type(i))
        x=yield i+1
        print(x)
#生成器表达式使用圆括号  列表解析使用[]
G=(a*4 for a in "spam")

a=iter(G)
next(a)
#s
next(a)
#p
b=iter(G)
next(b)
#a
#使用同一个生成器对象


t1=zip([1,2,3],[2,3,4,5])

def tesfunc1(t):
    for tt in t:
        print(tt)


def myZip(*seq):
    seqs=[list(S) for S in seq]
    print(seqs)
    while all(seqs):
        yield tuple((S.pop(0) for S in seqs))

myZip('abc','123456')


#生成器send

"""
执行流程：

1、通过g.send(None)或者next(g)可以启动生成器函数，并执行到第一个yield语句结束的位置。此时，执行完了yield语句，但是没有给receive赋值。yield value会输出初始值0注意：在启动生成器函数时只能send(None),如果试图输入其它的值都会得到错误提示信息。
2、通过g.send(‘aaa’)，会传入aaa，并赋值给receive，然后计算出value的值，并回到while头部，执行yield value语句有停止。此时yield value会输出”got: aaa”，然后挂起。
3、通过g.send(3)，会重复第2步，最后输出结果为”got: 3″
4、当我们g.send(‘e’)时，程序会执行break然后推出循环，最后整个函数执行完毕，所以会得到StopIteration异常。
最后的执行结果如下：
0
got: aaa
got: 3
Traceback (most recent call last):
File "h.py", line 14, in <module>
  print(g.send('e'))
StopIteration

"""

def gen():
    value = 0
    while True:
        receive = yield value
        if receive == 'e':
            break
        value = 'got: %s' % receive


g = gen()
print(g.send(None))
print(g.send('aaa'))
print(g.send(3))
print(g.send('e'))