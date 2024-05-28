package org.dfpl.lecture.database.assignment2.assignment2_20011727;

import java.util.*;

public class MyBPlusTreeNode {

	private List<Integer> keyList;
	private List<MyBPlusTreeNode> children;
	private MyBPlusTreeNode parent;
	private boolean isLeaf;

	public MyBPlusTreeNode(boolean isLeaf) {
		this.isLeaf = isLeaf;
		this.keyList = new ArrayList<>();
		this.children = new ArrayList<>();
	}

	public List<Integer> getKeyList() {
		return keyList;
	}

	public List<MyBPlusTreeNode> getChildren() {
		return children;
	}

	public MyBPlusTreeNode getParent() {
		return parent;
	}

	public void setParent(MyBPlusTreeNode parent) {
		this.parent = parent;
	}

	public boolean isLeaf() {
		return isLeaf;
	}

	public void addKey(Integer key) {
		keyList.add(key);
		Collections.sort(keyList);
	}

	public void addChild(MyBPlusTreeNode child) {
		children.add(child);
		Collections.sort(children, Comparator.comparingInt(a -> a.getKeyList().get(0)));
	}
}
