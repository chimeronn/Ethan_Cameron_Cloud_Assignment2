package edu.utexas.cs.cs378;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

public class MainServer {

	static public int portNumber = 33333;

	/**
	 * A main method to run examples.
	 *
	 * @param args not used
	 */
	public static void main(String[] args) {


		if (args.length > 0) {
			System.err.println("Usage: MainServer <port number>");
			portNumber = Integer.parseInt(args[0]);
		}

		ServerSocket serverSocket;
		Socket clientSocket;

		try {
			serverSocket = new ServerSocket(portNumber);
			System.out.println("Server is running on port number " + portNumber);
			System.out.println("Waiting for client connection ... ");

			clientSocket = serverSocket.accept();
			System.out.println("Client connected. Receiving driver aggregates ...");

			DataInputStream dis = new DataInputStream(clientSocket.getInputStream());

			int count = dis.readInt();
			Map<String, DriverStat> merged = new HashMap<>(count);

			for (int i = 0; i < count; i++) {
				String driverId = dis.readUTF();
				double total = dis.readDouble();
				int medCount = dis.readInt();
				DriverStat incoming = new DriverStat(driverId, total, medCount);
				merged.merge(driverId, incoming, DriverStat::merged);
			}

			List<DriverStat> top10 = topNByEarnings(merged, 10);
			System.out.println("Top drivers (driverId, distinct taxis, total earnings):");
			for (DriverStat stat : top10) {
				System.out.println(stat);
			}

			clientSocket.close();
			serverSocket.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private static List<DriverStat> topNByEarnings(Map<String, DriverStat> stats, int n) {
		PriorityQueue<DriverStat> pq = new PriorityQueue<>(Comparator.comparingDouble(DriverStat::getTotalEarnings));
		for (DriverStat stat : stats.values()) {
			pq.offer(stat);
			if (pq.size() > n) {
				pq.poll();
			}
		}
		List<DriverStat> result = new ArrayList<>(pq);
		result.sort(Comparator.comparingDouble(DriverStat::getTotalEarnings).reversed());
		return result;
	}

	
	

}