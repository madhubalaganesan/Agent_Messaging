#!/bin/bash
declare -i n=10
declare -i i
a='"'
ans=''
for (( i=1; i<=n; i++))
	do
		if [ $i -eq 1 ]
		then
		ans=$a'A'$i':HelloWorld'
		else
		ans=$ans';A'$i':HelloWorld'
		fi
done
        ans=$ans$a
	echo $ans

java jade.Boot -platform-id Platform1 -container -host 54.187.155.50 -port 1099 -agents $ans
exit 0
