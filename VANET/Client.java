import java.util.*;
import java.net.*;
import java.io.*;

public class Client{
	private Socket socket;
	private BufferedReader bufferedReader;
	private BufferedWriter bufferedWriter;
	private String vin;
	private String priority;

	public Client(Socket socket, String vin, String priority){
		try{
			this.socket=socket;
			this.bufferedWriter=new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			this.bufferedReader=new BufferedReader(new InputStreamReader(socket.getInputStream()));
			this.vin=vin;
			this.priority=priority;
		}catch(IOException e){
			closeEverything(socket, bufferedReader, bufferedWriter);
		}
	}
	public void sendMessage(){
		try{
			bufferedWriter.write(vin);
			bufferedWriter.newLine();
			bufferedWriter.flush();
			bufferedWriter.write(priority);
			bufferedWriter.newLine();
			bufferedWriter.flush();
			Scanner scanner=new Scanner(System.in);
			while(socket.isConnected()){
				String messageToSend=scanner.nextLine();
				bufferedWriter.write(messageToSend);
				bufferedWriter.newLine();
				bufferedWriter.flush();
				if(messageToSend.equalsIgnoreCase("exit")){
					System.exit(0);
				}
			}
		}catch(IOException e){
			closeEverything(socket, bufferedReader, bufferedWriter);
		}
	}
	public void ListenForMessage(){
		new Thread(new Runnable(){
			@Override
			public void run(){
				String messageFromSTMS;
				while(socket.isConnected()){
					try{
						messageFromSTMS=bufferedReader.readLine();
						System.out.println(messageFromSTMS);
					}catch(IOException e){
						closeEverything(socket, bufferedReader, bufferedWriter);
					}
				}
			}
		}).start();
	}
	public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter){
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
	public static void main(String[] args){
		try{
			Scanner scanner=new Scanner(System.in);
			System.out.print("Vin: ");
			String vin=scanner.nextLine();
			System.out.print("Priority: ");
			String priority=scanner.nextLine();
			Socket socket=new Socket("localhost", 1234);
			Client client= new Client(socket,vin,priority);
			client.ListenForMessage();
			client.sendMessage();
		}catch (IOException e){}
	}
}
