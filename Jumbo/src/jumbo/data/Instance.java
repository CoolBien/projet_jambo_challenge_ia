package jumbo.data;

import java.util.Map;

import jumbo.algo.AlgoGenetique;

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
	@SuppressWarnings("unused")
	private final Map<Integer, String> jumbosIdToName;

	/**
	 * Map l'id d'un item vers son nom.
	 */
	@SuppressWarnings("unused")
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

	public int getItemHeight(final int elem) {
		return items[elem * 3 + 2];
	}

	public int getItemArea(final int itemId) {
		return items[itemId * 3 + 1] * items[itemId * 3 + 2];
	}

	/**
	 * Convert from internal jumbo id to problem instance jumbo id
	 * @param jumboId
	 * @return
	 */
	public int getJumboId(final int jumboId) {
		return jumbos[jumboId * 3];
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

	public void addItems(final AlgoGenetique[] algorithms2, final WastePartInstance wastePartInstance) {
		// TODO Auto-generated method stub

	}
}
