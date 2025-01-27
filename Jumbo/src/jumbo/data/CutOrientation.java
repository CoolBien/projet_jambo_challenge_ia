package jumbo.data;

public enum CutOrientation {
	HORIZONTAL, VERTICAL;

	public CutOrientation other() {
		return CutOrientation.values()[ordinal()^1];
	}
}