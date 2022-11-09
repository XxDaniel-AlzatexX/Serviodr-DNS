package Boundary;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;
import Control.ControlServer;
import Control.ControlGeneral;
import Control.LecturaMaster;

public class DNSServer {

    public static void main(String[] args) throws IOException {

        //Socket al puerto DNS (#53)
        DatagramSocket socketUDP = new DatagramSocket(53);

        //Los mensajes UDP son de 512 bytes
        byte[] buffer = new byte[512];

        ControlServer controlador = new ControlServer();

        //Se guardan los datos del masterfile
        ArrayList<ArrayList<String>> master = LecturaMaster.leerMaster();
        System.out.println("Servidor encendido, esperando...");

        while (true) {
            // Construimos el DatagramPacket para recibir peticiones
            //NOTA: El Datagram es un Datagrama UDP (tamaño de UDP Datagram = 512Bytes)
            DatagramPacket query = new DatagramPacket(buffer, buffer.length);

            // Leemos una petición del DatagramSocket
            socketUDP.receive(query);

            System.out.println("Datagrama recibido del host: " + query.getAddress());
            System.out.println("Puerto remoto: " + query.getPort());

            // HashMap de Campos compartidos entre Query y Response.
            HashMap<String, String> queryResponses = new HashMap<>();

            // Obtenemos los datos de la Query
            ArrayList<byte[]> datosQuery = controlador.extraerDatos(query.getData(), queryResponses);

            // Preparamos la response
            ArrayList<Byte> responseBytes = controlador.crearRespuesta(queryResponses, master, datosQuery);

            // Asignamos espacio para la response con el mismo tamaño del datagrama
            byte[] response = new byte[512];

            //Verificamos si conocemos o no el destino 
            if (responseBytes != null) {
                System.out.println(".:Realizado por nuestro DNS:.");
                response = ControlGeneral.deArrayAByte(responseBytes);

                // Construimos el DatagramPacket para enviar la response
                DatagramPacket responsePacket = new DatagramPacket(response, response.length, query.getAddress(), query.getPort());

                //Se envia la response al host
                socketUDP.send(responsePacket);
                System.out.println("Datagrama enviado al host: " + responsePacket.getAddress());
                System.out.println("Puerto remoto: " + responsePacket.getPort());

            } else {
                System.out.println(".:No podemos resolver la solicitud, redirigiendo a DNS de Google:.");
                InetAddress dnsIP = InetAddress.getByName("8.8.8.8");
                DatagramPacket foreingQuery = new DatagramPacket(query.getData(), query.getLength(), dnsIP, 53);

                //envia la query a Google
                socketUDP.send(foreingQuery);

                // Construimos el DatagramPacket para recibir la response
                DatagramPacket foreingResponse = new DatagramPacket(buffer, buffer.length);
                socketUDP.receive(foreingResponse);

                //Remplazamos la direccion y el puerto con el del host
                foreingResponse.setAddress(query.getAddress());
                foreingResponse.setPort(query.getPort());

                //envia la query al host
                socketUDP.send(foreingResponse);
                System.out.println("Datagrama enviado al host: " + foreingResponse.getAddress());
                System.out.println("Puerto remoto: " + foreingResponse.getPort());
            }
        }
    }

}
