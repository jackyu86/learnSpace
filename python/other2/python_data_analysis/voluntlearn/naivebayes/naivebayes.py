__author__ = 'Changdalin'

import jieba
from numpy import *

##desc:
##获取数据
def loadDataSet():
    text_data = []
    file1 = open('data.txt',encoding="utf-8")
    while 1:
        text = file1.readline()
        if not text:
            break
        words = jieba.cut(text, cut_all=False)
        line_text = list(words)
        text_data.append(line_text)
    file1.close()
    file2 = open('result.txt',encoding="utf-8")
    while 1:
        result = file2.readline()
        if not result:
            break
        result = result.split(",")
        for i in range(len(result)):
            result[i] = int(result[i])
        result_data = list(result)
    file2.close()
    return text_data,result_data

##desc:
##对于样本，将所有词不重复的提取出来，构成不重复词词典
def createVocabList(dataSet):
    vocabSet = set([])
    for document in dataSet:
        vocabSet = vocabSet | set(document)
    return list(vocabSet)



##desc:
##vocab是词典，input是一行
def setOfWords2Vec(vocabList, inputSet):
    ##比如有1000个词的词典，与voca一样大的行
    returnVec = [0]*len(vocabList)
    for word in inputSet:
        if word in vocabList:
            ## 一千行的这个位置置为1
            returnVec[vocabList.index(word)] = 1
        # else: print ("词语 %s 不存在" % word)
    ##返回的是1000大小的，有词的位置置为1
    return returnVec



##desc:
##训练矩阵最后是每一篇形成一个字典大小的行，并且是所有行
##category是类别的一行
def trainNB0(trainMatrix,trainCategory):
    ##多少行的文档
    numTrainDocs = len(trainMatrix)
    ##一行大小
    numWords = len(trainMatrix[0])
    ## 1 确定概率是多少
    pAbusive = sum(trainCategory)/float(numTrainDocs)
    ##一行1 p1Num 和 p0Num都是一个一行矩阵，一行1
    ##是为了让概率不等于0
    p0Num = ones(numWords); p1Num = ones(numWords)
    p0Denom = 2.0; p1Denom = 2.0

    ## 遍历 所有的数据集
    for i in range(numTrainDocs):
        ##如果是1的样本，那么这一行，这是区分 符合=1 与 不符合=0的关键一步
        if trainCategory[i] == 1:
            ## 形成一个[2,1,1,……]
            ## 累加 += 是p1Num每一个元素的加
            p1Num += trainMatrix[i]
            ##trainMatrix 是 [1,0,0,……]的矩阵
            ##p1Denom加起来所有的1，出现的词
            p1Denom += sum(trainMatrix[i])
        else:
            p0Num += trainMatrix[i]
            p0Denom += sum(trainMatrix[i])


    ##p1Num是一个dict这么大的一行， 在每一个元素上，都有一个数字
    ## 再除以p1Denom ，这个数值，是每一行，所有的1加起来，即 =1文档所有的词数
    ##每一个词出现的概率
    #取log是为了计算方便
    p1Vect = log(p1Num/p1Denom)          #change to log()
    p0Vect = log(p0Num/p0Denom)          #change to log()
    ## p1Vect是符合要求的一行，记录的都是符合要求文档的信息，每一个词的概率值
    ## 最后就是两个字典大小的行，每一行代表，每个词是符合条件的概率
    ## 这个概率越大，表示有这个词的越可能为  符合条件的art
    return p0Vect,p1Vect,pAbusive


## desc:
## 检验一个行向量(一个数据是否是符合要求 =1 的 ,pclass1 是 p_all,符合要求的占比)
def classifyNB(vec2Classify, p0Vec, p1Vec, pClass1):
    p1 = sum(vec2Classify * p1Vec) + log(pClass1)    #element-wise mult
    p0 = sum(vec2Classify * p0Vec) + log(1.0 - pClass1)
    if p1 > p0:
        return 1
    else:
        return 0

##desc:
##initVect 初始化字典向量
def initVect():
    listOPosts, listClasses = loadDataSet()
    myVocabList = createVocabList(listOPosts)
    trainMat = []
    for postinDoc in listOPosts:
        trainMat.append(setOfWords2Vec(myVocabList, postinDoc))
    p0V, p1V, pAb = trainNB0(array(trainMat), array(listClasses))
    return myVocabList,p0V, p1V, pAb

##desc:
##测试输入
def test(text,myVocabList,p0V,p1V,pAb):
    words = jieba.cut(text, cut_all=False)
    line_text = list(words)
    thisDoc = array(setOfWords2Vec(myVocabList, line_text))
    print('判断其类别: ', classifyNB(thisDoc, p0V, p1V, pAb))


if __name__ == '__main__':
    myVocabList,p0V, p1V, pAb = initVect()
    test("服务态度很不错的",myVocabList,p0V, p1V, pAb)


