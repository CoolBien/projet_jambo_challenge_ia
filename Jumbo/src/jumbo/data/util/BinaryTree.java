package jumbo.data.util;

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
}
