package org.dfpl.lecture.database.assignment2.assignment2_20011727;

import java.util.*;

public class MyBPlusTree implements NavigableSet<Integer> {

	private MyBPlusTreeNode root;
	private final int m;

	public MyBPlusTree(int m) {
		if (m < 3) throw new IllegalArgumentException("Order must be at least 3.");
		this.m = m;
		this.root = new MyBPlusTreeNode(true);
	}

	public MyBPlusTreeNode getNode(Integer key) {
		MyBPlusTreeNode current = root;
		while (!current.isLeaf()) {
			List<Integer> keys = current.getKeyList();
			List<MyBPlusTreeNode> children = current.getChildren();
			int i = 0;

			// 키 비교 및 중간 과정 출력
			while (i < keys.size() && key >= keys.get(i)) {
				i++;
			}

			if (i < keys.size()) {
				System.out.println("less than " + keys.get(i));
			}
			if (i > 0) {
				System.out.println("larger than or equal to " + keys.get(i - 1));
			} else {
				System.out.println("larger than or equal to " + keys.get(0));
			}

			// 다음 노드로 이동
			current = children.get(i);
		}

		// 리프 노드에서 키 검색 및 출력
		int idx = Collections.binarySearch(current.getKeyList(), key);
		if (idx >= 0) {
			System.out.println(key + " found");
			return current;
		} else {
			System.out.println(key + " not found");
			return null;
		}
	}




	public void inorderTraverse() {
		inorderTraverse(root);
	}

	private void inorderTraverse(MyBPlusTreeNode node) {
		if (node.isLeaf()) {
			for (Integer key : node.getKeyList()) {
				System.out.print(key + " ");
			}
		} else {
			List<MyBPlusTreeNode> children = node.getChildren();
			List<Integer> keys = node.getKeyList();
			for (int i = 0; i < keys.size(); i++) {
				inorderTraverse(children.get(i));
				System.out.print(keys.get(i) + " ");
			}
			inorderTraverse(children.get(keys.size()));
		}
	}

	@Override
	public boolean add(Integer e) {
		MyBPlusTreeNode leaf = findLeafNode(e);
		leaf.addKey(e);
		if (leaf.getKeyList().size() > m - 1) {
			split(leaf);
		}
		return true;
	}

	@Override
	public boolean remove(Object o) {
		return false;
	}

	private MyBPlusTreeNode findLeafNode(Integer key) {
		MyBPlusTreeNode current = root;
		while (!current.isLeaf()) {
			List<Integer> keys = current.getKeyList();
			List<MyBPlusTreeNode> children = current.getChildren();
			int i = 0;
			while (i < keys.size() && key >= keys.get(i)) {
				i++;
			}
			current = children.get(i);
		}
		return current;
	}

	private void split(MyBPlusTreeNode node) {
		int midIndex = (m + 1) / 2;
		MyBPlusTreeNode newNode = new MyBPlusTreeNode(node.isLeaf());

		List<Integer> keys = node.getKeyList();
		List<MyBPlusTreeNode> children = node.getChildren();

		newNode.getKeyList().addAll(keys.subList(midIndex, keys.size()));
		keys.subList(midIndex, keys.size()).clear();

		if (!node.isLeaf()) {
			newNode.getChildren().addAll(children.subList(midIndex, children.size()));
			for (MyBPlusTreeNode child : newNode.getChildren()) {
				child.setParent(newNode); // 부모 노드 설정
			}
			children.subList(midIndex, children.size()).clear();
		}

		if (node == root) {
			MyBPlusTreeNode newRoot = new MyBPlusTreeNode(false);
			newRoot.getKeyList().add(newNode.getKeyList().remove(0));
			newRoot.getChildren().add(node);
			newRoot.getChildren().add(newNode);
			root = newRoot;
			node.setParent(newRoot); // 부모 노드 설정
			newNode.setParent(newRoot); // 부모 노드 설정
		} else {
			MyBPlusTreeNode parent = node.getParent();
			parent.addKey(newNode.getKeyList().remove(0));
			parent.addChild(newNode);
			newNode.setParent(parent); // 부모 노드 설정
			if (parent.getKeyList().size() > m - 1) {
				split(parent);
			}
		}
	}

	@Override
	public Integer lower(Integer e) {
		throw new UnsupportedOperationException("Not implemented yet.");
	}

	@Override
	public Integer floor(Integer e) {
		throw new UnsupportedOperationException("Not implemented yet.");
	}

	@Override
	public Integer ceiling(Integer e) {
		throw new UnsupportedOperationException("Not implemented yet.");
	}

	@Override
	public Integer higher(Integer e) {
		throw new UnsupportedOperationException("Not implemented yet.");
	}

	@Override
	public Integer pollFirst() {
		throw new UnsupportedOperationException("Not implemented yet.");
	}

	@Override
	public Integer pollLast() {
		throw new UnsupportedOperationException("Not implemented yet.");
	}

	@Override
	public Comparator<? super Integer> comparator() {
		return null;
	}

	@Override
	public SortedSet<Integer> subSet(Integer fromElement, Integer toElement) {
		throw new UnsupportedOperationException("Not implemented yet.");
	}

	@Override
	public SortedSet<Integer> headSet(Integer toElement) {
		throw new UnsupportedOperationException("Not implemented yet.");
	}

	@Override
	public SortedSet<Integer> tailSet(Integer fromElement) {
		throw new UnsupportedOperationException("Not implemented yet.");
	}

	@Override
	public Integer first() {
		throw new UnsupportedOperationException("Not implemented yet.");
	}

	@Override
	public Integer last() {
		throw new UnsupportedOperationException("Not implemented yet.");
	}

	@Override
	public int size() {
		throw new UnsupportedOperationException("Not implemented yet.");
	}

	@Override
	public boolean isEmpty() {
		throw new UnsupportedOperationException("Not implemented yet.");
	}

	@Override
	public boolean contains(Object o) {
		throw new UnsupportedOperationException("Not implemented yet.");
	}

	@Override
	public Iterator<Integer> iterator() {
		throw new UnsupportedOperationException("Not implemented yet.");
	}

	@Override
	public NavigableSet<Integer> descendingSet() {
		return null;
	}

	@Override
	public Iterator<Integer> descendingIterator() {
		return null;
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
	public Object[] toArray() {
		throw new UnsupportedOperationException("Not implemented yet.");
	}

	@Override
	public <T> T[] toArray(T[] a) {
		throw new UnsupportedOperationException("Not implemented yet.");
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		throw new UnsupportedOperationException("Not implemented yet.");
	}

	@Override
	public boolean addAll(Collection<? extends Integer> c) {
		throw new UnsupportedOperationException("Not implemented yet.");
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		throw new UnsupportedOperationException("Not implemented yet.");
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		throw new UnsupportedOperationException("Not implemented yet.");
	}

	@Override
	public void clear() {
		root = new MyBPlusTreeNode(true);
	}
}
