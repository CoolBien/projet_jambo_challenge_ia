package jumbo.utils;

public class Utils {
	public <T> void shuffleArray(T[] array) {
		T pivot;
		for (int i =0; i < array.length; i++) {
			final int randomItemSelected1 = 0 + (int)(Math.random() * array.length);
			
			if (randomItemSelected1 == i) {
				continue;
			}

			pivot = array[randomItemSelected1];
			array[randomItemSelected1] = array[i];
			array[i] = pivot;
		}
	}
}