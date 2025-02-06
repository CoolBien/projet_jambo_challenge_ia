package jumbo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import jumbo.algo.MainAlgorithm;
import jumbo.data.Instance;
import jumbo.data.Solution;
import jumbo.data.io.InstanceLoader;
import jumbo.data.io.SolutionExporter;

public class Main {

	public static void main(final String[] args) throws FileNotFoundException, IOException, InterruptedException {
		final long startMillis = System.currentTimeMillis();
		try {
			System.out.println("Hello world!");

//			final Instance instance = new InstanceLoader(new File("resources/0_ultra_easy_debug/instance_00.json")).parse();
//			final Instance instance = new InstanceLoader(new File("resources/1_easy/instance_01.json")).parse();
//			final Instance instance = new InstanceLoader(new File("resources/2_hard/instance_21.json")).parse();

			final Instance instance = new InstanceLoader(new File(args[0])).parse();
			System.out.println("Nombre d'items initial:   "+instance.getItems().length/3);
			System.out.println("Nombre de jumbos initial: "+instance.getJumbos().length/3);
//			int[][] indexItems = new AlgoPPC(instance).partitionning();

			System.out.println(instance);

			final Solution solution = new MainAlgorithm(instance).run();
			if (solution != null) {
				System.out.println("Exporting");
				new SolutionExporter(solution).export(new File(args[1]));
			}
			System.out.println("Done");
		} catch (final Exception e) {
			e.printStackTrace();
		} finally {
			System.out.println("Took: "+(System.currentTimeMillis() - startMillis) + " ms");
		}
	}
}
