/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servidor;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Servidor {
	 
	public static void main(String[] args)  {
		ServerSocket servidor= null;	
	try{
		System.out.println("Startando o servidor");
		servidor= new ServerSocket(9090);
		System.out.println("Servidor startado");
		
		while(true){
			Socket cliente = servidor.accept();
			new GerenciadorCliente(cliente);
		}
	}catch(IOException e){
	try{
		if(servidor!=null)
			servidor.close();
	} catch(IOException el){}
	System.err.println("Erro ao conectar ao servidor");
	e.printStackTrace();
	}
	}

}