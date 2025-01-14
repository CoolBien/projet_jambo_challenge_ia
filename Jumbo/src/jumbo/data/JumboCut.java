package jumbo.data;

public class JumboCut {

	private int jumboId;

	private final Cut[] cuts;

	private int[] cuttedResultSizes;

	private int[] itemIds;
	
	private int[] scraps;

	public JumboCut(final Cut[] cuts) {
		this.cuts = cuts;
	}

	public int getJumboId() {
		return jumboId;
	}

	public Cut[] getCuts() {
		return cuts;
	}

	public int getLeftIdOf(final int i) {
		final int id = ((i+1) << 1) - 1;
		if (cuts[id] != null) {
			return id;
		}
		return -1;
	}

	public int getRightIdOf(final int i) {
		final int id = ((i+1) << 1);
		if (cuts[id] != null) {
			return id;
		}
		return -1;
	}

	public int getItemIdOf(final int i) {
		return itemIds[i];
	}
	
	public int computeAreaWaste() {
		return jumboId;
		
	}
}
