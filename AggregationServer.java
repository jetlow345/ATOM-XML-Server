
// Importing majorly Socket and ServerSocket class
// from java.net package
import java.net.*;
import java.util.LinkedList;
import java.util.Scanner;
import java.io.*;

public class AggregationServer {
    private static int portNumber;
    protected static ServerSocket serverSocket;
    public static void main(String[] args) throws Exception {
        if (args.length > 0) {
            portNumber = Integer.parseInt(args[0]);
        } else {
            portNumber = 4567;
        }

        serverSocket = new ServerSocket(portNumber);
        Socket socket = new Socket();
        System.out.println("Server started");

        while (true) {
            try {
                socket = serverSocket.accept();
                System.out.println("Status 201 - HTTP_CREATED");
                ServerThread serverThread = new ServerThread(socket);
                serverThread.start();

                heartbeatThread heartbeat = new heartbeatThread(socket);
                heartbeat.start();

            } catch (Exception e) {
                System.out.println("Error: " + e);
            }
        }

    }
}

class ServerThread extends Thread implements Runnable {
    Socket socket;

    public ServerThread(Socket socket) {
        this.socket = socket;
    }

    public synchronized void run() {
        // Try block to check for exceptions
        try {
            // Invoked DataInputStream and DataOutputStream class objects
            DataInputStream input_data = new DataInputStream(socket.getInputStream());
            DataOutputStream output_to_Client = new DataOutputStream(socket.getOutputStream());

            String str = input_data.readUTF();

            if (str.contains("GET")) {
                System.out.println("Status 200 - GET request successful");
                System.out.println("Client connected through: " + socket.getLocalPort() + " port");
                String temp = null;
                output_to_Client.writeUTF(readFile(temp));

            } else if (str.contains("PUT")) {
                System.out.println("Status 200 - PUT reqeuest successful");
                System.out.println("Content Server connected through: " + socket.getLocalPort() + " port");
                
                File file = new File("Feed.txt");
                FileWriter fw = new FileWriter(file);
                fw.write(str);
                fw.close();

            } else if (str.equals("")) {
                System.out.println("Status 204 - No Content");

            } else {
                System.out.println("Status 400 - Bad Request");
            }

            input_data.close();
            output_to_Client.close();

        }

        // Catch block to handle for exceptions
        catch (Exception e) {

            // Display the exception on the console
            System.out.println(e);
        }

    }

    public static String readFile(String get_body) {
        try {

            File file = new File("Feed.txt"); // creates a new file instance
            FileReader fr = new FileReader(file); // reads the file
            BufferedReader br = new BufferedReader(fr); // creates a buffering character input stream
            StringBuffer sb = new StringBuffer(); // constructs a string buffer with no characters
            String line;

            while ((line = br.readLine()) != null) {
                sb.append(line); // appends line to string buffer
                sb.append("\n"); // line feed
            }
            fr.close(); // closes the stream and release the resources
            // System.out.println(sb.toString()); //returns a string that textually
            // represents the object
            get_body = sb.toString();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return get_body;
    }

}
class heartbeatThread extends Thread implements Runnable {
    Socket socket;

    public heartbeatThread(Socket socket) {
        this.socket = socket;
    }

    public void run() {
        try {
            int i = 1;
            while (true) {
                if (i > 1000) {
                    socket.close();
                    System.out.println("Connection closed");
                    break;
                }
                i++;
                Thread.sleep(12 * 1000);
            }
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
    }
}
