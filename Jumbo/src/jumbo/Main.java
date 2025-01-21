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

	public static void main(final String[] args) throws FileNotFoundException, IOException {
		System.out.println("Hello world!");

		final Instance i = new InstanceLoader(new File("resources/1_easy/instance_01.json")).parse();

		System.out.println(i);

		new SolutionExporter(new MainAlgorithm(i).run()).export(new File("output_test.json"));
	}
}
