import Protocol.ProtocolClient;

import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class ProtocolClientTest {
   ProtocolClient client;


    public ProtocolClientTest() {
        this.client = new ProtocolClient();
    }

    @Test
    public void test_ready() {
        try {
            int sessionID = 56;
            String readReady = String.valueOf(client.readReady());

            assertEquals("2 56", readReady);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Test
    public void test_admit() {
        try {

            String readAdmit = String.valueOf(client.readAdmit());
            assertEquals("4 1", readAdmit);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test_result() {
        try {
            String word = "^^^^^";

            String readResult = String.valueOf(client.readResult());
            assertEquals("6 ^^^^^", readResult);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test_stats() {
        //falta posar el json
    }

    @Test
    public void test_error() {
        try {
            int errCode = 1;
            String msg = "CARACTER NO RECONEGUT";

            String readError = String.valueOf(client.readError());
            assertEquals("8 1 CARACTER NO RECONEGUT 0", readError);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}