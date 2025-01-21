package jumbo.algo;

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

	public Solution run() {

		// PPC pour récupérer partitioning:
		final AlgoPPC ppc = new AlgoPPC(instance);
		final int[][] partitionning = ppc.partitionning();

		// Génétique pour améliorer la solution
		final AlgoGenetique[] algorithms = new AlgoGenetique[partitionning.length];
		final ThreadPool pool = new ThreadPool(8, partitionning.length, id -> {
			algorithms[id] = new AlgoGenetique(instance, 16, id, IntStream.of(partitionning[id]).mapToObj(i -> i).toList());
			algorithms[id].run(42);
		});
		pool.startAndWait();

		return new Solution(instance, Stream.of(algorithms).map(AlgoGenetique::getBestResult).toList());
	}
}
