package edu.utexas.cs.cs378;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.stream.Collectors;

public class MainServer {
	private static void addToTop10(PriorityQueue<DriverStat> top10Drivers,
			DriverStat candidate) {
		if (top10Drivers.size() < 10) {
			top10Drivers.offer(candidate);
		} else if (candidate.getTotalEarnings() > top10Drivers.peek().getTotalEarnings()) {
			top10Drivers.poll();
			top10Drivers.offer(candidate);
		}
	}

	public static void main(String[] args) {
		int port = 33333;
		System.out.println("Starting server on port " + port);

		PriorityQueue<DriverStat> top10Drivers = new PriorityQueue<>(10,
				Comparator.comparingDouble(e -> e.getTotalEarnings()));

		try (ServerSocket serverSocket = new ServerSocket(port);
			Socket clientSocket = serverSocket.accept();
			BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {
			System.out.println("Client connected!");

			Map<String, DriverStat> driverMap = new HashMap<>();

			String line;
			long count = 0;

			while((line = in.readLine()) != null) {
				String[] parts = line.split(",");
				if(parts.length != 3) {
					System.err.println("Invalid line: " + line);
					continue;
				}

				String driverId = parts[0];
				String medallion = parts[1];
				double totalAmount = Double.parseDouble(parts[2]);

				driverMap.putIfAbsent(driverId, new DriverStat(driverId));
				DriverStat stat = driverMap.get(driverId);
				stat.increaseEarnings(totalAmount);
				stat.addMedallion(medallion);

				addToTop10(top10Drivers, stat);

				count++;
				if(count % 1000 == 0) {
					System.out.println("Processed " + count + " lines.");
				}
			}
			System.out.println("Done receiving data. Calculating top 10 drivers...");
			List<DriverStat> topDriversList = new ArrayList<>(top10Drivers);
			topDriversList.sort(Comparator.comparingDouble(DriverStat::getTotalEarnings).reversed());
			System.out.println("Top 10 Drivers:");
			for(DriverStat ds : topDriversList) {
				System.out.println(ds);
			}
		} catch (IOException e) {
			System.err.println("Error starting server: " + e.getMessage());
		}
	}
}