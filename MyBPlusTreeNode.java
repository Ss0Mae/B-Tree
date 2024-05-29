package org.dfpl.lecture.database.assignment2.assignment2_20011727;
import java.util.ArrayList;
import java.util.List;

public class MyBPlusTreeNode {
	boolean isLeaf;
	int m;
	List<Integer> keys;
	List<MyBPlusTreeNode> children;
	MyBPlusTreeNode next;

	public MyBPlusTreeNode(boolean isLeaf, int m) {
		this.isLeaf = isLeaf;
		this.m = m;
		this.keys = new ArrayList<>();
		this.children = new ArrayList<>();
		this.next = null;
	}

	public void splitChild(int i, MyBPlusTreeNode y) {
		MyBPlusTreeNode z = new MyBPlusTreeNode(y.isLeaf, m);
		int mid;
		int midValue;

		if (y.isLeaf) {
			mid = (m % 2 == 0) ? ((m - 1) / 2) + 1 : (m - 1) / 2;
			midValue = y.keys.get(mid);

			// 중간값 이후의 키를 새로운 노드 z로 이동
			for (int j = mid; j < y.keys.size(); j++) {
				z.keys.add(y.keys.get(j));
			}
			y.keys.subList(mid, y.keys.size()).clear();

			z.next = y.next;
			y.next = z;
		} else {
			mid = (m - 1) / 2;
			midValue = y.keys.get(mid);

			// 중간값 이후의 키를 새로운 노드 z로 이동
			for (int j = mid + 1; j < y.keys.size(); j++) {
				z.keys.add(y.keys.get(j));
			}
			y.keys.subList(mid, y.keys.size()).clear();

			// 중간값 이후의 자식 노드를 새로운 노드 z로 이동
			for (int j = mid + 1; j < y.children.size(); j++) {
				z.children.add(y.children.get(j));
			}
			y.children.subList(mid + 1, y.children.size()).clear();
		}

		children.add(i + 1, z);
		keys.add(i, midValue); // 중간값을 부모 노드로 올림
	}



}
