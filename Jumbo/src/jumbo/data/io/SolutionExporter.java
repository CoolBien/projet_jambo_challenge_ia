package jumbo.data.io;

import java.io.File;

import org.json.JSONArray;
import org.json.JSONObject;

import jumbo.data.Cut;
import jumbo.data.JumboCut;
import jumbo.data.Solution;
import jumbo.data.util.BinaryTree;

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
		json.put("cut-tree", exportCutTree(jumboCut, jumboCut.getCuts()));
		return json;
	}

	private JSONObject exportCutTree(final JumboCut jumboCut, final BinaryTree<Cut> node) {
		// save current cut
		final JSONObject json = new JSONObject();
		final Cut cut = node.getItem();
		json.put("dir-cut", cut.orientation().name().toLowerCase());
		json.put("offset", cut.computePosition());

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

		// item ids:
		final int[] itemId = cut.itemIds();
		if (itemId.length == 1) {
			json.put("item_id", itemId[0]);
		}

		return json;
	}
}
