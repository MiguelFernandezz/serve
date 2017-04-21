package py.sd.server;


import java.net.*;
import java.io.*;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.Logger;

public class TCPMultiServer {
    
    private final static Logger logger = Logger.getLogger(TCPMultiServer.class);

    public static void main(String[] args) throws IOException {
        
        PropertyConfigurator.configure("log4j.properties");
        
        String inputLine, outputLine;
        ServerSocket serverSocket = null;
        Socket clientSocket = null;
        boolean listening = true;

        try {
            serverSocket = new ServerSocket(4444);
        } catch (IOException e) {
            logger.error("No se puede abrir el puerto: 4444.");
            System.err.println("No se puede abrir el puerto: 4444.");
            System.exit(1);
        }
        logger.info("Puerto abierto: 4444.");
        int i = 0;
        while (listening) {
            new TCPServerHilo(serverSocket.accept(),i++).start();
        }

        serverSocket.close();
    }
}
