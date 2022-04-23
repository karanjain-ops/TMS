import java.io.*;
import java.net.*;

public class Server{
	public ServerSocket serverSocket;	
	static String light="RED";
	static int num=0;
	static int time=0;
	public Server(ServerSocket serverSocket){
		this.serverSocket=serverSocket;
	}

	public void startServer(){
		try{
			while(!serverSocket.isClosed()){
				Socket socket=serverSocket.accept();
				num++;
				//System.out.println("A new client has connected!");
				ClientHandler clientHandler=new ClientHandler(socket);
				Thread thread=new Thread(clientHandler);
				thread.start();
			}
		}catch(IOException e){
		}
	}
	public void closedServerSocket(){
		try{
			if(serverSocket!=null){
				serverSocket.close();
			}
		}catch(IOException e){
			e.printStackTrace();
		}
	}

	public static void main(String[] args){
		try{
			ServerSocket serverSocket=new ServerSocket(1234);
			Server server=new Server(serverSocket);
			server.startServer();
		}
		catch(IOException e){}
	}
}