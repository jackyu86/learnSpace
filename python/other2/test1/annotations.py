
#python 注解 返回值注解在->后，参数注解在参数:后
def func(a:'spam',b,c:100) -> int:
    return a+b+c

func(1,2,3)
print(func.__annotations__)

for anargs in func.__annotations__:
    print(anargs,'=>',func.__annotations__[anargs])