package jumbo.data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WastePartInstance extends Instance {

	private final Map<Integer, JumboPath> jumboIdToPath;

	public WastePartInstance(final int[] jumbos, final int[] items, final Map<Integer, JumboPath> jumboIdToPath) {
		super(jumbos, items, null, null);
		this.jumboIdToPath = jumboIdToPath;
	}

	public static WastePartInstance create(final List<WastePart> allWasteParts, final List<Integer> allMissingItems) {
		final Map<Integer, JumboPath> jumboToPathMap = new HashMap<>();
		final int numWasteParts = allWasteParts.size();
		final int[] jumbos = new int[3*numWasteParts];

		for (int i = 0; i < numWasteParts; i++) {
			final WastePart part = allWasteParts.get(i);
			jumbos[3*i  ] = i;
			jumbos[3*i+1] = part.sizeX();
			jumbos[3*i+2] = part.sizeY();
			jumboToPathMap.put(i, new JumboPath(part.positionCoding(), part.depth()));
		}

		return new WastePartInstance(jumbos, allMissingItems.stream().mapToInt(i -> i).toArray(), jumboToPathMap);
	}
}
