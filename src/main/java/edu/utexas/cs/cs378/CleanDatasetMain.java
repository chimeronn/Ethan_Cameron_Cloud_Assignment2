package edu.utexas.cs.cs378;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class CleanDatasetMain {

    private CleanDatasetMain() {
    }

    public static void main(String[] args) {
        String input = args.length >= 1 ? args[0] : "taxi-data-sorted-small.csv.bz2";
        Path inputPath = Paths.get(input);

        try {
            TaxiDataCleaner.CleanResult result = TaxiDataCleaner.cleanFile(inputPath);
            // result.printErrorSamples(System.out);
            // System.out.println("Valid lines: " + result.getValidItems().size() + " of " + result.getTotalLines()
            //         + ", dropped: " + result.getInvalidLines());
        } catch (IOException e) {
            System.err.println("Failed to clean dataset: " + e.getMessage());
        }
    }
}
