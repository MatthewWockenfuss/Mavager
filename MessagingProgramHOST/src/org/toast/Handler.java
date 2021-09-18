package org.toast;

import java.awt.Color;

public class Handler {
	
	private GameServer gameServer;
	private Console console;
	

	
	public Color gold,light_blue;
	
	private boolean trace = false;
	private boolean DisplayRawPackets = false;
	
	public Handler(Console console) {
		this.console = console;
		gold = new Color(255,215,0);
		light_blue = new Color(173,216,230);
	}
	
	
	
	
	public Console getConsole() {
		return console;
	}
	public GameServer getGameServer() {
		return gameServer;
	}
	public void setGameServer(GameServer g) {
		this.gameServer = g;
	}
	
	
	
	
	
	public Color Gold() {
		return gold;
	}
	public Color LightBlue() {
		return light_blue;
	}




	
	
	/////////////////////////GETTERS AND SETTERS///////////////////
	public boolean shouldTrace() {
		return trace;
	}
	public void setTrace(boolean trace) {
		this.trace = trace;
	}
	public boolean ShouldDisplayRawPackets() {
		return DisplayRawPackets;
	}
	public void setDisplayRawPackets(boolean DisplayRawPackets) {
		this.DisplayRawPackets = DisplayRawPackets;
	}
	
	

	
}
