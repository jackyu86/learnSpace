#!/bin/bash
#author jack
＃用途重命名当前目录jpg与png文件
export LANG="en_US.UTF-8"

#批量重命名与移动
count=1;
for img in *.img *.png
do
	newimg=${img}-${count}.${img##*.}
	mv ${img} ${newimg} 2>/dev/null
	#成功
	if [ $? -eq 0 ];
	then

	echo "Renaming $img to $newimg"
	let count++

	fi
done

#other way


#将文件.JPG重命名为.jpg
rename *.JPG *.jpg
#将文件名中的空格替换成字符_
rename 's/ /_/g' *

#转换文件名大小写
rename 'y/A-Z/a-z/' *
rename 'y/a-z/A-Z/' *

#实例
#将所有mp3文件移入给定的目录
find . -type f -name "*.mp3" -exec mv {} ./mp3file \;
#将文件名的空格替换成_
find . -type f -exec rename 's/ /_/g' {} \;