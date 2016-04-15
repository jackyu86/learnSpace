#!/usr/bin/expect -f
# 设置超时时间为 60 秒
set timeout  60                                     	
spawn ssh -p65535 -l yuhy 222.73.182.27
expect "password:" 
send "yuhy@cloud&0931" 
interact 
