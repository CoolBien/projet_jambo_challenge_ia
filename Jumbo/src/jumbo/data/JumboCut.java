package jumbo.data;

import jumbo.utils.BinaryTree;

public class JumboCut {

	private int jumboId;

	private final BinaryTree<Cut> cuts;

//	private int[] cuttedResultSizes;
//
//	private int[] itemIds;
//
//	private int[] scraps;

	public JumboCut(final BinaryTree<Cut> cuts) {
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
}
