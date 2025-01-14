package jumbo.algo;

import jumbo.data.JumboCut;

public class AlgoGenetique {

	private final JumboCut[] population;

	public AlgoGenetique(final int size) {
		population = new JumboCut[size];
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

	}
}
