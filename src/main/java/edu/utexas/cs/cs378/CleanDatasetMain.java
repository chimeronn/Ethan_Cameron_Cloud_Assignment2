package edu.utexas.cs.cs378;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class CleanDatasetMain {

    private CleanDatasetMain() {
    }

    public static void main(String[] args) {
        String input = args.length >= 1 ? args[0] : "taxi-data-sorted-small.csv.bz2";
        Path inputPath = Paths.get(input);

        String outputPath = "cleaned-data.csv";

        try (PrintWriter writer = new PrintWriter(outputPath)) {
            TaxiDataCleaner.CleanResult result = TaxiDataCleaner.cleanFile(inputPath, (cleanedLine, totalAmount) -> {
                writer.println(cleanedLine);
            });

            result.printErrorSamples(System.out);

            long validCount = result.getTotalLines() - result.getInvalidLines();

            System.out.println("Valid lines: " + validCount + " of " + result.getTotalLines()
                    + ", dropped: " + result.getInvalidLines());
                    
        } catch (IOException e) {
            System.err.println("Failed to clean dataset: " + e.getMessage());
        }
    }
}