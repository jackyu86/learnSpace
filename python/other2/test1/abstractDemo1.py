#python3.0 抽象实现
from abc import ABCMeta,abstractmethod

class Super(metaclass=ABCMeta):
    def delelgate(self):
        self.action()
    #子类必须实现此方法
    @abstractmethod
    def action(self):
        pass

x=Super()

class Sub(Super):
    pass

y=Sub()
y.delelgate()

class Sub2(Super):
    def action(self):
        print('abc')

z=Sub2()
z.delelgate()

#python 2.6抽象类实现方式
class Super2:
    __meteclass__= ABCMeta
    def action(self):
        pass
