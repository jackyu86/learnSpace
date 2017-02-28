package DataStructures;

import java.util.Iterator;

public class MyArrayList<T> implements Iterable<T> {
	
	private static final int DEFAULTSIZE = 10 ;
	
	
	private int theSize;
	
	private T [] theItems;
	
	public MyArrayList(){
		//清空
		clear();
		//初始化数组
		ensureCapacity(DEFAULTSIZE);
	}
	public void clear(){
		this.theSize=0;
	}
	public void ensureCapacity(int newCapacity){
		if(newCapacity<this.theSize){
			return;
		}
		T [] old = theItems;
		theItems = (T[]) new Object[newCapacity];
		for(int i =0;i<size();i++){
			theItems[i] = old[i];
		}
	}
	public int size(){
		return this.theSize;
	}
	public boolean isempty(){
		return this.theSize==0;
	}
	public void trimtosize(){
		ensureCapacity(size());
	}
	public T get(int idx){
		if(idx<0||idx>=size())
			throw new ArrayIndexOutOfBoundsException(idx);
		return this.theItems[idx];
	}
	public T set(int idx,T v){
		if(idx<0||idx>=size())
			throw new ArrayIndexOutOfBoundsException(idx);
		T old = this.theItems[idx];
		this.theItems[idx]=v;
		return old;
	}
	public void add(T v){
		add(size(), v);
	}
	public void add(int idx,T v){
		if(this.theSize==size()){
			ensureCapacity(size()*2+1);
		}
		for(int i = this.theSize;i>idx;i--){
			this.theItems[i] = this.theItems[i-1];
			this.theItems[idx] = v;
		}
	}
	public T remove(int idx){
		T removeItem = get(idx);
		for(int i = idx;i<size()-1;i++){
			this.theItems[i] = this.theItems[i+1];
		}
		this.theSize--;
		return removeItem;
	}
	@Override
	public Iterator<T> iterator() {
		// TODO Auto-generated method stub
		return null;
	}
	
	private class ArrayListIterator implements Iterator<T>{

		private int count = 0;
		@Override
		public boolean hasNext() {
			return count<size();
		}

		@Override
		public T next() {
			return get(count);
		}

		@Override
		public void remove() {
			MyArrayList.this.remove(--count);
		}
		
	}
	

	
}
