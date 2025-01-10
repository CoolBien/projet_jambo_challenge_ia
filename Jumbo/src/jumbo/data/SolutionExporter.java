package jumbo.data;

import java.io.File;

import org.json.JSONArray;
import org.json.JSONObject;

public class SolutionExporter {

	private final Solution solution;

	public SolutionExporter(final Solution solution) {
		this.solution = solution;
	}

	public void export(final File dest) {
		final JSONObject root = new JSONObject();
		final JSONArray opList = new JSONArray();
		root.put("op_list", opList);

		for (final JumboCut jumboCut : solution.getJumboCuts()) {
			opList.put(exportJumboCut(jumboCut));
		}
	}

	private JSONObject exportJumboCut(final JumboCut jumboCut) {
		final JSONObject json = new JSONObject();
		json.put("jumbo_id", jumboCut.getJumboId());
		json.put("cut-tree", exportCutTree(jumboCut));
		return json;
	}

	private JSONObject exportCutTree(final JumboCut jumboCut) {
		return new JSONObject();
	}
}
