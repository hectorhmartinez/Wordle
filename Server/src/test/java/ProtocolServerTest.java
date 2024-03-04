
import ProtocolServer.ProtocolServidor;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class ProtocolServerTest {

    ProtocolServidor servidor;

    public ProtocolServerTest() {

        this.servidor = new ProtocolServidor();
    }

    @Test
    public void test_hello() {
        try {
            int sessionID = 56;
            String name = "Marc i Hector";


            String readHello = String.valueOf(servidor.readHello());

            assertEquals("1 56 Marc i Hector 0", readHello);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Test
    public void test_play() {
        try {
            int sessionID = 56;

            String readPlay = String.valueOf(servidor.readPlay());
            assertEquals("3 56", readPlay);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test_word() {
        try {
            String word = "PINSO";

            String readWord = String.valueOf(servidor.readWord());
            assertEquals("5 PINSO", readWord);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }




}