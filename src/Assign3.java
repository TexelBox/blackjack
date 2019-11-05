import java.util.Arrays;
import java.util.stream.IntStream;

public class Assign3 {
//	static int best = 99;

	@SuppressWarnings("unused")
	private static float imbalance(int[] input) {
		int n = input.length;
		if(n==0) {return 0;}

		
		for(int i = n - 1 ; i > 0;i--) {
			
				
				float a  = Math.max(IntStream.of(Arrays.copyOfRange(input, i, n)).sum(),IntStream.of(Arrays.copyOfRange(input, 0, i)).sum());
				float b  = Math.min(IntStream.of(Arrays.copyOfRange(input, 0, i)).sum(),IntStream.of(Arrays.copyOfRange(input, i, n)).sum());
				float max = a/b;
//				System.out.println(max);
				int[] temp = Arrays.copyOfRange(input, i, n);
				int[] temp2 = Arrays.copyOfRange(input, 0, i);
				int min = (int) Math.max(Math.max(imbalance(temp), imbalance(temp2)),max);
				System.out.println(min);
//				if(min < best) {
//					System.out.println("anything");
//					best = min;
//				}

		}
		
		return 0;
	}
	public static void main(String[] args) {
		int[] value = {5,4,3,4,4};
		int n = value.length;
		
//		int best = imbalance(value);//
//		System.out.println(best);
	}
}
