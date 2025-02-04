package jumbo.data;

import java.util.ArrayList;
import java.util.List;

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

	public JumboCut copy(final int jumboId) {
		return new JumboCut(jumboId, cuts.copy());
	}

	public List<WastePart> simplify() {
		return simplify(cuts, 0, 0);
	}

	private List<WastePart> simplify(final BinaryTree<Cut> node, final int positionCoding, final int n) {

		// Si on est une feuille:
		if (node.isLeave()) {
			if (node.getItem().itemIds().length > 0) {
				return List.of();
			}
			return List.of(WastePart.from(node.getItem(), positionCoding, n));
		}

		// Sinon recursive
		final BinaryTree<Cut> left = node.getLeft();
		final BinaryTree<Cut> right = node.getRight();

		final List<WastePart> leftWaste = simplify(left, positionCoding << 1, n+1);
		final List<WastePart> rightWaste = simplify(right, (positionCoding << 1) | 1, n+1);

//		// On peut les fusionner ici:
//		if (leftWaste.size() == 1 && rightWaste.size() == 1) {
//			return List.of(leftWaste.get(0).combinedWith(rightWaste.get(0)));
//		}

		// Sinon, on renvoie les zones indépendamment:
		final List<WastePart> waste = new ArrayList<>(leftWaste);
		waste.addAll(rightWaste);
		return waste;
	}
}
