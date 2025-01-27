package jumbo.data.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import jumbo.data.Cut;
import jumbo.data.JumboCut;
import jumbo.data.Solution;
import jumbo.utils.BinaryTree;

public class SolutionExporter {

	private final Solution solution;

	public SolutionExporter(final Solution solution) {
		this.solution = solution;
	}

	public void export(final File dest) throws JSONException, FileNotFoundException {
		final JSONObject root = new JSONObject();
		final JSONArray opList = new JSONArray();
		root.put("op_list", opList);

		for (final JumboCut jumboCut : solution.getJumboCuts()) {
			opList.put(exportJumboCut(jumboCut));
		}
		try (final PrintWriter writer = new PrintWriter(dest)) {
			root.write(writer);
		}
	}

	private JSONObject exportJumboCut(final JumboCut jumboCut) {
		final JSONObject json = new JSONObject();
		json.put("jumbo_id", solution.getInstance().getJumboId(jumboCut.getJumboId()));
		json.put("cut-tree", exportCutTree(jumboCut, jumboCut.getCuts()));
		return json;
	}

	private JSONObject exportCutTree(final JumboCut jumboCut, final BinaryTree<Cut> node) {
		// save current cut
		final JSONObject json = new JSONObject();
		final Cut cut = node.getItem();

		// item ids:
		final int[] itemId = cut.itemIds();
		if (itemId.length == 1) {
			json.put("item_id", itemId[0]);
			return json;
		}

		// Only save those if there are no item in it:
		json.put("dir-cut", cut.orientation().name().toLowerCase());
		json.put("offset", cut.computePosition(solution.getInstance()));

		// get children
		final BinaryTree<Cut> left = node.getLeft();
		final BinaryTree<Cut> right = node.getRight();

		// recursive
		if (left != null) {
			json.put("left", exportCutTree(jumboCut, left));
		}
		if (right != null) {
			json.put("right", exportCutTree(jumboCut, right));
		}

		return json;
	}
}
