package il.ac.kinneret.mjmay.sentenceServerMulti;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;

import static il.ac.kinneret.mjmay.sentenceServerMulti.FileHandler.FileToJson;

public class HandleClientThread extends Thread {

    private Socket clientSock;

    public HashMap<String, FileDTO> FilesStorage = new HashMap<String, FileDTO>();

    public FileHandler fileHandler = new FileHandler();

    /**
     * Initializes the server.
     * @param sock The client socket to handle.
     */
    public HandleClientThread(Socket sock)
    {
        super("HandleClientThread-" + sock.getRemoteSocketAddress().toString());
        this.clientSock = sock;
    }

    private String LockFile(String fileName) {
        FileDTO file = FilesStorage.get(fileName);
        if(file != null){
            file.isLocked = true;
            FilesStorage.put(fileName, file);
            return "File locked";
        }
        else{
            return "File not found";
        }
    }

    private String AddFile(String fileName) {
        FileDTO file = FilesStorage.get(fileName);
        if(file == null){
            FileDTO newFile = new FileDTO();
            newFile.isLocked = false;
            FilesStorage.put(fileName, newFile);
            FileDTO test = FilesStorage.get(fileName);
            System.out.println("File added" + fileName + test.isLocked);
            return "File added";
        }
        else{
            return "File already exists";
        }
    }

    /**
     * Runs the server
     */
    public void run()
    {
        // listen for a connection
        try(
                // attach a buffered reader and print writer
            BufferedReader brIn  = new BufferedReader(new InputStreamReader(clientSock.getInputStream()));
            PrintWriter pw = new PrintWriter(clientSock.getOutputStream(), true))
        {
            while (true) {
                // now listen for a sentence
                String lineIn = brIn.readLine();
                String[] clientInput;
                String inputFileName = "";
                String inputCommand = "";
                if ( lineIn == null)
                {
                    // something is wrong - just quit
                    break;
                }
                if(lineIn.contains(" ")){
                    clientInput = lineIn.split(" ");
                    inputCommand = clientInput[0];
                    inputFileName = clientInput[1];
                }
                else{
                    inputCommand = lineIn;
                }

                System.out.println("Received from " + clientSock.getRemoteSocketAddress().toString() + ": " + lineIn);
                switch (inputCommand.toUpperCase()) {
                    case "UPDATE" -> {
                        System.out.println("Updating...");
                    }
                    case "LOCK" -> {
                        System.out.println("Locking...");
                        FileDTO lockedFile = this.fileHandler.LockFile(inputFileName);
                        if (lockedFile != null) {
                            pw.println("File locked");
                        } else {
                            pw.println("File not found");
                        }
                    }
                    case "ADD" -> {
                        FileDTO addedFile = this.fileHandler.AddFile(inputFileName);
                        if (addedFile != null) {
                            pw.println("File added");
                        } else {
                            pw.println("File already exists");
                        }
                    }
                    case "GET" -> {
                        System.out.println("Getting...");
                        FileDTO file = this.fileHandler.GetFile(inputFileName);
                        if (file != null) {
                            FileToJson(file);
                        } else {
                            pw.println("File not found");
                        }
                    }
                    case "QUIT" -> System.out.println("Quitting...");
                }

                // convert to uppercase and count the letters
                String lineOut = lineIn.toUpperCase();
                int count = lineIn.length();

                // send back the results
                pw.println(lineOut);
                pw.println(count);
            }
        } catch (IOException e) {
            System.out.println("Error in communication.  Closing.");
            e.printStackTrace();
        }
        finally {
            System.out.println("Finished and closed on " + clientSock.getRemoteSocketAddress().toString());
            try { clientSock.close(); } catch (Exception ex) {}
        }

        return;
    }

}
