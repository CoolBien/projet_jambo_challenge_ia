package jumbo.utils;

import java.util.Iterator;

public class BinaryTree<T> {

	private BinaryTree<T> left;
	private BinaryTree<T> right;

	private final T item;

	public BinaryTree(final T item) {
		this.item = item;
	}

	public BinaryTree(final T item, final BinaryTree<T> left, final BinaryTree<T> right) {
		this.item = item;
		this.left = left;
		this.right = right;
	}

	public void setLeft(final BinaryTree<T> left) {
		this.left = left;
	}

	public void setRight(final BinaryTree<T> right) {
		this.right = right;
	}

	public T getItem() {
		return item;
	}

	public BinaryTree<T> getLeft() {
		return left;
	}

	public BinaryTree<T> getRight() {
		return right;
	}

	public Iterable<T> traverseLeaves() {
		return LeavesIterator::new;
	}

	private class LeavesIterator implements Iterator<T> {

		private boolean onLeft = true;
		private final LeavesIterator leftIter;
		private final LeavesIterator rightIter;

		@SuppressWarnings("unchecked")
		public LeavesIterator() {
			leftIter  = left  != null? (LeavesIterator) left.traverseLeaves(): null;
			rightIter = right != null? (LeavesIterator) right.traverseLeaves(): null;
		}

		@Override
		public boolean hasNext() {
			return onLeft || (rightIter != null && rightIter.hasNext());
		}

		@Override
		public T next() {
			if (onLeft) {
				if (leftIter == null || !leftIter.hasNext()) {
					onLeft = false;
					return item;
				}
				return leftIter.next();
			}
			return rightIter.next();
		}

	}
}
