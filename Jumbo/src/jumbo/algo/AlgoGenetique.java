package jumbo.algo;

import jumbo.data.Instance;
import jumbo.data.JumboCut;

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
				final int randomBitFlip = 0 + (int)(Math.random() * individu.getItemIdOf(i));
				individu.getItemIdOf(randomBitFlip);
			}
		}
	}
}
