package jumbo.data;

public class Solution {

	private final Instance instance;

	private final Cut[] cuts;

	private int[] cuttedResultSizes;

	public Solution(final Instance instance, final Cut[] cuts) {
		this.instance = instance;
		this.cuts = cuts;
	}

}
