package Control;

import java.util.ArrayList;
import java.util.HashMap;

public class ControlServer {

    public ArrayList<byte[]> extraerDatos(byte[] datosQuery, HashMap<String, String> queryResponses) {

        //Seccion del Header
        /*0  1  2  3  4  5  6  7  8  9  10 11 12 13 14 15 
        +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+ 
        |                      ID                       | 
        +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+ 
        |QR|   Opcode  |AA|TC|RD|RA|   Z    |   RCODE   | 
        +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+ 
        |                    QDCOUNT                    | 
        +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+ 
        |                    ANCOUNT                    | 
        +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+ 
        |                    NSCOUNT                    | 
        +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+ 
        |                    ARCOUNT                    | 
        +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+*/
        //Array en donde se guardaran los datos del query
        ArrayList<byte[]> queryData = new ArrayList<>();

        //Estraccion de ID (2 Bytes)
        byte[] id = {0, 0};
        int aux = 0;
        String idBit = "";
        while (aux <= 1) {
            //ID generado aleatoriamente en bytes guardado en id
            id[aux] = datosQuery[aux];
            //El mismo id guardado en bits
            idBit += ControlGeneral.byte_a_Bits(datosQuery[aux]);
            aux++;
        }

        //QR, OpCode, AA, TC, RD (Todo 1 Byte)
        byte[] tercerByte = {0};
        tercerByte[0] = datosQuery[aux++];

        //Extrayendo OPCode (campo compartido entre Query y Response)
        String tercerByteBit = ControlGeneral.byte_a_Bits(tercerByte[0]);

        //Solo se guardan los bits que corresponden a laa ubicacion de OpCode
        String opCode = tercerByteBit.substring(1, 5);

        //RA, Z, Rcode (Todo 1 Byte)
        byte[] razRcode = {0};
        razRcode[0] = datosQuery[aux++];

        //QDCOUNT (2 Bytes)
        byte[] qdCount = {0, 0};
        String qdCountBit = "";
        for (int i = 0; i < 2; i++) {
            //qdCount guardado en bytes
            qdCount[i] = datosQuery[aux];
            //qdCount guardado en bits
            qdCountBit += ControlGeneral.byte_a_Bits(datosQuery[aux]);
            aux++;
        }

        //ANCOUNT (2 Bytes)
        byte[] anCount = {0, 0};
        for (int i = 0; i < 2; i++) {
            //anCount guardado en bytes
            anCount[i] = datosQuery[aux];
            aux++;
        }

        //NSCOUNT (2 Bytes)
        byte[] nsCount = {0, 0};
        for (int i = 0; i < 2; i++) {
            //nsCount guardado en bytes
            nsCount[i] = datosQuery[aux];
            aux++;
        }

        //ARCOUNT (2 Bytes)
        byte[] arCount = {0, 0};
        for (int i = 0; i < 2; i++) {
            //arCount guardado en bytes
            arCount[i] = datosQuery[aux];
            aux++;
        }

        //Armando el ArrayList de byte[]...
        queryData.add(id);
        queryData.add(tercerByte);
        queryData.add(razRcode);
        queryData.add(qdCount);
        queryData.add(anCount);
        queryData.add(nsCount);
        queryData.add(arCount);

        //Seccion de Query
        /*0  1  2  3  4  5  6  7  8  9  10 11 12 13 14 15 
        +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+ 
        |                                               | 
        |                     QNAME                     | 
        |                                               | 
        +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+ 
        |                     QTYPE                     | 
        +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+ 
        |                     QCLASS                    | 
        +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+*/
        //qdCountInt: qdCount en entero
        int qdCountInt = Integer.parseInt(qdCountBit, 2);

        //Se realiza la misma cantidad de veces que querys realizadas
        for (int i = 0; i < qdCountInt; i++) {

            //QNAME
            //Label: cada una de los strings (sin los puntos) que contiene un URL
            //Ejemplo: www.google.com (www, google, com)
            String qName = "";
            int tamanioLabel = 0;

            //String donde se va a almacenar el nombre de dominio
            String nombre = "";

            ArrayList<Byte> qNameByte = new ArrayList<>();

            //Recorre la parte de qName hasta que encuentre un byte en 0, que es cuando acaba la seccion qName
            while (datosQuery[aux] != 0) {

                qNameByte.add(datosQuery[aux]);

                //Convertimos Bytes del qName a bits
                qName = ControlGeneral.byte_a_Bits(datosQuery[aux++]);
                tamanioLabel = Integer.parseInt(qName, 2);

                //Obtiene los label en bytes
                byte[] byteName = new byte[tamanioLabel];
                for (int k = 0; k < tamanioLabel; k++) {
                    byteName[k] = datosQuery[aux];
                    qNameByte.add(byteName[k]);
                    aux++;
                }

                //Label pasado a string y agregandole el punto de la direccion
                nombre += new String(byteName) + '.';
            }

            //Quitamos el punto extra generado anteriormente
            nombre = nombre.substring(0, nombre.length() - 1);
            byte[] qNameByteLista = ControlGeneral.deArrayAByte(qNameByte);

            //QNAME (Serie de Bytes) (Posicion 7)
            queryData.add(qNameByteLista);

            //QTYPE (2 Bytes) (Posicion 8)
            byte[] qType = {0, 0};
            qType[0] = datosQuery[aux++];
            qType[1] = datosQuery[aux++];
            queryData.add(qType);

            //QCLASS (2 Bytes) (Posicion 9)
            byte[] qClass = {0, 0};
            qClass[0] = datosQuery[aux++];
            qClass[1] = datosQuery[aux++];
            queryData.add(qClass);

            //Agregamos al mapa nuestros datos de interes
            queryResponses.put("ID", idBit);
            queryResponses.put("OPCODE", opCode);
            queryResponses.put("QDCOUNT", qdCountBit);
            queryResponses.put("QNAME", nombre);

        }
        return queryData;
    }

    public ArrayList<Byte> crearRespuesta(HashMap<String, String> queryResponses, ArrayList<ArrayList<String>> master, ArrayList<byte[]> datosQuery) {

        ArrayList<Byte> response = new ArrayList<>();
        String responseStr = "";

        //Preparando Header
        String id = queryResponses.get("ID");
        //QR = 1 porque es una respuesta
        String QR = "1";
        String opCode = queryResponses.get("OPCODE");
        String qdCount = queryResponses.get("QDCOUNT");
        String qName = queryResponses.get("QNAME");
        String AA = "0";

        //Guardamos las coincidencias de qName encontradas en nuestro masterFile
        ArrayList<ArrayList<String>> nombreEncontrado = ControlGeneral.encontrarEnMasterFile(qName, master);

        if (nombreEncontrado != null) {
            //Guardado el tamaño de anCount
            Integer anCount = nombreEncontrado.size();
            //De string a Byte
            String anCountStr = String.format("%16s", Integer.toBinaryString(anCount)).replace(' ', '0');

            //Sigue preparando header
            AA = "1";
            String TC = "0";
            String RD = "1";
            String RA = "0";
            String Z = "000";
            String RCODE = "0000";
            String NSCOUNT = "0000000000000000";

            //Organiza el header y lo agrega a un string
            responseStr += id;
            responseStr += QR + opCode + AA + TC + RD;
            responseStr += RA + Z + RCODE;
            responseStr += qdCount;
            responseStr += anCountStr;
            //NSCOUNT = ARCOUNT
            responseStr += NSCOUNT + NSCOUNT;

            for (int i = 0; i < responseStr.length(); i += 8) {
                //Empieza a convertirlo en octetos
                Integer octeto = Integer.parseInt(responseStr.substring(i, i + 8), 2);
                byte octetByte = octeto.byteValue();
                response.add(octetByte);
            }

            /*
            //Agregando QNAME a la respuesta (bytes)...
            for (int i = 0; i < datosQuery.get(7).length; i++) {
                response.add(datosQuery.get(7)[i]);
            }
            
            //Se agrega el 0 al final del Qname 
            byte terminador = 0;
            response.add(terminador);

            //Agregando QTYPE a la respuesta (bytes)...
            byte uno = 1;
            response.add(terminador);
            response.add(uno);

            //Agregando QCLASS a la respuesta (bytes)...
            response.add(datosQuery.get(9)[0]);
            response.add(datosQuery.get(9)[1]);
             */
            //Seccion response
            /* 0  1  2  3  4  5  6  7  8  9  10 11 12 13 14 15
            +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
            |                                               |
            |                                               |
            |                      NAME                     |
            |                                               |
            +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
            |                      TYPE                     |
            +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
            |                     CLASS                     |
            +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
            |                      TTL                      |
            |                                               |
            +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
            |                   RDLENGTH                    |
            +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--|
            |                     RDATA                     |
            |                                               |
            +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+*/
            //ArrayList para guardar los resource records
            ArrayList<Byte> rr_response = new ArrayList<>();

            for (int rrCount = 0; rrCount < anCount; rrCount++) {
                //Agregando Name a la respuesta (bytes)...
                /*
                for(int i = 0; i < datosQuery.get(7).length; i++){
                    rr_response.add(datosQuery.get(7)[i]);
                }
                Integer[] namePtrIntVals = {192, 168};
                byte namePtr1 = namePtrIntVals[0].byteValue();
                byte namePtr2 = namePtrIntVals[1].byteValue();
                rr_response.add(namePtr1);
                rr_response.add(namePtr2);
                 */
                //Type (2 Bytes)
                byte[] type = {0, 0};
                //Extraemos el type de nuestro masterFile
                String tipoRegistro = nombreEncontrado.get(rrCount).get(3);
                int RD_LENGTH = 4;

                //verificacion ipv4
                if (tipoRegistro.equals("A")) {
                    //Agregando TYPE: A = 1 (0000000000000001)
                    type[1] = 1;
                    RD_LENGTH = 4;
                }
                //Verificacion de ipv6
                if (tipoRegistro.equals("AAAA")) {
                    //Agregando TYPE: AAAA = 28 (0000000000011100)
                    type[1] = 28;
                    RD_LENGTH = 16;
                }
                rr_response.add(type[0]);
                rr_response.add(type[1]);

                //CLASS (2 Bytes)
                byte[] CLASS = {0, 0};
                String claseRegistro = nombreEncontrado.get(rrCount).get(2);
                if (claseRegistro.compareTo("IN") == 0) {
                    //Agregando CLASS: IN = 1 (0000000000000001)
                    CLASS[1] = 1;
                }
                rr_response.add(CLASS[0]);
                rr_response.add(CLASS[1]);

                //TTL (4 Bytes)
                byte[] ttl = ControlGeneral.convertirIntAByte(1000, 4, false);
                for (byte b : ttl) {
                    rr_response.add(b);
                }

                //RD_LENGTH (2 Bytes)
                byte[] rdLength = ControlGeneral.convertirIntAByte(RD_LENGTH, 2, false);
                for (byte b : rdLength) {
                    rr_response.add(b);
                }

                //R_DATA (4 Bytes)
                String direccionIP = "";
                direccionIP = nombreEncontrado.get(rrCount).get(5);
                String[] direccionIPsin = direccionIP.split("\\.");

                byte[] rData = new byte[4];
                for (int i = 0; i < 4; i++) {
                    Integer a = Integer.parseInt(direccionIPsin[i]);
                    //Convertimos en byte la direccion IP din puntos
                    byte b = a.byteValue();
                    rr_response.add(b);
                }
            }
            response.addAll(rr_response);

        } else {
            response = null;
        }
        return response;
    }
}
