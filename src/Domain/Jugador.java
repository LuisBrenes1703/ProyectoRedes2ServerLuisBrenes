package Domain;

public class Jugador {

    private String nombre;
    private String rutaPersonaje;
    private String partida;
    

    public Jugador() {
        this.nombre = "";
        this.rutaPersonaje = "";
    }

    public Jugador(String nombre, String rutaPersonaje, String partida) {
        this.nombre = nombre;
        this.rutaPersonaje = rutaPersonaje;
        this.partida = partida;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getRutaPersonaje() {
        return rutaPersonaje;
    }

    public void setRutaPersonaje(String rutaPersonaje) {
        this.rutaPersonaje = rutaPersonaje;
    }

    public String getPartida() {
        return partida;
    }

    public void setPartida(String partida) {
        this.partida = partida;
    }

}
