package Domain;

import Servidor.AtiendeCliente;
import java.io.IOException;
import java.util.ArrayList;

public class ListaClienteJugador {

    private static ListaClienteJugador singletonPartidasJugadores;
    private ArrayList<AtiendeCliente> atiendeCliente;
          
    private ListaClienteJugador() {
        this.atiendeCliente = new ArrayList<>();
      
    }

    public static ListaClienteJugador obtenerInstancia() throws IOException {
        if (singletonPartidasJugadores == null) {
            singletonPartidasJugadores = new ListaClienteJugador();
        }
        return singletonPartidasJugadores;
    }//obtenerInstancia

   
    public ArrayList<AtiendeCliente> getAtiendeCliente() {
        return atiendeCliente;
    }

    public void setAtiendeCliente(ArrayList<AtiendeCliente> atiendeCliente) {
        this.atiendeCliente = atiendeCliente;
    }      
       
}
