
// package edu.utexas.cs.cs378;

// import java.io.DataOutputStream;
// import java.io.IOException;
// import java.net.Socket;
// import java.nio.file.Path;
// import java.nio.file.Paths;
// import java.util.HashMap;
// import java.util.HashSet;
// import java.util.List;
// import java.util.Map;
// import java.util.Set;

// public class MainClient {

// 	private static final String DEFAULT_HOST = "localhost";
// 	private static final int DEFAULT_PORT = 33333;
// 	private static final int DEFAULT_BATCH_SIZE = 4000;
// 	private static final String DEFAULT_DATASET = "taxi-data-sorted-small.csv.bz2";
// 	private static final boolean USE_DATASET = true;

// 	static public int portNumber = DEFAULT_PORT;
// 	static public String hostName = DEFAULT_HOST;

// 	static public int batchSize = DEFAULT_BATCH_SIZE;
// 	private static Socket mySocket;

// 	/**
// 	 * A main method to run examples.
// 	 *
// 	 * @param args not used
// 	 */
// 	public static void main(String[] args) {

// 		// Defaults are defined above; change them in code for new runs.
// 		String datasetPath = USE_DATASET ? DEFAULT_DATASET : null;

// 		try {

// 			System.out.println("Connecting to server " + hostName + ":" + portNumber);
// 			if (datasetPath != null) {
// 				System.out.println("Dataset: " + datasetPath + " (errors capped at 5 lines)");
// 			} else {
// 				System.out.println("Using generated demo data (" + batchSize + " items)");
// 			}

// 			Map<String, DriverAccumulator> aggregates = new HashMap<>();
// 			long[] validCount = new long[1];
// 			long[] invalidCount = new long[1];

// 			if (datasetPath != null) {
// 				try {
// 					Path inputPath = Paths.get(datasetPath);
// 					TaxiDataCleaner.CleanResult cleanResult = TaxiDataCleaner.cleanFile(inputPath,
// 							(medallion, driverId, totalAmount) -> {
// 							DriverAccumulator acc = aggregates.computeIfAbsent(driverId, k -> new DriverAccumulator());
// 							acc.totalEarnings += totalAmount;
// 							acc.medallions.add(medallion);
// 							validCount[0]++;
// 						});
// 					invalidCount[0] = cleanResult.getInvalidLines();
// 					cleanResult.printErrorSamples(System.out);
// 					System.out.println("Valid lines: " + validCount[0] + " of "
// 							+ cleanResult.getTotalLines() + ", dropped: " + cleanResult.getInvalidLines());
// 					if (validCount[0] == 0) {
// 						System.out.println("No valid data to send after cleaning.");
// 						return;
// 					}
// 				} catch (IOException e) {
// 					System.err.println("Failed to clean dataset: " + e.getMessage());
// 					return;
// 				}
// 			} else {
// 				List<DataItem> dataItems = Utils.generateExampleData(batchSize);
// 				for (DataItem item : dataItems) {
// 					String[] parts = item.getLine().split(",", 3);
// 					if (parts.length < 2) {
// 						continue;
// 					}
// 					String medallion = parts[0];
// 					String driverId = parts[1];
// 					double total = item.getValueA();
// 					DriverAccumulator acc = aggregates.computeIfAbsent(driverId, k -> new DriverAccumulator());
// 					acc.totalEarnings += total;
// 					acc.medallions.add(medallion);
// 					validCount[0]++;
// 				}
// 			}

// 			DriverStat[] stats = new DriverStat[aggregates.size()];
// 			int idx = 0;
// 			for (Map.Entry<String, DriverAccumulator> entry : aggregates.entrySet()) {
// 				DriverAccumulator acc = entry.getValue();
// 				stats[idx++] = new DriverStat(entry.getKey(), acc.totalEarnings, acc.medallions.size());
// 			}

// 			try (Socket socket = new Socket(hostName, portNumber);
// 					DataOutputStream dos = new DataOutputStream(socket.getOutputStream())) {
// 				dos.writeInt(stats.length);
// 				for (DriverStat stat : stats) {
// 					dos.writeUTF(stat.getDriverId());
// 					dos.writeDouble(stat.getTotalEarnings());
// 					dos.writeInt(stat.getDistinctMedallions());
// 				}
// 				dos.flush();
// 				System.out.println("Sent " + stats.length + " driver aggregates to reducer.");
// 			}

// 		} catch (IOException e) {
// 			e.printStackTrace();
// 		}

// 	}
// }

// class DriverAccumulator {
// 	double totalEarnings = 0.0;
// 	Set<String> medallions = new HashSet<>();
// }