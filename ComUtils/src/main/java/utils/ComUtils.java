package utils;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class ComUtils {
    private final int STRSIZE = 40;

    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;
    //TODO: preguntar quin dels dos constructors volem fer servir

    // TODO: crear read_opcode

    /* Constructor ComUtils amb socket directament*/
    public ComUtils(Socket socket) throws IOException {
        dataInputStream = new DataInputStream(socket.getInputStream());
        dataOutputStream = new DataOutputStream(socket.getOutputStream());
    }

    /* Constructor ComUtils que li pases el is i os del socket directament */
    public ComUtils(InputStream inputStream, OutputStream outputStream) throws IOException {
        dataInputStream = new DataInputStream(inputStream);
        dataOutputStream = new DataOutputStream(outputStream);
    }

    public int read_int32() throws IOException {
        byte bytes[] = read_bytes(4);

        return bytesToInt32(bytes, Endianness.BIG_ENNDIAN);
    }

    public void write_int8(int number) throws IOException {
        byte[] bytes = new byte[1];
        bytes[0] = (byte) (number);
        dataOutputStream.write(bytes, 0, 1);
    }

    public int read_int8() throws IOException {
        byte[] bInt = read_bytes(1);
        return (bInt[0] & 0xFF);
    }

    public void write_int32(int number) throws IOException {
        byte bytes[] = int32ToBytes(number, Endianness.BIG_ENNDIAN);

        dataOutputStream.write(bytes, 0, 4);
    }

    public void write_string(String str) throws IOException {
        int size = str.length();
        byte[] bStr = new byte[size];

        for (int i = 0; i < size; i++) {
            bStr[i] = (byte) str.charAt(i);
        }
        dataOutputStream.write(bStr, 0, size);
    }

    private byte[] int32ToBytes(int number, Endianness endianness) {
        byte[] bytes = new byte[4];

        if (Endianness.BIG_ENNDIAN == endianness) {
            bytes[0] = (byte) ((number >> 24) & 0xFF);
            bytes[1] = (byte) ((number >> 16) & 0xFF);
            bytes[2] = (byte) ((number >> 8) & 0xFF);
            bytes[3] = (byte) (number & 0xFF);
        } else {
            bytes[0] = (byte) (number & 0xFF);
            bytes[1] = (byte) ((number >> 8) & 0xFF);
            bytes[2] = (byte) ((number >> 16) & 0xFF);
            bytes[3] = (byte) ((number >> 24) & 0xFF);
        }
        return bytes;
    }

    /* Passar de bytes a enters */
    private int bytesToInt32(byte bytes[], Endianness endianness) {
        int number;

        if (Endianness.BIG_ENNDIAN == endianness) {
            number = ((bytes[0] & 0xFF) << 24) | ((bytes[1] & 0xFF) << 16) |
                    ((bytes[2] & 0xFF) << 8) | (bytes[3] & 0xFF);
        } else {
            number = (bytes[0] & 0xFF) | ((bytes[1] & 0xFF) << 8) |
                    ((bytes[2] & 0xFF) << 16) | ((bytes[3] & 0xFF) << 24);
        }
        return number;
    }

    //llegir bytes.
    private byte[] read_bytes(int numBytes) throws IOException {
        int len = 0;
        byte bStr[] = new byte[numBytes];
        int bytesread = 0;
        do {
            bytesread = dataInputStream.read(bStr, len, numBytes - len);
            if (bytesread == -1)
                throw new IOException("Broken Pipe");
            len += bytesread;
        } while (len < numBytes);
        return bStr;
    }

    public String read_until_0() throws IOException {
        String s = "";
        byte b;

        do{
            b = dataInputStream.readByte();
            if (b != 0){
                s = s + (char) b;
            }
        }while(b != 0);

        return s;
    }

    /* Escriure un string mida variable, size = nombre de bytes especifica la longitud  */
    /* String str = string a escriure.*/
    public void write_string_variable(String str) throws IOException {
        int numBytes = str.length();
        byte bStr[] = new byte[numBytes];

        for (int i = 0; i < numBytes; i++)
            bStr[i] = (byte) str.charAt(i);
        dataOutputStream.write(bStr);
    }

    public void write_byte(byte b) throws IOException {
        dataOutputStream.write(b);
    }

    public byte read_byte() throws IOException {
        return dataInputStream.readByte();
    }

    public String read_string_5() throws IOException{
        String result;
        byte[] bStr;
        char[] cStr = new char[5];

        bStr = read_bytes(5);

        for(int i = 0; i < 5;i++)
            cStr[i]= (char) bStr[i];

        result = String.valueOf(cStr);

        return result.trim();
    }


    public enum Endianness {
        BIG_ENNDIAN,
        LITTLE_ENDIAN
    }
}


