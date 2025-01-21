package jumbo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import jumbo.algo.AlgoPPC;
import jumbo.data.Instance;
import jumbo.data.io.InstanceLoader;

public class Main {

	public static void main(final String[] args) throws FileNotFoundException, IOException {
		System.out.println("Hello world!");

		final Instance instance = new InstanceLoader(new File("resources/1_easy/instance_01.json")).parse();
		
		int[][] indexItems = new AlgoPPC(instance).partitionning();

		System.out.println(instance);
	}
}
