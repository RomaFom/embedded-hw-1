package il.ac.kinneret.mjmay.sentenceServerMulti;

import java.net.ServerSocket;
import java.net.Socket;

public class Listener extends Thread{

    private ServerSocket serverSocket;

    public Listener (ServerSocket socket){
        serverSocket = socket;
    }

    @Override
    public void run() {
        // start to listen on the server socket
        System.out.println("Listening on " + serverSocket.getInetAddress().toString() + ":" + serverSocket.getLocalPort());
        while (!interrupted()) {
            try {
                // get a new connection
                Socket clientSocket = serverSocket.accept();
                // start a worker
                HandleClientThread clientThread = new HandleClientThread(clientSocket);
                clientThread.start();
            } catch (Exception ex)
            {
                // something is wrong, let's quit
            }
        }
        // we're done!
        System.out.println("Stopped listening.");
        try {
            serverSocket.close();
        } catch (Exception ex)
        {
            //noting to do
        }
    }
}
