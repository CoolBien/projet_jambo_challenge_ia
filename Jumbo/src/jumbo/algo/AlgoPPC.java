package jumbo.algo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ilog.concert.IloConstraint;
import ilog.concert.IloCumulFunctionExpr;
import ilog.concert.IloException;
import ilog.concert.IloIntVar;
import ilog.concert.IloIntervalVar;
import ilog.cp.IloCP;
import jumbo.data.Instance;

public class AlgoPPC {
	private final Instance instance;
	private final int timeSolve;
	private final double lossPercentage;

	public AlgoPPC(final Instance instance, final int timeSolve, final double lossPercentage) {
		this.instance = instance;
		this.timeSolve = timeSolve;
		this.lossPercentage = lossPercentage;
	}
	
	/**
	 * Return the partitioning of the items in the jumbos (without guillotine cut)
	 * @return list of items index in each jumbo
	 */
	public int[][] partitioning() {
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
			
			// limit the optimization (take into account that adding guillotine cut constraint will take more space)
			IloIntVar[][] spaceInJumbo = new IloIntVar[nbJumbos][nbItems];
			for (int j = 0; j < nbJumbos; j++) {
				for (int i = 0; i < items.length; i++) {
					spaceInJumbo[j][i] = model.intVar(0, IloCP.IntMax);
					model.add(model.eq(model.presenceOf(jumbos[j][i]), model.eq(spaceInJumbo[j][i], instance.getItemWidth(i) * instance.getItemHeight(i))));
					model.add(model.eq(model.not(model.presenceOf(jumbos[j][i])), model.eq(spaceInJumbo[j][i], 0)));
				}
				
				//model.add(model.ge(model.div(model.diff(instance.getJumboWidth(j) * instance.getJumboHeight(j), model.sum(spaceInJumbo[j])), instance.getJumboWidth(j) * instance.getJumboHeight(j)), lossPercentage));
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
			
			model.setParameter("TimeLimit", timeSolve);
			model.setParameter(IloCP.IntParam.DefaultInferenceLevel, IloCP.ParameterValues.Medium);

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

	/**
	 * Return the items that fit in the bloc (without guillotine cut)
	 * @param items to try
	 * @param widthBloc
	 * @param heightBloc
	 * @return list of items index
	 */
	public int[] faisable(int[] items, int widthBloc, int heightBloc) {
		try {
			
			// Model
			IloCP model = new IloCP();
			
			// Cumulative constraint
			IloCumulFunctionExpr heightUsages = model.cumulFunctionExpr();
			
			IloIntervalVar[] itemsModel = new IloIntervalVar[items.length];
			for (int i = 0; i < items.length; i++) {
				itemsModel[i] = model.intervalVar();
			}

			IloIntervalVar[][][] itemsVH = new IloIntervalVar[2][items.length][2];
			// Bloc is the first jumbo, the second jumbo is fictive
			IloIntervalVar[][] jumbos = new IloIntervalVar[2][items.length];
            for (int i = 0; i < 2; i++) {
                for (int j = 0; j < items.length; j++) {
                    jumbos[i][j] = model.intervalVar();
                    jumbos[i][j].setOptional();

    				itemsVH[i][j][0] = model.intervalVar(instance.getItemWidth(j), "Item_" + j);
    				itemsVH[i][j][0].setOptional();
    				itemsVH[i][j][1] = model.intervalVar(instance.getItemHeight(j), "ItemAlt_" + j);
    				itemsVH[i][j][1].setOptional();
                }
            }
            
            for( int i = 0; i < items.length; i++)
            {
            	// resource used in a jumbo => sum of heigth used
				heightUsages = model.sum(heightUsages, model.pulse(itemsVH[0][i][0], instance.getItemWidth(items[i])));
				heightUsages = model.sum(heightUsages, model.pulse(itemsVH[0][i][1], instance.getItemWidth(items[i])));
            }

			// limit height to the height of the jumbo
			// no overlap
			model.add(model.le(heightUsages, heightBloc));
			model.add(model.noOverlap(jumbos[0]));
			for (int i = 0; i < items.length; i++)
			{					
				// items end at most at the end of the jumbo
				jumbos[0][i].setEndMax(widthBloc);
			}

			for (int j = 0; j < items.length; j++)
			{
    			// only one jumbo for an item/itemAlt
				IloIntervalVar[] alternatives = new IloIntervalVar[2];
	            for (int i = 0; i < 2; i++) {
					// either item or turned item
	                model.add(model.alternative(jumbos[i][j], itemsVH[i][j]));
	                alternatives[i] = jumbos[i][j];

	            }
	            model.add(model.alternative(itemsModel[j], alternatives));
			}
			
			// maximize number space used in the first jumbo
			IloConstraint[] spaceInJumbo = new IloConstraint[items.length];
			for (int i = 0; i < itemsModel.length; i++) {
				spaceInJumbo[i] = model.eq(model.presenceOf(jumbos[0][i]), model.eq(spaceInJumbo[i], itemsModel[i].getSizeMax() * itemsModel[i].getLengthMax()));
				spaceInJumbo[i] = model.eq(model.not(model.presenceOf(jumbos[0][i])), model.eq(spaceInJumbo[i], 0));
			}
			
			model.add(model.minimize(model.sum(spaceInJumbo)));
			
			model.setParameter("TimeLimit", 10);

			// Résolution
	        if (model.solve()) {
	            System.out.println("Nombre de jumbo utilise : " + model.getObjValue());
            	List<Integer> indItemArr = new ArrayList<>();
                for (int j = 0; j < items.length; j++)
                {
                	if (model.isPresent(jumbos[0][j]))
                		indItemArr.add(j);
                }
    			int[] index = new int[indItemArr.size()];
                index = indItemArr.stream().mapToInt(l -> l).toArray();
                System.out.println("Block : " + Arrays.toString(index));
        		return index;
	        } else {
	            System.out.println("Pas de solution trouvee.");
	        }
			
		} catch (IloException e) {
			System.err.println("Erreur partitionning avec Cplex : ");
			e.printStackTrace();
		}
		
		int[] indexNull = new int[0];
		return indexNull;
	}
}
