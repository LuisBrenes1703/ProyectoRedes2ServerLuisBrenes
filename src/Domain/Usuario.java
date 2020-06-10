package Domain;

public class Usuario {

    protected String nombre;
    protected String contraseña;


    public Usuario() {
        this.nombre = "";
        this.contraseña = "";

    }

    public Usuario(String nombre, String contraseña, String rutaPersonaje) {
        this.nombre = nombre;
        this.contraseña = contraseña;

    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getContraseña() {
        return contraseña;
    }

    public void setContraseña(String contraseña) {
        this.contraseña = contraseña;
    }


}
