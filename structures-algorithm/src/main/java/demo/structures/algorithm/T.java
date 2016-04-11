package demo.structures.algorithm;

import java.util.LinkedList;


/**
 * 
 题目:
1、    设有n个球队要进行排球循环赛，设计一个满足以下要求的比赛日程表：

a)         每个球队必须与其他n-1个球队各赛一次；

b)        每个球队一天只能赛一次；

c)         当n是偶数时，循环赛进行n-1天。当n是奇数时，循环赛进行n天。

n=6的比赛日程表示例（把6个队从1到6进行编号）：

n=6的比赛日程表

第一天 1~2  3~5  4~6

第二天1~3 2~4  5~6

第三天1~4 2~5  3~6

第四天1~5 2~6  3~4

第五天1~6 2~3 4~5

n=5的比赛日程表示例（增加编号0，凡碰0者该天即轮空）：

n=5的比赛日程表

第一天 1~0 2~5 3~4

第二天1~5 0~4 2~3

第三天1~4 5~3 0~2

第四天1~3 4~2 5~0

第五天1~2 3~0 4~5


想了好久也看了很多参考资料  什么分治算法 。（看的不是很明白）

  后来在贴吧上看的有人跟了个很棒的思路！

------------------------------------------------------------------------------------------------

以6队为例，用数字表示，排成一个环形。
1号队不动，每一轮，逆时针转动，让各队从1号头上依次跳过，每移位一次得出的对阵就是本轮对阵。
无论有多少个队结果都是符合要求的
具体实现时用一个数组来处理就好了

---
1 4 
2 5 
3 6  
---
1 5 
4 6 
2 3 
--- 
1 6 
5 3 
4 2 
--- 
1 3 
6 2 
5 4 
---- 
1 2 
3 4 
6 5
----------------------------------------------
 * 
 * 
 * @author jack-yu
 *
 */
public class T {
	private int num; // 队伍数

	private LinkedList<Integer> list = new LinkedList<Integer>();

	public T(int n) {
		this.num = n;
		init();
	}

	private void init() {
		if (num % 2 == 0) // 偶数个队伍
		{
			for (int i = 0; i < num; i++) {
				list.add(i + 1);
			}
		} else // 奇数个队伍
		{
			for (int i = 0; i < num; i++) {
				list.add(i + 1);
			}
			list.add(0);
		}

	}

	public void print() {
		for (int i = 0; i < list.size() - 1; i++) {
			System.out.println("第" + (i + 1) + "天");
			for (int j = 0; j < list.size() / 2; j++) {
				System.out.println(list.get(j)  + "--" + list.get(list.size() - 1 - j));
			}
			int temp = list.pollLast(); // 移动
			list.add(1, temp);
		}
	}

	public static void main(String[] args) {
		T t = new T(6);
		t.print();
	}
}