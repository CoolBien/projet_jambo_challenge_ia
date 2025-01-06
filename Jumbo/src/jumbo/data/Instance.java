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

}
