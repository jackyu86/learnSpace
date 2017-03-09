#from __future__ imports must occur at the beginning of the file
#必须在第一位导入
#from __future__ import absolute_import
#相对导入
from . import sys as abc
#绝对导入
#from other2 import sys as abc
#默认导入标准库
#import sys
#谨记：执行应用程序的模块，不能使用相对导入；比如a.py，打算执行该文件，
# 那么该文件文件名就会变成__main__，若使用相对导入 也就是from .* 这种形式，
# 就会出错；因为当前的模块名改变后，解释器认为相对路径失效；

def printer():
    print(abc.__name__)
    print(abc.a)