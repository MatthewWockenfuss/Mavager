package org.toast;

import java.awt.Color;

import org.crypto.CeasarCipherEngine;

public class Handler {
	
	private GameClient gameClient;
	private Console console;
	private Color gold,light_blue;
	
	CeasarCipherEngine ccE;
	
	/////Console settings
	private boolean displayRawPackets = false;
	
	
	
	
	public Handler(Console console) {
		this.console = console;
		gold = new Color(255,215,0);
		light_blue = new Color(173,216,230);
	}
	
	
	
	
	public Console getConsole() {
		return console;
	}
	public GameClient getGameClient() {
		return gameClient;
	}
	public void setGameClient(GameClient g) {
		this.gameClient = g;
	}
	
	public Color Gold() {
		return gold;
	}
	public Color LightBlue() {
		return light_blue;
	}




	
	
	
	public boolean shouldDisplayRawPackets() {
		return displayRawPackets;
	}




	public void setDisplayRawPackets(boolean displayRawPackets) {
		this.displayRawPackets = displayRawPackets;
	}
	public void setCCEngine(CeasarCipherEngine ccE) {
		this.ccE = ccE;
	}
	public CeasarCipherEngine getCCE() {
		return ccE;
	}
	
	
	
	
	
	
	
}
