
// Importing required libraries
import java.io.*;
import java.net.*;

// Main class
public class ContentServer implements Runnable {
    private static String inputFile;
    private static final int TRYCONNECT = 3;
    private static int tryCount = 0;
    private static Socket contentSocket;
    private static int logical_clock = 0;
    private static int portNumber;

    // When server is down, it will try to connect to the server for 3 times
    private boolean Connect() {
        while (tryCount < TRYCONNECT) {
            try {
                contentSocket = new Socket("localhost", portNumber);
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

    public static void main(String[] args) {
        if (args.length > 1) {
            inputFile = args[1];
        } else {
            inputFile = "input.txt";
        }

        if (args.length > 0) {
            portNumber = Integer.parseInt(args[0]);
        } else {
            portNumber = 4567;
        }

        // create a new put thread to run this content server
        ContentServer content = new ContentServer();
        boolean successful = content.Connect();
        if (successful) {
            Thread contentThread = new Thread(content);
            contentThread.start();
        }
    }

    private void incrementLamport() {
        logical_clock++;
    }

    private void updateLamport(int timestamp) {
        logical_clock = Math.max(logical_clock, timestamp) + 1;
    }

    private void PUT() {
        // Try block to check if exception occurs
        try {
            // Invoked DataInputStream and DataOutputStream class objects
            DataOutputStream output_to_AS = new DataOutputStream(contentSocket.getOutputStream());
            DataInputStream input_from_Client = new DataInputStream(contentSocket.getInputStream());

            String temp = null;
            // PUT request
            String header = "PUT /atom.xml HTTP/1.1";
            String user_agent = "User-Agent: ATOMClient/1/0";
            String content_type = "Content-Type: application/atom+xml";
            String xml_version = "<?xml version='1.0'encoding='iso-8859-1'?>";
            String feed_start = "<feed xml:lang='en-US'xmlns='http://www.w3.org/2005/Atom'>";
            String body = readFile(temp);
            String feed_end = "</feed>";

            String put_request = header + "\r\n"
                    + user_agent + "\r\n"
                    + content_type + "\r\n" + "\n"
                    + xml_version + "\r\n"
                    + feed_start + "\r\n"
                    + body
                    + feed_end + "\r\n";

            // Message display to Aggregation Server
            output_to_AS.writeUTF(put_request);

            // Flushing out internal buffers, optimizing for better performance
            output_to_AS.flush();

            // Closing Input and Output Stream
            input_from_Client.close();
            output_to_AS.close();
            // // Closing socket
            contentSocket.close();
        }

        // Catch block to handle exceptions
        catch (Exception e) {

            // Print the exception on the console
            System.out.println(e);
        }
    }

    public static String readFile(String get_body) {
        try {

            File file = new File(inputFile); // creates a new file instance
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

    @Override
    public void run() {
        PUT();
    }
}