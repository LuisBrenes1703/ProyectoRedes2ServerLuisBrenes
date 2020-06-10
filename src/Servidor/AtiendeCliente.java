package Servidor;

import Domain.Jugador;
import Domain.ListaClienteJugador;
import Domain.Partida;
import Domain.Usuario;
import Utility.MyUtility;
import com.mysql.jdbc.PreparedStatement;
import com.sun.org.apache.xml.internal.serializer.ElemDesc;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.StringReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

public class AtiendeCliente extends Thread {

    private Socket socket;
    private Element elemento;
    private Usuario usuarioAtentiendose;
    private Jugador jugadorEnLinea;
    private String opcionQuiero;
    private ListaClienteJugador listaPartidasJugadoresSingleton;

    private PrintStream send;
    private BufferedReader receive;
    private DataOutputStream sendArchivo;
    private DataInputStream receiveArchivo;

    public AtiendeCliente(Socket socket) throws IOException, JDOMException {

        super("Hilo Servidor");

        this.socket = socket;

        this.jugadorEnLinea = new Jugador();
        this.listaPartidasJugadoresSingleton = ListaClienteJugador.obtenerInstancia();
        this.send = new PrintStream(this.socket.getOutputStream());
        this.receive = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
        this.sendArchivo = new DataOutputStream(this.socket.getOutputStream());
        this.receiveArchivo = new DataInputStream(this.socket.getInputStream());

    }

    public void run() {
        try {

            System.out.println("Servidor ejecutando");

            while (true) {
                System.out.println("Cliente acceptado");

                this.opcionQuiero = "";

                this.opcionQuiero = receive.readLine();
                Element element = stringToXML(opcionQuiero);
                this.opcionQuiero = element.getChild("accion").getValue();

                System.out.println(this.opcionQuiero + "  opcion enviada");
                System.out.println(this.opcionQuiero + "  opcion");

                switch (this.opcionQuiero) {

                    case "logear":

                        System.out.println("Entre a loguarme");

                        this.usuarioAtentiendose = xmlAUsuario(element);

                        System.out.println("nombre:" + this.usuarioAtentiendose.getNombre());
                        System.out.println("contaseña:" + this.usuarioAtentiendose.getContraseña());
                        boolean encotrado = false;

                        Conectar conect = new Conectar();
                        Connection conectar;
                        try {

                            conectar = conect.conexion();
                            Statement pst = conectar.createStatement();
                            ResultSet rs = pst.executeQuery("call get_Usuario('" + this.usuarioAtentiendose.getNombre() + "','" + this.usuarioAtentiendose.getContraseña() + "')");

                            String nombreRe = "";
                            String contrasenaRe = "";
                            while (rs.next()) {
                                nombreRe = rs.getString("nombre");
                                contrasenaRe = rs.getString("contrasena");
                                //System.out.println("nombre = " + nombreRe + " contrasena" + apellidoRe);
                            }

                            Element elementoEnviar = new Element("Logueo");
                            Element accion = new Element("accion");

                            if (nombreRe.equals("")) {

                                accion.addContent("no logueo");
                                elementoEnviar.addContent(accion);
                                send.println(xmlToString(elementoEnviar));
                                System.out.println("no logueo");

                            } else {

                                accion.addContent("si logueo");
                                elementoEnviar.addContent(accion);
                                send.println(xmlToString(elementoEnviar));
                                System.out.println("si logueo");

                                listarArchivos();

                            }

                        } catch (SQLException ex) {
                            Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
                        }

                        break;

                    case "cargarArchivo":
                        System.out.println("Entre a leer archivo");
                        recibirArchivo(element.getChild("archivo").getValue());
                        break;

                    case "pedirArchivo":
                        System.out.println("Entre a pedir archivo");
                       // element.getChild("archivo").getValue();
                       enviarArchivo(element.getChild("archivo").getValue()); 
                       
                        
                        break;

                        

                }

            }

        } catch (IOException ex) {
            Logger.getLogger(AtiendeCliente.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JDOMException ex) {
            Logger.getLogger(AtiendeCliente.class.getName()).log(Level.SEVERE, null, ex);
        }
    } // run

    private Element stringToXML(String stringMensaje) throws JDOMException, IOException {
        SAXBuilder saxBuilder = new SAXBuilder();
        StringReader stringReader = new StringReader(stringMensaje);
        Document doc = saxBuilder.build(stringReader);
        return doc.getRootElement();
    } // stringToXML   

    private Usuario xmlAUsuario(Element elementoActual) {

        Usuario usuarioActual = new Usuario();

        usuarioActual.setNombre(elementoActual.getAttributeValue("nombre"));
        usuarioActual.setContraseña(elementoActual.getChild("Contrasena").getValue());

        return usuarioActual;

    } // xmlAEstudiante

    private Element generarUsuarioXML(Usuario usuario) {

        Element mUsuario = new Element("Usuario");
        mUsuario.setAttribute("nombre", usuario.getNombre());

        Element mContrasena = new Element("Contrasena");
        mContrasena.addContent(usuario.getContraseña());

        mUsuario.addContent(mContrasena);

        return mUsuario;

    } // generEstudianteXML

    private String xmlToString(Element element) {
        XMLOutputter output = new XMLOutputter(Format.getCompactFormat());
        String xmlStringElement = output.outputString(element);
        xmlStringElement = xmlStringElement.replace("\n", "");
        return xmlStringElement;
    } // xmlToString 

    public Usuario getUsuarioAtentiendose() {
        return usuarioAtentiendose;
    }

    public void setUsuarioAtentiendose(Usuario usuarioAtentiendose) {
        this.usuarioAtentiendose = usuarioAtentiendose;
    }

    public String getOpcionQuiero() {
        return opcionQuiero;
    }

    public void setOpcionQuiero(String opcionQuiero) {
        this.opcionQuiero = opcionQuiero;
    }

    public void escribirACliente(String send) {
        this.send.println(send);
    }

    public Jugador getJugadorEnLinea() {
        return jugadorEnLinea;
    }

    public void setJugadorEnLinea(Jugador jugadorEnLinea) {
        this.jugadorEnLinea = jugadorEnLinea;
    }

    public void recibirArchivo(String nombre) throws FileNotFoundException, IOException {

        String nombreFin = "usuarios\\" + this.usuarioAtentiendose.getNombre() + "\\" + nombre;

        System.out.println("nombre del archivo: " + nombreFin);
        int lectura;
        BufferedOutputStream outputFile = new BufferedOutputStream(new FileOutputStream(new File(nombreFin)));

        byte byteArray[] = new byte[1024];

        while ((lectura = receiveArchivo.read(byteArray)) != -1) {
            outputFile.write(byteArray, 0, lectura);
        }
        outputFile.close();
    }

    public void listarArchivos() throws IOException {
        String nombreFin = "usuarios\\" + this.usuarioAtentiendose.getNombre();
        File carpeta = new File(nombreFin);
        String[] listado = carpeta.list();
        if (listado == null || listado.length == 0) {
            System.out.println("No hay elementos dentro de la carpeta actual");
        } else {

            for (int i = 0; i < listado.length; i++) {

                Element elementoActual = new Element("MandarArch");
                Element mAccion = new Element("accion");
                mAccion.addContent("verNombres");

                Element mElemento = new Element("nombreAr");
                mElemento.addContent(listado[i]);

                elementoActual.addContent(mAccion);
                elementoActual.addContent(mElemento);

                this.send.println(xmlToString(elementoActual));

            
            }
        }
    }
    
    
     public void enviarArchivo(String filename) throws FileNotFoundException, IOException {
         
          String nombreEnviar = filename;
          filename = "usuarios\\" + this.usuarioAtentiendose.getNombre() + "\\" + filename;
          System.out.println(filename);
          
        if (!filename.equalsIgnoreCase("")) {
            int lectura;

            BufferedInputStream outputFile = new BufferedInputStream(new FileInputStream(new File(filename)));

            byte byteArray[] = new byte[1024];


            Element elementoActual = new Element("MandarArch");
            Element mAccion = new Element("accion");
            mAccion.addContent("cargarArchivo");
            Element mArchivo = new Element("archivo");
            mArchivo.addContent(nombreEnviar);
            elementoActual.addContent(mAccion);
            elementoActual.addContent(mArchivo);

            this.send.println(xmlToString(elementoActual));

            while ((lectura = outputFile.read(byteArray)) != -1) {
                this.send.write(byteArray, 0, lectura);
            }

            filename = "";
            outputFile.close();
        }
    }

} // fin clase
