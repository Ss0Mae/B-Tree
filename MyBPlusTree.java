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
			while (i < keys.size() && key >= keys.get(i)) {
				i++;
			}
			if (i < keys.size() && key < keys.get(i)) {
				System.out.println("> less than " + keys.get(i));
			} else if (i > 0) {
				System.out.println("> larger than or equal to " + keys.get(i - 1));
			} else {
				System.out.println("> larger than or equal to " + keys.get(0));
			}
			current = children.get(i);
		}

		int idx = Collections.binarySearch(current.getKeyList(), key);
		if (idx >= 0) {
			System.out.println("> " + key + " found");
			return current;
		} else {
			System.out.println("> " + key + " not found");
			return null;
		}
	}

	public void inorderTraverse() {
		inorderTraverse(root);
	}

	private void inorderTraverse(MyBPlusTreeNode node) {
		if (node.isLeaf()) {
			for (Integer key : node.getKeyList()) {
				System.out.println(key);
			}
		} else {
			List<MyBPlusTreeNode> children = node.getChildren();
			List<Integer> keys = node.getKeyList();
			for (int i = 0; i < keys.size(); i++) {
				inorderTraverse(children.get(i));
				System.out.println(keys.get(i));
			}
			inorderTraverse(children.get(keys.size()));
		}
	}

	@Override
	public boolean add(Integer e) {
		MyBPlusTreeNode leaf = findLeafNode(e);
		if (leaf.getKeyList().contains(e)) {
			return false; // 이미 존재하는 키는 추가하지 않음
		}
		leaf.addKey(e);
		if (leaf.getKeyList().size() >= m) {
			splitLeafNode(leaf);
		}
		return true;
	}

	private MyBPlusTreeNode findLeafNode(Integer key) {
		MyBPlusTreeNode node = root;
		while (!node.isLeaf()) {
			List<Integer> keys = node.getKeyList();
			List<MyBPlusTreeNode> children = node.getChildren();
			int i = 0;
			while (i < keys.size() && key >= keys.get(i)) {
				i++;
			}
			node = children.get(i);
		}
		return node;
	}

	private void insertIntoParent(MyBPlusTreeNode oldNode, Integer key, MyBPlusTreeNode newNode) {
		MyBPlusTreeNode parent = oldNode.getParent();
		if (parent == null) {
			MyBPlusTreeNode newRoot = new MyBPlusTreeNode(false);
			newRoot.addKey(key);
			newRoot.addChild(oldNode);
			newRoot.addChild(newNode);
			root = newRoot;
			oldNode.setParent(newRoot);
			newNode.setParent(newRoot);
			return;
		}

		parent.addKey(key);
		parent.getChildren().add(parent.getKeyList().indexOf(key) + 1, newNode);
		newNode.setParent(parent);
		if (parent.getKeyList().size() >= m) {
			splitInternalNode(parent);
		}
	}

	private void splitLeafNode(MyBPlusTreeNode leaf) {
		MyBPlusTreeNode newLeaf = new MyBPlusTreeNode(true);
		int mid = (m + 1) / 2;
		List<Integer> leafKeys = leaf.getKeyList();
		newLeaf.getKeyList().addAll(leafKeys.subList(mid, leafKeys.size()));
		leafKeys.subList(mid, leafKeys.size()).clear();

		newLeaf.setNext(leaf.getNext());
		leaf.setNext(newLeaf);

		if (leaf == root) {
			MyBPlusTreeNode newRoot = new MyBPlusTreeNode(false);
			newRoot.getKeyList().add(newLeaf.getKeyList().get(0));
			newRoot.getChildren().add(leaf);
			newRoot.getChildren().add(newLeaf);
			root = newRoot;
			leaf.setParent(newRoot);
			newLeaf.setParent(newRoot);
		} else {
			newLeaf.setParent(leaf.getParent());
			insertIntoParent(leaf, newLeaf.getKeyList().get(0), newLeaf);
		}
	}

	private void splitInternalNode(MyBPlusTreeNode node) {
		MyBPlusTreeNode newInternal = new MyBPlusTreeNode(false);
		int mid = m / 2;
		List<Integer> nodeKeys = node.getKeyList();
		newInternal.getKeyList().addAll(nodeKeys.subList(mid + 1, nodeKeys.size()));
		nodeKeys.subList(mid + 1, nodeKeys.size()).clear();

		newInternal.getChildren().addAll(node.getChildren().subList(mid + 1, node.getChildren().size()));
		node.getChildren().subList(mid + 1, node.getChildren().size()).clear();

		for (MyBPlusTreeNode child : newInternal.getChildren()) {
			child.setParent(newInternal);
		}

		if (node == root) {
			MyBPlusTreeNode newRoot = new MyBPlusTreeNode(false);
			newRoot.getKeyList().add(nodeKeys.remove(mid));
			newRoot.getChildren().add(node);
			newRoot.getChildren().add(newInternal);
			node.setParent(newRoot);
			newInternal.setParent(newRoot);
			root = newRoot;
		} else {
			insertIntoParent(node, nodeKeys.remove(mid), newInternal);
		}
	}

	@Override
	public boolean remove(Object o) {
		if (!(o instanceof Integer)) return false;
		Integer key = (Integer) o;
		MyBPlusTreeNode node = getNodeForRemove(key);
		if (node == null) return false;

		node.getKeyList().remove(key);
		if (node == root) {
			if (node.getKeyList().isEmpty() && !node.isLeaf()) {
				root = node.getChildren().isEmpty() ? null : node.getChildren().get(0);
				if (root != null) root.setParent(null);
			}
			return true;
		}

		// Underflow 처리 필요
		while (node.getKeyList().size() < (m - 1) / 2) {
			MyBPlusTreeNode parent = node.getParent();
			if (parent == null) break; // Parent가 없으면 종료

			int idx = parent.getChildren().indexOf(node);
			if (idx < 0 || idx >= parent.getChildren().size()) {
				throw new IndexOutOfBoundsException("Invalid child index");
			}

			MyBPlusTreeNode sibling;
			boolean borrowFromLeft = false;

			if (idx > 0 && parent.getChildren().get(idx - 1).getKeyList().size() > (m - 1) / 2) {
				sibling = parent.getChildren().get(idx - 1);
				borrowFromLeft = true;
			} else if (idx < parent.getChildren().size() - 1 && parent.getChildren().get(idx + 1).getKeyList().size() > (m - 1) / 2) {
				sibling = parent.getChildren().get(idx + 1);
			} else {
				if (idx > 0) {
					sibling = parent.getChildren().get(idx - 1);
					sibling.getKeyList().addAll(node.getKeyList());
					if (!node.isLeaf()) {
						sibling.getChildren().addAll(node.getChildren());
					}
					parent.getKeyList().remove(idx - 1);
					parent.getChildren().remove(idx);
				} else if (idx < parent.getChildren().size() - 1) {
					sibling = parent.getChildren().get(idx + 1);
					node.getKeyList().addAll(sibling.getKeyList());
					if (!sibling.isLeaf()) {
						node.getChildren().addAll(sibling.getChildren());
					}
					parent.getKeyList().remove(idx);
					parent.getChildren().remove(idx + 1);
				} else {
					break; // No valid sibling to borrow or merge
				}
				if (parent == root && parent.getKeyList().isEmpty()) {
					root = sibling;
					root.setParent(null);
				}
				return true;
			}

			// Borrow key from sibling
			if (borrowFromLeft) {
				node.getKeyList().add(0, parent.getKeyList().get(idx - 1));
				parent.getKeyList().set(idx - 1, sibling.getKeyList().remove(sibling.getKeyList().size() - 1));
				if (!sibling.isLeaf()) {
					node.getChildren().add(0, sibling.getChildren().remove(sibling.getChildren().size() - 1));
				}
			} else {
				node.getKeyList().add(parent.getKeyList().get(idx));
				parent.getKeyList().set(idx, sibling.getKeyList().remove(0));
				if (!sibling.isLeaf()) {
					node.getChildren().add(sibling.getChildren().remove(0));
				}
			}
		}
		return true;
	}

	private MyBPlusTreeNode getNodeForRemove(Integer key) {
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

		int idx = Collections.binarySearch(current.getKeyList(), key);
		if (idx >= 0) {
			return current;
		} else {
			return null;
		}
	}

	@Override
	public Integer lower(Integer e) {
		return getLowerOrHigher(e, false);
	}

	@Override
	public Integer floor(Integer e) {
		return getFloorOrCeiling(e, false);
	}

	@Override
	public Integer ceiling(Integer e) {
		return getFloorOrCeiling(e, true);
	}

	@Override
	public Integer higher(Integer e) {
		return getLowerOrHigher(e, true);
	}

	private Integer getFloorOrCeiling(Integer e, boolean isCeiling) {
		MyBPlusTreeNode current = root;
		Integer result = null;
		while (!current.isLeaf()) {
			List<Integer> keys = current.getKeyList();
			List<MyBPlusTreeNode> children = current.getChildren();
			int i = 0;
			while (i < keys.size() && (isCeiling ? e > keys.get(i) : e >= keys.get(i))) {
				i++;
			}
			current = children.get(i);
		}

		for (Integer key : current.getKeyList()) {
			if (isCeiling) {
				if (key >= e) {
					result = key;
					break;
				}
			} else {
				if (key <= e) {
					result = key;
				} else {
					break;
				}
			}
		}
		return result;
	}

	private Integer getLowerOrHigher(Integer e, boolean isHigher) {
		MyBPlusTreeNode current = root;
		Integer result = null;
		while (!current.isLeaf()) {
			List<Integer> keys = current.getKeyList();
			List<MyBPlusTreeNode> children = current.getChildren();
			int i = 0;
			while (i < keys.size() && (isHigher ? e >= keys.get(i) : e > keys.get(i))) {
				i++;
			}
			current = children.get(i);
		}

		for (Integer key : current.getKeyList()) {
			if (isHigher) {
				if (key > e) {
					result = key;
					break;
				}
			} else {
				if (key < e) {
					result = key;
				} else {
					break;
				}
			}
		}
		return result;
	}

	@Override
	public Comparator<? super Integer> comparator() {
		return null;
	}

	@Override
	public Integer first() {
		MyBPlusTreeNode node = root;
		while (!node.isLeaf()) {
			node = node.getChildren().get(0);
		}
		return node.getKeyList().get(0);
	}

	@Override
	public Integer last() {
		MyBPlusTreeNode node = root;
		while (!node.isLeaf()) {
			node = node.getChildren().get(node.getChildren().size() - 1);
		}
		return node.getKeyList().get(node.getKeyList().size() - 1);
	}

	@Override
	public int size() {
		int size = 0;
		MyBPlusTreeNode node = root;
		while (!node.isLeaf()) {
			node = node.getChildren().get(0);
		}
		while (node != null) {
			size += node.getKeyList().size();
			node = node.getNext();
		}
		return size;
	}

	@Override
	public boolean isEmpty() {
		return root.getKeyList().isEmpty();
	}

	@Override
	public boolean contains(Object o) {
		if (!(o instanceof Integer)) return false;
		Integer key = (Integer) o;
		return getNode(key) != null;
	}

	@Override
	public Iterator<Integer> iterator() {
		return new BPlusTreeIterator();
	}

	@Override
	public Integer pollFirst() {
		if (isEmpty()) return null;
		Integer first = first();
		remove(first);
		return first;
	}

	@Override
	public Integer pollLast() {
		if (isEmpty()) return null;
		Integer last = last();
		remove(last);
		return last;
	}

	private class BPlusTreeIterator implements Iterator<Integer> {
		private MyBPlusTreeNode current;
		private int index;

		public BPlusTreeIterator() {
			this.current = root;
			while (!current.isLeaf()) {
				current = current.getChildren().get(0);
			}
			this.index = 0;
		}

		@Override
		public boolean hasNext() {
			return current != null && index < current.getKeyList().size();
		}

		@Override
		public Integer next() {
			if (!hasNext()) throw new NoSuchElementException();
			Integer result = current.getKeyList().get(index);
			index++;
			if (index >= current.getKeyList().size()) {
				current = current.getNext();
				index = 0;
			}
			return result;
		}
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
	public Object[] toArray() {
		throw new UnsupportedOperationException();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		for (Object o : c) {
			if (!contains(o)) return false;
		}
		return true;
	}

	@Override
	public boolean addAll(Collection<? extends Integer> c) {
		boolean modified = false;
		for (Integer e : c) {
			if (add(e)) modified = true;
		}
		return modified;
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		boolean modified = false;
		for (Object o : c) {
			if (remove(o)) modified = true;
		}
		return modified;
	}

	@Override
	public void clear() {
		root = new MyBPlusTreeNode(true);
	}
}
