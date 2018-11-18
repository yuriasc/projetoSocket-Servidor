/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servidor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class GerenciadorCliente extends Thread {

    private Socket cliente;
    private String nomecliente;
    private BufferedReader leitor;
    private PrintWriter escritor;
    private static final Map<String, GerenciadorCliente> clientes = new HashMap<String, GerenciadorCliente>();

    public GerenciadorCliente(Socket cliente) {
        this.cliente = cliente;
        start();
    }

    @Override
    public void run() {
        try {
            leitor = new BufferedReader(new InputStreamReader(cliente.getInputStream()));// esta recebendo do cliente																							 																							 
            escritor = new PrintWriter(cliente.getOutputStream(), true);// esta mandando pro cliente, true e o autoflush																		 																		 																		 																																			 
            escritor.println("Bem vindo ao chat. Por favor digite seu nome");
            
            String msg = leitor.readLine();
            this.nomecliente = msg;
            
            escritor.println("Ola " + this.nomecliente + "(Digite bye para sair do chat)");
            clientes.put(this.nomecliente, this);
            
            while (true) {
                msg = leitor.readLine();
                Date date = new Date();
                SimpleDateFormat formatador = new SimpleDateFormat("dd/MM/yyyy");
                Calendar data = Calendar.getInstance();
                int horas = data.get(Calendar.HOUR_OF_DAY);
                int minutos = data.get(Calendar.MINUTE);
                //int segundos = data.get(Calendar.SECOND);

                // OPCAO SAIR DO CHAT
                if (msg.equalsIgnoreCase("bye")) {
                    this.cliente.close();

                    // ENVIAR MENSAGEM PRIVADA
                    //ENVIAR MENSAGEM PRIVADA
                } else if (msg.toLowerCase().startsWith("send -user")) {
//					String nome = msg.substring(9, msg.indexOf(" "));
//					System.out.println(nome);
                    String[] msgArray = msg.split(" ");
                    String nome = msgArray[2];
                    String mensagem = "";
                    for (int i = 0; i < msgArray.length; i++) {
                        if (i > 2) {
                            mensagem = mensagem + " " + msgArray[i];
                        }
                    }
//					String mensagem = msg.substring(msg.indexOf(" "), msg.length());
                    GerenciadorCliente destinatario = clientes.get(nome);

                    if (destinatario == null || mensagem.isEmpty()) {
                        escritor.println("Usuario " + nome + " nao existe");
                    } else {
                        destinatario.getEscritor()
                                .println(cliente.getInetAddress().getHostAddress() + ":"
                                        + cliente.getPort() + "/~" + this.nomecliente + ":" + mensagem + " " + horas
                                        + "h" + minutos + " " + formatador.format(date));
                    }

                    // LISTA TODOS OS USUARIOS
                } else if (msg.equals("list")) {
                    StringBuilder str = new StringBuilder();
                    for (String c : clientes.keySet()) {
                        str.append(c);
                        str.append(",");
                    }
                    str.delete(str.length() - 1, str.length());
                    escritor.println("Nome dos usuarios: " + str.toString());

                    // ENVIAR MENSAGEM PARA TODOS
                } else if (msg.toLowerCase().startsWith("send -all ")) {
                    String mensagem = msg.substring(10, msg.length());

                    for (String c : clientes.keySet()) {

                        GerenciadorCliente destinatario = clientes.get(c);
                        destinatario.getEscritor()
                                .println(cliente.getInetAddress().getHostAddress() + ":" + cliente.getPort() + "/~"
                                        + this.nomecliente + ":" + mensagem + " " + horas + "h" + minutos + " "
                                        + formatador.format(date));
                    }
                } // RENOMEAR NOME DO USUARIO
                else if (msg.toLowerCase().startsWith("rename")) {
                    String novonome = msg.substring(msg.indexOf(" "), msg.length());
                    novonome = novonome.trim();
                    System.out.println(novonome);
                    StringBuilder str = new StringBuilder();
                    GerenciadorCliente destinatario = clientes.get(novonome);
                    for (String c : clientes.keySet()) {
                        str.append(c);
                        GerenciadorCliente nomelista = clientes.get(c);
                        if (nomelista != destinatario) {
                            clientes.remove(this.nomecliente);
                            System.out.println("Nome depois de remover" + novonome);
                            clientes.put(novonome, this);
                            System.out.println(novonome);
                            this.nomecliente = novonome;
                            escritor.println("Nome alterado com sucesso");
                            break;
                        } else if (nomelista == destinatario) {
                            escritor.println("Nome " + novonome + " ja existe");
                            break;
                        }

                    }

                } else {

                    escritor.println("Comando Inexistente");// COMANDO INVALIDO
                }
            }
        } catch (IOException e) {
            System.err.println("O cliente " + this.nomecliente + " saiu do chat");
            clientes.remove(this.nomecliente);// REMOVE O CLIENTE DA LISTA
            e.printStackTrace();
        }
    }

    public PrintWriter getEscritor() {
        return escritor;
    }

    public BufferedReader getLeitor() {
        return leitor;
    }

    /*
	 * public String getNomecliente() { return nomecliente; } public void
	 * setNomecliente(String nomecliente) { this.nomecliente = nomecliente; }
     */
}
