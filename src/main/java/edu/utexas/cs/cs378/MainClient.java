package edu.utexas.cs.cs378;

import java.io.PrintWriter;
import java.net.Socket;
import java.nio.file.Path;
import java.nio.file.Paths;

public class MainClient {
	public static void main(String[] args) {
		String input = args.length >= 1 ? args[0] : "taxi-data-sorted-small.csv.bz2";
		Path inputPath = Paths.get(input);

		String serverAddress = "localhost";
		int port = 33333;

		try (Socket socket = new Socket(serverAddress, port);
			PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
			System.out.println("Connected to server at " + serverAddress + ":" + port);

			TaxiDataCleaner.cleanFile(inputPath, out);
		} catch(IOException e) {
			System.err.println("Error connecting to server: " + e.getMessage());
		}
	}
}