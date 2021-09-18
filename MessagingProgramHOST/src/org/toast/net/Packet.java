package org.toast.net;

public class Packet {
	
	public static final int INVALID = 99;
	public static final int CONNECT = 10;
	public static final int DISCONNECT = 11;
	public static final int FORCEDDISCONNECT = 12;
	public static final int MESSAGE = 13;
	public static final int SERVERCLOSED = 14;
	public static final int KICKED = 15;
	
	
	private int ID;
	private String data;
	
	public Packet(int ID,String data) {
		this.ID = ID;
		this.data = data;
	}

	public String getData() {
		return data;
	}
	public int getID() {
		return ID;
	}
	
	
	
	
	
	
	
	
	
	
}
