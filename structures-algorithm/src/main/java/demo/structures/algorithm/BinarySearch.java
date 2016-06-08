package demo.structures.algorithm;

public class BinarySearch {

	static int binay(int k, int[] array, int left, int right) {

		int l = left - 1;
		int r = left + 1;
		while (l + 1 != r) {
			int i = (l + r) / 2;
			// k在数组的左半部分
			if (k < array[i]) {
				r = i;
			}
			if (k == array[i]) {
				return i;
			}
			// k在数组的右半部分
			if (k > array[i]) {
				l = i;
			}
		}

		return -1;
	}

	// Arrays binarySearch
	 static int binarySearch(double[] a, int fromIndex, int toIndex,
			double key) {
		int low = fromIndex;
		int high = toIndex - 1;

		while (low <= high) {
			//相当于除以2
			int mid = (low + high) >>> 1;
			
			double midVal = a[mid];

			if (midVal < key)
				low = mid + 1; // Neither val is NaN, thisVal is smaller
			else if (midVal > key)
				high = mid - 1; // Neither val is NaN, thisVal is larger
			else {
					
				long midBits = Double.doubleToLongBits(midVal);
				long keyBits = Double.doubleToLongBits(key);
				if (midBits == keyBits) // Values are equal
					return mid; // Key found
				else if (midBits < keyBits) // (-0.0, 0.0) or (!NaN, NaN)
					low = mid + 1;
				else
					// (0.0, -0.0) or (NaN, !NaN)
					high = mid - 1;
			}
		}
		return -(low + 1); // key not found.
	}

}
