package jumbo.algo;

import ilog.concert.IloException;
import ilog.concert.IloIntVar;
import ilog.concert.IloIntervalVar;
import ilog.cp.IloCP;
import jumbo.data.Instance;
import jumbo.data.Solution;

public class AlgoPPC {
	private final Instance instance;

	private final Solution solution;

	public AlgoPPC(final Instance instance, final Solution solution) {
		this.instance = instance;
		this.solution = solution;
	}

	public int[] partitionning() {
		try {
			// Model
			IloCP model = new IloCP();
			
			// Variables
			
			// Stating time of each task (position of the item in the jumbo)
			IloIntVar[] startTimes = new IloIntVar[instance.getItems().length];
			for (int i = 0; i < instance.getItems().length; i++) {
				startTimes[i] = model.intVar(0, instance.getMaxJumboSize(), "Start" + i);
			}			
			
			// Tasks (width of each task)
			IloIntervalVar[] tasks = new IloIntervalVar[instance.getItems().length];
			for (int i = 0; i < instance.getItems().length; i++) {
				tasks[i] = model.intervalVar(instance.getItemWidth(i), "Task" + i);
			}
			
			
		} catch (IloException e) {
			System.err.println("Erreur partitionning avec Cplex : ");
			e.printStackTrace();
		}

		int[] index = { 0 };
		return index;
	}

	public boolean faisable(int[] items, int widthBloc, int heigthBloc) {

		return false;
	}
}
