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
		final int[] itemIds = cut.itemIds();
		if (left == null && right == null && itemIds.length > 1) {
			System.out.println("\u001b[0;33mWARNING: Situation encore expérimentale, en test.\u001b[0m");
			final JSONObject leftJson = new JSONObject();
			json.put("left", leftJson);
			leftJson.put("dir-cut", cut.orientation().other().name().toLowerCase());

			// Récursif itératif:
			int maxTall = 0;
			JSONObject subJson = new JSONObject();
			leftJson.put("left", subJson);

			// Pour tout les items à placer:
			for (int i = 0; i < itemIds.length; i++) {
				final int item = itemIds[i];

				// Booléen indicateur de si l'item a été tourné:
				final boolean flipped = (cut.itemFlipCoding()&(1<<i))==0;

				// Infos de base sur l'item non flip:
				final int tall = instance.getItemHeight(item);
				final int wide = instance.getItemWidth(item);

				// Obtention de la position de l'offset suivant le flipping ou non de l'item:
				final int offset = flipped? tall: wide;
				final int otherSize = flipped? wide: tall;
				if (otherSize > maxTall) {
					maxTall = otherSize;
				}

				// On crée un JSon pour l'item
				final JSONObject leftItemTopJson = new JSONObject();
				leftItemTopJson.put("item_id", instance.getItemId(item));

				// L'item est ajouté à gauche, et en haut de sa gauche
				final JSONObject leftItemJson = new JSONObject();
				leftItemJson.put("dir-cut", cut.orientation().other().name().toLowerCase());
				leftItemJson.put("offset", otherSize);
				leftItemJson.put("left", leftItemTopJson);

				// Le cut est toujours le même:
				subJson.put("left", leftItemJson);
				subJson.put("dir-cut", cut.orientation().name().toLowerCase());
				subJson.put("offset", offset);

				// Le dernier item n'a pas de droite:
				if (i == itemIds.length - 1) {
					break;
				}

				// Et a droite on a un sous-json:
				final JSONObject newSubJson = new JSONObject();
				subJson.put("right", newSubJson);
				subJson = newSubJson;
			}

			// Finalisation:
			leftJson.put("offset", maxTall);
			return json;
		}

		if (itemIds.length == 1) {
			final int item = itemIds[0];
			final JSONObject leftJson = new JSONObject();
			json.put("left", leftJson);
			leftJson.put("dir-cut", cut.orientation().other().name().toLowerCase());

			final int offset = cut.itemFlipCoding()==0?
				instance.getItemHeight(item):
				instance.getItemWidth(item);

			leftJson.put("offset", offset);
			final JSONObject topJson = new JSONObject();
			leftJson.put("left", topJson);
			topJson.put("item_id", instance.getItemId(item));
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
