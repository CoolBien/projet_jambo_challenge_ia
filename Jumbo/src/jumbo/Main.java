package jumbo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import jumbo.data.Instance;
import jumbo.data.InstanceLoader;

public class Main {

	public static void main(final String[] args) throws FileNotFoundException, IOException {
		System.out.println("Hello world!");

		final Instance i = new InstanceLoader(new File("resources/1_easy/instance_01.json")).parse();

		System.out.println(i);
	}
}
