#!/usr/bin/python2.6
# -*- coding: utf-8 -*
# Filename: run_hive_script.py

import sys
import os
import getopt

gBaseDir = '/data/home/yuhaiyang'
gDirList = ['hql','sql','script']
gCmd = "hive "
gOpts, gArgs = getopt.getopt(sys.argv[1:], "hf:p:")

# 根据文件名称，在指定目录列表中获取文件的完整路径
def getFilePath(baseDir,dirList,fname):
    flist = []
    for d in dirList:
        dir = baseDir + '/' + d
        flist = walkDir(dir,flist,fname)

    if len(flist) == 1:
        return flist[0]
    elif len(flist) > 1:
        raise IOError,"Repeat file " + fname
    else:
        raise IOError,"File  " + fname + " not fund"


def walkDir(dir, flist, fname, topdown=True):
    for root, dirs, files in os.walk(dir, topdown):
        for name in files:
            if name == fname:
                flist.append(os.path.join(root,name))
    return flist



def main():
    global gBaseDir, gDirList, gOpts
    global gCmd

    for op, value in gOpts:
        if op == "-f":
            fpath = getFilePath(gBaseDir, gDirList, value)
            file = "-f " + fpath
            cmd = gCmd + file
            os.system(cmd)
            print 'Success!'
        else:
            raise Exception,"Invalid parameter"


if __name__ == "__main__":
    try:
        main()
    except Exception,e:

        raise Exception
