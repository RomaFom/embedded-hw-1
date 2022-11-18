package il.ac.kinneret.mjmay.sentenceServerMulti;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;

/**
 * A class for a multithreaded sentence server. It receives a sentence, changes it to upper case, counts the letters
 * and returns the modified sentence and its length to the client.
 */
public class SentenceServerMulti {

	/**
	 * Runs the multithreaded sentence server.
	 * @param args The parameters for the server.  Should be two parameters - the IP address and port to listen on.
	 */
	public static void main(String[] args) {
		if (args.length < 2) {
			System.out.println("Usage: SentenceServer ip-add port");
			return;
		}

		String ipAddress = args[0];
		int portNum = Integer.parseInt(args[1]);

		InetAddress add = null;
		try
		{
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
				System.out.println("Something weird happened: " + ex.getMessage());
				return;
			}
		}

		String line = "";
		Listener listener = null;
		BufferedReader brIn = new BufferedReader(new InputStreamReader(System.in));
		while (true) {
			// open a socket on the port
			ServerSocket serverSock = null;
			try {
				serverSock = new ServerSocket(portNum, 10, add);
				listener = new Listener(serverSock);
				listener.start();
			} catch (IOException e) {
				System.out.println("Error creating socket " + add + ":"
						+ portNum);
				e.printStackTrace();
			}

			System.out.println(
					"Started to listen.  Enter \"stop\" to pause listening: "
			);

			try {
				do {
					line = brIn.readLine();
				} while (!line.equalsIgnoreCase("stop"));

				// user asked to stop
				listener.interrupt();
 				serverSock.close();
				System.out.println("Stopped listening.  To quit, enter \"quit\".  To resume listening, enter \"resume\": ");

				do {
					line = brIn.readLine();
				} while (!line.equalsIgnoreCase("quit") && !line.equalsIgnoreCase("resume"));

				if (line.equals("resume")) {
					continue;
				} else if (line.equals("quit")) {
					break;
				}
			} catch (IOException ex)
			{
				// this shouldn't happen, just quit
				listener.interrupt();
				break;
			}
		}
		System.out.println("Goodbye.");
		return;
	}
}
