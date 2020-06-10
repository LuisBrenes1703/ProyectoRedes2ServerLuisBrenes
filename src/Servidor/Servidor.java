package Servidor;

import Domain.ListaClienteJugador;
import com.mysql.jdbc.PreparedStatement;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;
import javax.swing.JTextField;
import org.jdom.JDOMException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.JOptionPane;

public class Servidor extends JFrame implements Runnable, ActionListener {

    private int socketPortNumber;

    private JLabel labelIpServidor, labelCliente, labelContrasena;

    JTextField textCliente;
    JPasswordField textContrasena;
    JButton buttonRegistrar;

    private Thread hilo;
    private ListaClienteJugador listaPartidasJugadoresSingleton;

    public Servidor(int socketPortNumber) throws JDOMException, IOException {
        super("Server");
        this.setLayout(null);
        this.setSize(300, 300);
        this.socketPortNumber = socketPortNumber;
        this.listaPartidasJugadoresSingleton = ListaClienteJugador.obtenerInstancia();
        init();
        this.hilo = new Thread(this);
        this.hilo.start();
    }

    public void init() throws JDOMException, IOException {

        this.labelContrasena = new JLabel("Nombre cliente");
        this.labelContrasena.setBounds(10, 10, 150, 30);
        this.add(labelContrasena);

        this.labelCliente = new JLabel("Contraseña cliente ");
        this.labelCliente.setBounds(10, 70, 150, 30);
        this.add(labelCliente);

        this.textContrasena = new JPasswordField();
        this.textContrasena.setBounds(150, 70, 100, 30);
        this.add(textContrasena);

        this.textCliente = new JTextField();
        this.textCliente.setBounds(150, 10, 100, 30);
        this.add(textCliente);

        this.buttonRegistrar = new JButton("Registrar Cliente");
        this.buttonRegistrar.setBounds(50, 120, 150, 30);
        this.buttonRegistrar.addActionListener(this);
        this.add(buttonRegistrar);

        this.labelIpServidor = new JLabel();
        this.labelIpServidor.setBounds(50, 150, 500, 100);
        this.add(this.labelIpServidor);
    }

    @Override
    public void run() {
        try {
            ServerSocket serverSocket = new ServerSocket(this.socketPortNumber);
            InetAddress address = InetAddress.getLocalHost();
            this.labelIpServidor.setText(String.valueOf(address));
            do {
                Socket socket = serverSocket.accept();
                AtiendeCliente atiendeCliente = new AtiendeCliente(socket);
                Thread hilo = new Thread(atiendeCliente);
                this.listaPartidasJugadoresSingleton.getAtiendeCliente().add(atiendeCliente);
                for (int i = 0; i < this.listaPartidasJugadoresSingleton.getAtiendeCliente().size(); i++) {
                    System.out.println("estoy en el servidor principal" + this.listaPartidasJugadoresSingleton.getAtiendeCliente().get(i));
                }
                hilo.start();

//              atiendeCliente.start();
            } while (true);

        } catch (IOException ex) {
            Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JDOMException ex) {
            Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == this.buttonRegistrar) {

            Conectar conect = new Conectar();
            Connection conectar;
            try {

                conectar = conect.conexion();
                Statement pst = conectar.createStatement();
                ResultSet rs = pst.executeQuery("call get_Usuario('" + textCliente.getText() + "','" + textContrasena.getText() + "')");

                String nombreRe = "";

                while (rs.next()) {
                    nombreRe = rs.getString("nombre");

                    //System.out.println("nombre = " + nombreRe + " contrasena" + apellidoRe);
                }
                if (nombreRe.equals("")) {
                    pst.executeQuery("call insert_Usuario('" + textCliente.getText() + "','" + textContrasena.getText() + "')");
                    
                    File directorio = new File("usuarios\\"+this.textCliente.getText());
                    if (!directorio.exists()) {
                        if (directorio.mkdirs()) {
                            System.out.println("Directorio creado");
                        } else {
                            System.out.println("Error al crear directorio");
                        }
                    }
                    JOptionPane.showMessageDialog(null, "Usuario ingresado con exito");
                } else {
                    JOptionPane.showMessageDialog(null, "El usuario ya existe");
                }

            } catch (SQLException ex) {
                Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
            }

        }

    }
    
    
    
    

}//fin clase
