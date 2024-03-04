package Servidor;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;


public class Server {


    public static void main(String[] args) {
        Logger logger = Logger.getLogger("myLog");
        // Declaració paràmetres
        ServerSocket serverSocket = null;
        Socket socketClient = null;
        int numPort;
        ConcurrentHashMap<Integer, HashMap<String, Stats>> sessions = new ConcurrentHashMap<>();

        argumentsChecker(args);

        numPort = Integer.parseInt(args[1]);

        try {
            // Creem servidor
            serverSocket = new ServerSocket(numPort);
            System.out.println("Server socket preparat en el port " + numPort);

            while (true) {
                System.out.println("Esperant una connexió d'un client.");
                /* Esperem que un client vulgui connectar-se*/
                socketClient = serverSocket.accept();

                Thread newGame = new Thread(new GameServidor(socketClient, sessions));
                newGame.start();
            } // fi del while true

        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
        } finally {
            try {
                if (serverSocket != null) {
                    serverSocket.close();
                    System.out.println("TANCANT SOCKET CLIENT");
                }
            } catch (IOException ex) {
                System.out.println("Els errors han de ser tractats correctament pel vostre programa");
            } // fi del catch
        } // fi finally
    } // fi del main


    /**
     * Mètode per comprovar els arguments introduits
     *
     * @param args Arguments introduits
     */
    private static void argumentsChecker(String[] args) {
        if (args.length == 0 || args.length > 4) {
            System.out.println("No has introduit cap paràmetre, per ajuda escriu -h com a paràmetre.");
            System.exit(0);
        }

        if (args[0].equals("-h")) {
            System.out.println("Us: java –jar server.jar -p <port>");
            System.exit(0);
        }

        if (!args[0].equals("-p")) {
            System.out.println("Error en els paràmetres, per consultar posa -h");
            System.exit(1);
        }
        try {
            int port = Integer.parseInt(args[1]);
            if (port < 0 || port > 65535) {
                System.out.println("No has introduit un port vàlid del 0 al 65535");
                System.exit(0);
            }

        } catch (NumberFormatException ex) {
            System.out.println("No s'ha introduit un numero en un dels camps");
            System.exit(0);
        }
    }


} // fi de clase
