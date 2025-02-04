package jumbo.data;

public record WastePart(int sizeX, int sizeY, int positionCoding, int depth) {

	public static WastePart from(final Cut cut, final int positionCoding, final int depth) {
		return new WastePart(cut.sizeX(), cut.sizeY(), positionCoding, depth);
	}

//	public WastePart combinedWith(final WastePart wastePart) {
//		// TODO Auto-generated method stub
//		return null;
//	}
}
