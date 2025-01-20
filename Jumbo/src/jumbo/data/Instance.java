package jumbo.data;

import java.util.Map;

public class Instance {

	/**
	 * triplets de int pour : (id, width, height)
	 */
	private final int[] jumbos;

	/**
	 * triplets de int pour : (id, width, height)
	 */
	private final int[] items;

	/**
	 * Map l'id d'un jumbo vers son nom.
	 */
	private final Map<Integer, String> jumbosIdToName;

	/**
	 * Map l'id d'un item vers son nom.
	 */
	private final Map<Integer, String> itemsIdToName;

	/**
	 * Constructeur.
	 * @param jumbos : triplets de int pour : (id, width, height)
	 * @param items : triplets de int pour : (id, width, height)
	 */
	public Instance(final int[] jumbos, final int[] items, final Map<Integer, String> jumbosIdToName, final Map<Integer, String> itemsIdToName) {
		this.jumbos = jumbos;
		this.items = items;
		this.jumbosIdToName = jumbosIdToName;
		this.itemsIdToName = itemsIdToName;
	}

	public int[] getJumbos() {
		return jumbos;
	}

	public int[] getItems() {
		return items;
	}

	public int getItemId(final int elem) {
		return items[elem * 3];
	}

	public int getItemWidth(final int elem) {
		return items[elem * 3 + 1];
	}

	public int getItemHeigth(final int elem) {
		return items[elem * 3 + 2];
	}

	public int getItemArea(final int itemId) {
		return items[itemId * 3 + 1] * items[itemId * 3 + 2];
	}

	public int getMaxJumboSize() {
		int maxSize = 0;
		for (int i = 0; i < jumbos.length; i++) {
			if (i % 3 != 0 && jumbos[i] > maxSize) {
				maxSize = jumbos[i];
			}
		}
		return maxSize;
	}

	public int getJumboWidth(final int jumboId) {
		return jumbos[jumboId * 3 + 1];
	}

	public int getJumboHeight(final int jumboId) {
		return jumbos[jumboId * 3 + 2];
	}

	public int getJumboSize(final int jumboId) {
		return jumbos[jumboId * 3 + 1] * jumbos[jumboId * 3 + 2];
	}
}
