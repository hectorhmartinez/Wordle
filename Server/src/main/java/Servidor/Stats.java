package Servidor;

import org.json.simple.JSONObject;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;

public class Stats {
    private int jugades;
    private int Ractual, Rmax, intents, numVictories;
    private float exits;
    private HashMap<String, Integer> llista = new HashMap<>();

    /**
     * Constructor de la classe Stats.
     */
    public Stats() {
        jugades = 0;
        Ractual = 0;
        Rmax = 0;
        intents = 0;
        exits = 0;
        numVictories = 0;
        llista = generarHashMap();
    }

    /**
     * genera el JSON
     * @return
     * @throws IOException
     */
    public String generateJSON() throws IOException {
        JSONObject obj = new JSONObject();
        obj.put("Jugades:", jugades);
        obj.put("Exits %:", exits);
        obj.put("Ratxa Actual:", Ractual);
        obj.put("Ratxa Maxima:", Rmax);
        obj.put("Victories:", getLlista());

        StringWriter out = new StringWriter();
        obj.writeJSONString(out);

        String jsonText = out.toString();
        return jsonText;
    }

    /**
     * Retorna la llista en forma d'string
     * @return llista amb les stats
     */
    private String getLlista() {
        return llista.toString();
    }

    /**
     * Genera un hashmap. Es a dir, omple la llista.
     * @return llista amb les stats
     */
    public HashMap<String, Integer> generarHashMap() {
        for (int i = 0; i < 6; i++) {
            llista.put(String.valueOf(i + 1), 0);
        }
        return llista;
    }

    /**
     * Suma una victoria a las stats
     */
    public void addWin() {
        String sIntent = String.valueOf(intents);
        int count = llista.containsKey(sIntent) ? llista.get(sIntent) : 0;
        llista.put(sIntent, count + 1);
        numVictories++;
        exits = (numVictories / jugades)*100;
    }

    /**
     * Mètode per aconseguir la ratxa actual.
     * @return ratxa actual
     */
    public int getRactual() {
        return Ractual;
    }

    /**
     * Actualitza la variable Ractual.
     * @param ractual
     */
    public void setRactual(int ractual) {
        Ractual = ractual;
    }

    /**
     * Mètode per aconseguir la ratxa màxima.
     * @return ratxa maxima
     */
    public int getRmax() {
        return Rmax;
    }

    /**
     * Actualitza la variable Rmax.
     * @param rmax
     */
    public void setRmax(int rmax) {
        Rmax = rmax;
    }

    /**
     * Mètode per aconseguir el numero d'intents.
     * @return
     */
    public int getIntents() {
        return intents;
    }
    /**
     * Actualitza la variable intents.
     * @param intents
     */
    public void setIntents(int intents) {
        this.intents = intents;
    }

    /**
     * Incrementa en 1 la ratxa actual
     */
    public void increaseRactual() {
        Ractual++;
    }
    /**
     * Incrementa en 1 el numero de jugades
     */
    public void increaseJugades() {
        jugades++;
    }
    /**
     * Incrementa en 1 els intents
     */
    public void increaseIntents() {
        intents++;
    }
}
