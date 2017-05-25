'''
Created on Oct 27, 2010

@author: Peter
'''
from numpy import *

import kNN
import matplotlib
import matplotlib.pyplot as plt
fig = plt.figure()
ax = fig.add_subplot(111)
datingDataMat,datingLabels = kNN.file2matrix('/Users/yuhaiyang/GitHub/haiyang/learnSpace/python/other2/machinelearninginaction/Ch02/datingTestSet2.txt')
#ax.scatter(datingDataMat[:,1], datingDataMat[:,2])
        #第二列每行(玩视频游戏时间占比)    第三列每行(每周消费的冰激凌)
ax.scatter(datingDataMat[:,1], datingDataMat[:,2], 15.0*array(datingLabels), 15.0*array(datingLabels))
ax.axis([-2,25,-0.2,2.0])
plt.xlabel('Percentage of Time Spent Playing Video Games')
plt.ylabel('Liters of Ice Cream Consumed Per Week')
plt.show()
