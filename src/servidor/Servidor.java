package servidor;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Servidor {

    public static void main(String[] args) {
        ServerSocket servidor = null;
        
        try {
            System.out.println("Iniciando o servidor ...");
            servidor = new ServerSocket(9090);
            System.out.println("Servidor Iniciado");

            while (true) {
                Socket cliente = servidor.accept();
                new GerenciadorCliente(cliente);
            }
            
        } catch (IOException e) {
            try {
                if (servidor != null) {
                    servidor.close();
                }
            } catch (IOException el) {
                
            }
            System.err.println("Erro ao conectar ao servidor");
            e.printStackTrace();
        }
    }

}
