class MyClass:
    pass

myClass=MyClass()

#这是因为函数type实际上是一个元类。type就是Python在背后用来创建所有类的元类
myClass2=type('MyClass2',(),{'name':'jack','age':12})



class UpperAttrMetaclass(type):
    def __new__(cls, name, bases, dct):
        attrs = ((name, value) for name, value in dct.items() if not name.startswith('__'))
        uppercase_attr  = dict((name.upper(), value) for name, value in attrs)
        return type.__new__(cls, name, bases, uppercase_attr)


class MyClass3(metaclass=UpperAttrMetaclass):
        pass


