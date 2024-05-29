package org.dfpl.lecture.database.assignment2.assignment2_20011727;

import java.util.*;

public class MyBPlusTree implements NavigableSet<Integer> {

	private MyBPlusTreeNode root;
	private int m; // 최대 자식 노드 수

	public MyBPlusTree(int m) {
		this.root = new MyBPlusTreeNode(true, m); // root 생성
		this.m = m;
	}

	public void add(int key) { // key를 추가하는 메소드
		MyBPlusTreeNode r = root; // root부터 시작
		if (r.keys.size() == m - 1 && r.children.size() == 0) { // root가 가득 차 있으면
			System.out.println("Root before splitting: " + r.keys);
			MyBPlusTreeNode s = new MyBPlusTreeNode(false, m); // 새로운 root 생성
			s.children.add(r); // 기존 root를 자식으로 추가
			root = s; // 새로운 root로 설정
			s.splitChild(0, r); // 기존 root를 split
			insertNonFull(s, key); // 새로운 root에 key 삽입
			System.out.println("Root after splitting: " + root.keys);

		} else {
			insertNonFull(r, key); // root가 가득 차 있지 않으면 root에 key 삽입
			if (r.keys.size() == m) { // 삽입 후 루트 노드가 가득 찬 경우
				MyBPlusTreeNode s = new MyBPlusTreeNode(false, m);
				s.children.add(r);
				root = s;
				s.splitChild(0, r);
			}

		}
	}

	/**
	 * 과제 Assignment4를 위한 메소드:
	 * key로 검색하면 root부터 시작하여, key를 포함할 수 있는 leaf node를 찾고 key가 실제로 존재하면 해당 Node를
	 * 반환하고, 그렇지 않다면 null을 반환한다. 중간과정을 System.out.println(String) 으로 출력해야 함.
	 *
	 * @param
	 * @return
	 */
	private void insertNonFull(MyBPlusTreeNode x, int k) { // x is not full when this function is called

		int i = x.keys.size() - 1; // 마지막 인덱스부터 시작
		if (x.isLeaf) { // x is a leaf node
			x.keys.add(null); // 공간 확보를 위해 null을 추가
			while (i >= 0 && k < x.keys.get(i)) { // k보다 큰 값들을 한 칸씩 뒤로 이동
				x.keys.set(i + 1, x.keys.get(i)); // 한 칸씩 뒤로 이동
				i--; // 이전 인덱스로 이동
			}
			x.keys.set(i + 1, k); // k를 삽입
		}
		else { // x is an internal node
			while (i >= 0 && k < x.keys.get(i)) { // k보다 큰 값들을 찾아내기
				i--;
			}
			i++; // i는 k보다 큰 값 중 가장 작은 값의 인덱스
			MyBPlusTreeNode ci = x.children.get(i); // i번째 자식 노드
			if (ci.keys.size() == m - 1) { // i번째 자식 노드가 가득 차 있으면
				System.out.println("Splitting child at index " + i + " with key " + k); // 중간 과정 출력
				x.splitChild(i, ci); // i번째 자식 노드를 split
				if (k > x.keys.get(i)) { // k가 i번째 자식 노드의 중간값보다 크면
					i++; // i를 1 증가
				}
			}
			insertNonFull(x.children.get(i), k); // i번째 자식 노드에 k 삽입
		}
	}

	public MyBPlusTreeNode getNode(Integer key) {
		// root부터 시작하여, key를 포함할 수 있는 leaf node를 찾고 key가 실제로 존재하면 해당 Node를 반환하고,
		// 그렇지 않다면 null을 반환한다. 중간과정을 System.out.println(String) 으로 출력해야 함.
		MyBPlusTreeNode current = root;
		while (!current.isLeaf) {
			int i = 0;
			while (i < current.keys.size() && key >= current.keys.get(i)) {
				i++;
			}
			if (i < current.keys.size()) {
				if (key < current.keys.get(i)) {
					System.out.println("less than " + current.keys.get(i));
				} else {
					System.out.println("larger than or equal to " + current.keys.get(i - 1));
				}
			} else {
				System.out.println("larger than or equal to " + current.keys.get(i - 1));
			}
			current = current.children.get(i);
		}

		int i = 0;
		while (i < current.keys.size() && key > current.keys.get(i)) {
			i++;
		}

		if (i < current.keys.size() && key.equals(current.keys.get(i))) {
			System.out.println(key + " found");
			return current;
		} else {
			System.out.println(key + " not found");
			return null;
		}
	}



	/**
	 * 과제 Assignment4를 위한 메소드:
	 * 재귀적으로 inorder traversal을 수행하여, 값을 오름차순으로 출력한다.
	 */
	public void inorderTraverse() {
		inorderTraverse(root);
		System.out.println(); // 줄 바꿈 추가
	}

	private void inorderTraverse(MyBPlusTreeNode node) {
		if (node.isLeaf) {
			for (Integer key : node.keys) {
				System.out.println(key);
			}
		} else {
			for (int i = 0; i < node.children.size(); i++) {
				inorderTraverse(node.children.get(i));
			}
		}
	}


	/**
	 * B+ 트리의 구조를 출력하는 메소드
	 */
	public void printTreeStructure() { // B+ 트리의 구조를 출력
		if (root == null) {
			System.out.println("The tree is empty.");
			return;
		}

		Queue<MyBPlusTreeNode> queue = new LinkedList<>();
		queue.add(root);

		while (!queue.isEmpty()) {
			int levelSize = queue.size();
			while (levelSize > 0) {
				MyBPlusTreeNode node = queue.poll();
				System.out.print("[");
				for (int i = 0; i < node.keys.size(); i++) {
					System.out.print(node.keys.get(i));
					if (i < node.keys.size() - 1) {
						System.out.print(", ");
					}
				}
				System.out.print("] ");
				if (!node.isLeaf) {
					queue.addAll(node.children);
				}
				levelSize--;
			}
			System.out.println();
		}
	}

	@Override
	public Iterator<Integer> iterator() {
		return new Iterator<Integer>() {
			MyBPlusTreeNode current = root;
			int keyIndex = 0;

			{
				while (!current.isLeaf) {
					current = current.children.get(0);
				}
			}

			@Override
			public boolean hasNext() {
				return keyIndex < current.keys.size() || current.next != null;
			}

			@Override
			public Integer next() {
				if (keyIndex >= current.keys.size()) {
					current = current.next;
					keyIndex = 0;
					if (current == null) throw new NoSuchElementException();
				}
				return current.keys.get(keyIndex++);
			}
		};
	}

	@Override
	public NavigableSet<Integer> descendingSet() {
		return null;
	}

	@Override
	public Iterator<Integer> descendingIterator() {
		throw new UnsupportedOperationException();
	}

	@Override
	public NavigableSet<Integer> subSet(Integer fromElement, boolean fromInclusive, Integer toElement, boolean toInclusive) {
		return null;
	}

	@Override
	public NavigableSet<Integer> headSet(Integer toElement, boolean inclusive) {
		return null;
	}

	@Override
	public NavigableSet<Integer> tailSet(Integer fromElement, boolean inclusive) {
		return null;
	}

	@Override
	public int size() {
		return size(root);
	}

	private int size(MyBPlusTreeNode node) {
		if (node.isLeaf) {
			return node.keys.size();
		} else {
			int size = 0;
			for (MyBPlusTreeNode child : node.children) {
				size += size(child);
			}
			return size;
		}
	}

	@Override
	public boolean isEmpty() {
		return root.keys.isEmpty();
	}

	@Override
	public boolean contains(Object o) {
		return contains(root, (Integer) o);
	}

	private boolean contains(MyBPlusTreeNode node, Integer key) {
		int pos = Collections.binarySearch(node.keys, key);
		if (pos >= 0) {
			return true;
		} else if (node.isLeaf) {
			return false;
		} else {
			pos = -(pos + 1);
			return contains(node.children.get(pos), key);
		}
	}

	@Override
	public Object[] toArray() {
		throw new UnsupportedOperationException();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean add(Integer integer) {
		return false;
	}

	@Override
	public boolean remove(Object o) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean containsAll(java.util.Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean addAll(java.util.Collection<? extends Integer> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean retainAll(java.util.Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean removeAll(java.util.Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void clear() {
		root = new MyBPlusTreeNode(true, m);
	}

	@Override
	public Integer lower(Integer e) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Integer floor(Integer e) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Integer ceiling(Integer e) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Integer higher(Integer e) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Integer pollFirst() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Integer pollLast() {
		throw new UnsupportedOperationException();
	}

	@Override
	public java.util.Comparator<? super Integer> comparator() {
		throw new UnsupportedOperationException();
	}

	@Override
	public java.util.SortedSet<Integer> subSet(Integer fromElement, Integer toElement) {
		throw new UnsupportedOperationException();
	}

	@Override
	public java.util.SortedSet<Integer> headSet(Integer toElement) {
		throw new UnsupportedOperationException();
	}

	@Override
	public java.util.SortedSet<Integer> tailSet(Integer fromElement) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Integer first() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Integer last() {
		throw new UnsupportedOperationException();
	}
}
