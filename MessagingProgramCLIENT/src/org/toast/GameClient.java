package org.toast;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.URL;
import java.net.UnknownHostException;

import org.toast.net.Packet;
import org.toast.utils.Utils;

public class GameClient extends Thread {

	private Handler handler;
	private boolean running = true;
	private boolean isLocalServer;

	private String username;

	private String myLocalIP;
	private String myIP;
	private int myPort;

	private InetAddress serverIP;
	private int serverPort;

	private DatagramSocket socket;

	public GameClient(String username, InetAddress serverIP, int serverPort, boolean isLocalServer, Handler handler) {
		this.username = username;
		this.serverIP = serverIP;
		this.serverPort = serverPort;
		this.isLocalServer = isLocalServer;
		this.handler = handler;
		try {
			socket = new DatagramSocket();
			socket.connect(serverIP, serverPort);
			myLocalIP = InetAddress.getLocalHost().getHostAddress();
			myPort = socket.getLocalPort();

			///// ATTEMPT TO GET PUBLIC IP FROM WEBSITE

			URL url_name = new URL("http://bot.whatismyipaddress.com");

			BufferedReader sc = new BufferedReader(new InputStreamReader(url_name.openStream()));

			// reads system IPAddress
			myIP = sc.readLine().trim();

		} catch (SocketException | UnknownHostException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Send data to server

		if (isLocalServer) {
			sendData(Packet.CONNECT + username + "::" + myLocalIP + "::" + myPort);
			handler.getConsole().getFrame().setTitle(myLocalIP + "    " + myPort);
		} else {
			sendData(Packet.CONNECT + username + "::" + myIP + "::" + myPort);
			handler.getConsole().getFrame().setTitle(myIP + "    " + myPort);
		}
		
		


	}

	public void run() {
		while (running) {
			Packet p = recieveData();
			handlePackets(p);

		} // Run with the game
	}

	public void handlePackets(Packet p) {
		///////////print out if want
		if(handler.shouldDisplayRawPackets())
			handler.getConsole().println("IN:      " + p.getID() + p.getData(), Color.magenta);
		
		
		
		String[] tokens = p.getData().split("::");
		if (p.getID() == Packet.CONNECT) {

			if (tokens.length == 3) {
				String username = tokens[0];
				String ip = tokens[1];
				if (username.equalsIgnoreCase(this.username)) {
					handler.getConsole().println("Connecting to " + serverIP.toString().substring(1), Color.magenta);
				}
				handler.getConsole().println(username + "[" + ip + "] has Connected!", Color.green);
			} else {
				handler.getConsole().println("INVALID LOGIN PACKET", Color.red);
			}

		}else if (p.getID() == Packet.DISCONNECT) {
			handler.getConsole().println(tokens[0] + " has left the server!", Color.red);
		}else if (p.getID() == Packet.FORCEDDISCONNECT) {

			// force this client to disconnect
			handler.getConsole().println("You have been kicked!", Color.red);
			forcedClosed();

		}else if (p.getID() == Packet.MESSAGE) {
			handler.getConsole().println(tokens[1] + " >> " + tokens[0], Color.black);
		}else if (p.getID() == Packet.SERVERCLOSED) {
			handler.getConsole().println("The Server has closed!", Color.red);
			forcedClosed();
		}else if (p.getID() == Packet.KICKED) {
			handler.getConsole().println(p.getData() + " has been kicked!", Color.red);
		}else {
			handler.getConsole().println("[ERROR] Unknown Packet ID recieved from GAME CLIENT CLASS", Color.RED);
		}

	}

	
	
	
	
	
	
	
	
	
	
	// SENDS DATA TO THE SERVER IT IS CURRENTLY CONNECTED TO
	public void chatMessage(String chatMessage) {
		String message = Packet.MESSAGE + chatMessage + "::" + username;
		sendData(message);
	}

	public void sendData(String data) {
		String encryptedData = handler.getCCE().encrypt(data);
		DatagramPacket packet = new DatagramPacket(encryptedData.getBytes(), encryptedData.getBytes().length, serverIP, serverPort);
		if(handler.shouldDisplayRawPackets())
			handler.getConsole().println("OUT:      " + encryptedData, Color.cyan);

		try {
			socket.send(packet);
		} catch (IOException e) {
			e.printStackTrace();
		}
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
		String decryptedPacket = handler.getCCE().decrypt(message);
		
		
		int id = Utils.parseInt(decryptedPacket.substring(0, 2));

		return new Packet(id, decryptedPacket.substring(2));
	}

	// Stop the server, disconnect
	@SuppressWarnings("deprecation")
	public synchronized void myStop() {
		running = false;

		if (isLocalServer) {
			sendData(Packet.DISCONNECT + username + "::" + myLocalIP);
		}else{
			sendData(Packet.DISCONNECT + username + "::" + myIP);
		}

		socket.close();
		this.stop();
	}
	@SuppressWarnings("deprecation")
	public synchronized void forcedClosed() {
		running = false;
		socket.close();
		this.stop();
	}

	/////////////////////////////////// GETTERS AND
	/////////////////////////////////// SETTERS/////////////////////////////////////////////////////////
	public String getUserName() {
		return username;
	}
	public String getMyLocalIP() {
		return myLocalIP;
	}
	public String getMyIP() {
		return myIP;
	}
	public int getMyPort() {
		return myPort;
	}
	public InetAddress getServerIP() {
		return serverIP;
	}
	public int getServerPort() {
		return serverPort;
	}
	public boolean isRunning() {
		return running;
	}

}
