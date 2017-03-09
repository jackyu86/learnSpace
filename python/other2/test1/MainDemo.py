#绝对导入 这里是执行使用相对导入会报错,执行文件名就会变成__main__,相对路径失效
from other2.test1 import ImportDemo
import sys


def aa():
    print(sys.path)
    #相对导入&绝对导入
    ImportDemo.printer()


aa()