package Servidor;

import ProtocolServer.ProtocolServidor;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.FileHandler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;


public class GameServidor implements Runnable {

    Stats stats = new Stats();
    ProtocolServidor servidor;

    String name, word, result, answerWord, errMsg;
    int opCode, sessionID, playSID, errCode;
    boolean admit, sortir = false;
    List<String> diccionari;
    Logger logger;
    Socket socketClient;
    ConcurrentHashMap<Integer, HashMap<String, Stats>> sessions;
    HashMap<String, Stats> nameStats = new HashMap<>();

    ObjectMapper mapper = new ObjectMapper();

    /**
     * Constructor de la classe GameServidor.
     *
     * @param socket
     * @param sessions
     * @throws IOException
     */
    public GameServidor(Socket socket, ConcurrentHashMap<Integer, HashMap<String, Stats>> sessions) throws IOException {

        this.sessions = sessions;
        servidor = new ProtocolServidor(socket);
        socketClient = socket;
        this.logger = Logger.getLogger("myLog");
        diccionari = getDictionary();
        try {
            createLog(logger);
        } catch (IOException e) {
            System.out.println("ERROR AL CREAR LOG: " + e.getMessage());
        }
    }

    /**
     * El metode run() permet iniciar el joc.
     */
    @Override
    public void run() {
        try {
            logger.info("C - [TCP Accept]");
            System.out.println("Connexió acceptada d'un client en el Thread: " + Thread.currentThread().getName());
            this.startServerGame();
            socketClient.setSoTimeout(30000);
            this.socketClient.close();
            System.out.println("Client " + Thread.currentThread().getName() + " tancat correctament");
        } catch (IOException e) {
            try {
                logger.info("ERROR a run gameServer " + e.getMessage());
                //if (e.getMessage().contains("Broken Pipe")) {
                logger.info("ERROR2 a run gameServer " + e.getMessage());

                System.out.println("CLIENT: ADEU");
                //}
                this.socketClient.close();
            } catch (IOException ex) {
                logger.info("ERROR3 a run gameServer " + e.getMessage());

                System.out.println("FAILED TO CLOSE SOCKET CONNECTION.");
            }
        } finally {
            try {
                socketClient.close();
            } catch (IOException e) {
                System.out.println("FAILED TO CLOSE SOCKET CONNECTION.");
            }
        }
    }

    /**
     * Metode on es desenvolupa la lógica del joc. Depenent del opcode fara una cosa o una altra.
     *
     * @throws IOException
     */
    public void startServerGame() throws IOException {
        while (!sortir) {
            ArrayList<Object> message = new ArrayList<>();
            message = servidor.getMessage();
            opCode = (byte) message.get(0);
            switch (opCode) {
                case 1:
                    sessionID = (int) message.get(1);
                    name = (String) message.get(2);
                    if (sessionID != 0) { // vol iniciar sessió
                        if (comprovarSessioAntiga(sessionID, name) == 0) {
                            // si inicia sessió correctament
                            logger.info("HELLO   C -------" + opCode + " " + sessionID + " " + name + " --------> S");
                            servidor.sendReady(sessionID);
                        }
                    } else { // partida nova (helloSID == 0)
                        sessionID = generateSessionID();
                        name = (String) message.get(2);
                        logger.info("HELLO   C -------" + opCode + " " + sessionID + " " + name + " --------> S");
                        servidor.sendReady(sessionID);
                    }
                    break;

                case 3:
                    logger.info("READY  C <------2" + " " + sessionID + " --------- S");
                    //llegim play
                    playSID = (int) message.get(1);

                    logger.info("PLAY     C -------" + opCode + " " + playSID + " --------> S");
                    admit = (playSID == sessionID);
                    logger.info("ADMIT    C <------4" + " " + (admit ? 1 : 0) + " ---------------- S");
                    servidor.sendAdmit(admit);

                    nameStats.put(name, stats);
                    sessions.put(sessionID, nameStats);
                    answerWord = getRandomWord();
                    System.out.println("RESPOSTA: " + answerWord);
                    //incrementem el nombre de partides jugades en 1
                    stats.increaseJugades();
                    break;
                case 5:
                    System.out.println("Intent numero: " + (stats.getIntents() + 1));
                    word = (String) message.get(1);

                    logger.info("WORD   C -------" + opCode + " " + word + " -----------> S");

                    result = generateResult(word, answerWord);
                    System.out.println(stats.getIntents());
                    if (result.equals("error")) {
                        servidor.sendError(5);
                        logger.info("ERROR C <------8 " + 5 + " PARAULA DESCONEGUDA ------------ S");

                    } else if (result.equals("^^^^^")) { // ha guanyat
                        logger.info("RESULT C <------6 " + result + " ------------ S");
                        servidor.sendResult(result);
                        //incrementem la ratxa de victores en 1
                        stats.increaseRactual();
                        stats.addWin();
                        enviarStats("win");

                    } else if (stats.getIntents() == 6) { // ha perdut
                        logger.info("RESULT C <------6 " + result + " ------------ S");
                        logger.info("WORD   C <------5 " + answerWord + " ------------ S");
                        servidor.sendResult(result);
                        servidor.sendWord(answerWord);
                        //Posem la ratxa de victories a 0
                        stats.setRactual(0);
                        enviarStats("lost");

                    } else {
                        logger.info("RESULT C <------6 " + result + " ------------ S");
                        servidor.sendResult(result);
                    }
                    break;
                case 8:
                    // rebre ERROR
                    errCode = (byte) message.get(1);
                    errMsg = (String) message.get(2);
                    logger.info("ERROR C ------8 " + errCode + " " + errMsg + " ------------> S");
                    switch (errCode) {
                        case 1:
                            // caracter no reconegut
                            System.out.println(errMsg);
                            break;
                        case 2:
                            // missatge desconegut
                            System.out.println(errMsg);
                            break;
                        case 3:
                            // missatge fora de protocol
                            System.out.println(errMsg);
                            sortir = true;
                            break;
                        case 4:
                            // inici de sessió incorrecte
                            System.out.println(errMsg);
                            break;
                        case 5:
                            // paraula desconeguda
                            System.out.println(errMsg);
                            break;
                        case 6:
                            // missatge mal format
                            System.out.println(errMsg);
                            break;
                        default:
                            //error desconegut
                            System.out.println(errMsg);
                            break;
                    }
                    break;
                default:
                    break;
            } // fi switch
        }
    }

    /**
     * Envia les stats al client un cop ha acabat el joc. Tant si ha perdut com si ha guanyat
     *
     * @param res
     * @throws IOException
     */

    private void enviarStats(String res) throws IOException {
        if (stats.getRactual() > stats.getRmax()) {
            stats.setRmax(stats.getRactual());
        }

        if (res.equals("lost")) {
            stats.setRactual(0);
        }
        logger.info("STATS  C <------7 " + mapper.writerWithDefaultPrettyPrinter().writeValueAsString(mapper.readTree(stats.generateJSON())) + " ------------- S");
        nameStats.put(name, stats);
        sessions.put(sessionID, nameStats);
        servidor.sendStats(stats.generateJSON());
        stats.setIntents(0);
    }

    /**
     * Comprova si la sessio antiga que volem recuperar existeix o no.
     *
     * @param sessionID
     * @param name
     */

    private int comprovarSessioAntiga(int sessionID, String name) throws IOException {
        HashMap<String, Stats> values;
        if (sessions.isEmpty()) {
            System.out.println("No hi ha cap sessió antiga");
            servidor.sendError(4);
            return -1;
        }
        if (sessionID == 0) {
            System.out.println("sessionid0 error");
            return -1;
        }
        if (sessions.containsKey(sessionID)) {
            values = sessions.get(sessionID);
            if (values.containsKey(name)) {
                stats = values.get(name);
            }
            return 0;

        } else {
            System.out.println("F");
            return -1;
        }
    }

    /**
     * Genera un nombre random
     *
     * @return random int del 0 al 99999
     */
    private int generateSessionID() {
        Random random = new Random();
        return Math.abs(random.nextInt(100000));
    }


    /**
     * Mètode per comprovar si la paraula és la resposta
     *
     * @param word
     * @param answer
     * @return combinació de ^ i * en funció de el nombre de lletres correctes
     */
    private String generateResult(String word, String answer) throws IOException {
        char lletraWord, lletraAnswer;
        StringBuilder result = new StringBuilder();

        if (!checkWordExists(word)) return "error";

        for (int i = 0; i < 5; i++) {
            lletraWord = word.charAt(i);
            lletraAnswer = answer.charAt(i);
            if (answer.indexOf(lletraWord, i) != -1) {
                if (lletraWord == lletraAnswer) {
                    result.append("^");
                } else {
                    result.append("?");
                }
            } else {
                result.append("*");
            }
        }

        stats.increaseIntents();
        return String.valueOf(result);
    } // fi compareString

    private boolean checkWordExists(String word) {
        return diccionari.contains(word);
    }

    /**
     * Mètode per omplir el hashmap diccionari en funció del fitxer txt que el conté
     *
     * @return diccionari ple
     */
    public static List<String> getDictionary() {
        String line;
        List<String> dict = new ArrayList<>();
        try {
            String path = "DISC2-LP-WORDLE.txt";
            BufferedReader reader = new BufferedReader
                    (new BufferedReader(
                            new InputStreamReader(
                                    Objects.requireNonNull(GameServidor.class.getResourceAsStream(path)),
                                    StandardCharsets.UTF_8)));
            while (true) {
                line = reader.readLine();
                if (line == null) break;
                dict.add(line);
            }
            reader.close();
        } catch (Exception e) {
            //Server.info("ERROR WHILE READING TXT");
            return null;
        }
        return dict;
    }

    /**
     * Retorna una paraula random de dins del diccionari
     *
     * @return string random de diccionari
     */
    private String getRandomWord() {
        int i = new Random().nextInt(diccionari.size());
        return diccionari.get(i);
    }

    /**
     * Mètode per a crear log de l'execució del programa
     *
     * @param logger
     * @throws IOException
     */
    private static void createLog(Logger logger) throws IOException {
        //Tractament de paràmetres de consola
        FileHandler file = new FileHandler("Server" + Thread.currentThread().getName() + ".log");
        SimpleFormatter format = new SimpleFormatter() {
            private static final String format = "[%1$tF %1$tT] [%2$-7s] %3$s %n";

            @Override
            public synchronized String format(LogRecord lr) {
                return String.format(format, new Date(lr.getMillis()),
                        lr.getLevel().getLocalizedName(), lr.getMessage()
                );
            }
        };
        file.setFormatter(format);
        logger.addHandler(file);
        logger.setUseParentHandlers(false);
    }

}
