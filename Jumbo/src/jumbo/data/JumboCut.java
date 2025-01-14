package jumbo.data;

import jumbo.data.util.BinaryTree;

public class JumboCut {

	private int jumboId;

	private final BinaryTree<Cut> cuts;

	private int[] cuttedResultSizes;

	private int[] itemIds;

	private int[] scraps;

	public JumboCut(final BinaryTree<Cut> cuts) {
		this.cuts = cuts;
	}

	public int getJumboId() {
		return jumboId;
	}

	public BinaryTree<Cut> getCuts() {
		return cuts;
	}

	public int getItemIdOf(final int i) {
		return itemIds[i];
	}

	public int computeAreaWaste() {
		return jumboId;

	}
}
