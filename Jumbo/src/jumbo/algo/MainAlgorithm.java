package jumbo.algo;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import jumbo.data.Instance;
import jumbo.data.Solution;
import jumbo.data.WastePart;
import jumbo.utils.ThreadPool;

public class MainAlgorithm {

	private final Instance instance;

	public MainAlgorithm(final Instance instance) {
		this.instance = instance;
	}

	public Solution run() throws InterruptedException {

		// PPC pour récupérer partitioning:
		final AlgoPPC ppc = new AlgoPPC(instance, 10, 0.1);
		final int[][] partitioning = ppc.partitioning();
		if (partitioning[0] == null) {
			return null;
		}

		// Génétique pour améliorer la solution
		final AlgoGenetique[] algorithms = new AlgoGenetique[partitioning.length];
		final ThreadPool pool = new ThreadPool(32, partitioning.length, id -> {
			algorithms[id] = new AlgoGenetique(instance, 16, id, IntStream.of(partitioning[id]).mapToObj(i -> i).toList());
			algorithms[id].run(42);
		});
		pool.startAndWait();

		// Il faut maintenant inclure les items manquant dans les chutes de la solution.
		// Pour cela, il faut récupérer la liste des items manquants
		final List<Integer> allMissingItems = new ArrayList<>();
		final List<WastePart> allWasteParts = new ArrayList<>();
		final List<Integer> emptyJumbos = new ArrayList<>();
		final List<Integer> emptyJumboIndices = new ArrayList<>();
		for (int i = 0; i < algorithms.length; i++) {
			final AlgoGenetique algo = algorithms[i];
			final AlgoResult result = algo.getBestResult();
			if (result.best() == null) {
				emptyJumbos.add(algo.getJumboId());
				emptyJumboIndices.add(i);
				continue;
			}
			allWasteParts.addAll(result.best().simplify());
			allMissingItems.addAll(result.missingItems());
		}
		System.out.println("Missing items: "+allMissingItems);
		System.out.println("Waste parts:   "+allWasteParts);
		System.out.println("Empty Jumbos:  "+emptyJumbos);

		if (allMissingItems.size() > 0) {
			final int[] allMissingItemArray = new int[3 * allMissingItems.size()];//allMissingItems.stream().mapToInt(i -> i).toArray();
			final int[] emptyJumbosArray = new int[3 * emptyJumbos.size()];
			for (int i=0; i< allMissingItems.size(); i++) {
				final int iid = allMissingItems.get(i);
				allMissingItemArray[3*i  ] = iid;
				allMissingItemArray[3*i+1] = instance.getItemWidth(iid);
				allMissingItemArray[3*i+2] = instance.getItemHeight(iid);
			}
			for (int i=0; i< emptyJumbos.size(); i++) {
				final int jid = emptyJumbos.get(i);
				emptyJumbosArray[3*i  ] = jid;
				emptyJumbosArray[3*i+1] = instance.getJumboWidth(jid);
				emptyJumbosArray[3*i+2] = instance.getJumboHeight(jid);
			}

			final Instance inst = new Instance(emptyJumbosArray, allMissingItemArray, null, null);
			final AlgoPPC ppc2 = new AlgoPPC(inst, 10, 0.5);
			final int[][] partitioning2 = ppc2.partitioning();
			if (partitioning2 == null || partitioning2.length == 0 || partitioning2[0] == null) {
				System.err.println("Problème pour loger");
			} else {
				// Génétique pour améliorer la solution
				final AlgoGenetique[] algorithms2 = new AlgoGenetique[partitioning2.length];
				final ThreadPool pool2 = new ThreadPool(32, partitioning2.length, id -> {
					algorithms2[id] = new AlgoGenetique(inst, 16, id, IntStream.of(partitioning2[id]).mapToObj(i -> i).toList());
					algorithms2[id].run(12);
				});
				pool2.startAndWait();

				for (int i=0; i < emptyJumboIndices.size(); i++) {
					final int indexInOriginal = emptyJumboIndices.get(i);
					algorithms2[i].setJumboId(indexInOriginal);
					algorithms[indexInOriginal] = algorithms2[i];
				}

				final List<Integer> allMissingItems1 = new ArrayList<>();
				final List<WastePart> allWasteParts1 = new ArrayList<>();
				final List<Integer> emptyJumbos1 = new ArrayList<>();
				final List<Integer> emptyJumboIndices1 = new ArrayList<>();
				for (int i = 0; i < algorithms.length; i++) {
					final AlgoGenetique algo = algorithms[i];
					final AlgoResult result = algo.getBestResult();
					if (result.best() == null) {
						emptyJumbos1.add(algo.getJumboId());
						emptyJumboIndices1.add(i);
						continue;
					}
					allWasteParts1.addAll(result.best().simplify());
					allMissingItems1.addAll(result.missingItems());
				}
				System.out.println("Missing items2: "+allMissingItems1);
				System.out.println("Waste parts2:   "+allWasteParts1);
				System.out.println("Empty Jumbos2:  "+emptyJumbos1);
			}
//			final WastePartInstance wastePartInstance = WastePartInstance.create(allWasteParts, allMissingItems);
//
//			final AlgoPPC ppc2 = new AlgoPPC(instance, 60, 0.1);
//			final int[][] partitioning2 = ppc2.partitioning();
//			if (partitioning2[0] != null) {
//				final AlgoGenetique[] algorithms2 = new AlgoGenetique[partitioning2.length];
//				final ThreadPool pool2 = new ThreadPool(32, partitioning2.length, id -> {
//					algorithms2[id] = new AlgoGenetique(wastePartInstance, 16, id, IntStream.of(partitioning2[id]).mapToObj(i -> i).toList());
//					algorithms2[id].run(42);
//				});
//				pool2.startAndWait();
//				instance.addItems(algorithms2, wastePartInstance);
//			}
		}

		return new Solution(instance, Stream.of(algorithms).map(AlgoGenetique::getBestResult).filter(e -> e.best() != null).map(AlgoResult::best).toList());
	}
}
