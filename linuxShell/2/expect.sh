#!/usr/bin/expect
#author jack
＃用途: expect 实现自动化

spawn ./interactive.sh
expect "Enter number :"
send "1\n"
expect "Enter name :"
send "jack\n"
expect eof
