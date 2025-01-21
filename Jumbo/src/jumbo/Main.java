package jumbo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import jumbo.algo.MainAlgorithm;
import jumbo.data.Instance;
import jumbo.data.io.InstanceLoader;
import jumbo.data.io.SolutionExporter;

public class Main {

	public static void main(final String[] args) throws FileNotFoundException, IOException, InterruptedException {
		System.out.println("Hello world!");

		final Instance instance = new InstanceLoader(new File("resources/1_easy/instance_01.json")).parse();

//		int[][] indexItems = new AlgoPPC(instance).partitionning();

		System.out.println(instance);

		new SolutionExporter(new MainAlgorithm(instance).run()).export(new File("output_test.json"));
	}
}
