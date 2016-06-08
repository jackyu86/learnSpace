package demo.structures.algorithm;

public class BinarySearch {
	
	
	
	static int binay(int k,int [] array,int left,int right){
		
		int l = left-1;
		int r = left+1;
		while(l+1!=r){
			
			int i =(l+r)/2;
			//k在数组的左半部分
			if(k<array[i]){
				r=i;
			}
			if(k==array[i]){
				return i;
			}
			//k在数组的右半部分
			if(k>array[i]){
				l=i;
			}
			
		}
		
		
		return -1;
	}

}
