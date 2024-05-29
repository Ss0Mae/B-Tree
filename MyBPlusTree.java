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
		if (r.keys.size() == m - 1 && r.children.size() == 0) { // root가 가득 차 있고 leaf 노드인 경우
			MyBPlusTreeNode s = new MyBPlusTreeNode(false, m); // 새로운 root 생성
			s.children.add(r); // 기존 root를 자식으로 추가
			root = s; // 새로운 root로 설정
			s.splitChild(0, r); // 기존 root를 split
			insertNonFull(s, key); // 새로운 root에 key 삽입

		} else {
			insertNonFull(r, key); // root가 가득 차 있지 않으면 root에 key 삽입
			if (r.keys.size() == m) { // 삽입 후 루트 노드가 가득 찬 경우
				MyBPlusTreeNode s = new MyBPlusTreeNode(false, m); // 새로운 root 생성
				s.children.add(r); // 기존 root를 자식으로 추가
				root = s; // 새로운 root로 설정
				s.splitChild(0, r); // 기존 root를 split
			}
		}
	}

	private void insertNonFull(MyBPlusTreeNode x, int k) { // x에 k를 삽입하는 메소드
		int i = x.keys.size() - 1; // 마지막 인덱스부터 시작
		if (x.isLeaf) { // x is a leaf node
			x.keys.add(null); // 공간 확보를 위해 null을 추가
			while (i >= 0 && k < x.keys.get(i)) { // k보다 큰 값들을 한 칸씩 뒤로 이동
				x.keys.set(i + 1, x.keys.get(i)); // 한 칸씩 뒤로 이동
				i--; // 이전 인덱스로 이동
			}
			x.keys.set(i + 1, k); // k를 삽입
		} else { // x is an internal node
			while (i >= 0 && k < x.keys.get(i)) { // k보다 큰 값들을 찾아내기
				i--;
			}
			i++; // i는 k보다 큰 값 중 가장 작은 값의 인덱스
			MyBPlusTreeNode ci = x.children.get(i); // i번째 자식 노드
			if (ci.keys.size() == m - 1) { // i번째 자식 노드가 가득 차 있으면
				x.splitChild(i, ci); // i번째 자식 노드를 split
				if (k > x.keys.get(i)) { // k가 i번째 자식 노드의 중간값보다 크면
					i++; // i를 1 증가
				}
			}
			insertNonFull(x.children.get(i), k); // i번째 자식 노드에 k 삽입
		}
	}

	public MyBPlusTreeNode getNode(Integer key) { // key를 가지고 있는 노드를 찾는 메소드
		MyBPlusTreeNode current = root; // root부터 시작
		while (!current.isLeaf) { // leaf 노드가 아닐 때까지
			int i = 0;
			while (i < current.keys.size() && key >= current.keys.get(i)) { // key보다 큰 값이 나올 때까지
				i++;
			}
			if (i < current.keys.size()) { // i가 마지막 인덱스가 아니면
				if (key < current.keys.get(i)) { // key보다 작은 값이면
					System.out.println("less than " + current.keys.get(i));
				} else { // key보다 크거나 같은 값이면
					System.out.println("larger than or equal to " + current.keys.get(i - 1));
				}
			} else { // i가 마지막 인덱스이면
				System.out.println("larger than or equal to " + current.keys.get(i - 1));
			}
			current = current.children.get(i);
		}

		int i = 0;
		while (i < current.keys.size() && key > current.keys.get(i)) { // key보다 큰 값이 나올 때까지
			i++;
		}

		if (i < current.keys.size() && key.equals(current.keys.get(i))) { // key를 찾은 경우
			System.out.println(key + " found");
			return current;
		} else { // key를 찾지 못한 경우
			System.out.println(key + " not found");
			return null;
		}
	}

	public void inorderTraverse() { // inorder traversal을 수행하는 메소드
		inorderTraverse(root); // root부터 시작
		System.out.println();
	}

	private void inorderTraverse(MyBPlusTreeNode node) { // inorder traversal을 수행하는 메소드
		if (node.isLeaf) { // leaf 노드인 경우
			for (Integer key : node.keys) {  // key를 출력
				System.out.println(key);
			}
		} else { // internal node인 경우
			for (int i = 0; i < node.children.size(); i++) {
				inorderTraverse(node.children.get(i)); // 자식 노드로 이동
			}
		}
	}


	@Override
	public Iterator<Integer> iterator() {
		return new Iterator<Integer>() { // iterator를 반환하는 메소드
			MyBPlusTreeNode current = root; // root부터 시작
			int keyIndex = 0;

			{
				while (!current.isLeaf) { // leaf 노드가 아닐 때까지
					current = current.children.get(0); // 가장 왼쪽 자식 노드로 이동
				}
			}

			@Override
			public boolean hasNext() { // 다음 값이 있는지 확인하는 메소드
				return keyIndex < current.keys.size() || current.next != null; // 다음 값이 있으면 true, 없으면 false
			}

			@Override
			public Integer next() { // 다음 값을 반환하는 메소드
				if (keyIndex >= current.keys.size()) { // 현재 노드의 key를 모두 반환한 경우
					current = current.next; // 다음 노드로 이동
					keyIndex = 0; // keyIndex 초기화
					if (current == null) throw new NoSuchElementException();
				}
				return current.keys.get(keyIndex++); // 다음 key 반환
			}
		};
	}

	@Override
	public NavigableSet<Integer> descendingSet() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Iterator<Integer> descendingIterator() {
		throw new UnsupportedOperationException();
	}

	@Override
	public NavigableSet<Integer> subSet(Integer fromElement, boolean fromInclusive, Integer toElement, boolean toInclusive) {
		throw new UnsupportedOperationException();
	}

	@Override
	public NavigableSet<Integer> headSet(Integer toElement, boolean inclusive) {
		throw new UnsupportedOperationException();
	}

	@Override
	public NavigableSet<Integer> tailSet(Integer fromElement, boolean inclusive) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int size() { // size를 반환하는 메소드
		return size(root);
	}

	private int size(MyBPlusTreeNode node) { // size를 반환하는 메소드
		if (node.isLeaf) {
			return node.keys.size(); // leaf 노드인 경우 key의 개수 반환
		} else {
			int size = 0;
			for (MyBPlusTreeNode child : node.children) {
				size += size(child);
			}
			return size; // internal node인 경우 자식 노드의 size를 합한 값 반환
		}
	}

	@Override
	public boolean isEmpty() {
		return root.keys.isEmpty(); // root의 key가 비어있으면 true, 아니면 false
	}

	@Override
	public boolean contains(Object o) {
		return contains(root, (Integer) o); // root부터 시작하여 key를 찾는 메소드
	}

	private boolean contains(MyBPlusTreeNode node, Integer key) { // key를 찾는 메소드
		int pos = Collections.binarySearch(node.keys, key); // 이진 탐색으로 key를 찾음
		if (pos >= 0) {
			return true; // key를 찾은 경우 true 반환
		} else if (node.isLeaf) {
			return false; // leaf 노드인 경우 key를 찾지 못한 경우 false 반환
		} else {
			pos = -(pos + 1); // key를 찾지 못한 경우 pos를 다시 설정
			return contains(node.children.get(pos), key); // 자식 노드로 이동
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
	public boolean add(Integer integer) { // integer를 추가하는 메소드
		add((int) integer); // add(int) 메소드 호출
		return true;
	}

	@Override
	public boolean remove(Object o) { // o를 삭제하는 메소드
		remove(root, (Integer) o);
		if (root.keys.size() == 0) {  // root의 key가 비어있으면
			if (!root.isLeaf) {
				root = root.children.get(0); // root가 leaf 노드가 아니면 root를 자식 노드로 변경
			} else {
				root = null; // root가 leaf 노드이면 root를 null로 변경
			}
		}
		return true;
	}


	private int findKey(MyBPlusTreeNode node, int key) {
		int idx = 0;
		while (idx < node.keys.size() && node.keys.get(idx) < key) {
			idx++;
		}
		return idx;
	}
	private void remove(MyBPlusTreeNode node, int key) {
		int idx = findKey(node, key);

		if (idx < node.keys.size() && node.keys.get(idx) == key) {
			if (node.isLeaf) {
				removeFromLeaf(node, idx);
			} else {
				removeFromNonLeaf(node, idx);
			}
		} else {
			if (node.isLeaf) {
				System.out.println("The key " + key + " does not exist in the tree.");
				return;
			}

			boolean flag = (idx == node.keys.size());

			if (node.children.get(idx).keys.size() < (m + 1) / 2) {
				fill(node, idx);
			}

			if (flag && idx > node.keys.size()) {
				remove(node.children.get(idx - 1), key);
			} else {
				remove(node.children.get(idx), key);
			}
		}
	}

	private void removeFromLeaf(MyBPlusTreeNode node, int idx) {
		for (int i = idx + 1; i < node.keys.size(); i++) {
			node.keys.set(i - 1, node.keys.get(i));
		}
		node.keys.remove(node.keys.size() - 1);
	}

	private void removeFromNonLeaf(MyBPlusTreeNode node, int idx) {
		int key = node.keys.get(idx);

		if (node.children.get(idx).keys.size() >= (m + 1) / 2) {
			int pred = getPred(node, idx);
			node.keys.set(idx, pred);
			remove(node.children.get(idx), pred);
		} else if (node.children.get(idx + 1).keys.size() >= (m + 1) / 2) {
			int succ = getSucc(node, idx);
			node.keys.set(idx, succ);
			remove(node.children.get(idx + 1), succ);
		} else {
			merge(node, idx);
			remove(node.children.get(idx), key);
		}
	}

	private int getPred(MyBPlusTreeNode node, int idx) {
		MyBPlusTreeNode current = node.children.get(idx);
		while (!current.isLeaf) {
			current = current.children.get(current.keys.size());
		}
		return current.keys.get(current.keys.size() - 1);
	}

	private int getSucc(MyBPlusTreeNode node, int idx) {
		MyBPlusTreeNode current = node.children.get(idx + 1);
		while (!current.isLeaf) {
			current = current.children.get(0);
		}
		return current.keys.get(0);
	}

	private void fill(MyBPlusTreeNode node, int idx) {
		if (idx != 0 && node.children.get(idx - 1).keys.size() >= (m + 1) / 2) {
			borrowFromPrev(node, idx);
		} else if (idx != node.keys.size() && node.children.get(idx + 1).keys.size() >= (m + 1) / 2) {
			borrowFromNext(node, idx);
		} else {
			if (idx != node.keys.size()) {
				merge(node, idx);
			} else {
				merge(node, idx - 1);
			}
		}
	}

	private void borrowFromPrev(MyBPlusTreeNode node, int idx) {
		MyBPlusTreeNode child = node.children.get(idx);
		MyBPlusTreeNode sibling = node.children.get(idx - 1);

		child.keys.add(0, node.keys.get(idx - 1));
		if (!child.isLeaf) {
			child.children.add(0, sibling.children.remove(sibling.children.size() - 1));
		}

		node.keys.set(idx - 1, sibling.keys.remove(sibling.keys.size() - 1));
	}

	private void borrowFromNext(MyBPlusTreeNode node, int idx) {
		MyBPlusTreeNode child = node.children.get(idx);
		MyBPlusTreeNode sibling = node.children.get(idx + 1);

		child.keys.add(node.keys.get(idx));
		if (!child.isLeaf) {
			child.children.add(sibling.children.remove(0));
		}

		node.keys.set(idx, sibling.keys.remove(0));
	}

	private void merge(MyBPlusTreeNode node, int idx) {
		MyBPlusTreeNode child = node.children.get(idx);
		MyBPlusTreeNode sibling = node.children.get(idx + 1);

		if (!child.isLeaf) {
			child.keys.add(node.keys.get(idx));
		}

		for (int i = 0; i < sibling.keys.size(); i++) {
			child.keys.add(sibling.keys.get(i));
		}

		if (!child.isLeaf) {
			for (int i = 0; i < sibling.children.size(); i++) {
				child.children.add(sibling.children.get(i));
			}
		}

		node.keys.remove(idx);
		node.children.remove(idx + 1);

		if (node == root && node.keys.size() == 0) {
			root = child;
		}
	}

//	public void printTreeStructure() { // B+ 트리의 구조를 출력
//		if (root == null) {
//			System.out.println("The tree is empty.");
//			return;
//		}
//
//		Queue<MyBPlusTreeNode> queue = new LinkedList<>();
//		queue.add(root);
//
//		while (!queue.isEmpty()) {
//			int levelSize = queue.size();
//			while (levelSize > 0) {
//				MyBPlusTreeNode node = queue.poll();
//				System.out.print("[");
//				for (int i = 0; i < node.keys.size(); i++) {
//					System.out.print(node.keys.get(i));
//					if (i < node.keys.size() - 1) {
//						System.out.print(", ");
//					}
//				}
//				System.out.print("] ");
//				if (!node.isLeaf) {
//					queue.addAll(node.children);
//				}
//				levelSize--;
//			}
//			System.out.println();
//		}
//	}

	@Override
	public boolean containsAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean addAll(Collection<? extends Integer> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean removeAll(Collection<?> c) {
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
	public Comparator<? super Integer> comparator() {
		throw new UnsupportedOperationException();
	}

	@Override
	public SortedSet<Integer> subSet(Integer fromElement, Integer toElement) {
		throw new UnsupportedOperationException();
	}

	@Override
	public SortedSet<Integer> headSet(Integer toElement) {
		throw new UnsupportedOperationException();
	}

	@Override
	public SortedSet<Integer> tailSet(Integer fromElement) {
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
