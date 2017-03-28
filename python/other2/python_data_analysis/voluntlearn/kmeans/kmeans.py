__author__ = 'Changdalin'

import random

def loadDataSet(fileName):#导入数据集
    datemat = []
    file1 = open(fileName)
    line = file1.readline()
    while line:
        line = file1.readline()
        temp = line.strip().split(',')
        datemat.append(temp)
    file1.close()
    datemat.pop()
    return datemat

def changeDateSet(datemat):#对数据集进行预处理
    # dict1 = {'FEMALE' : 0,'MALE' : 1,'INNER_CITY' : 0,'TOWN' : 1,'RURAL' : 2,'SUBURBAN':3,'YES':1,'NO':0}
    # #数据集行数
    # for i in range(len(datemat)):
    #      for j in range(len(datemat[i])):
    #          # FEMALE
    #         if datemat[i][j] in dict1:
    #            # FEMALE = dict1['FEMALE']=0
    #            datemat[i][j] =  dict1[datemat[i][j]]

    #归一化处理
    for i in [1,2,3]:
          lmax = max([float(x[i]) for x in datemat])
          lmin = min([float(x[i]) for x in datemat])

          for j in range(len(datemat)):
              datemat[j][i] = float(datemat[j][i])
              datemat[j][i] = ((datemat[j][i] - lmin) / (lmax - lmin))
    #最后全部变成了数字
    return datemat




def randCent(dataSet,k): #构建一个包含k个随机簇中心的集合
    m = len(dataSet)    #行数   数据集个数
    n = len(dataSet[1]) #列数  （特征数量）
    #构建全0的矩阵
    centroids = [[0 for a in range(n)] for b in range(k)]
    rams = []
    for i in range(k):#创建k个 0~1的随机数
        #随机一个0-1之间的数字
        ram = random.uniform(0,1)
        rams.append(ram)

    #0-5
    for c in range(k):
        #每一行第一个值,是它的特征
        centroids[c][0] = str(c+1)  #k个簇 每个簇为一个列表 第一值为序号 其余为特征
        #循环每一行的第二个数往后面所有的数
        for j in range(1,n):
            #for x in dataset 意思是每一行 每一行的[j]这个数里面，找到最小的
            #也就是，所有行，在同一列最小的数
            minJ = min([x[j] for x in dataSet])
            # 也就是，所有行，在同一列最大的数
            maxJ = max([x[j] for x in dataSet])
            rangeJ=float(maxJ - minJ)
            centroids[c][j] = minJ+rangeJ*rams[c] #产生k个坐标随机的簇
    #到最后生成的，就是与dataset一样规模的，且每一个位置，都是随机值的一个list
    return centroids

##desc:
##更新质心的位置
def updateCentroids(teamdate,centroids,k):   #更新簇中心的位置
    n = 4 #列数  （特征数量)
    #随机行，n列 0-5行
    new = [[0 for a in range(n)] for b in range(k)] #新建k个簇坐标
    #最前面一个值，代表一个标识
    for c in range(k):
        new[c][0] = str(c+1)


    for kk in range(k):
        #列数
        m = len(teamdate[kk])
        #循环从第二列开始的每一列
        for i in range (1,n):
            distance = 0
            for j in range(m):
                distance += teamdate[kk][j][i]
            if m != 0 :
              distance/=m
              new[kk][i] = distance

    return new


def kmeans(k):
    #导入数据集
    datemat = loadDataSet('pvinfo.csv')
    datemat = changeDateSet(datemat)
    #生成随机位置的k个族 每个族第一个数字为编号
    clusts = randCent(datemat,k)
    m = len(datemat) #数据集数量
    n = len(datemat[0]) #特征数量
    clusterChanged=True
    xhcs = 0
    while clusterChanged:
        xhcs = xhcs + 1
        #建立k行
        team = [[] for i in range(k)] #建立k个分组 对样点进行分组
        #利用欧氏距离公式寻找每个样本与之最近的簇 并进行分组
        for c in range(m):
            dis = [0 for i in range(k)]
            for i in range(k):
                for j in range(1,n):
                     dis[i]=( dis[i] + (datemat[c][j] - clusts[i][j])**2)

            g = dis.index(min(dis)) #获得最小值的索引 根据索引分配
            team[g].append(datemat[c])

        New_clusts = updateCentroids(team,clusts,k)

    #判断 质心是否改变 若不再改变则停止循环
        Change = 0
        Changes = 0
        for kk in range(k):
            for i in range(1,n):
               Change +=(New_clusts[kk][i] - clusts[kk][i])**2

            Changes += Change


        if Changes <= 0.0000001:
            clusterChanged=False
        else:
            clusts = New_clusts
    #print('循环多少次完善质心%'%xhcs)
    for kk in range(k):

        print('\n组%s：'%(kk+1) )
        for i in range(len(team[kk])):
           print (team[kk][i][0])
        print ('\n','该组共有%s个pv签'%len(team[kk]))


if __name__ == '__main__':
    kmeans(3)