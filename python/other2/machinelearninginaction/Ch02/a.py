def fib(n):
    result = []
    a, b = 0, 1
    while b < n:
        result.append(b)
        a, b = b, a+b
    return result



def fib2(n):
    a, b = 0, 1
    while a < n:
        yield a
        a, b = b, a + b


aa=fib2(10)
print(aa.next(),aa.next(),aa.next())
print(aa.next(),aa.next(),aa.next())
print(aa.next(),aa.next(),aa.next())