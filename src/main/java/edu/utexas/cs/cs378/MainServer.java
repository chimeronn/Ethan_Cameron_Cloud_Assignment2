package edu.utexas.cs.cs378;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.stream.Collectors;

public class MainServer {
	public static void main(String[] args) {
		int port = 33333;
		System.out.println("Starting server on port " + port);
	
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

				count++;
				if(count % 100000 == 0) {
					System.out.println("Processed " + count + " lines.");
				}
			}
			System.out.println("Done receiving data. Calculating top 10 drivers...");
			List<Map.Entry<String, DriverStat>> topDrivers = new ArrayList<>(driverMap.entrySet());
			topDrivers.sort((e1, e2) -> Double.compare(e2.getValue().getTotalEarnings(), e1.getValue().getTotalEarnings()));
			System.out.println("Top 10 Drivers:");
			for(int i = 0; i < Math.min(10, topDrivers.size()); i++) {
				System.out.println(topDrivers.get(i).getValue());
			}
		} catch (IOException e) {
			System.err.println("Error starting server: " + e.getMessage());
		}
	}
}