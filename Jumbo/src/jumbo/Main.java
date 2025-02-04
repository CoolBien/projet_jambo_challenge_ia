package jumbo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.json.JSONException;

import jumbo.algo.MainAlgorithm;
import jumbo.data.Instance;
import jumbo.data.Solution;
import jumbo.data.io.InstanceLoader;
import jumbo.data.io.SolutionExporter;

public class Main {

	public static void main(final String[] args) throws FileNotFoundException, IOException, InterruptedException {
		try {
			System.out.println("Hello world!");

//		final Instance instance = new InstanceLoader(new File("resources/0_ultra_easy_debug/instance_00.json")).parse();
//		final Instance instance = new InstanceLoader(new File("resources/1_easy/instance_01.json")).parse();
//		final Instance instance = new InstanceLoader(new File("resources/2_hard/instance_21.json")).parse();

			final Instance instance = new InstanceLoader(new File(args[0])).parse();
			System.out.println("Nombre d'items initial:   "+instance.getItems().length/3);
			System.out.println("Nombre de jumbos initial: "+instance.getJumbos().length/3);
//		int[][] indexItems = new AlgoPPC(instance).partitionning();

			System.out.println(instance);

			Solution solution = new MainAlgorithm(instance).run();
			if (solution != null) {
				new SolutionExporter(solution).export(new File("output_test.json"));
			}
			System.out.println("Done");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
