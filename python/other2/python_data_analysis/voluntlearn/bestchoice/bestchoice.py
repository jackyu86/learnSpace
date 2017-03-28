__author__ = 'Changdalin'

import numpy as np
from math import log

##desc:
##是否手机端订单  是否大城市  是否异地 价格层次 南北方 城市交通情况 平均消费水平
dataSet = np.array([[1, 1, 0, 2, 1, 1, 1, 1],
                    [1, 1, 0, 3, 2, 1, 1, 1],
                    [0, 1, 0, 4, 1, 1, 1, 0],
                    [0, 1, 1, 2, 2, 1, 1, 0]
                    ])
category = ['1', '1', '1', '0']


##desc:
##计算熵


def calc_shannon_ent(category_list):
    """
    :param category_list: 类别列表
    :return: 该类别列表的熵值
    """
    label_count = {}  # 统计数据集中每个类别的个数
    num = len(category_list)  # 数据集个数
    for i in range(num):
        try:
            label_count[category_list[i]] += 1
        except KeyError:
            label_count[category_list[i]] = 1
    shannon_ent = 0.
    for k in label_count:
        prob = float(label_count[k]) / num
        shannon_ent -= prob * log(prob, 2)  # 计算信息熵
    return shannon_ent


##desc:
##切割数据，取得符合要求的,最后返回的是[yes ,no ]
def split_data(feature_matrix, category_list, feature_index, value):
    # ret_index 是哪几行
    ret_index = np.where(feature_matrix[:, feature_index] == value)[0]
    ret_category_list = [category_list[i] for i in ret_index]  # 根据索引取得指定的所属类别，构建为列表
    return ret_category_list


##desc:
##选择最合适的特征
def choose_best_feature(feature_matrix, category_list):
    feature_num = len(feature_matrix[0])  # 特征个数
    data_num = len(category_list)  # 数据集的个数
    base_shannon_ent = calc_shannon_ent(category_list=category_list)  # 原始数据的信息熵
    best_info_gain = 0  # 最优信息增益
    # 最优特征对应的索引
    best_feature_index = -1
    ## 循环几个特征 现有两个 所以f=0,1,即 两列
    for f in range(feature_num):
        # 在第一列，set()是找distinct的值
        uni_value_list = set(feature_matrix[:, f])
        new_shannon_ent = 0.
        # 第一列不同的值
        for value in uni_value_list:
            # 一列就是一个属性
            # 按照不同的值开始划分,选出  这一列里面  value=它的那一些行，组成新的list集合
            # 循环了 uni_value_list 次，每一个list，都有它的熵
            sub_cate_list = split_data(feature_matrix, category_list, f, value)
            # 这个值 比如value=n ，找到K行，k在总行数里占多少
            prob = float(len(sub_cate_list)) / data_num
            # 累加混乱度
            new_shannon_ent += prob * calc_shannon_ent(sub_cate_list)
        info_gain = base_shannon_ent - new_shannon_ent  # 信息增益
        print('序列总混乱度：', base_shannon_ent, '当前分类： %i 其信息混乱度是：' % f, new_shannon_ent,
              '信息增益：', info_gain)
        if info_gain > best_info_gain:
            best_info_gain = info_gain
            best_feature_index = f
    return best_feature_index


if __name__ == '__main__':
    index = choose_best_feature(dataSet, category)
    print(index)
