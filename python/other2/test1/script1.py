import os

print(os.getcwd())

print(2**2)

#字符串替换
'That is %d %s bird' %(1,'deal')


somelist=list('SPAM')

'first={0[0]},third={0[2]}'.format(somelist)


res=[a*3 for a in 'SPAM']

a=10
while a>0:
    print(a)
    a=a-1
else:
    print('end...')

#迭代生产列表
L=[1,2,3,4,5,6,7]

L=[ x+10 for x in L]

s1 = "SPAM"
s2 = "SCAM"
[ x for x in s1 if x in s2]