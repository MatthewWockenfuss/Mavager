package org.toast;

public class Launcher {

	public static void main(String[] args) {
		@SuppressWarnings("unused")
		Console c = new Console("Server");

	}

}



/*
 * 				Known Bugs
 * Concurrent modification error sometimes when a player leaves while trying to send data to all clients
 * to fix make queue of data that needs to be sent and each tick the loop will run throgh all in an order
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 */ 
