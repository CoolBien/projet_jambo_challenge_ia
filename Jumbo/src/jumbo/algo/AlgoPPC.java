package jumbo.algo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ilog.concert.IloConstraint;
import ilog.concert.IloCumulFunctionExpr;
import ilog.concert.IloException;
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
			
			IloIntervalVar[] items = new IloIntervalVar[nbItems];
			for (int i = 0; i < nbItems; i++) {
				items[i] = model.intervalVar();
			}

			IloIntervalVar[][][] itemsVH = new IloIntervalVar[nbJumbos][nbItems][2];
			IloIntervalVar[][] jumbos = new IloIntervalVar[nbJumbos][nbItems];
            for (int i = 0; i < nbJumbos; i++) {
                heightUsages[i] = model.cumulFunctionExpr();
                for (int j = 0; j < nbItems; j++) {
                    jumbos[i][j] = model.intervalVar();
                    jumbos[i][j].setOptional();

    				itemsVH[i][j][0] = model.intervalVar(instance.getItemWidth(j), "Item_" + j);
    				itemsVH[i][j][0].setOptional();
    				itemsVH[i][j][1] = model.intervalVar(instance.getItemHeight(j), "ItemAlt_" + j);
    				itemsVH[i][j][1].setOptional();			
    				
					// resource used in a jumbo => sum of heigth used
					if (instance.getItemHeight(j) > 0) {
						heightUsages[i] = model.sum(heightUsages[i], model.pulse(itemsVH[i][j][0], instance.getItemHeight(j)));
						heightUsages[i] = model.sum(heightUsages[i], model.pulse(itemsVH[i][j][1], instance.getItemWidth(j)));
					}
                }
            }

			// limit height to the height of the jumbo
			// no overlap
			for (int j = 0; j < nbJumbos; j++)
			{
				model.add(model.le(heightUsages[j], instance.getJumboHeight(j)));
				model.add(model.noOverlap(jumbos[j]));
				for (int i = 0; i < nbItems; i++)
				{					
					// items end at most at the end of the jumbo
					jumbos[j][i].setEndMax(instance.getJumboWidth(j));
				}
			}
			
			for (int j = 0; j < nbItems; j++)
			{
    			// only one jumbo for an item/itemAlt
				IloIntervalVar[] alternatives = new IloIntervalVar[nbJumbos];
	            for (int i = 0; i < nbJumbos; i++) {
					// either item or turned item
	                model.add(model.alternative(jumbos[i][j], itemsVH[i][j]));
	                alternatives[i] = jumbos[i][j];

	            }
	            model.add(model.alternative(items[j], alternatives));
			}
			
			// minimize number of jumbo used
			IloConstraint[] jumboUsed = new IloConstraint[nbJumbos];
			for (int j = 0; j < nbJumbos; j++) {
				IloConstraint[] presenceInJumbo = new IloConstraint[nbItems];
				for (int i = 0; i < nbItems; i++) {
					presenceInJumbo[i] = model.presenceOf(jumbos[j][i]);
				}
				jumboUsed[j] = model.or(presenceInJumbo);
			}
			
			model.add(model.minimize(model.sum(jumboUsed)));
			
			model.setParameter("TimeLimit", 10);

			// Résolution
	        if (model.solve()) {
	            System.out.println("Nombre de jumbo utilise : " + model.getObjValue());
	            for (int i = 0; i < nbJumbos; i++) {
	            	List<Integer> indItemArr = new ArrayList<>();
	                for (int j = 0; j < nbItems; j++)
	                {
	                	if (model.isPresent(jumbos[i][j]))
	                		indItemArr.add(j);
	                }
	                index[i] = indItemArr.stream().mapToInt(l -> l).toArray();
	                System.out.println("Jumbo " + i + " : " + Arrays.toString(index[i]));
	            }
	        } else {
	            System.out.println("Pas de solution trouvee.");
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
