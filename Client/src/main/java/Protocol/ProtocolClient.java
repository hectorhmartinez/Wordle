package Protocol;

import utils.ComUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

public class ProtocolClient {
    private ComUtils comUtils;



    // TODO: potser canviar que el comUtils rebi directament el socket.
    /* Constructor ¿temporal? per a test */
    public ProtocolClient() {
        File file = new File("proves");
        try {
            file.createNewFile();
            comUtils = new ComUtils(new FileInputStream(file), new FileOutputStream(file));
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    /* Constructor amb socket */
    public ProtocolClient(Socket socket) {
        try {
            comUtils = new ComUtils(socket.getInputStream(), socket.getOutputStream());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Llegeix el opCode i retorna el missatge
     *
     * @return
     * @throws IOException
     */
    public ArrayList<Object> getMessage() throws IOException {
        switch (comUtils.read_byte()) {
            case 1:
                //readHello
            case 2:
                return readReady();
            case 3:
                //readPlay
            case 4:
                return readAdmit();
            case 5:
                return readWord();
            case 6:
                return readResult();
            case 7:
                return readStats();
            case 8:
                return readError();
            default:
                //
        }
        throw new IOException("NO LLEGIT RES PROTOCOLCLIENT");
    }

    /**
     * Envia a servidor un hello amb els pertinents missatges(opcode,sessionId...)
     * @param sessionID
     * @param name
     * @throws IOException
     */
    public void sendHello(int sessionID, String name) throws IOException {
        comUtils.write_byte((byte) 1);
        comUtils.write_int32(sessionID);
        comUtils.write_string_variable(name);
        comUtils.write_byte((byte) 0);
    }

    /**
     * Envia a servidor que vol jugar
     * @param sessionID
     * @throws IOException
     */
    public void sendPlay(int sessionID) throws IOException {
        this.comUtils.write_byte((byte) 3);
        this.comUtils.write_int32(sessionID);
    }

    /**
     * Envia a servidor la paraula amb la que vol jugar
     * @param word
     * @throws IOException
     */
    public void sendWord(String word) throws IOException {
        this.comUtils.write_byte((byte) 5);
        this.comUtils.write_string_variable(word);
    }

    /**
     * Llegueix de servidor  quan servidor li diu que pot demanar jugar
     * @return parametres lleguits en un array
     * @throws IOException
     */
    public ArrayList<Object> readReady() throws IOException {
        ArrayList<Object> ret = new ArrayList<>();
        ret.add((byte) 2);
        ret.add(comUtils.read_int32());
        return ret;
    }

    /**
     * llegueix de servidor quan ha sigut admes per jugar
     * @return parametres lleguits en un array
     * @throws IOException
     */
    public ArrayList<Object> readAdmit() throws IOException {
        ArrayList<Object> ret = new ArrayList<>();
        ret.add((byte) 4);
        ret.add(this.comUtils.read_byte());
        return ret;
    }

    /**
     * Llegueix la paraula nomes quan ha finalitzat el joc
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
     * Llegueix el resultat corresponent a cada paraula que enviem
     * @return parametres lleguits en un array
     * @throws IOException
     */
    public ArrayList<Object> readResult() throws IOException {
        ArrayList<Object> ret = new ArrayList<>();
        ret.add((byte) 6);
        ret.add(this.comUtils.read_string_5());
        return ret;
    }

    /**
     * Llegueix les Stats de la nostra partida quan cop finalitzades
     * @return parametres lleguits en un array
     * @throws IOException
     */
    public ArrayList<Object> readStats() throws IOException {
        ArrayList<Object> ret = new ArrayList<>();
        ret.add((byte)7);
        ret.add(comUtils.read_until_0());

        return ret;
    }

    /**
     * Llegueix l'error  del servidor
     * @return parametres lleguits en un array
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
     * Envia un error a servidor
     * @param errCode
     * @throws IOException
     */
    public void sendError(int errCode) throws IOException {
        comUtils.write_byte((byte) 8);
        comUtils.write_byte((byte)errCode);
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

        ErrorsEnum(String error){
            this.error = error;
        }

        public String getError(int errCode){
            switch (errCode){
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
