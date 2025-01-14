package jumbo.data.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import jumbo.data.Instance;

public class InstanceLoader {

	private final JSONObject object;

	public InstanceLoader(final File file) throws FileNotFoundException, IOException {
		String jsonText = "";
		try (BufferedReader buf = new BufferedReader(new FileReader(file))) {
			jsonText = buf.lines().reduce(String::concat).get();
		}
		object = new JSONObject(jsonText);
	}

	public Instance parse() {
		final List<Integer> jumbos = new ArrayList<>();
		final List<Integer> items = new ArrayList<>();
		final Map<Integer, String> jumbosIdToName = new HashMap<>();
		final Map<Integer, String> itemsIdToName  = new HashMap<>();

		parseObject(items, itemsIdToName, "items");
		parseObject(jumbos, jumbosIdToName, "jumbos");

		return new Instance(jumbos.stream().mapToInt(i -> i).toArray(), items.stream().mapToInt(i -> i).toArray(), jumbosIdToName, itemsIdToName);
	}

	private void parseObject(final List<Integer> items, final Map<Integer, String> itemsIdToName, final String key) {
		final JSONArray jsonArray = object.getJSONArray(key);
		final int n = jsonArray.length();
		for (int i=0; i<n; i++) {

			// Get info from JSON
			final JSONObject object = jsonArray.getJSONObject(i);
			final int id = object.getInt("id");
			final String name = object.getString("name");
			final int nb = object.getInt("nb");
			final JSONObject sizeObject = object.getJSONObject("size");
			final int width = sizeObject.getInt("width");
			final int height = sizeObject.getInt("height");

			// Put name into map
			itemsIdToName.put(id, name);

			// Put the triplet.
			for (int j=0; j<nb; j++) {
				items.add(id);
				items.add(width);
				items.add(height);
			}
		}
	}
}
