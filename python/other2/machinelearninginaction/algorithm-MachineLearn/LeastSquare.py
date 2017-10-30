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
        # 取所有数据之间的值计算残差平方和最小的
        a=0.1965;b=-14.486
        #a=0.1612;b=-8.6394

        x=np.arange(90.0,250.0,0.1)
        y=a*x+b
        plt.plot(x,y)
    plt.show()


#python实现最小二乘法
#公式: y=mx+b
# m is slope, b is y-intercept
#

def compute_error_for_line_given_points(b,m,coordinates):
    totalError=0
    for i in range(len(coordinates)):
        x=coordinates[i][0]
        y=coordinates[i][1]
        totalError+=(y-(m*x+b))**2

    return totalError/float(len(coordinates))


compute_error_for_line_given_points(1,2[[3,6],[6,9],[12,18]])

#最小二乘法类库实现
import numpy as np  # 引入numpy
import scipy as sp
import pylab as pl
from scipy.optimize import leastsq  # 引入最小二乘函数

n = 9  # 多项式次数


# 目标函数
def real_func(x):
    return np.sin(2 * np.pi * x)


# 多项式函数
def fit_func(p, x):
    f = np.poly1d(p)
    return f(x)


# 残差函数
def residuals_func(p, y, x):
    ret = fit_func(p, x) - y
    return ret


x = np.linspace(0, 1, 9)  # 随机选择9个点作为x
x_points = np.linspace(0, 1, 1000)  # 画图时需要的连续点

y0 = real_func(x)  # 目标函数
y1 = [np.random.normal(0, 0.1) + y for y in y0]  # 添加正太分布噪声后的函数

p_init = np.random.randn(n)  # 随机初始化多项式参数

plsq = leastsq(residuals_func, p_init, args=(y1, x))

print
'Fitting Parameters: ', plsq[0]  # 输出拟合参数

pl.plot(x_points, real_func(x_points), label='real')
pl.plot(x_points, fit_func(plsq[0], x_points), label='fitted curve')
pl.plot(x, y1, 'bo', label='with noise')
pl.legend()
pl.show()

if __name__ == "__main__":
    drawScatterDiagrama('leastSquareData')


