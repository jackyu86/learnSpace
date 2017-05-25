# -*- coding: utf-8 -*-

import logging
import pymysql
#import traceback
import other2.test1 as sqlc

class MLActivity(object):
    def __init__(self):
        self.host = sqlc.host
        self.port = sqlc.port
        self.user = sqlc.user
        self.password = sqlc.passwd
        self.db = sqlc.db
        self.table = sqlc.table_ml_activity

    def  connectMysql(self):
        try:
            self.conn = pymysql.connect(host=self.host,port=self.port,user=self.user,passwd=self.password,db=self.db,charset='utf8')
            self.cursor = self.conn.cursor()
        except BaseException as e:
            logging.error('connection mysql error'+e)


    ##获取数据集
    def queryMLActivity(self,cityName):
        self.connectMysql()
        sql = "SELECT * FROM " + self.table + " limit 3 "
        print(sql)
        try:
            self.cursor.execute(sql)
            row = self.cursor.fetchall()
            print(row)
            return row
        except BaseException as e:
            logging.error(' execute failed.'+e)
            if(hasattr(self,'cursor') and hasattr(self,'conn')):
                self.closeMysql()


    def closeMysql(self):
        self.cursor.close()
        self.conn.close()

aa=MLActivity()
aa.queryMLActivity('aa')
aa.closeMysql()