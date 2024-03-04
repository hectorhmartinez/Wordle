package ProtocolServer;

import utils.ComUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

public class ProtocolServidor {
    private ComUtils comUtils;


    /* Constructor ¿temporal? per a test */
    public ProtocolServidor() {
        File file = new File("proves");
        try {
            file.createNewFile();
            comUtils = new ComUtils(new FileInputStream(file), new FileOutputStream(file));
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Costructor classe ProtocolServidor
     * @param socket
     */
    public ProtocolServidor(Socket socket) {//throws IOException{
        try {
            comUtils = new ComUtils(socket.getInputStream(), socket.getOutputStream());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }


    /**
     * Llegeix el opCode i retorna el missatge
     *
     * @return parametres lleguits en un array
     * @throws IOException
     */
    public ArrayList<Object> getMessage() throws IOException {
        switch (comUtils.read_byte()) {
            case 1:
                return readHello();
            case 3:
                return readPlay();
            case 5:
                return readWord();
            case 8:
                return readError();
            default:
                //
        }
        throw new IOException("NO LLEGIT RES PROTOCOLSERVER");
    }

    /**
     * Llegueix el hello de client i retorna un array amb l'opcode,la sessionid i el nom
     * @return parametres lleguits en un array
     * @throws IOException
     */
    public ArrayList<Object> readHello() throws IOException {
        ArrayList<Object> ret = new ArrayList<>();
        ret.add((byte) 1);
        ret.add(comUtils.read_int32());
        ret.add(comUtils.read_until_0());
        return ret;
    }

    /**
     * Llegueix el play de client i retorna un array amb l'opcode i la sessioid
     * @return parametres lleguits en un array
     * @throws IOException
     */
    public ArrayList<Object> readPlay() throws IOException {
        ArrayList<Object> ret = new ArrayList<>();
        ret.add((byte) 3);
        ret.add(comUtils.read_int32());
        return ret;
    }

    /**
     * Llegueix la paraula de client i retorna un array amb l'opcode i la paraula
     * @return parametres lleguits en un array
     * @throws IOException
     */
    public ArrayList<Object> readWord() throws IOException {
        ArrayList<Object> ret = new ArrayList<>();
        ret.add((byte) 5);
        ret.add(comUtils.read_string_5());
        return ret;
    }

    /**
     * Llegueix l'error de client i retorna un array.
     * @return
     * @throws IOException
     */

    public ArrayList<Object> readError() throws IOException {
        ArrayList<Object> ret = new ArrayList<>();
        ret.add((byte) 8);
        ret.add(comUtils.read_byte());
        ret.add(comUtils.read_until_0());
        return ret;
    }

    /**
     * Envia el missatge 'ready' al client
     * @param sessionID
     * @throws IOException
     */
    public void sendReady(int sessionID) throws IOException {
        comUtils.write_byte((byte) 2);
        comUtils.write_int32(sessionID);
    }

    /**
     * Envia el missatge 'Admit' al client
     * @param bool
     * @throws IOException
     */
    public void sendAdmit(boolean bool) throws IOException {
        comUtils.write_byte((byte) (4));
        comUtils.write_byte((byte) (bool ? 1 : 0));
    }

    /**
     * Envia el missatge 'Word' al client. Nomes s'utilitza cuan ja s'ha acabat el joc per mostrar quina era la paraula que buscavem.
     * @param word
     * @throws IOException
     */
    public void sendWord(String word) throws IOException {
        this.comUtils.write_byte((byte) 5);
        this.comUtils.write_string_variable(word);
    }

    /**
     * Envia el resultat en aquell intent al client
     * @param result
     * @throws IOException
     */
    public void sendResult(String result) throws IOException {
        comUtils.write_byte((byte) 6);
        comUtils.write_string(result);
    }

    /**
     * Envia les Stats finals del joc un cop s'ha acabat la partida
     * @param json
     * @throws IOException
     */

    public void sendStats(String json) throws IOException {
        comUtils.write_byte((byte) 7);
        comUtils.write_string_variable(json);
        comUtils.write_byte((byte) 0);
    }

    /**
     * Envia un error a client
     * @param errCode
     * @throws IOException
     */

    public void sendError(int errCode) throws IOException {
        comUtils.write_byte((byte) 8);
        comUtils.write_byte((byte) errCode);

        System.out.println("SENDERROR SERVER: "+ErrorsEnum.ERRCODE_1.getError(errCode));
        comUtils.write_string(ErrorsEnum.ERRCODE_1.getError(errCode));
        comUtils.write_byte((byte) 0);
    }

    /**
     * Conte els diferents missatges d'error que es mostraran a pantalla
     */
    public enum ErrorsEnum {
        ERRCODE_1("CARÀCTER NO RECONEGUT"),
        ERRCODE_2("MISSATGE DESCONEGUT"),
        ERRCODE_3("MISSATGE FORA DE PROTOCOL"),
        ERRCODE_4("INICI DE SESSIÓ INCORRECTE"),
        ERRCODE_5("PARAULA DESCONEGUDA"),
        ERRCODE_6("MISSATGE MAL FORMAT"),
        ERRCODE_99("ERROR DESCONEGUT");

        private final String error;

        ErrorsEnum(String error) {
            this.error = error;
        }


        public String getError(int errCode) {
            switch (errCode) {
                case 1:
                    return ERRCODE_1.error;
                case 2:
                    return ERRCODE_2.error;
                case 3:
                    return ERRCODE_3.error;
                case 4:
                    return ERRCODE_4.error;
                case 5:
                    return ERRCODE_5.error;
                case 6:
                    return ERRCODE_6.error;
                default:
                    return ERRCODE_99.error;
            }
        }

    }
}
