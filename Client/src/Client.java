import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.*;
import javax.swing.*;


public class Client extends JFrame{
	
	private JTextField userText;
	private JTextArea chatWindow;
	private ObjectOutputStream output;
	private ObjectInputStream input;
	private String message = "";
	private String serverIP;
	private Socket connection;
	
	//constructor 
	/*in order for it to work you need the IP address of the server you are accessing. 
	 * */
	public Client(String host){
		super("Client mofo!");
		serverIP = host;
		userText = new JTextField();
		userText.setEditable(false);
		userText.addActionListener(
				new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent event) {
						sendData(event.getActionCommand());
						userText.setText("");
						
					}
				}
				);
		add(userText, BorderLayout.SOUTH);
		chatWindow = new JTextArea();
		add(new JScrollPane(chatWindow), BorderLayout.CENTER);
		setSize(400,250);
		setVisible(true);
	}
	
	//connect to server 
	public void startRunning(){
		try{
			connectToServer();
			setupStreams();
			whileChatting();
		}catch(EOFException eof){ //eof exception signifies the end of a file or stream. 
			showMessage("\n Client terminated the connection!");
		}catch(IOException io){
			io.getMessage();	
		}finally{
			closeConnection();
		}
	}



	private void connectToServer() throws IOException{
		// TODO Auto-generated method stub
		showMessage("Attempting connection... \n");
		connection = new Socket(InetAddress.getByName(serverIP), 6789);
		showMessage("Connect to: " + connection.getInetAddress().getHostName());
	}
	
	//set up streams to send and receive messages
	private void setupStreams() throws IOException{
		output = new ObjectOutputStream(connection.getOutputStream());
		output.flush();
		input = new ObjectInputStream(connection.getInputStream());
		showMessage("\n Dude your streams are good to go!");
	}
	
	private void whileChatting() throws IOException{
		// TODO Auto-generated method stub
		ableToType(true);
		do{
			try{
				message = (String) input.readObject(); //read whatever object they send and store it as a string.
				showMessage("\n" + message);
			}catch(ClassNotFoundException cnf){
				showMessage("\n I don't know that object type");
			}
		}while(!message.equals("SERVER - END"));
	}
	
	
	private void closeConnection() {
		// TODO Auto-generated method stub
		showMessage("\n closing connection...");
		ableToType(false);
		try{
			output.close();
			input.close();
			connection.close();
		}catch(IOException io){
			io.printStackTrace();
		}
		
	}
	
	//send messages to server 
	private void sendData(String message){
		try{
			output.writeObject("CLIENT - " + message);
			output.flush();
			showMessage("\nCLIENT - " + message);
		}catch(IOException ioException){
			chatWindow.append("\n something went wrong sending the data.");
		}
	}
	
	//Method for updating the chat window
		private void showMessage(final String msg){
			SwingUtilities.invokeLater(
					new Runnable(){
						public void run(){
							chatWindow.append(msg);
						}
					}
					);
		}
		
		//give user permission to type into the text box.
		private void ableToType(final boolean b){
			SwingUtilities.invokeLater(
					new Runnable(){
						public void run(){
							userText.setEditable(b);
						}
					}
					);
		}
	
}
