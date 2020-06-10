/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Servidor;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Conectar {
    
    private Connection conexion = null;
    
       public Connection conexion() throws SQLException{
        
        try{
            Class.forName("org.gjt.mm.mysql.Driver");             
            conexion =DriverManager.getConnection("jdbc:mysql://163.178.107.10:3306/proyectoRedesLuisBrenes?" + "user=laboratorios&password=UCRSA.118");
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
        }
        return conexion;
    }
    
}
