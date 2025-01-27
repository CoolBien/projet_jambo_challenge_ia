package jumbo.data.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import jumbo.data.Cut;
import jumbo.data.Instance;
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
		final Instance instance = solution.getInstance();
		final JSONObject json = new JSONObject();
		final Cut cut = node.getItem();
		json.put("dir-cut", cut.orientation().name().toLowerCase());
		json.put("offset", cut.computePosition(instance));

		// get children
		final BinaryTree<Cut> left = node.getLeft();
		final BinaryTree<Cut> right = node.getRight();

		// item ids:
		final int[] itemId = cut.itemIds();
		if (left == null && right == null && itemId.length > 1) {
			System.out.println("\u001b[0;33mWARNING: Apparamment je peux me retrouver dans cette situation.\u001b[0m");
		}

		if (itemId.length == 1) {
			final int item = itemId[0];
			final JSONObject leftJson = new JSONObject();
			json.put("left", leftJson);
			leftJson.put("dir-cut", cut.orientation().other().name().toLowerCase());
			final int offset = cut.itemFlipCoding()==0? instance.getItemHeight(item): instance.getItemWidth(item);
			leftJson.put("offset", offset);
			final JSONObject topJson = new JSONObject();
			leftJson.put("left", topJson);
			topJson.put("item_id", item);
			return json;
		}

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
