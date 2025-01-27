package jumbo.data;

import jumbo.utils.BinaryTree;

public class JumboCut {

	private final int jumboId;

	private final BinaryTree<Cut> cuts;

//	private int[] cuttedResultSizes;
//
//	private int[] itemIds;
//
//	private int[] scraps;

	public JumboCut(final int jumboId, final BinaryTree<Cut> cuts) {
		this.jumboId = jumboId;
		this.cuts = cuts;
	}

	public int getJumboId() {
		return jumboId;
	}

	public BinaryTree<Cut> getCuts() {
		return cuts;
	}

	public int computeAreaWaste(final Instance instance) {
		final int jumboSize = instance.getJumboSize(jumboId);
		int itemSize = 0;

		// Parcourir uniquement les feuilles de l'arbre pour savoir les items qui sont dedans.
		for (final Cut c: cuts.traverseLeaves()) {
			for (final int itemId : c.itemIds()) {
				itemSize += instance.getItemArea(itemId);
			}
		}

		return jumboSize - itemSize;
	}

	public JumboCut copy() {
		return new JumboCut(jumboId, cuts.copy());
	}

	public void simplify() {
		simplify(cuts);
	}

	private void simplify(final BinaryTree<Cut> node) {
		// Ne pas simplifier si il y a des items
		for (final Cut c: node.traverseLeaves()) {
			if (c.itemIds().length > 0) {
				return;
			}
		}

		// Sinon recursive
		final BinaryTree<Cut> left = node.getLeft();
		if (left != null) {
			simplify(left);
		}

		final BinaryTree<Cut> right = node.getRight();
		if (right != null) {
			simplify(right);
		}
	}
}
