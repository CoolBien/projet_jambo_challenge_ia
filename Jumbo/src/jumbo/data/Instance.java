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
	
	public int getItemId(int elem) {
		return items[elem * 3];
	}
	
	public int getItemWidth(int elem) {
		return items[elem * 3 + 1];
	}
	
	public int getItemHeight(int elem) {
		return items[elem * 3 + 2];
	}
	
	public int getJumboWidth(int elem) {
		return jumbos[elem * 3 + 1];
	}
	
	public int getJumboHeight(int elem) {
		return jumbos[elem * 3 + 2];
	}
}
