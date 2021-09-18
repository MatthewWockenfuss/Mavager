package org.toast;

import java.awt.Color;
import java.awt.HeadlessException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;

import org.toast.net.Client;
import org.toast.net.Packet;
import org.toast.utils.Utils;

public class GameServer implements Runnable{
	
	private Thread thread;
	private boolean running = false;
	private Handler handler;
	
	//NET
	private Server socketServer;
	
	private String myLocalIP;
	private String myIP;
	private int port;
	
	
	//FROM TEXT FILE
	private String name;			//Name of Server
	private int max_num_of_player;
	private String key;
	
	public GameServer(String nameOfServerFolder,Handler handler) {
		this.handler = handler;
		loadServerFromFile(nameOfServerFolder + "/server.txt"); 
		handler.getConsole().println(name + " on " + port + ", with a max of " + max_num_of_player + " users!", Color.green); 
		
		try {
			myLocalIP = InetAddress.getLocalHost().getHostAddress();
			
			///// ATTEMPT TO GET PUBLIC IP FROM WEBSITE

			URL url_name = new URL("http://bot.whatismyipaddress.com");

			BufferedReader sc = new BufferedReader(new InputStreamReader(url_name.openStream()));

			// reads system IPAddress
			myIP = sc.readLine().trim();
			
			
			
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {  
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		

		
		
		
	}
	
	
	
	
	
	public void run() {
		int fps = 60;
		double timePerTick = 1000000000 / fps;
		double delta = 0;
		long now;
		long lastTime = System.nanoTime();
		long timer = 0;
		@SuppressWarnings("unused")
		int ticks = 0;
		
		while(running) {
			now = System.nanoTime();
			delta += (now - lastTime) / timePerTick;
			timer += now - lastTime;
			lastTime = now;
			if(delta >= 1) {
				removeInactive();
				ticks++;
				delta--;
			}

			
			try {
				Thread.sleep(2);
			} catch (InterruptedException e) {
				e.printStackTrace();
			} 
			
			if(timer >= 1000000000) {
				ticks = 0;
				timer = 0;
			}
			

		}
		stop();
		
		
	}
	

	
	public void removeInactive() {
		for(int i = 0;i < socketServer.getConnectedClients().size();i++) {
			if(!socketServer.getConnectedClients().get(i).isActive()) {
				socketServer.getConnectedClients().remove(i);
			}
		}
	}
	
	public synchronized void start() throws HeadlessException, UnknownHostException { 
		if(running)
			return;
		running = true;
		thread = new Thread(this); 
		thread.start();
		socketServer = new Server(handler);		//STARTS ACCEPTING PACKETS
		//System.out.println("starts accepting packets");
		socketServer.start();
		
	}
	@SuppressWarnings("deprecation")
	public synchronized void stop() {
		if(!running)
			return;
		running = false;
		try {
			socketServer.getSocket().close();
			socketServer.stop();
			socketServer = null;
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	public void closeServer() {
		//the server has been closed, tell clients and close
		
		socketServer.sendDataToAllClients(Packet.SERVERCLOSED + "SERVER HAS CLOSED");
		handler.getConsole().println("The server has closed!",Color.white);
		//all clients will disconnect
		for(Client c : socketServer.getConnectedClients()) {
			c.setActive(false);
		}
		//stop();
		
		
		
	}
	
	
	
	public void loadServerFromFile(String path) {
		String file = Utils.loadFileAsString(path);
		String[] lines = file.split("\n");//splits file into each line
		
		for(String line : lines) {//loops through each line
			String[] tokens = line.split("=");//each line gets split into two parts on either side of the equal sign
			
			if(tokens[0].equalsIgnoreCase("Server Name")) {
				name = tokens[1];
			}else if(tokens[0].equalsIgnoreCase("port")) {
				port = Utils.parseInt(tokens[1]);
			}else if(tokens[0].equalsIgnoreCase("Maximum Number of Players")) {
				max_num_of_player = Utils.parseInt(tokens[1]);
			}else if(tokens[0].equalsIgnoreCase("key")) {
				key = tokens[1];
			}
			
			
			
		}
		
		
		
	}
	
	///////////////////////////////////GETTERS AND SETTERS/////////////////////////////////////////////////////////
	public String getName() {
		return name;
	}
	public int getMaxNumOfPlayers() {
		return max_num_of_player;
	}
	
	public int getPort() {
		return port;
	}
	public Server getSocketServer() {
		return socketServer;
	}

	public boolean isRunning() {
		return running;
	}
	public String getMyLocalIP() {
		return myLocalIP;
	}
	public String getMyIP() {
		return myIP;
	}
	public String getKey() {
		return key;
	}
	
}
