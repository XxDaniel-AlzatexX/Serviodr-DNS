package Control;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class LecturaMaster {

    public static ArrayList<ArrayList<String>> leerMaster() {

        //Listado de registros tipo A
        ArrayList<ArrayList<String>> registros = new ArrayList<>();
        try {
            String filePath = "C:/Users/judar/OneDrive/Documentos/Universidad/Sexto semestre/redes/DNS/masterFile.txt";

            //Guardamos el archivo en reader
            File masterFile = new File(filePath);
            FileReader masterReader = new FileReader(masterFile);
            BufferedReader reader = new BufferedReader(masterReader);

            String fila;

            //Guardamos el archivo en nuestro arreglo de arreglos tipo string
            while ((fila = reader.readLine()) != null) {
                String[] columna = fila.split(",");
                ArrayList<String> filaList = new ArrayList<>();
                filaList.add(columna[0]);
                filaList.add(columna[1]);
                filaList.add(columna[2]);
                filaList.add(columna[3]);
                filaList.add(columna[4]);
                filaList.add(columna[5]);

                registros.add(filaList);
            }

            reader.close();
        } catch (SocketException e) {
            System.out.println("Socket: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("IO: " + e.getMessage());
        }
        return registros;
    }

}
