package jumbo.algo;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import jumbo.data.Instance;
import jumbo.data.Solution;
import jumbo.utils.ThreadPool;

public class MainAlgorithm {

	private final Instance instance;

	public MainAlgorithm(final Instance instance) {
		this.instance = instance;
	}

	public Solution run() throws InterruptedException {

		// PPC pour récupérer partitioning:
		final AlgoPPC ppc = new AlgoPPC(instance, 60, 0.1);
		final int[][] partitioning = ppc.partitioning();
		if (partitioning[0] == null)
		{
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
		// Pour cela, il faut récupérer la listes des items manquant
		final List<Integer> allMissingItems = new ArrayList<>();
		for (final AlgoGenetique algo: algorithms) {
			final AlgoResult result = algo.getBestResult();
			if (result.best() == null) {
				continue;
			}
			result.best().simplify();
			allMissingItems.addAll(result.missingItems());
		}
		System.out.println("Missing items: "+allMissingItems);

		return new Solution(instance, Stream.of(algorithms).map(AlgoGenetique::getBestResult).filter(e -> e.best() != null).map(AlgoResult::best).toList());
	}
}
