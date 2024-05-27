package org.dfpl.lecture.database.assignment2.assignment2_20011727;

import java.util.ArrayList;
import java.util.List;

public class MyBPlusTreeNode {

	private MyBPlusTreeNode parent;
	private List<Integer> keyList;
	private List<MyBPlusTreeNode> children;
	private boolean isLeaf;
	private MyBPlusTreeNode next;

	// Constructor for leaf node
	public MyBPlusTreeNode(boolean isLeaf) {
		this.isLeaf = isLeaf;
		this.keyList = new ArrayList<>();
		this.children = new ArrayList<>();
	}

	// Add a key to the node
	public void addKey(Integer key) {
		int pos = 0;
		while (pos < keyList.size() && keyList.get(pos) < key) {
			pos++;
		}
		keyList.add(pos, key);
	}

	// Add a child to the node
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

	// Set parent
	public void setParent(MyBPlusTreeNode parent) {
		this.parent = parent;
	}

	// Get parent
	public MyBPlusTreeNode getParent() {
		return parent;
	}

	// Get next leaf node
	public MyBPlusTreeNode getNext() {
		return next;
	}

	// Set next leaf node
	public void setNext(MyBPlusTreeNode next) {
		this.next = next;
	}

	// Getters for keyList and children
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
