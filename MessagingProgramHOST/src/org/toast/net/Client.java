package org.toast.net;

import java.net.InetAddress;

import org.toast.Handler;

public class Client {
	
	private Handler handler;
	private InetAddress ip;
	private int port;
	
	
	
	private String username;
	private boolean isActive = true;
	

	public Client(Handler handler,InetAddress ip,int port,String username) {
		this.handler = handler;
		this.ip = ip;
		this.port = port;
		this.username = username;
		 
		
		
	}
	
	public InetAddress getIp() {
		return ip;
	}
	public String getUsername() {
		return username;
	}
	public int getPort() {
		return port;
	}
	public boolean isActive() {
		return isActive;
	}
	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}
	public Handler getHandler() {
		return handler;
	}

	
	
	
	
}
