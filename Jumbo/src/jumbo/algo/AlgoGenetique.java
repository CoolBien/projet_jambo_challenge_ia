package jumbo.algo;
import java.util.ArrayList;
import java.util.List;

import jumbo.data.Cut;
import jumbo.data.CutOrientation;
import jumbo.data.Instance;
import jumbo.data.JumboCut;
import jumbo.utils.BinaryTree;

public class AlgoGenetique {

	/** Référence vers l'instance. */
	private final Instance instance;

	/** Population de l'algorithme */
	private final JumboCut[] population;

	/** Utilisé pour le résultat du croisement et l'entrée du tournoi. */
	private final JumboCut[] largePopulation;

	public AlgoGenetique(final Instance instance, final int size) {
		this.instance = instance;
		population = new JumboCut[size];
		largePopulation = new JumboCut[size << 1];
	}
	
	public BinaryTree<Cut> init(final int sizeX, final int sizeY, List<Integer> itemIds) {
		List<Integer> itemsChosenLeft = new ArrayList<>(); //
		List<Integer> itemsChosenRight = new ArrayList<>(); //
		
		// Choisir l'orientation de coupage
		CutOrientation orientation;
		if (Math.random() < .5) {
			orientation = CutOrientation.HORIZONTAL; 
		} else {
			orientation = CutOrientation.VERTICAL; 
		}
		
		//int randomSizeItemsChosen = 0 + (int)(Math.random() * itemIds.length);
		int itemFlipCoding = 0;
		
		for (int i=0; i < itemIds.size(); i++) {
			// On ignore certains items aléatoirement:
			if (Math.random() < .5) {
				itemsChosenRight.add(itemIds.get(i));
				continue; //On passe à l'itération suivante de la boucle et on skippe les étapes suivantes
			}
			itemsChosenLeft.add(itemIds.get(i));
			if (Math.random() < .5) {
				itemFlipCoding |=  (1 << i) ; //On flippe le ième bit à 1
			}
		}
		Cut cut = new Cut(orientation, sizeX, sizeY, itemFlipCoding, itemsChosenLeft.stream().mapToInt(i -> i).toArray()); // Ne pas trop chercher
		final BinaryTree<Cut> itemDisposition = new BinaryTree<>(cut);
		itemDisposition.setLeft(init(sizeX, sizeY, itemIds));
		itemDisposition.setRight(null);
		
		return itemDisposition;
//		taille sous_jumbo, liste des items, 
	}

	public void run(final int n) {
		// TODO : init


		for (int i=0; i<n; i++) {
			cross();
			tournoi();
			mutate();
		}
	}

	/**
	 * Croisement
	 * <p>
	 * Input from {@link #population}
	 * <p>
	 * Output to {@link #largePopulation}
	 */
	private void cross() {
		// TODO Auto-generated method stub

	}

	/**
	 * Tournoi
	 * <p>
	 * Input from {@link #largePopulation}
	 * <p>
	 * Output to {@link #population}
	 */
	private void tournoi() {
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
				population[i >>> 1] = largePopulation[i];
			} else {
				// B gagne
				population[i >>> 1] = largePopulation[i+1];
			}
		}
	}

	/**
	 * Mutation
	 * <p>
	 * Input from {@link #population}
	 * <p>
	 * Output to {@link #population} as well
	 */
	private void mutate() {
		// TODO Auto-generated method stub
		for (int i = 0; i < population.length; i++) {
			final JumboCut individu = population[i];

			final int randomChangeItem = 0 + (int)(Math.random() * 100);
			if (randomChangeItem <= 30) {
				//int randomBitFlip = 0 + (int)(Math.random() * individu[i]);
				//individu.getItemIdOf(randomBitFlip);
			}
			
		}
	}
}
