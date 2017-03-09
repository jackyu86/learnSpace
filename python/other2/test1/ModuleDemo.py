#python 2x
#from imp import reload
#python 3.4x
from importlib import reload
import sys

"""
模块
import 获取整个模块
from 从模块中回去某个变量或函数
imp.reload 重载模块
"""


#模块搜索路径
#1.程序的主目录
#2.PYTHONPATH目录
#3.标准链接库目录
#4.任何.pth文件的内容


#重载

mes='first version'

def changeMes(cmes):
    global mes
    mes=cmes


def printer():
    print('==>',mes)

reload(other2)

print(sys.modules['other2'].name)
getattr(other2,'name')