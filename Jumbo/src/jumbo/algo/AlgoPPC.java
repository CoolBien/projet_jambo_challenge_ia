package jumbo.algo;

import java.util.ArrayList;
import java.util.List;

import ilog.concert.IloCumulFunctionExpr;
import ilog.concert.IloException;
import ilog.concert.IloIntExpr;
import ilog.concert.IloIntVar;
import ilog.concert.IloIntervalVar;
import ilog.cp.IloCP;
import jumbo.data.Instance;

public class AlgoPPC {
	private final Instance instance;

	public AlgoPPC(final Instance instance) {
		this.instance = instance;
	}

	public int[][] partitionning() {
		int nbItems = instance.getItems().length / 3;
		int nbJumbos = instance.getJumbos().length / 3;
		
		int[][] index = new int[nbJumbos][];
		
		try {
			
			// Model
			IloCP model = new IloCP();
			
			// Cumulative constraint
			IloCumulFunctionExpr[] heightUsages = new IloCumulFunctionExpr[nbJumbos];
			
			IloIntVar[] jumboUsed = new IloIntVar[nbJumbos];
			IloIntervalVar[][] jumbos = new IloIntervalVar[nbJumbos][nbItems * 2];
            for (int i = 0; i < nbJumbos; i++) {
                for (int j = 0; j < nbItems * 2; j++) {
                    jumbos[i][j] = model.intervalVar();
                    //model.add(model.presenceOf(jumbos[i][j]));
                }
                heightUsages[i] = model.cumulFunctionExpr();
                jumboUsed[i] = model.intVar(0, 1);
            }
			
			IloIntervalVar[] items = new IloIntervalVar[nbItems * 2];
			for (int i = 0; i < nbItems; i++) {
				// task (duration => width of each task)
				items[i * 2] = model.intervalVar(instance.getItemWidth(i), "Item" + i);
				items[i * 2 + 1] = model.intervalVar(instance.getItemHeight(i), "ItemAlt" + i);
				for (int j = 0; j < nbJumbos; j++)
				{
					// resource used in a jumbo => sum of heigth used
					if (instance.getItemHeight(i) > 0)
						heightUsages[j] = model.sum(heightUsages[j], model.pulse(items[i * 2], instance.getItemHeight(i)));
					if (instance.getItemWidth(i) > 0)
						heightUsages[j] = model.sum(heightUsages[j], model.pulse(items[i * 2 + 1], instance.getItemHeight(i)));
					
					// either item or item turned
					//model.add(model.eq(model.presenceOf(items[i * 2]), model.not(model.presenceOf(items[i * 2 + 1]))));
				}
			}
			/*
			// add alternative task (item turned)
			IloIntervalVar[] itemsAlt = new IloIntervalVar[nbItems];
			for (int i = 0; i < nbItems; i++) {
				// task (duration => width of each task)
				itemsAlt[i] = model.intervalVar(instance.getItemWidth(i), "ItemAlt" + i);
				for (int j = 0; j < nbJumbos; j++)
				{
					// resource used in a jumbo => sum of heigth used
					if (instance.getItemHeight(i) > 0)
						heightUsages[j] = model.sum(heightUsages[j], model.pulse(itemsAlt[i], instance.getItemHeight(i)));
					// items end at most at the end of the jumbo
					model.add(model.ifThen(model.presenceOf(itemsAlt[i]), model.eq(model.endOf(itemsAlt[i]), instance.getJumboWidth(j))));
					// count number of jumbo used
					model.add(model.ifThen(model.presenceOf(itemsAlt[i]), model.eq(jumboUsed[j], 1)));
				}
			}
			*/
			// limit height to the height of the jumbo
			// no overlap
			for (int j = 0; j < nbJumbos; j++)
			{
				model.add(model.le(heightUsages[j], instance.getJumboHeight(j)));
				model.add(model.noOverlap(jumbos[j]));
				for (int i = 0; i < nbItems; i++)
				{
					// count number of jumbo used
					model.add(model.ifThen(model.presenceOf(jumbos[j][i * 2]), model.eq(jumboUsed[j], 1)));
					model.add(model.ifThen(model.not(model.presenceOf(jumbos[j][i * 2])), model.eq(jumboUsed[j], 0)));
					model.add(model.ifThen(model.presenceOf(jumbos[j][i * 2 + 1]), model.eq(jumboUsed[j], 1)));
					model.add(model.ifThen(model.not(model.presenceOf(jumbos[j][i * 2 + 1])), model.eq(jumboUsed[j], 0)));
					// items end at most at the end of the jumbo
					model.add(model.ifThen(model.presenceOf(jumbos[j][i * 2]), model.eq(model.endOf(items[i * 2]), instance.getJumboWidth(j))));
					model.add(model.ifThen(model.presenceOf(jumbos[j][i * 2 + 1]), model.eq(model.endOf(items[i * 2 + 1]), instance.getJumboHeight(j))));
				}
			}
			
			// only one jumbo for an item/itemAlt
			for (int j = 0; j < nbItems * 2; j++)
			{
				IloIntervalVar[] alternatives = new IloIntervalVar[nbJumbos];
	            for (int i = 0; i < nbJumbos; i++) {
	                alternatives[i] = jumbos[i][j];
	            }
	            //model.add(model.alternative(items[j], alternatives));
	            //model.add(model.or(model.alternative(items[j], alternatives), model.alternative(itemsAlt[j], alternatives)));
			}
			
			// minimize number of jumbo used
			IloIntExpr nbJumboUsed = model.sum(jumboUsed);
			model.minimize(nbJumboUsed);

			// Résolution
	        if (model.solve()) {
	            System.out.println("Solution trouvée !");
	            System.out.println("Nombre de jumbo utilisé : " + model.getObjValue());
	            for (int i = 0; i < nbJumbos; i++) {
	            	List<Integer> indItemArr = new ArrayList<>();
	                for (int j = 0; j < nbJumbos; j++)
	                {
	                	if (model.isPresent(jumbos[i][j]))
	                		indItemArr.add(j);
	                }
	                index[i] = indItemArr.stream().mapToInt(l -> l).toArray();
	                System.out.println("Jumbo " + i + " : " + index[i]);
	            }
	        } else {
	            System.out.println("Pas de solution trouvée.");
	        }
			
		} catch (IloException e) {
			System.err.println("Erreur partitionning avec Cplex : ");
			e.printStackTrace();
		}
		
		return index;
	}

	public boolean faisable(int[] items, int widthBloc, int heigthBloc) {

		return false;
	}
}
