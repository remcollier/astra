package graph.util;

import java.util.Comparator;

public class Heap<K, V> implements PriorityQueue<K, V> {
	private class Entry implements Position<V> {
		int index;
		K key;
		V value;

		public Entry(K key, V value, int index) {
			this.key = key;
			this.value = value;
			this.index = index;
		}
		
		public String toString() {
			return "{" + key + "," + value + "," + index + "}";
		}

		@Override
		public V element() {
			return value;
		}
		
	}
	
	private Comparator<K> comparator;
	private Object[] array;
	private int end;
	
	public Heap() {
		this(new Comparator<K>() {
			@SuppressWarnings("unchecked")
			@Override
			public int compare(K key0, K key1) {
				if (Comparable.class.isInstance(key0)) {
					return ((Comparable) key0).compareTo(key1);
				}
				
				throw new UncomparableException();
			}
			
		});
	}
	
	@SuppressWarnings("unchecked")
	public Heap(Comparator<K> comparator) {
		this.comparator = comparator;
		array = new Object[20];
		end = 1;
	}
	
	@SuppressWarnings("unchecked")
	public void doubleArray() {
		Object[] temp = new Object[array.length*2];
		System.arraycopy(array, 0, temp, 0, array.length);
		array = temp;
	}
	
	@Override
	public int size() {
		return end-1;
	}

	@Override
	public boolean isEmpty() {
		return end == 1;
	}


	@Override
	public Position<V> replaceKey(Position<V> position, K key) {
		Entry entry = (Entry) position;
		
		entry.key = key;
		
		int p = parent(entry.index);
		if ((p > 0) && comparator.compare(getEntry(p).key, entry.key) > 0) {
			upHeap(entry.index);
		} else {
			//System.out.println("size: " + this.size());
			//System.out.println("heap" + toString());
			//System.out.println("entry.index: " + entry.index);
			downHeap(entry.index);
		}
		
		return entry;
	}
	
	@Override
	public Position<V> insert(K key, V value) {
		if (end == array.length) doubleArray();
		
		array[end] = new Entry(key, value, end);

		int x = upHeap(end);
		end++;
		return getEntry(x);
	}
	
	private int upHeap(int start) {
		int x = start;
		int p = parent(x);
		while ((x > 1) && (comparator.compare(getEntry(p).key, getEntry(x).key) > 0)) {
			Object temp = array[p];
			array[p] = array[x];
			array[x] = temp;
			
			getEntry(p).index = p;
			getEntry(x).index = x;
			
			// move up a level
			x = p;
			p = parent(x);
		}
		return x;
	}

	@Override
	public V min() {
		if (end == 1) throw new HeapEmptyException();
		return getEntry(1).value;
	}

	@Override
	public V remove() {
		V temp = getEntry(1).value;
		array[1] = array[end-1];
		getEntry(1).index = 1;
		array[end-1] = null;
		end--;
		
		if (end == 1) return temp;

		downHeap(1);
		
		return temp;
	}
	
	private int downHeap(int start) {
		int x = start;
		int c = smallerChild(x);
		while ((x < end) && (c > 0)) {
			Object t = array[c];
			array[c] = array[x];
			array[x] = t;
			
			getEntry(c).index = c;
			getEntry(x).index = x;
			
			// move up a level
			x = c;
			c = smallerChild(x);
		}
		
		return x;
	}

	private int parent(int child) {
		return child/2;
	}
	
	private int smallerChild(int parent) {
		K p = getEntry(parent).key;
		if (end == 1+parent*2) {
			K c1 = getEntry(parent*2).key;
			if (comparator.compare(p, c1) > 0) {
				return parent*2;
			}
		} else if (end > 1+parent*2) {
			K c1 = getEntry(parent*2).key;
			K c2 = getEntry(1+ parent*2).key;
			if (comparator.compare(c1, c2) < 0) {
				if (comparator.compare(p, c1) > 0) {
					return parent*2;
				}
			} else {
				if (comparator.compare(p, c2) > 0) {
					return 1+parent*2;
				}
			}
		}
		return -1;
	}
	
	@SuppressWarnings("unchecked")
	private Entry getEntry(int index) {
		return (Entry) array[index];
	}

	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append("[");
		int i = 1;
		while (i < end) {
			if (i > 1) buf.append(" ");
			buf.append(array[i]);
			i++;
		}
		buf.append("]");
		return buf.toString();
	}
	
	public static void main(String[] args) {
		Heap<Integer, String> heap = new Heap<Integer, String>();
		System.out.println("Heap: " + heap.toString());
		heap.insert(3, "Rem");
		System.out.println("Heap: " + heap.toString());
		heap.insert(5, "Bob");
		System.out.println("Heap: " + heap.toString());
		heap.insert(7, "Fred");
		System.out.println("Heap: " + heap.toString());
		heap.insert(9, "Henry");
		System.out.println("Heap: " + heap.toString());
		heap.insert(11, "George");
		System.out.println("Heap: " + heap.toString());
		Position<String> p = heap.insert(13, "Hans");
		System.out.println("Heap: " + heap.toString());
		heap.insert(1, "Niki");
		System.out.println("Heap: " + heap.toString());
		heap.remove();
		System.out.println("Heap: " + heap.toString());
		heap.replaceKey(p, 6);
		System.out.println("Heap: " + heap.toString());
		heap.remove();
		System.out.println("Heap: " + heap.toString());
		heap.remove();
		System.out.println("Heap: " + heap.toString());
		heap.remove();
		System.out.println("Heap: " + heap.toString());
		heap.remove();
		System.out.println("Heap: " + heap.toString());
		heap.remove();
		System.out.println("Heap: " + heap.toString());
		heap.remove();
		System.out.println("Heap: " + heap.toString());
	}
}
