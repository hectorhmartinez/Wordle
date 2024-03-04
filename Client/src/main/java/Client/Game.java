package Client;

import Protocol.ProtocolClient;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class Game {


    ProtocolClient client;
    List<String> diccionari;
    int sessionID, mode, intents, errCode;
    byte opCode;
    String name, result, answerWord, stats, seguirJugant, word, errMsg;
    boolean admit, sortir = false;
    ObjectMapper mapper = new ObjectMapper();

    Scanner sc = new Scanner(System.in);

    /**
     * Constructor de la classe game
     *
     * @param client
     * @param mode
     */
    public Game(ProtocolClient client, int mode) {
        this.client = client;
        diccionari = getDictionary();

        this.mode = mode;
    }

    /**
     * Inicialitza el joc a traves de play() i caça excepcions
     */
    public void startGame() {
        try {
            play();
        } catch (IOException e) {
            System.out.println("ERROR A START GAME: " + e.getMessage());
        }
    } // fi StartGame

    /**
     * Es qui conte cota la logica del joc.A través d'un switch i utilitzan l'opecode fara una cosa o una altre
     */
    private void play() throws IOException {
        // enviar HELLO
        askInfo();
        client.sendHello(sessionID, name);

        while (!sortir) {
            ArrayList<Object> message = new ArrayList<>();
            message = client.getMessage();
            opCode = (byte) message.get(0);
            switch (opCode) {
                case 2:
                    // read READY
                    sessionID = (int) message.get(1);
                    System.out.println("La teva sessionID és: " + sessionID);
                    // enviar PLAY
                    client.sendPlay(sessionID);
                    break;
                case 4:
                    // read ADMIT
                    admit = ((int) ((byte) message.get(1)) == 1);
                    if (!admit) {
                        System.out.println("Les sessionsid no coincideixen. Sortim del programa");
                        System.exit(8);
                    }
                    if (mode == 0) word = askWord();
                    else word = getRandomWord();
                    System.out.println("intent Client: " + (intents + 1));
                    // enviar WORD
                    System.out.println(word);
                    client.sendWord(word);
                    break;

                case 6:
                    // readResult
                    result = (String) message.get(1);
                    System.out.println(result);
                    intents++;
                    if (result.equals("^^^^^")) {
                        System.out.println("Enhorabona, has guanyat! ");
                    } else if (intents == 6) {
                        message = client.getMessage();
                        answerWord = (String) message.get(1);
                        System.out.println("La paraula corecte era. " + answerWord);
                    } else { // sino guanya ni perd continua jugant
                        enviaWord();
                    }
                    break;
                case 7:
                    // rebre STATS
                    stats = (String) message.get(1);
                    System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(mapper.readTree(stats)));
                    // updatejar estat a PLAY
                    System.out.println("Vols seguir jugant?(S/N)");
                    seguirJugant = sc.nextLine().toUpperCase();
                    if (seguirJugant.charAt(0) == 'S') {
                        client.sendPlay(sessionID);
                        intents = 0;
                    } else {
                        sortir = true;
                    }
                    break;
                case 8:
                    // rebre ERROR
                    errCode = (byte) message.get(1);
                    errMsg = (String) message.get(2);
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
                            break;
                        case 4:
                            // inici de sessió incorrecte
                            System.out.println(errMsg);
                            // tornem a enviar un hello
                            askInfo();
                            client.sendHello(sessionID, name);

                            break;
                        case 5:
                            // paraula desconeguda
                            System.out.println(errMsg);
                            enviaWord();

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
                default:
                    // send error missatge fora de protocol

                    //client.sendError(3);
                    break;
            } // fi switch
        }
    }

    /**
     * Metode encarregat de enviar la paraula al servidor
     */
    private void enviaWord() throws IOException {
        if (mode == 0) word = askWord();
        else word = getRandomWord();
        System.out.println("intent Client: " + (intents + 1));
        // enviar WORD
        System.out.println(word);
        client.sendWord(word);
    }


    /**
     * Demana la paraula amb la que el jugador jugara.
     *
     * @return String paraula
     */
    private String askWord() {
        System.out.println("--------------------------------------------");
        System.out.println("Amb quina paraula de 5 lletres vols jugar?");
        String word = sc.nextLine();
        while (word.length() != 5 && !word.isEmpty()) {
            System.out.println("Error, no es de 5 lletres,tornala a escriure");
            word = sc.nextLine();
        }
        System.out.println("--------------------------------------------");

        return word.toUpperCase();
    }

    /**
     * Demana la informació previa a l'inici del joc
     */

    private void askInfo() {
        System.out.println("Vols generar una sessió nova (0) o recuperar una anteriror (1)?");
        String input = sc.nextLine();
        Boolean bool = false;

        //throw away the \n not consumed by nextInt()

        while (bool == false) {
            if (input.equals("0")) {
                sessionID = 0;
                name = askPlayerName();
                bool = true;
            } else if (input.equals("1")) {
                System.out.println("Introdueix la sessionID i el nom d'usuari que vulguis recuperar.");
                String inputValue = sc.nextLine();
                String[] value = inputValue.split(" ");
                if (checkIfInputIsInt(value[0]) && checkIfInputIsString(value[1])) {
                    if (Integer.parseInt(value[0]) == 0) sessionID = 1;
                    else sessionID = Integer.parseInt(value[0]);
                    name = value[1];
                    bool = true;
                } else {
                    System.out.println("Has d'introduir un int i un string.");
                }
            } else {
                System.out.println("Has d'introduir un 0 o un 1.");
                input = sc.nextLine();

            }
        }

    }

    /**
     * Mètode per pasar el contingut de totes les paraules a un hashmap amb forma <paraula, paraula>
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
                                    Objects.requireNonNull(Game.class.getResourceAsStream(path)),
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
     * Demana el nom del jugador.
     *
     * @return nom introduit pel jugador
     */
    private String askPlayerName() {
        System.out.println("Quin és el teu nom?");
        String nom = sc.nextLine();
        return nom;
    }

    private boolean checkIfInputIsString(String s) {
        for (int i = 0; i < s.length(); i++) {
            if (!Character.isLetter(s.charAt(i))) return false;
        }
        return true;
    }

    private boolean checkIfInputIsInt(String s) {
        for (int i = 0; i < s.length(); i++) {
            if (!Character.isDigit(s.charAt(i))) return false;
        }
        return true;
    }
}
