package edu.utexas.cs.cs378;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;

public final class TaxiDataCleaner {

    private static final int EXPECTED_COLUMNS = 17;
    private static final int MAX_ERROR_LINES = 5;
    private static final double TOTAL_TOLERANCE = 0.01d;
    private static final double TOTAL_LIMIT = 500.0d;
    private static final Locale LOCALE = Locale.ROOT;

    private TaxiDataCleaner() {
    }

    public static CleanResult cleanFile(Path inputPath) throws IOException {
        try (InputStream fileStream = Files.newInputStream(inputPath);
                InputStream decompressed = wrapIfCompressed(inputPath, fileStream);
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(decompressed, StandardCharsets.UTF_8, 64 * 1024))) {
            return cleanLines(reader);
        }
    }

    private static void cleanLines(BufferedReader reader) throws IOException {
        long total = 0;
        long invalid = 0;

        String line;
        while((line = reader.readLine()) != null) {
            total++;
            ValidatedRide validated = validate(line, null);
            if (validated == null) {
                invalid++;
                System.out.println("Invalid line: " + line);
            }
        }
        System.out.println("Total lines: " + total + ", Invalid lines: " + invalid);
    }

    public static CleanResult cleanFile(Path inputPath, LineConsumer consumer) throws IOException {
        try (InputStream fileStream = Files.newInputStream(inputPath);
                InputStream decompressed = wrapIfCompressed(inputPath, fileStream);
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(decompressed, StandardCharsets.UTF_8, 64 * 1024))) {
            return cleanLines(reader);
        }
    }

    // public static CleanResult cleanLines(BufferedReader reader) throws IOException {
    //     return cleanLines(reader, null);
    // }

    // private static CleanResult cleanLines(BufferedReader reader, LineConsumer consumer) throws IOException {
    //     List<DataItem> validItems = consumer == null ? new ArrayList<>() : Collections.emptyList();
    //     List<String> errorSamples = new ArrayList<>();
    //     long total = 0;
    //     long invalid = 0;

        // String line;
        // String[] scratch = new String[EXPECTED_COLUMNS];
        // while ((line = reader.readLine()) != null) {
        //     total++;
        //     ValidatedRide validated = validate(line, scratch);
        //     if (validated == null) {
        //         invalid++;
        //         if (errorSamples.size() < MAX_ERROR_LINES) {
        //             errorSamples.add(line);
        //         }
        //         continue;
        //     }
        //     if (consumer != null) {
        //         consumer.accept(validated.cleanedLine, validated.totalAmount);
        //     } else {
        //         validItems.add(validated.toDataItem());
        //     }
        // }

    //     return new CleanResult(validItems, errorSamples, total, invalid);
    // }

    private static InputStream wrapIfCompressed(Path path, InputStream baseStream) throws IOException {
        String name = path.getFileName().toString().toLowerCase(LOCALE);
        if (name.endsWith(".bz2")) {
            return new BZip2CompressorInputStream(baseStream);
        }
        return baseStream;
    }

    private static ValidatedRide validate(String line, String[] buffer, boolean buildLine) {
        if (line == null || line.isEmpty()) {
            return null;
        }

        String[] parts = split17(line, buffer);
        if (parts == null) {
            return null;
        }

        String[] trimmed = new String[EXPECTED_COLUMNS];
        for (int i = 0; i < parts.length; i++) {
            trimmed[i] = parts[i].trim();
        }

        Double fareAmount = parseDouble(trimmed[11]);
        Double surcharge = parseDouble(trimmed[12]);
        Double mtaTax = parseDouble(trimmed[13]);
        Double tipAmount = parseDouble(trimmed[14]);
        Double tollsAmount = parseDouble(trimmed[15]);
        Double totalAmount = parseDouble(trimmed[16]);

        if (fareAmount == null || surcharge == null || mtaTax == null || tipAmount == null
                || tollsAmount == null || totalAmount == null) {
            return null;
        }

        double computedTotal = fareAmount + surcharge + mtaTax + tipAmount + tollsAmount;
        if (Math.abs(totalAmount - computedTotal) > TOTAL_TOLERANCE) {
            return null;
        }

        if (totalAmount > TOTAL_LIMIT) {
            return null;
        }

        String cleanedLine = buildLine ? String.join(",", trimmed) : null;
        return new ValidatedRide(cleanedLine, trimmed[0], trimmed[1], totalAmount.floatValue());
    }

    // Fast split without regex; expects exactly 16 commas / 17 columns.
    private static String[] split17(String line, String[] reuse) {
        String[] out = reuse != null && reuse.length == EXPECTED_COLUMNS ? reuse : new String[EXPECTED_COLUMNS];
        int col = 0;
        int start = 0;
        int len = line.length();
        for (int i = 0; i < len; i++) {
            if (line.charAt(i) == ',') {
                if (col >= EXPECTED_COLUMNS - 1) {
                    return null; // too many columns
                }
                out[col++] = line.substring(start, i);
                start = i + 1;
            }
        }
        out[col++] = line.substring(start);
        if (col != EXPECTED_COLUMNS) {
            return null;
        }
        return out;
    }

    private static Double parseDouble(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        try {
            return Double.valueOf(value);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    public static final class CleanResult {
        private final List<DataItem> validItems;
        private final List<String> errorSamples;
        private final long totalLines;
        private final long invalidLines;

        CleanResult(List<DataItem> validItems, List<String> errorSamples, long totalLines, long invalidLines) {
            this.validItems = Collections.unmodifiableList(new ArrayList<>(validItems));
            this.errorSamples = Collections.unmodifiableList(new ArrayList<>(errorSamples));
            this.totalLines = totalLines;
            this.invalidLines = invalidLines;
        }

        public List<DataItem> getValidItems() {
            return validItems;
        }

        public List<String> getErrorSamples() {
            return errorSamples;
        }

        public long getTotalLines() {
            return totalLines;
        }

        public long getInvalidLines() {
            return invalidLines;
        }

        public void printErrorSamples(PrintStream out) {
            Objects.requireNonNull(out, "out");
            if (errorSamples.isEmpty()) {
                out.println("No erroneous data lines found.");
                return;
            }
            out.println("Erroneous data lines (up to " + MAX_ERROR_LINES + "):");
            for (String line : errorSamples) {
                out.println(line);
            }
        }
    }

    private static final class ValidatedRide {
        private final String cleanedLine;
        private final String medallion;
        private final String driverId;
        private final float totalAmount;

        ValidatedRide(String cleanedLine, String medallion, String driverId, float totalAmount) {
            this.cleanedLine = cleanedLine;
            this.medallion = medallion;
            this.driverId = driverId;
            this.totalAmount = totalAmount;
        }

        DataItem toDataItem() {
            String lineOut = cleanedLine != null ? cleanedLine : medallion + "," + driverId;
            return new DataItem(lineOut, totalAmount, 0.0f);
        }
    }

    @FunctionalInterface
    public interface LineConsumer {
        void accept(String medallion, String driverId, float totalAmount);
    }
}
