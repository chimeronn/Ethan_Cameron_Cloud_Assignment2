package edu.utexas.cs.cs378;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

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
		PrintWriter out = null;
		BufferedReader in = null;

		try {
			serverSocket = new ServerSocket(portNumber);
			System.out.println("Server is running on port number " + portNumber);

			System.out.println("Waiting for client connection ... ");

			clientSocket = serverSocket.accept();

			DataInputStream dis = new DataInputStream(clientSocket.getInputStream());

			// Use the output stream if you need it.
			DataOutputStream dos = new DataOutputStream(clientSocket.getOutputStream());
			System.out.println("Server is hearing on port " + portNumber);
			int hasData; 
			
			
			while(true) {
				hasData = dis.readInt();
				
				System.out.println("Flag is:" + hasData);
				
				if(hasData==1) {
				System.out.println("We have a page of data to process!");
				
				// 64 MB page Size
				byte[] page = new byte[Const.PAGESIZE];

				// read the main byte array into memory
				dis.readFully(page);

				//TODO: Remove the Sleep when you run your program. This is just for demo. 
				Thread.sleep(500);
				
				List<DataItem> dataItems = Utils.readFromAPage(page) ;
				
				//TODO process the data here !
//				for (DataItem dataItem : dataItems) {
//					// We just print it to the stdout
//					// You need to receive the data here and process it. 
//						System.out.println(dataItem);
//				}
				
				
				System.out.println("Number of Objects received:" + dataItems.size());
				
				
				// This tells the client to send more data. 
				// Give me more data if you have
				dos.writeInt(1);
				dos.flush();
				
				}else {
					System.out.println("Terminate beecause Flag is: " + hasData);
					break; // break out of while true if we get no more data. 					
				}
				
				
			}// End of while true





		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}

	}

	
	

}