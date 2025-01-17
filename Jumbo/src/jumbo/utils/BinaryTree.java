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
			if (leftIter == null) {
				// Si c'est une feuille:
				if (rightIter == null && onLeft) {
					return true;
				}
			} else if (leftIter.hasNext()) {
				// Si il y a une suite à gauche
				return true;
			}
			// Si il y a une suite à droite
			return rightIter != null && rightIter.hasNext();
		}

		@Override
		public T next() {
			if (onLeft) {
				// Si on est sur une feuille, on renvoie l'item
				if (leftIter == null) {
					if (rightIter == null) {
						onLeft = false;
						return item;
					}
				} else if (leftIter.hasNext()) {
					// Si on est pas sur une feuille, on renvoie le suivant sur l'arbre gauche si existant
					return leftIter.next();
				}
				onLeft = false;
			}
			// Théoriquement, si on est une feuille on ne peux jamais arriver ici
			// Et comme on est pas à gauche, on est à droite et on a théoriquement une valeur aussi
			return rightIter.next();
		}

	}
}
