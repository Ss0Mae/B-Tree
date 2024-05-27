package org.dfpl.lecture.database.assignment2.assignment2_20011727;

import java.util.ArrayList;
import java.util.List;

public class MyBPlusTreeNode {

	private MyBPlusTreeNode parent;
	private List<Integer> keyList;
	private List<MyBPlusTreeNode> children;
	private boolean isLeaf;
	private MyBPlusTreeNode next;

	public MyBPlusTreeNode(boolean isLeaf) {
		this.isLeaf = isLeaf;
		this.keyList = new ArrayList<>();
		this.children = new ArrayList<>();
	}

	public void addKey(Integer key) {
		int pos = 0;
		while (pos < keyList.size() && keyList.get(pos) < key) {
			pos++;
		}
		keyList.add(pos, key);
	}

	public void addChild(MyBPlusTreeNode child) {
		if (this.isLeaf) {
			throw new UnsupportedOperationException("Leaf nodes can't have children");
		}
		int pos = 0;
		while (pos < children.size() && children.get(pos).getKeyList().get(0) < child.getKeyList().get(0)) {
			pos++;
		}
		children.add(pos, child);
		child.setParent(this);
	}

	public void setParent(MyBPlusTreeNode parent) {
		this.parent = parent;
	}

	public MyBPlusTreeNode getParent() {
		return parent;
	}

	public MyBPlusTreeNode getNext() {
		return next;
	}

	public void setNext(MyBPlusTreeNode next) {
		this.next = next;
	}

	public List<Integer> getKeyList() {
		return keyList;
	}

	public List<MyBPlusTreeNode> getChildren() {
		return children;
	}

	public boolean isLeaf() {
		return isLeaf;
	}
}
