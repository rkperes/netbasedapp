package app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.Socket;
import java.net.URL;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * HTTP/1.1
 *
 */
public class App {

    private static final int DEFAULT_PORT = 80;
    private static final Logger LOGGER = Logger.getLogger(App.class.getName());

    static void sendGET(String host, Socket socket){
    	String path = "/index.html";
    	PrintWriter request = null;
		try {
			request = new PrintWriter( socket.getOutputStream() );
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	request.print(  "GET " + path + " HTTP/1.1\r\n" + 
    	                       "Host: " + host + "\r\n" + 
    	                       "Connection: close\r\n\r\n"); 
    	request.flush( ); 
    	
    	InputStream inStream = null;
		try {
			inStream = socket.getInputStream( );
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
    	BufferedReader rd = new BufferedReader(
    	        new InputStreamReader(inStream));
    	String line;
    	try {
			while ((line = rd.readLine()) != null) {
			    System.out.println(line);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    static Socket connect(String host, int port) throws IOException {
        LOGGER.log(Level.FINE, "Connecting to: {0}:{1}",
                new Object[]{host, port});
        Socket clientSocket = new Socket(host, port);
        sendGET(host, clientSocket);
        return clientSocket;
    }

    static void start(String[] args) throws Exception {
        LOGGER.log(Level.FINER, "Start called with args: {0}", args);

        if (args.length < 1) {
            throw new Exception("I need an URL to GET.");
        } else {
            URL url = new URL(args[0]);
            LOGGER.log(Level.FINER, "Parsed HOST:", url.getHost());
            LOGGER.log(Level.FINER, "Parsed PORT:", url.getPort());

            /* if URL does not contain PORT, set default value */
            int port = url.getPort();
            if (port == -1) {
                port = DEFAULT_PORT;
            }

            Socket sock = connect(url.getHost(), port);
            System.out.println("connected");
        }
    }

    public static void main(String[] args) throws Exception {
        Handler fh = new FileHandler("./all.log");
        LOGGER.addHandler(fh);
        LOGGER.setLevel(Level.FINEST);
        try {
            LOGGER.log(Level.FINE, "Main called with args: {0}", args);
            start(args);
        } catch (Exception e) {
            System.out.println("Execution returned an exception:");
            System.out.println(e.getMessage());

            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            LOGGER.log(Level.SEVERE, sw.toString());
        }
    }

}
