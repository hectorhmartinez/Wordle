package Client;

import Protocol.ProtocolClient;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;


public class Client {
    public static void main(String[] args) {
        //Tractament de paràmetres de consola ????????????

        // Declarem paràmetres necesaris
        Socket socket = null;
        InetAddress ipServer;
        int numPort, mode = 0;

        ProtocolClient client;
        HashMap<String, String> options = new HashMap<>();
        Game game;

        argumentsChecker(args, mode);

        for (int i = 0; i < args.length; i = i + 2) {
            options.put(args[i], args[i + 1]);
        }

        numPort = Integer.parseInt(options.get("-p"));
        if (args.length != 4) {
            mode = Integer.parseInt(options.get("-i"));
        }
        try {
            // Obtenim la IP de la maquina servidora
            ipServer = InetAddress.getByName(options.get("-s"));
            // Obrim una connexió amb el servidor
            socket = new Socket(ipServer, numPort);
        } catch (IOException e) {
            try {
                socket.close();
                System.out.println("Tancant socket");
            } catch (IOException ex) {
                System.out.println("Error al tancar socket");
                ex.printStackTrace();
            }
        }

        try {
            // Setejem un timeout pel socket
            socket.setSoTimeout(300000); //en ms.

            // Obrim un flux d'entrada/sortida amb el protocol
            client = new ProtocolClient(socket);
            game = new Game(client, mode);
            game.startGame();
        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
        } finally {
            try {
                socket.close();
            } catch (IOException ex) {
                System.out.println("Els errors han de ser tractats correctament pel vostre programa");
            } // fi del catch
        } // fi finally

    } // fi main


    /**
     * Mètode per comprovar els arguments introduits
     *
     * @param args Arguments introduits
     */
    private static void argumentsChecker(String[] args, int mode) {
        if (args.length == 0 || args.length > 6) {
            System.exit(0);
        }
        if (args[0].equals("-h")) {
            System.out.println("Us: java -jar client -s <maquina_servidora> -p <port> [-i 0|1]");
            System.exit(0);
        }

        if (args.length == 4) { // no porta -i
            if (!args[0].equals("-s") && !args[2].equals("-p")) {
                System.out.println("Error en els paràmetres, per consultar posa -h");
                System.exit(1);
            }
        } else if (!args[0].equals("-s") && !args[2].equals("-p") && !args[4].equals("-i")) {
            System.out.println("Error en els paràmetres, per consultar posa -h");
            System.exit(1);
        }

        try {
            InetAddress ip = InetAddress.getByName(args[1]);
            int port = Integer.parseInt(args[3]);
            if (args.length != 4) {
                mode = Integer.parseInt(args[5]);
            }// sinó ja està inicialitzada a 0

            if (port < 0 || port > 65535) {
                System.out.println("No has introduit un port vàlid del 0 al 65535");
                System.exit(0);
            }

            if (mode != 0 && mode != 1) {
                System.out.println("Mode de joc mal especificat");
                System.exit(0);
            }

        } catch (NumberFormatException | UnknownHostException ex) {
            System.out.println("No s'ha introduit un numero en un dels camps");
            System.exit(0);
        }
    }

} // fi clase
