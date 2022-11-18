package il.ac.kinneret.mjmay.sentenceServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class SentenceServer {

	/**
	 * Runs the single client sentence server
	 * @param args Should provide two parameters - the ip address to listen on and the port
	 */
	public static void main(String[] args) {
		
		if ( args.length < 2)
		{
			System.out.println("Usage: SentenceServer ip-add port");
			return;
		}
		
		String ipAddress = args[0];
		int portNum = Integer.parseInt(args[1]);
		
		InetAddress add = null;
		try
		{
		    // gets the IP address in an object form and checks that it's valid
			add = InetAddress.getByName(ipAddress);
		}
		catch (UnknownHostException ex)
		{
			System.out.println("Illegal ip address: " + ipAddress + "\nWill listen on ANY IP address");
			add = null;
		}

		if ( add == null)
        {
            try {
                add = InetAddress.getByAddress(new byte[]{0, 0, 0, 0});
            } catch (UnknownHostException ex)
            {
                System.out.println("Something weird happenend: " + ex.getMessage());
                return;
            }
        }

		// open a socket on the port
		ServerSocket serverSock = null;
		try {
			serverSock = new ServerSocket(portNum, 10, add);
			System.out.println("Listening on " + add.toString() + ":" + portNum);
		} catch (IOException e) {
			System.out.println("Error creating socket " + add.toString() + ":" + portNum);
			e.printStackTrace();
		}
		
		// listen for a connection
		try {
			Socket sock = serverSock.accept();
			
			// attach a buffered reader and print writer
			BufferedReader brIn  = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			PrintWriter pw = new PrintWriter(sock.getOutputStream(), true);
			
			// now listen for a sentence
			String lineIn = brIn.readLine();
			
			// convert to uppercase and count the letters
			String lineOut = lineIn.toUpperCase();
			int count = lineIn.length();
			
			// send back the results
			pw.println(lineOut);
			pw.println(count);
			
			sock.close();
			serverSock.close();
			
			System.out.println("Finished and closed");
		} catch (IOException e) {
			System.out.println("Error in communication.  Closing.");
			e.printStackTrace();
		}	
		
		return;
	}

}
