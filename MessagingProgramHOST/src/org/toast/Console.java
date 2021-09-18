package org.toast;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.UnknownHostException;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.UIManager;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import org.toast.net.Client;
import org.toast.net.Packet;

public class Console {
		
	private JFrame frame;
	private JTextPane console;
	private JTextField input;
	private JScrollPane scrollpane;
	private boolean timeToReset = false;
	
	private StyledDocument document;
	

	
	
	ArrayList<String> recent_used = new ArrayList<String>();
	int recent_used_id = 0;
	int recent_used_maximum = 10;
	@SuppressWarnings("unused")
	private String title;
	
	private GameServer server;
	private Handler handler;
	
	public Console(String title) {
		handler = new Handler(this);
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}catch(Exception ex) {}
		this.title = title;
		frame = new JFrame(title);
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.addWindowListener(new WindowAdapter() {
		    @Override
		    public void windowClosing(WindowEvent event) {
		        

		    	if(server != null) {
		        	server.closeServer();
		        }
		    	
		    	frame.dispose();
		        System.exit(0);
		    }
		});
		
		
		console = new JTextPane();
		console.setEditable(false);
		console.setFont(new Font("Segoe UI",Font.PLAIN,14)); 
		console.setOpaque(false);
		
		document = console.getStyledDocument();
		
		
		input = new JTextField();
		//input.setBorder(null);
		input.setEditable(true); 
		input.setFont(new Font("Segoe UI",Font.PLAIN,14));
		input.setForeground(Color.WHITE);
		input.setCaretColor(Color.WHITE);
		input.setOpaque(false); 
		
		input.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				String text = input.getText();
				if(text.length() > 1) {
					recent_used.add(text);
					recent_used_id = 0;
					doCommand(text); //perform command
					scrollBottom();
					input.setText(""); 
				}
			}
			
		});
		input.addKeyListener(new KeyListener() {

			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_UP) {
					if(recent_used_id < (recent_used_maximum - 1) && recent_used_id < (recent_used.size() - 1)) {
						recent_used_id++;
					}
					if(recent_used_id == 1 && timeToReset) {
						recent_used_id = 0;
						timeToReset = false;
					}
					input.setText(recent_used.get(recent_used.size() - 1 - recent_used_id));
				}
				else if(e.getKeyCode() == KeyEvent.VK_DOWN) {
					if(recent_used_id > 0)
						recent_used_id--;
					input.setText(recent_used.get(recent_used.size() - 1 - recent_used_id));
				}
				//System.out.println("id" + recent_used_id);
			}

			public void keyReleased(KeyEvent e) {
				
			}

			public void keyTyped(KeyEvent e) {

			}
			
		});
		
		
		
		scrollpane = new JScrollPane(console);
		scrollpane.setBorder(null); 
		scrollpane.setOpaque(false); 
		scrollpane.getViewport().setOpaque(false);
		
		frame.add(input, BorderLayout.SOUTH);
		frame.add(scrollpane, BorderLayout.CENTER);
		
		frame.getContentPane().setBackground(new Color(50,50,50));
		
		frame.setSize(660, 350);
		frame.setLocationRelativeTo(null); 
		frame.setResizable(false); 
		frame.setVisible(true);
		
		//println("Welcome!",false,Color.green);  //PRINTS A WELCOME MESSAGE TO THE CONSOLE
		
	}
	
	public void doCommand(String command) {
			final String[] commands = command.split(" ");
			timeToReset = true;
			
			try {
				
				if(commands[0].equalsIgnoreCase("clear") || commands[0].equalsIgnoreCase("cls"))
				
						clear();
				
				else if(commands[0].equalsIgnoreCase("newserver")) {
					
						newserver(commands);
					
				}else if(commands[0].equalsIgnoreCase("myinfo")) {
					
					println("Public IP: " + server.getMyIP(),Color.cyan);
					println("Local IP:  " + server.getMyLocalIP(),Color.cyan);
					
				}else if(commands[0].equalsIgnoreCase("help")) {
					
					help();
				
			}else if(commands[0].equalsIgnoreCase("online")) {	
					
						online();
				
				}else if(commands[0].equalsIgnoreCase("exit")) {
					
						exit();
				
				}else if(commands[0].equalsIgnoreCase("kick")) {
					
						kick(commands); 
				
				}else if(commands[0].equalsIgnoreCase("kickall")) {
					
						kickall(commands); 
					
				}else if(commands[0].equalsIgnoreCase("close")) {
					
						close();

				}else if(commands[0].equalsIgnoreCase("settings") || commands[0].equalsIgnoreCase("setting")) {
					
						settings();
				
				}else {
					//println(command,trace,Color.white);
					println(command + " - I don't recognize that command, try 'help'",false,new Color(255,0,0));
				}
	
				
				
				
				
			}catch(Exception e) {
				println("[ERROR] " + e.getMessage(),true,new Color(255,0,0));
			}
			
			
			
	}
	
	public void scrollTop() {
		console.setCaretPosition(0); 
	}
	public void scrollBottom() {
		console.setCaretPosition(console.getDocument().getLength());
	}
	
	
	public void print(String s) {
		print(s,handler.shouldTrace(),Color.white);
	}
	public void print(String s,Color c) {
		print(s,handler.shouldTrace(),c);
	}
	public void print(String s,boolean trace) {
		print(s,trace,Color.white);
	}
	public void print(String s,boolean trace, Color c) {
		Style style = console.addStyle("Style", null);
		StyleConstants.setForeground(style, c);
		
//		if(trace) {
//		Throwable t = new Throwable();
//		StackTraceElement[] elements = t.getStackTrace();
//		String caller = elements[0].getClassName();
//		
//		s = caller + " -> " + s;
//		}
		
		try {
			document.insertString(document.getLength(), s, style);
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		scrollBottom();
		
		
	}
	
	public void println(String s) {
		println(s,handler.shouldTrace(),Color.white);
	}
	public void println(String s,Color color) {
		println(s,handler.shouldTrace(),color);
	}
	public void println(String s,boolean trace) {
		println(s,trace,Color.white);
	}
	public void println(String s,boolean trace,Color c) {
		print(s + "\n",trace,c);
	}
	
	


	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/////////////////////////////////////////Command Functions////////////////////////////////////////////
	public void help() {
		println("help - shows this list",Color.orange);
		println("myinfo - Gets info on Computer",Color.orange);
		println("newserver <name of server folder> - creates a new server listening on specified port",Color.orange);
		println("online - shows all players online",Color.orange);
		println("kick <Username> - kicks the player",Color.orange);
		println("kickall - kicks all players online",Color.orange);
		println("settings - adjust settings",Color.orange);
		println("clear/cls - Clears the Screen",Color.orange);
		println("close - closes the server",Color.orange);
		println("exit - Exits Console (also closes server)",Color.orange);
	}
	
	
	public void newserver(String[] commands) {

		if(server != null) {
//			for(Client c : handler.getGameServer().getSocketServer().getConnectedClients()) {
//				handler.getGameServer().getSocketServer().sendData((Packet.INVALID + "The Server Has Closed!").getBytes(),c.getIp(),c.getPort()); 
//			}
			
			server.stop();
			
			
		}
		if(commands.length <= 2) {
			server = new GameServer(commands[1],handler);
			handler.setGameServer(server); 
			try {
				server.start();
			} catch (HeadlessException e) {
				e.printStackTrace();
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}
			//println("New Server " + "'" + server.getName() + "'" + " created on port " + server.getPort(),false,Color.green);
			frame.setTitle(server.getName());
		}

	
	}
	public void online() {
		
		if(server != null) {
			String message = "Users Online > ";
			
			if(handler.getGameServer().getSocketServer().getConnectedClients().size() == 0) {
				message = "There are currently no players online!";
			}else if(handler.getGameServer().getSocketServer().getConnectedClients().size() == 1) {
				message += handler.getGameServer().getSocketServer().getConnectedClients().get(0).getUsername();
			}else {
				for(Client c : handler.getGameServer().getSocketServer().getConnectedClients()) {
					message += c.getUsername() + " ";
				}
			}
			println(message, Color.green);
		}else {
			println("No Servers Currently running!", Color.red);
		}
		
	}
	
	
	
	public void kick(String[] commands) {
		if(server != null) {
			if(commands.length > 2) {
				println("Too Many Arguments!", Color.RED);
			}else if(commands.length <= 1) {
				println("Please specify the username /kick <username>", Color.RED);
			}else {
				for(Client c : handler.getGameServer().getSocketServer().getConnectedClients()) {
					if(commands[1].equalsIgnoreCase(c.getUsername())) {
						println(c.getUsername() + " has been kicked!",Color.red); 
						c.setActive(false);
						//send the person the forced disconnet packet
						handler.getGameServer().getSocketServer().sendData((Packet.FORCEDDISCONNECT + ""), c.getIp(), c.getPort()); 
						//tell all the other players that this person has been kicked
						handler.getGameServer().getSocketServer().sendDataToAllClients((Packet.KICKED + c.getUsername()));
						return;
					}
				}
			}

			println("Player " + commands[1] + " not found!", Color.RED);
			
			
		}else {
			println("No Servers currently running!", Color.red);
		}
	
	}
	public void kickall(String[] commands) {
		if(server != null) {
			if(commands.length >= 2) {
				println("Too Many Arguments!", Color.RED);
			}else {
				for(Client c : handler.getGameServer().getSocketServer().getConnectedClients()) {
						println(c.getUsername() + " has been kicked!",Color.red); 
						c.setActive(false);
						//send the person the forced disconnet packet
						handler.getGameServer().getSocketServer().sendData((Packet.FORCEDDISCONNECT + ""), c.getIp(), c.getPort()); 
						//tell all the other players that this person has been kicked
						handler.getGameServer().getSocketServer().sendDataToAllClients((Packet.KICKED + c.getUsername()));
						return;
				}
			}

			println("Player " + commands[1] + " not found!", Color.RED);
			
			
		}else {
			println("No Servers currently running!", Color.red);
		}
	
	}
	
	public void settings() {
		
		//adjust the settings of the console using the joption panes
		
		
		String[] options = {"Yes"," No"};
		
		String current = "No";
		if(handler.ShouldDisplayRawPackets())
			current = "Yes";
		
		
		int choice = JOptionPane.showOptionDialog(null, "Currently: " + current, "Settings - Print Raw Packets", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
		if(choice == 0) {
			handler.setDisplayRawPackets(true);
		}else {
			handler.setDisplayRawPackets(false);
		}
		
		
		
		///////////////////////////////////////////////////////////////// Tracing
		
		String currentTrace = "No";
		if(handler.shouldTrace())
			currentTrace = "Yes";
		
		
		int choiceTrace = JOptionPane.showOptionDialog(null, "Currently: " + currentTrace, "Settings - Print Stack Trace", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
		if(choiceTrace == 0) {
			handler.setTrace(true);
		}else {
			handler.setTrace(false);
		}
		
		
		
		println(" > Adjusted Settings",Color.cyan); 
		
		
		
		
		
	
	}
	
	public void clear() {
		try {
			document.remove(0, document.getLength());
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	public void close() {
		if(server != null) {
			handler.getGameServer().closeServer();
		}else {
			exit();
		} 
	}
	public void exit() {
		if(server != null) {
			handler.getGameServer().closeServer();
		}else {
			System.exit(0);
		} 
	
	}
	






















}
