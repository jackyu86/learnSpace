#!/bin/bash
#author jack
export LANG="en_US.UTF-8"
#echo -e "bash\nfoss\nhack\nhack" >uniqfile.txt

sort uniqfile.txt | uniq

#显示统计数量
#sort uniqfile.txt | uniq -c
#显示唯一的行
sort uniqfile.txt | uniq -u
#显示重复的行
sort uniqfile.txt| uniq -d

