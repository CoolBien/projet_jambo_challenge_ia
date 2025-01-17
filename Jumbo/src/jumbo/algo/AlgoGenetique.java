package jumbo.algo;

import java.util.ArrayList;
import java.util.List;

import jumbo.data.Cut;
import jumbo.data.CutOrientation;
import jumbo.data.JumboCut;
import jumbo.data.util.BinaryTree;

public class AlgoGenetique {

	private final JumboCut[] population;

	public AlgoGenetique(final int size) {
		population = new JumboCut[size];
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

	private void cross() {
		// TODO Auto-generated method stub

	}

	private void tournoi() {
		// TODO

	}

	private void mutate() {
		// TODO Auto-generated method stub
		
		for (int i = 0; i < population.length; i++) {
			JumboCut individu = population[i];

			
			int randomChangeItem = 0 + (int)(Math.random() * 100);
			if (randomChangeItem <= 30) {
				//int randomBitFlip = 0 + (int)(Math.random() * individu[i]);
				//individu.getItemIdOf(randomBitFlip);
			}
			
		}
	}
}
