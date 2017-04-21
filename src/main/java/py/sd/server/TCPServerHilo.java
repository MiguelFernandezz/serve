package py.sd.server;

import com.google.gson.Gson;
import java.net.*;
import java.io.*;
import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.Logger;

public class TCPServerHilo extends Thread {
    Integer i;
    
    private final static Logger logger = Logger.getLogger(TCPServerHilo.class);

    private Socket socket = null;
    Gson gson = new Gson();
    String direccion = "";

    public TCPServerHilo(Socket socket, Integer i) {
        super("TCPServerHilo");
        this.socket = socket;
    }

    public void run() {
        logger.info("nuevo hilo levantado: "+i );
        try {
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(
                            socket.getInputStream()));
            
            Respuesta saludo = new Respuesta();
            saludo.setEstado(1);
            saludo.setMensaje("Bienvenido!");
            
            out.println(gson.toJson(saludo));
            String inputLine, outputLine;
            double size = 0;
            double ini = System.currentTimeMillis();
            while ((inputLine = in.readLine()) != null) {

                if (!inputLine.equalsIgnoreCase("Bye")) {
                    Objeto o = new Objeto();
                    
                    o = gson.fromJson(inputLine, Objeto.class);
                    direccion = o.getDireccion();
                    File file = new File("newfile.png");
                    FileOutputStream fop = new FileOutputStream(file);

                    fop.write(Base64.decodeBase64(o.getFile()));
                    fop.flush();
                    
                    double bytes = file.length();
                    double kilobytes = (bytes / 1024);
                    size += kilobytes;
                    
                    fop.close();
                }

                if (inputLine.equalsIgnoreCase("Bye")) {

                    double fin = System.currentTimeMillis();
                    double tiempo = fin - ini;
                    tiempo = tiempo / 1000;
                    double velocidad = size / tiempo * 8 ;
                    
                    Respuesta resp = new Respuesta();
                    resp.setEstado(0);
                    resp.setMensaje("velocidad: " + velocidad+" kbps");
                    logger.info("recibido de: "+direccion);
                    logger.info(resp);
                    out.println(gson.toJson(resp));
                    break;
                }
                Respuesta eco = new Respuesta();
                eco.setEstado(2);
                eco.setMensaje("trama recibida");
                out.println(gson.toJson(eco));
            }
            out.close();
            in.close();
            socket.close();
            logger.info("Finalizando Hilo");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
