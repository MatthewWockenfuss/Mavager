package org.toast;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.InetAddress;
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

import org.crypto.CeasarCipherEngine;
import org.toast.utils.Utils;

public class Console {
		
	private JFrame frame;
	private JTextPane console;
	private JTextField input;
	private JScrollPane scrollpane;
	private boolean timeToReset = false;
	
	private StyledDocument document;
	
	boolean trace = false;
	
	ArrayList<String> recent_used = new ArrayList<String>();
	int recent_used_id = 0;
	int recent_used_maximum = 10;
	@SuppressWarnings("unused")
	private String title;
	
	private GameClient client;
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
		        
		    	//if we are connected to a server, than we want to make sure to tell the server we have closed the program
		    	if(client != null) {
		        	client.myStop();
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
		input.setForeground(Color.BLACK);
		input.setCaretColor(Color.BLACK);
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
		
		frame.getContentPane().setBackground(Color.white);
		
		frame.setSize(660, 350);
		frame.setLocationRelativeTo(null); 
		frame.setResizable(false); 
		frame.setVisible(true);
		
		println("Version 1.0.0 Messaging Client",false,Color.black);  //PRINTS A WELCOME MESSAGE TO THE CONSOLE
		
	}
	
	public void doCommand(String command) {
			
			//if the socket was closed and is not running, then delete the object so we can recreate it.
			if(client != null && !client.isRunning()) {
				client = null;
			}
		
		
		
			final String[] commands = command.split(" ");
			timeToReset = true;
			
			try {
				
				if(commands[0].equalsIgnoreCase("clear") || commands[0].equalsIgnoreCase("cls"))///////////////////////////////////
					clear();
				else if(commands[0].equalsIgnoreCase("help")) {////////////////////////////////////////////////////////////////////
					println("help - shows this list",false,Color.black);
					println("myinfo - Gets info on Computer",false,Color.black);
					println("settings - adjust console settings",false,Color.black);
					println("connect <ip> <port> - Joins server",false,Color.black);
					println("disconnect - Leave the currently connected server",false,Color.black);
					println("clear/cls - Clears the Screen",false,Color.black);
					println("exit - exits the program",false,Color.black);

				}else if(commands[0].equalsIgnoreCase("connect")) {//////////////////////////////////////////////////////////////////
						if(commands.length == 3) {
							
							String username = JOptionPane.showInputDialog("Please Enter a Unique Username!").trim();
							
							//check to see if the username has spaces or ::
							
							if(username.equalsIgnoreCase("")) {
								
							}else if(username.contains("::") || username.contains(" ")){ 
								println("No Spaces or \"::\" allowed!",Color.red);
							}else {
								
								
								String[] options = {"Local","Public"};
								
								int choice = JOptionPane.showOptionDialog(null, "Please Choose where the server is located!", "", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]); 
								boolean isLocalServer = false;
								if(choice == 0)
									isLocalServer = true; 
								
								int keySize = Utils.parseInt(JOptionPane.showInputDialog("Please Enter Key"));
								CeasarCipherEngine ccE = new CeasarCipherEngine(keySize, handler);
								handler.setCCEngine(ccE); 
								
								
								
								client = new GameClient(username,InetAddress.getByName(commands[1]),Utils.parseInt(commands[2]),isLocalServer,handler);
								client.start();
							}
							
							

							
						}else {
							println("Not Enough Arguments!", Color.red);
						}
				}else if(commands[0].equalsIgnoreCase("myinfo")) {//////////////////////////////////////////////////////////////////////
					
					if(client != null) {
						println("Public IP: " + client.getMyIP(),Color.orange);
						println("Local IP:  " + client.getMyLocalIP(),Color.orange);
						println("");
						println("Server IP: " + client.getServerIP().toString().substring(1),Color.orange);
						println("Server Port: " + client.getServerPort(),Color.orange);
					}
					


				}else if(commands[0].equalsIgnoreCase("exit") || commands[0].equalsIgnoreCase("/exit")) {///////////////////////////////
					System.exit(0); 
				}else if(commands[0].equalsIgnoreCase("leave") || commands[0].equalsIgnoreCase("disconnect")){ 
					println("You have left the server!", Color.red);
					client.myStop();
					client = null;
				}else if(commands[0].equalsIgnoreCase("settings") || commands[0].equalsIgnoreCase("setting")){ 
					
					//adjust the settings of the console using the joption panes
					
					
					String[] options = {"Yes"," No"};
					
					String current = "No";
					if(handler.shouldDisplayRawPackets())
						current = "Yes";
					
					
					int choice = JOptionPane.showOptionDialog(null, "Currently: " + current, "Settings - Print Raw Packets", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
					if(choice == 0) {
						handler.setDisplayRawPackets(true);
					}else {
						handler.setDisplayRawPackets(false);
					}
					
					
					println(" > Adjusted Settings",Color.cyan); 
					
					
					
					
					
				}else {
					if(client != null) {
						client.chatMessage(command);
					}else {
						println("You need a server to chat!",Color.red);
					}
					
				}
				
				
				////////////////////////////////////////////////////////END OF COMMANDS
				
			}catch(Exception e) {
				println("[ERROR] " + e.getMessage(),true,Color.RED); 
			}
			
			
			
	}

	
	
	
	
	
	
	
	
	public void scrollTop() {
		console.setCaretPosition(0); 
	}
	public void scrollBottom() {
		console.setCaretPosition(console.getDocument().getLength());
	}
	
	

	
	public void print(String s) {
		print(s,trace,Color.white);
	}
	public void print(String s,Color c) {
		print(s,trace,c);
	}
	public void print(String s,boolean trace) {
		print(s,trace,Color.white);
	}
	public void print(String s,boolean trace, Color c) {
		Style style = console.addStyle("Style", null);
		StyleConstants.setForeground(style, c);
		
		if(trace) {
		Throwable t = new Throwable();
		StackTraceElement[] elements = t.getStackTrace();
		String caller = elements[0].getClassName();
		
		s = caller + " -> " + s;
		}
		
		try {
			document.insertString(document.getLength(), s, style);
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		scrollBottom();
		
		
	}
	
	public void println(String s) {
		println(s,trace,Color.white);
	}
	public void println(String s,Color color) {
		println(s,trace,color);
	}
	public void println(String s,boolean trace) {
		println(s,trace,Color.white);
	}
	public void println(String s,boolean trace,Color c) {
		print(s + "\n",trace,c);
	}
	
	
	public void clear() {
		try {
			document.remove(0, document.getLength());
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	/////////////////////////////////////////GETTERS AND SETTERS////////////////////////////////////
	public JFrame getFrame() {
		return frame;
	}
	
}
