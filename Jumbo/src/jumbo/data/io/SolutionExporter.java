package jumbo.data.io;

import java.io.File;

import org.json.JSONArray;
import org.json.JSONObject;

import jumbo.data.Cut;
import jumbo.data.JumboCut;
import jumbo.data.Solution;

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
		json.put("cut-tree", exportCutTree(jumboCut, 0));
		return json;
	}

	private JSONObject exportCutTree(final JumboCut jumboCut, final int i) {
		// save current cut
		final JSONObject json = new JSONObject();
		final Cut cut = jumboCut.getCuts()[i];
		json.put("dir-cut", cut.orientation().name().toLowerCase());
		json.put("offset", cut.position());

		// get children
		final int left = jumboCut.getLeftIdOf(i);
		final int right = jumboCut.getRightIdOf(i);

		// recursive
		if (left >= 0) {
			json.put("left", exportCutTree(jumboCut, left));
		}
		if (right >= 0) {
			json.put("right", exportCutTree(jumboCut, right));
		}

		// item ids:
		final int itemId = jumboCut.getItemIdOf(i);
		if (itemId >= 0) {
			json.put("item_id", itemId);
		}

		return json;
	}
}
