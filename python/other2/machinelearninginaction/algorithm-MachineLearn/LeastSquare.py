#coding=utf-8
#最小二乘法与解二元一次方程差别图
import os
import matplotlib.pyplot as plt
import numpy as np

def drawScatterDiagram(filename):
    os.chdir('/Users/yuhaiyang/GitHub/haiyang/learnSpace/python/other2/machinelearninginaction')
    xcord = [];ycord = [];
    fr=open(filename)
    for lines in fr.readlines():
        lineAttr = lines.strip().split()
        print(lineAttr)
        xcord.append(float(lineAttr[0]));ycord.append(float(lineAttr[1]))
    plt.scatter(xcord, ycord, s=30, c='red', marker='s')
    plt.show()




def drawScatterDiagrama(fileName):
    #改变工作路径到数据文件存放的地方
    os.chdir('/Users/yuhaiyang/GitHub/haiyang/learnSpace/python/other2/machinelearninginaction')
    xcord=[];ycord=[]
    fr=open(fileName)
    for line in fr.readlines():
        lineArr=line.strip().split()
        xcord.append(float(lineArr[0]));
        ycord.append(float(lineArr[1]))
        plt.scatter(xcord,ycord,s=30,c='red',marker='s')
        a=0.1965;b=-14.486
        #a=0.1612;b=-8.6394
        x=np.arange(90.0,250.0,0.1)
        y=a*x+b
        plt.plot(x,y)
    plt.show()

#python实现最小二乘法
#






if __name__ == "__main__":
    drawScatterDiagrama('leastSquareData')


