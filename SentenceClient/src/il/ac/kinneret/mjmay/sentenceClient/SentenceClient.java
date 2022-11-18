package il.ac.kinneret.mjmay.sentenceClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class SentenceClient {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length < 2) {
			System.out.println("Usage: SentenceClient ip-add port");
			return;
		}

		String ipAddress = args[0];
		int portNum = Integer.parseInt(args[1]);

		InetAddress add = null;
		try {
			add = InetAddress.getByName(ipAddress);
		} catch (UnknownHostException ex) {
			System.out.println("Illegal ip address: " + ipAddress);
			return;
		}

		// keyboard reading
		BufferedReader brKeyboard = new BufferedReader(new InputStreamReader(
				System.in));

		Socket sock;
		BufferedReader brIn;
		PrintWriter pwOut;
		try {
			sock = new Socket(add, portNum);
			// connect the readers and writers
			brIn = new BufferedReader(new InputStreamReader(
					sock.getInputStream()));
			pwOut = new PrintWriter(sock.getOutputStream(),
					true);
		} catch (IOException iox)
		{
			System.out.println("Error connecting to the server: " + iox.getMessage());
			return;
		}

		String lineIn = null;
		while (true) {
            // get a sentence to send
            System.out.print("Enter sentence to send (blank to quit): ");

            try {
				lineIn = brKeyboard.readLine();
				if ( lineIn.length() == 0)
                {
                    break;
                }
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.out.println("Error reading the sentence.  Quitting.");
				e.printStackTrace();
				return;
			}

			// connect to the server
			try {
				// send the sentence to the server
				pwOut.println(lineIn);

				// read the results
				String lineUpper = brIn.readLine();
				String length = brIn.readLine();

				// print the results
				System.out.println("Sentence in upper case: " + lineUpper);
				System.out.println("Count of letters: " + length);

			} catch (Exception e) {
				System.out
						.println("Error in communication with server.  Quitting.");
			}
		}

		// close up shop
		try {
			pwOut.close();
			brIn.close();
			sock.close();
			brKeyboard.close();
		} catch (Exception iox)
		{
			// nothing to do
		}
		System.out.println("Closed connection.");
	}
}
