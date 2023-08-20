// Importing required libraries
import java.io.*;
import java.net.*;

// Main class
public class GetClient implements Runnable {
    private static final int TRYCONNECT = 3;
    private static int tryCount = 0;
    private static Socket clientSocket;
    private static int logical_clock = 0;
    private static int portNumber;

    private boolean Connect() {
        while (tryCount < TRYCONNECT) {
            try {
                clientSocket = new Socket("localhost", portNumber);
                System.out.println("Connected to server");
                return true;
            } catch (IOException e) {
                try {
                    tryCount++;
                    System.out.println("Connection failed " + tryCount);
                    Thread.sleep(tryCount * 1000); 
                    if (tryCount == TRYCONNECT) {
                        System.out.println("ATOM Server is offline. Please come back later.");
                        System.exit(0);
                    }
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    // Main driver method
    public static void main(String[] args) {
        if (args.length > 0) {
            portNumber = Integer.parseInt(args[0]);
        } else {
            portNumber = 4567;
        }

        GetClient client = new GetClient();
        boolean successful = client.Connect();
        if (successful) {
            Thread clientThread = new Thread(client);
            clientThread.start();
        }
    }

    private void incrementLamport() {
        logical_clock++;
    }

    private void updateLamport(int timestamp) {
        logical_clock = Math.max(logical_clock, timestamp) + 1;
    }

    private void GET() {
        // System.out.println("Sending PUT request to AggregationServer");

        try {
            // Invoked DataInputStream and DataOutputStream class objects
            DataInputStream input_from_AS = new DataInputStream(clientSocket.getInputStream());
            DataOutputStream output_to_AS = new DataOutputStream(clientSocket.getOutputStream());

            //GET request
            String header = "GET /atom.xml HTTP/1.1\r\n";
            String user_agent = "User-Agent: ATOMClient/1/0";
            String content_type = "Content-Type: application/atom+xml";
            String xml_version = "<?xml version='1.0'encoding='iso-8859-1'?>";
            String temp = Integer.toString(logical_clock);
            String lamport_clock = "Lamport-Clock: " + temp;
           

            String get_request = header + "\r\n"
                    + user_agent + "\r\n"
                    + content_type + "\r\n" + "\n"
                    + xml_version + "\r\n"
                    + lamport_clock + "\r\n";

            // Message sent to Aggregation Server
            output_to_AS.writeUTF(get_request);
            incrementLamport();

            // Print response from server
            System.out.println(input_from_AS.readUTF());
            System.out.println("Update Lamport Clock: " + logical_clock);

            // Flushing out internal buffers, optimizing for better performance
            output_to_AS.flush();

            // Closing Input and Output Stream
            output_to_AS.close();
            input_from_AS.close();
            // Closing socket
            clientSocket.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void run() {
        GET();
    }
}