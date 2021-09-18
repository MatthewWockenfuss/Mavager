package org.toast;

import java.awt.Color;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;

import org.toast.net.Client;
import org.toast.net.Packet;
import org.toast.utils.Utils;

public class Server extends Thread{				//This is the socket for the Console,
	
	private Handler handler;
	private ArrayList<Client> playersConnected;
	
	 
	private DatagramSocket socket;
	
	
	boolean running = false;
	
	
	
	
	public Server(Handler handler) {
		this.handler = handler;
		playersConnected = new ArrayList<Client>();
		
		try {
			this.socket = new DatagramSocket(handler.getGameServer().getPort());
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}
	
	public void run() {
		
		while(handler.getGameServer().isRunning()) {
			Packet p = recieveData();
			//handler.getConsole().printToConsole(p.getData(), Color.green); 
			//System.out.println(p.getID() + p.getData() + ""); 
			handlePackets(p);
 
			  
		}//Run with the game
	}
	
	public Packet recieveData() {
		byte[] data = new byte[1024];
		DatagramPacket packet = new DatagramPacket(data, data.length);
		try {
			socket.receive(packet);
		} catch (IOException e) {
			e.printStackTrace();
		}
		String message = new String(packet.getData()).trim();
	
		
		int id = Utils.parseInt(message.substring(0, 2));
		if(id == 0 || id == 1)		//ADDS the port of the packet if its a login or disconnect packet
			message += "::" + packet.getPort();
		
		return new Packet(id,message.substring(2));
	}
	public void handlePackets(Packet p) {
		if(handler.ShouldDisplayRawPackets())
			handler.getConsole().println(p.getID() + p.getData(), Color.cyan); 
		if(p.getID() == Packet.CONNECT) {
			
			String[] tokens = p.getData().split("::");
			if(tokens.length == 3) {					//Checks if packet is built correctly
				InetAddress playerIP = null;	
				int playerPort = 0; 
				try {
					playerIP = InetAddress.getByName(tokens[1]);
					playerPort = Integer.parseInt(tokens[2]);
				} catch (UnknownHostException e1) {
					e1.printStackTrace();
				}
				if(playersConnected.size() == handler.getGameServer().getMaxNumOfPlayers()) {
					handler.getConsole().println("User[" + tokens[1] + "] attempted to connect but the server is Full!", Color.RED);
					sendData(("" + Packet.DISCONNECT + playerIP + playerPort + "The Server is Full!"),playerIP,playerPort);
				}else {
					
					if(isUniqueUserName(tokens[0])) {	//if the packet is built correctly and the username isnt already used, and the server isnt at cap, then they can join
						
						Client c;
						try {
							
							c = new Client(handler,InetAddress.getByName(tokens[1]),Integer.parseInt(tokens[2]),tokens[0]);
							playersConnected.add(c);
							sendDataToAllClients(Packet.CONNECT + p.getData()); 
							handler.getConsole().println(c.getUsername() + " has joined the server!", Color.green); 
							
							
						} catch (NumberFormatException | UnknownHostException e) {
							handler.getConsole().println("There was an error finding the IP or Parsing the port when " + tokens[0] + " connected!", Color.RED); 
							e.printStackTrace();
						}
					}else {
						handler.getConsole().println("A User[" + playerIP.getHostAddress() + "] tried to connect with the name '"+ tokens[0] + "' which already exists!", Color.RED);
						sendData(("" + Packet.DISCONNECT + tokens[0] + "::" + playerIP + "::" + playerPort + "::" + "That Username is already is use!"),playerIP,playerPort); 
					}
					
					
					
					
				}
			}else {
				handler.getConsole().println("else catch in Server handling connect packet!", Color.RED);
			}
			

			
	}else if(p.getID() == Packet.DISCONNECT) {
			String[] tokens = p.getData().split("::");
			for(Client c : playersConnected) {
				if(c.getUsername().equalsIgnoreCase(tokens[0])) {
					c.setActive(false);
					handler.getConsole().println(c.getUsername() + " has left D:",Color.red);
					sendDataToAllClients(p.getID() + p.getData());
				}
				
			}
			
		}else if(p.getID() == Packet.MESSAGE) {
			String[] tokens = p.getData().split("::");
			
			String message = tokens[0];
			String username = tokens[1];
			if(message.equalsIgnoreCase("")) {
				return;
			}
			for(Client c : playersConnected) {
				if(c.getUsername().equals(username)) {
					
					if(handler.shouldTrace()) {
						
						handler.getConsole().print(c.getUsername(), Color.ORANGE);
						handler.getConsole().print("[" + c.getIp().toString().substring(1) + "]", Color.white); 
						handler.getConsole().println(" > " + message, Color.ORANGE);
						
					}else {
						handler.getConsole().println(c.getUsername() +  " > " + message, Color.ORANGE);
					}
					
					
					 
				}
				
				sendData((Packet.MESSAGE + message + "::" + username), c.getIp(), c.getPort()); 
				
				
			}
			
			
		}
		
		
	}

	
	


	
	
	


	
	public boolean isUniqueUserName(String username) {
		for(Client c : playersConnected) {
			if(c.getUsername().equals(username)) { 
				return false;
			}
		}
		
		return true;
		
	}
	
	
	
	

	
	
	
	
	public void sendData(String data,InetAddress ipAddress,int port) {
		DatagramPacket packet = new DatagramPacket(data.getBytes(), data.getBytes().length, ipAddress, port);
		try {
			this.socket.send(packet);
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
	public void sendDataToAllClients(String data) {
		for(Client c : playersConnected) {
			sendData(data,c.getIp(),c.getPort());
		}
	}
	
	
	
	///////////////////////////////////////////////////GETTERS AND SETTERS/////////////////////////////////////////////////////////////////////////
	public ArrayList<Client> getConnectedClients() {
		return playersConnected; 
	}
	public DatagramSocket getSocket() {
		return socket;
	}
	 
	

	



	
	
	
	
	
	
	
	
	
	
	


}
