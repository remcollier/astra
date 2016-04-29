package graph.util;


import java.util.Iterator;


public class LinkedList<T> implements List<T> {
	private class Node implements Position<T> {
		T element;
		Node prev;
		Node next;
		
		public Node(T element, Node prev, Node next) { 
			this.element = element;
			this.prev = prev;
			this.next = next;
		}
		 
		@Override
		public T element() {
			return element;
		}
	}

	private class ListIterator implements Iterator<T> {
		Node cur;
		boolean isFirst;
		
		public ListIterator() {
			isFirst = true;
		}
		
		@Override
		public boolean hasNext() {
			if ((cur == null) && isFirst) {
				cur = first;
				isFirst = false;
			} else {
				cur = cur.next;
			}
			
			return cur != null;
		}

		@Override
		public T next() {
			return cur.element;
		}

		@Override
		public void remove() {
			LinkedList.this.remove(cur);
			
		}
	}
	
	private Node first;
	private Node last;
	private int size;
	
	public LinkedList() {
		first = last = null;
		size = 0;
	}
	
	@Override
	public int size() {
		return size;
	}

	@Override
	public boolean isEmpty() {
		return size == 0;
	}

	@Override
	public Position<T> first() {
		if (first == null) throw new ListEmptyException();
		return first;
	}

	@Override
	public Position<T> last() {
		if (last == null) throw new ListEmptyException();
		return last;
	}

	private Node toNode(Position<T> p) {
		return (Node) p;
	}
	
	@Override
	public Position<T> prev(Position<T> p) {
		if (p == first) throw new BoundaryViolationException();
		Node node = toNode(p);
		return node.prev;
	}

	@Override
	public Position<T> next(Position<T> p) {
		if (p == last) throw new BoundaryViolationException();
		Node node = toNode(p);
		return node.next;
	}

	@Override
	public Position<T> insertFirst(T e) {
		Node node = new Node(e, null, first);
		if (first != null) {
			first.prev = node;
		} else {
			last = node;
		}
		first = node;
		size++;
		return node;
	}

	@Override
	public Position<T> insertLast(T e) {
		Node node = new Node(e, last, null);
		if (last != null) {
			last.next = node;
		} else {
			first = node;
		}
		last = node;
		size++;
		return node;
	}

	@Override
	public Position<T> insertBefore(Position<T> p, T e) {
		Node node = toNode(p);
		if (node == first) {
			return insertFirst(e);
		}
		
		Node newNode = new Node(e, node.prev, node);
		node.prev.next = newNode;
		node.prev = newNode;
		size++;
		return newNode;
	}

	@Override
	public Position<T> insertAfter(Position<T> p, T e) {
		Node node = toNode(p);
		if (node == last) {
			return insertLast(e);
		}
		
		Node newNode = new Node(e, node, node.next);
		node.next.prev = newNode;
		node.next = newNode;
		size++;
		return newNode;
	}

	@Override
	public T replace(Position<T> p, T e) {
		Node node = toNode(p);
		T temp = node.element;
		node.element = e;
		return temp;
	}

	@Override
	public T remove(Position<T> p) {
		Node node = toNode(p);
		
		if (node == first) {
			first = first.next;
		} else {
			node.prev.next = node.next;
		}
		
		if (node == last) {
			last = last.prev;
		} else {
			node.next.prev = node.prev;
		}
		
		size--;
		
		return node.element;
	}

	public String toString() {
		if (first == null) return "EMPTY";
		
		StringBuffer buf = new StringBuffer();
		Node cur = first;
		while (cur != last) {
			buf.append(cur.element.toString() + " ");
			cur = cur.next;
		}
		buf.append(cur.element.toString());
		return buf.toString();
	}
	
	public static void main(String[] args) {
		LinkedList<String> list = new LinkedList<String>();
		Position<String> p1 = list.insertFirst("Rem");
		System.out.println("list: " + list);
		list.insertLast("Niki");
		System.out.println("list: " + list);
		Position<String> p3 = list.insertAfter(p1, "Coral");
		System.out.println("list: " + list);
		list.insertBefore(p3, "Tanya");
		System.out.println("list: " + list);
		Iterator it = list.iterator();
		while (it.hasNext()) {
			for (String s : list) {
				System.out.println(s);
			}
			it.remove();
		}
	}

	@Override
	public Iterator<T> iterator() {
		return new ListIterator();
	}
}
