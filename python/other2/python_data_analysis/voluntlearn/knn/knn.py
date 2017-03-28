from _testcapi import return_null_without_error

__author__ = 'Changdalin'

from numpy import *
import operator


##desc:
##将file转换成训练矩阵和结果矩阵
def file2matrix(filename):
    fr = open(filename,encoding="utf-8")
    ##desc:
    ##file多少行
    numberOfLines = len(fr.readlines())  # get the number of lines in the file
    ##desc:
    ##与file同样的行，3列，全是zero
    returnMat = zeros((numberOfLines, 6))  # prepare matrix to return
    ##desc:
    ##labels
    classLabelVector = []  # prepare labels return
    index = 0
    fr = open(filename, encoding="utf-8")
    for line in fr.readlines():
        line = line.strip()
        listFromLine = line.split(",")
        for temp in range(len(listFromLine)):
            listFromLine[temp] = float(listFromLine[temp])
        returnMat[index, :] = listFromLine[0:6]
        ##desc:
        ##这个classlabel是一个列矩阵
        classLabelVector.append(int(listFromLine[-1]))
        index += 1
    return returnMat, classLabelVector




##desc:
##测试函数
def classify_knn(in_vector, training_data, training_label, k):
    data_size = training_data.shape[0]  # .shape[0] 返回二维数组的行数
    diff_mat = tile(in_vector, (data_size, 1)) - data_set  # np.tile(array, (3, 2)) 对 array 进行 3×2 扩展为二维数组
    sq_diff_mat = diff_mat ** 2
    sq_distances = sq_diff_mat.sum(axis=1)  # .sum(axis=1) 矩阵以列求和
    distances_sorted_index = sq_distances.argsort()  # .argsort() 对array进行排序 返回排序后对应的索引
    class_count_dict = {}  # 用于统计类别的个数
    ##desc:
    ##找k次
    for i in range(k):
        label = training_label[distances_sorted_index[i]]
        try:
            class_count_dict[label] += 1
        except KeyError:
            class_count_dict[label] = 1
    # 根据字典的value值对字典进行逆序排序
    class_count_dict = sorted(class_count_dict.items(), key=operator.itemgetter(1),
                              reverse=True)
    return class_count_dict[0][0]


if __name__ == '__main__':
    data_set, labels = file2matrix("data.txt")
    in_vector = [600, 60, 70000, 0.1, 700000, 6000000]
    print(classify_knn(in_vector=in_vector, training_data=data_set, training_label=labels, k=1))
