package jumbo.data;

public record Cut(CutOrientation orientation, int sizeX, int sizeY, int itemFlipCoding, int... itemIds) {

	public int computePosition(Instance instance) {
		int positionCut = 0;
		for (int i=0; i<itemIds.length; i++) {
			if ((itemFlipCoding & (1 << i)) != 0) {
				positionCut += instance.getItemHeigth(itemIds[i]);
			}
			else {
				positionCut += instance.getItemWidth(itemIds[i]);
			}
		}
		return positionCut;
	}

}
