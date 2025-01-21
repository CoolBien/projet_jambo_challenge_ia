package jumbo.data;

import java.util.List;

public class Solution {

	private final Instance instance;

	private final List<JumboCut> jumboCuts;

	public Solution(final Instance instance, final List<JumboCut> jumboCuts) {
		this.instance = instance;
		this.jumboCuts = jumboCuts;
	}

	public List<JumboCut> getJumboCuts() {
		return jumboCuts;
	}

	public Instance getInstance() {
		return instance;
	}
}
