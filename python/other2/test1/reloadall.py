"""
reload模块及依赖模块
"""
import types
from imp import reload
#from importlib import reload
import sys


def status(module):
    print('reloading....'+module.__name__)


def transitive_reload(module,visited):
    if module not in visited:
        status(module)
        reload(module)
        visited[module]=None
        for attrobj in module.__dict__.values():
            if type(attrobj) == types.ModuleType :
                transitive_reload(attrobj,visited)


def reload_all(*modules):
    visited={}
    for arg in modules:
        if type(arg) == types.ModuleType:
            transitive_reload(arg,visited)



if __name__ == '__main__':
    import reloadall
    reload_all(reloadall)