package edu.utexas.cs.cs378;

import java.io.PrintWriter;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.IOException;

public class MainClient {
	public static void main(String[] args) {
		String input = args.length >= 1 ? args[0] : "taxi-data-sorted-small.csv.bz2";
		Path inputPath = Paths.get(input);

		System.out.println("Sorting dataset " + inputPath.toString() + " and sending to server...");

		String serverAddress = "localhost";
		int port = 33333;

		try (Socket socket = new Socket(serverAddress, port);
			PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), false)) {
			System.out.println("Connected to server at " + serverAddress + ":" + port);

			TaxiDataCleaner.cleanFile(inputPath, out);
		} catch(IOException e) {
			System.err.println("Error connecting to server: " + e.getMessage());
		}
	}
}