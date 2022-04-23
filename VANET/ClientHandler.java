import java.util.*;
import java.net.*;
import java.io.*;

public class ClientHandler implements Runnable{
	public static ArrayList<ClientHandler> clientHandlers=new ArrayList<>();
	private Socket socket;
	private BufferedReader bufferedReader;
	private BufferedWriter bufferedWriter;
	private String vinnum;
	private String priority;
	public ClientHandler(Socket socket){
		try{
			this.socket=socket;
			this.bufferedWriter=new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			this.bufferedReader=new BufferedReader(new InputStreamReader(socket.getInputStream()));
			this.vinnum=bufferedReader.readLine();
			this.priority=bufferedReader.readLine();
			clientHandlers.add(this);
			if(priority.equals("0")){
				Server.light="GREEN";
				if(Server.time<=15){
					Server.time+=40;
				}
				else if(Server.time<=30){
					Server.time+=20;				
				}
				multicastMessage("Signal Light GREEN for "+Server.time+" seconds. Give way for emergency services.");
				new Thread(new Runnable(){
					@Override
					public void run(){
						while(Server.time!=0){
							try{
								Thread.sleep(1000);
								Server.time--;
							}catch(Exception e){}
						}
					}
				}).start();
			}
			if(Server.light.equals("RED")){
				if(Server.num>=3){
					Server.light="GREEN";
					Server.time=30;
					multicastMessage("Signal Light GREEN for 30 seconds.");
				}
				else if(Server.num>3 && Server.num<=5){
					Server.light="GREEN";
					Server.time=50;
					multicastMessage("Signal Light GREEN for 50 seconds.");
				}
				else if(Server.num>5){
					Server.light="GREEN";
					Server.time=60;
					multicastMessage("Signal Light GREEN for 60 seconds.");
				}			
				new Thread(new Runnable(){
					@Override
					public void run(){
						while(Server.time!=0){
							try{
								Thread.sleep(1000);
								Server.time--;
							}catch(Exception e){}
						}
						Server.light="RED";
					}
				}).start();
			}
			else{
				if(Server.num>7){
					Server.time+=20;
					multicastMessage("Signal Light GREEN for "+Server.time+" seconds.");
				}
				new Thread(new Runnable(){
					@Override
					public void run(){
						while(Server.time!=0){
							try{
								Thread.sleep(1000);
								Server.time--;
							}catch(Exception e){}
						}
						Server.light="RED";
					}
				}).start();
			}
			bufferedWriter.write("STMS : Signal Light is "+Server.light+(Server.light.equals("RED")?".":(" for "+Server.time+" seconds.")));
			this.bufferedWriter.newLine();
			this.bufferedWriter.flush();
		}catch(IOException e){
			closeEverything(socket, bufferedReader, bufferedWriter);
		}
	}
	@Override
	public void run(){
		String messageFromClient;
		while(socket.isConnected()){
			try{
				messageFromClient=bufferedReader.readLine();
				if(messageFromClient.equalsIgnoreCase("Accident")){
					bufferedWriter.write("Emergency Services Alerted");
					this.bufferedWriter.newLine();
					this.bufferedWriter.flush();
				}
				/*if(messageFromClient.equalsIgnoreCase("exit")){
					throw new IOException();	
				}*/
				//broadcastMessage(messageFromClient);
			}catch(IOException e){
				closeEverything(socket, bufferedReader, bufferedWriter);
				break;
			}
		}
	}
	public void broadcastMessage(String messageToSend){
		for(ClientHandler clientHandler:clientHandlers){
			try{
				//if(!clientHandler.clientUserName.equlas(clientUserName))
				clientHandler.bufferedWriter.write(messageToSend);
				clientHandler.bufferedWriter.newLine();
				clientHandler.bufferedWriter.flush();
			}catch(IOException e){
				closeEverything(socket, bufferedReader, bufferedWriter);
			}
		}
	}
	public void multicastMessage(String messageToSend){
		for(ClientHandler clientHandler:clientHandlers){
			try{
				if(!clientHandler.vinnum.equals(vinnum)){
					clientHandler.bufferedWriter.write(messageToSend);
					clientHandler.bufferedWriter.newLine();
					clientHandler.bufferedWriter.flush();
				}
			}catch(IOException e){
				closeEverything(socket, bufferedReader, bufferedWriter);
			}
		}
	}
	public void removeClientHandler(){
		clientHandlers.remove(this);
		//System.out.println(vinnum+ " left");
		Server.num--;
	}
	public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter){
		removeClientHandler();
		try{
			if(bufferedReader!=null){
				bufferedReader.close();
			}
			if(bufferedWriter!=null){
				bufferedWriter.close();
			}
			if(socket!=null){
				socket.close();
			}
		}catch(IOException e){
			e.printStackTrace();		
		}
	}
}