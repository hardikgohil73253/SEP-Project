import java.util.Scanner;

public class TanCalculator {
    // NFR6: Code is modular and maintainable. [4]
    private static final double SINGULARITY_EPS = 1e-6; // FR3: Precision for detecting undefined angles. [1]

    public static void main(String[] args) {
        // NFR5: Display user-friendly messages. [3]
        System.out.println("=== Tangent Calculator ===");
        System.out.println("Enter angle in degrees (or type 'exit' to quit):");

        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.print("Input(Degree): "); // FR5: Prompt user for input.

            String input = scanner.nextLine().trim();

            // FR5: Handle exit command gracefully.
            if (input.equalsIgnoreCase("exit")) {
                System.out.println("Goodbye!");
                break;
            }

            double degrees;

	    // FR1: Accept any real number input (degrees). [2]
            // FR6: Handle invalid inputs (non-numeric or malformed entries).
            try {
                degrees = Double.parseDouble(input);
                if (Double.isInfinite(degrees) || Double.isNaN(degrees)) {
                    throw new IllegalArgumentException();
                }
            } catch (Exception e) {
                System.out.println("INVALID INPUT"); // FR6: Display invalid input message.
                continue;
            }

            // FR7: Catch unexpected runtime errors.
            try {
                Runtime runtime = Runtime.getRuntime();
                runtime.gc(); // Suggest garbage collection

                long usedMemoryBefore = runtime.totalMemory() - runtime.freeMemory();
                long startTime = System.nanoTime(); // Start timer

                String result = computeTan(degrees);

                long endTime = System.nanoTime(); // End timer
                long usedMemoryAfter = runtime.totalMemory() - runtime.freeMemory();
                long memoryUsed = usedMemoryAfter - usedMemoryBefore;

                double durationInMillis = (endTime - startTime) / 1000000.0;

		//NFR1: Compute and return result <1 ms
		//NFR4: Use ≤64 KB memory
				
		System.out.printf(
			"Result: %s | Time: %.6f ms | Memory: %.2f KB%n",
			result, durationInMillis, memoryUsed / 1024.0
		);
				
                //System.out.println("Result: " + result);
                //System.out.printf("Time taken: %.6f ms%n", durationInMillis); 
                //System.out.printf("Memory used: %.2f KB%n", memoryUsed / 1024.0); 
            } catch (Exception e) {
                System.out.println("ERROR"); // FR7: Display error message.
            }
        }

        scanner.close();
    }

    /**
     * Computes tan(x) for a given angle in degrees.
     * FR2: Uses Math.tan() after converting degrees to radians. [2]
     * FR4: Normalizes large/small angles for accuracy.
     * FR3: Displays "UNDEFINED" if tangent is undefined. [1]
     */
    public static String computeTan(double degrees) {
        double reducedDegrees = rangeReduce(degrees); // FR4: Normalize input angle.

        // FR3: Check for undefined tangent (90°, 270°, …). [1]
        double mod = Math.abs(reducedDegrees % 180);
        if (Math.abs(mod - 90) < SINGULARITY_EPS) {
            return "UNDEFINED"; // FR3: Show UNDEFINED for undefined angles. [1]
        }

        // FR2: Compute tan(x) using Math.tan(). [2]
        double radians = Math.toRadians(reducedDegrees); // FR2: Convert degrees to radians. [2]
        double tanValue = Math.tan(radians);
	if (tanValue == -0.0) {
   		 tanValue = 0.0;
	}
        return String.format("%.6f", tanValue); // FR2: Display result with six decimal places.
    }

    /**
     * Reduces angle to [-180°, 180°] for consistent accuracy.
     * FR4: Normalizes large and small angles.
     */
    private static double rangeReduce(double degrees) {
        double reduced = degrees % 360;
        if (reduced > 180) reduced -= 360;
        if (reduced < -180) reduced += 360;
        return reduced;
    }
}
