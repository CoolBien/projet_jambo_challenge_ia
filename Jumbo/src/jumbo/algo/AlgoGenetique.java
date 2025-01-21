package jumbo.algo;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.StreamSupport;

import jumbo.data.Cut;
import jumbo.data.CutOrientation;
import jumbo.data.Instance;
import jumbo.data.JumboCut;
import jumbo.utils.BinaryTree;
import jumbo.utils.Utils;

public class AlgoGenetique {

	/** Référence vers l'instance. */
	private final Instance instance;

	/** Population de l'algorithme */
	private final JumboCut[] smallPopulation;

	/** Utilisé pour le résultat du croisement et l'entrée du tournoi. */
	private final JumboCut[] largePopulation;

	/** Id du jumbo associé à cette instance de l'algo génétique. */
	private final int jumboId;

	private final List<Integer> itemIdsToAdd;

	public AlgoGenetique(final Instance instance, final int size, final int jumboId, final List<Integer> itemIdsToAdd) {
		this.instance = instance;
		this.jumboId = jumboId;
		smallPopulation = new JumboCut[size];
		largePopulation = new JumboCut[size << 1];
		this.itemIdsToAdd = itemIdsToAdd;
	}

	/**
	 * Initialise la population dans {@link #largePopulation}
	 */
	private void initPopulation() {

		// Get recurrent data
		final int jumboWidth = instance.getJumboWidth(jumboId);
		final int jumboHeight = instance.getJumboHeight(jumboId);

		for (int i=0; i < largePopulation.length; i++) {
			largePopulation[i] = new JumboCut(jumboId, initTree(jumboWidth, jumboHeight, itemIdsToAdd));
		}
	}

	private BinaryTree<Cut> initTree(final int sizeX, final int sizeY, final List<Integer> itemIdsToAdd) {
		final List<Integer> chosenItems = new ArrayList<>(); //

		// Choisir l'orientation de coupage
		final CutOrientation orientation;
		final int maxTall;
		final int maxWide;
		if (Math.random() < .5) {
			orientation = CutOrientation.HORIZONTAL;
			maxTall = sizeX;
			maxWide = sizeY;
		} else {
			orientation = CutOrientation.VERTICAL;
			maxTall = sizeY;
			maxWide = sizeX;
		}

		int itemFlipCoding = 0;
		int cutPos = 0;

		for (int i=0; i<itemIdsToAdd.size(); i++) {
			if (chosenItems.size() > 1 && Math.random() < .25) continue;

			// On check l'item
			final int itemId = itemIdsToAdd.get(i);
			final int itemWidth = instance.getItemWidth(itemId);
			final int itemHeight = instance.getItemHeight(itemId);
			final boolean normalPossible = cutPos + itemWidth < maxWide && itemHeight < maxTall;
			final boolean flippedPossible = cutPos + itemHeight < maxWide && itemWidth < maxTall;
			if (!normalPossible && !flippedPossible) continue;

			// On choisi le sens de l'item
			boolean flippedChosen = false;
			if (normalPossible && flippedPossible) flippedChosen = Math.random() < .5f;
			else if (!normalPossible) flippedChosen = true;

			// On ajoute l'item dans le bon sens
			if (flippedChosen) {
				cutPos += itemWidth;
				itemFlipCoding |= 1 << chosenItems.size();
			} else {
				cutPos += itemHeight;
			}
			chosenItems.add(itemId);
		}

		// On construit le nœud de l'arbre
		final BinaryTree<Cut> node = new BinaryTree<>(new Cut(orientation, sizeX, sizeY, itemFlipCoding, chosenItems.stream().mapToInt(i -> i).toArray()));

		if (chosenItems.size() == 1) {
			itemIdsToAdd.remove(chosenItems.get(0));
			return node;
		}
		if (chosenItems.isEmpty()) {
			return node;
		}

		if (orientation == CutOrientation.VERTICAL) {
			node.setLeft(initTree(cutPos, sizeY, itemIdsToAdd));
			node.setRight(initTree(sizeX - cutPos, sizeY, itemIdsToAdd));
		} else {
			node.setLeft(initTree(sizeX, cutPos, itemIdsToAdd));
			node.setRight(initTree(sizeX, sizeY - cutPos, itemIdsToAdd));
		}

		return node;
	}

	public void run(final int n) {
		// init:
		initPopulation();

		for (int i=0; i<n; i++) {
			tournoi();
			parthenogenesis();
			mutation();
		}
	}

	/**
	 * Parthénogenèse de la méduse
	 * <p>
	 * Input from {@link #smallPopulation}
	 * <p>
	 * Output to {@link #largePopulation}
	 */
	private void parthenogenesis() {
		for (int i = 0; i < smallPopulation.length; i ++) {
			largePopulation[ i << 1     ] = smallPopulation[i];
			largePopulation[(i << 1) | 1] = smallPopulation[i].copy();
		}
	}

	/**
	 * Tournoi
	 * <p>
	 * Input from {@link #largePopulation}
	 * <p>
	 * Output to {@link #smallPopulation}
	 */
	private void tournoi() {
		Utils.shuffleArray(largePopulation);
		final int[] score = new int[largePopulation.length];
		for (int i = 0; i < largePopulation.length; i++) {
			final JumboCut cut = largePopulation[i];
			score[i] = instance.getJumboSize(cut.getJumboId()) - cut.computeAreaWaste(instance);
		}
		for (int i = 0; i < score.length; i += 2) {
			final int scoreA = score[i];
			final int scoreB = score[i+1];
			final double random = Math.random() * (scoreA + scoreB);
			if (random < scoreA) {
				// A gagne
				smallPopulation[i >>> 1] = largePopulation[i];
			} else {
				// B gagne
				smallPopulation[i >>> 1] = largePopulation[i+1];
			}
		}
	}

	private BinaryTree<Cut> mutateNode(final BinaryTree<Cut> node) {
		final Cut item = node.getItem();

		return initTree(
			item.sizeX(),
			item.sizeY(),
			StreamSupport.stream(node.traverseLeaves().spliterator(), /*parallel=*/false)
			.filter(e -> e.itemIds().length == 1)
			.map(e -> e.itemIds()[0])
			.toList());
	}


	private BinaryTree<Cut> exploreTreeNode(final BinaryTree<Cut> node) {

		final int randomChangeItem = 0 + (int)(Math.random() * 100);
		if (randomChangeItem <= 5) {
			return mutateNode(node);
		}

		if (node.getLeft() != null) {
			node.setLeft(exploreTreeNode(node.getLeft()));
		}
		if (node.getRight() != null) {
			node.setRight(exploreTreeNode(node.getRight()));
		}
		return node;
	}

	/**
	 * Mutation
	 * <p>
	 * Input from {@link #smallPopulation}
	 * <p>
	 * Output to {@link #smallPopulation} as well
	 */
	private void mutation() {
		// TODO Auto-generated method stub
		for (int i = 0; i < smallPopulation.length; i++) {
			final JumboCut individu = smallPopulation[i];
			exploreTreeNode(individu.getCuts());
		}
	}
}
