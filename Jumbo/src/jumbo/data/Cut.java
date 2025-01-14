package jumbo.data;

public record Cut(CutOrientation orientation, int sizeX, int sizeY, int itemFlipCoding, int... itemIds) {

	public int computePosition() {
		// TODO Auto-generated method stub
		return 0;
	}

}
