package Control;

import java.util.ArrayList;

public class ControlGeneral {

    //Convertir Bytes a Bits
    public static String byte_a_Bits(final byte inicial) {
        String resultado = String.format("%8s", Integer.toBinaryString(inicial & 0xFF)).replace(' ', '0');
        return resultado.toString();
    }

    //Busca en la lista de registros la direccion pasada
    public static ArrayList<ArrayList<String>> encontrarEnMasterFile(String nombre, ArrayList<ArrayList<String>> master) {
        ArrayList<ArrayList<String>> registrosNombre = null;
        registrosNombre = new ArrayList<>();
        for (ArrayList<String> row : master) {
            if (row.get(0).compareTo(nombre) == 0) {
                registrosNombre.add(row);
            }
        }
        if (!registrosNombre.isEmpty()) {
            return registrosNombre;
        }
        return null; //Cuando se retorna NULL, se debe remitir el query a otro DNS
    }

    //Convertir los Arrays de Bytes a un Byte[]
    public static byte[] deArrayAByte(ArrayList<Byte> listaBytes) {
        int n = listaBytes.size();
        byte[] out = new byte[n];
        for (int i = 0; i < n; i++) {
            out[i] = listaBytes.get(i);
        }
        return out;
    }

    //Convertir un valor int a bytes
    public static byte[] convertirIntAByte(int valor, int bytes, boolean bandera) {
        if (bytes == 1 && !bandera) {
            return new byte[]{(byte) valor};
        } else if (bytes == 1 && bandera) {
            return new byte[]{(byte) (valor >>> 24)};
        } else if (bytes == 2 && !bandera) {
            return new byte[]{(byte) (valor >>> 8), (byte) valor};
        } else if (bytes == 2 && bandera) {
            return new byte[]{(byte) (valor >>> 24),
                (byte) (valor >>> 16)};
        } else if (bytes == 3 && !bandera) {
            return new byte[]{(byte) (valor >>> 16),
                (byte) (valor >>> 8), (byte) valor};
        } else if (bytes == 3 && bandera) {
            return new byte[]{(byte) (valor >>> 24),
                (byte) (valor >>> 16), (byte) (valor >>> 8)};
        } else {//www.j a  v a2 s  .c o m
            return new byte[]{(byte) (valor >>> 24),
                (byte) (valor >>> 16), (byte) (valor >>> 8),
                (byte) valor};
        }
    }

}
