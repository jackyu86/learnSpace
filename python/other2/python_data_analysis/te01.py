# -- coding: utf-8 -

import pandas as pd
import numpy as np
import json
import time
path='/Users/yuhaiyang/GitHub/haiyang/learnSpace/python/other2/usagov_bitly_data2012-03-16-1331923249.txt';
datas = [json.loads(line) for line in open(path)]
count=0;
"""
for li in open(path).readlines():
    count+=1
    if(count<=10):
        print (li)
    else:
        break
"""
print(len(datas))

fram = pd.DataFrame(datas)
print(fram[:1])
print('==>'*100)
print (fram['tz'][:10])

print(type(fram['tz']))


print(fram['tz'].value_counts())

print('====='*100)



"""
替换缺失的值与空字符串
"""

clean_tz = fram['tz'].fillna('Missing')
clean_tz[clean_tz=='']='UnKnown'
#series对象
v_clean_tz_count=clean_tz.value_counts()
print(type(v_clean_tz_count))
print(v_clean_tz_count[:10])

#使用series对象的plot方法绘制图形
v_clean_tz_count[:10].plot(kind='barh',rot=0)


print (fram[fram.a.notnull()][:1])
cfram=fram[fram.a.notnull()]
operation_sys=np.where(cfram.a.str.contains('Windows'),'Windows','Not Windows')


print(operation_sys[:3])
print('----'*100)

print(cfram[:1])
#groupby 内需要个列表 与sql类似保留 group的内容
tz_sys_group=cfram.groupby(['tz',operation_sys])






