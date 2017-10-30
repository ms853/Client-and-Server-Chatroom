import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
/*Anyone in the world can access this! */
public class Server extends JFrame {

	
	private JTextField userText; //variable for type in the message.
	private JTextArea chatWindow;
	private ObjectOutputStream output; //The stream that flows from this computer to the friends computer.
	private ObjectInputStream input;
	private ServerSocket server; //The server will be configured to the port number;
	private Socket connection; //Is the connection between your computer and the other one.
	
	/*Now I will set up the GUI!*/
	
	//constructor 
	public Server(){
		super("Mike's Instant Messenger");
		userText = new JTextField();
		userText.setEditable(false);
		userText.addActionListener(
				new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent e) {
						sendMessage(e.getActionCommand());
						userText.setText("");
					}
				}
				);
		add(userText, BorderLayout.SOUTH);
		chatWindow = new JTextArea();
		add(new JScrollPane(chatWindow));
		setSize(400,250);
		setVisible(true);
	}
	
	//set up and run the server
	public void startRunning(){
		try{
			/*
			 * Portnumber - where do you want to connect. 
			 * The backlog number in this case 100 is the number of people that can wait to connect.
			 * */
			server = new ServerSocket(6789, 100); //Port number 
			while(true){
				try{
					waitForConnection();
					setupStreams();
					whileChatting();
				}catch(EOFException eof){ //eof exception signifies the end of a file or stream. 
					showMessage("\n Server ended the Connection!");
				}finally{
					closeConnection();
				}
			}
		}catch(IOException e){
			e.getMessage();
		}
	}
	//wait for the connection then display the connection.
	private void waitForConnection() throws IOException{
		showMessage(" Waiting for someone to connect... \n");
		connection = server.accept(); 
		showMessage("Now connection to " + connection.getInetAddress().getHostAddress());

	}
	
	//get stream to send and receive data.
	private void setupStreams() throws IOException{
		output = new ObjectOutputStream(connection.getOutputStream());
		output.flush();
		input = new ObjectInputStream(connection.getInputStream());
		showMessage("\n Streams are now setup! \n");
	}
	
	//during the conversation 
	private void whileChatting() throws IOException{
		String message = " You are now connected!";
		sendMessage(message);
		ableToType(true); //allows the user to type stuff into the textbox.
		
		do{
			try{
				message = (String) input.readObject();
				showMessage("\n" + message);
			}catch(ClassNotFoundException classNot){
				showMessage("\n I do not know what the user just sent");
			}
		}while(!message.equals("CLIENT - END"));
	}
	
	//Method for closing the streams and sockets 
	private void closeConnection(){
		showMessage("\n Closing connections...\n");
		ableToType(false);
		try{
			output.close(); //closes the stream from them
			input.close();
			connection.close();
		}catch(IOException ioe){
			ioe.printStackTrace();
		}
	}
	
	//Method for sending the message.  
	private void sendMessage(String msg){
		try{
			output.writeObject("SERVER - " + msg);
			output.flush(); //push all the extra bytes to the user. 
			showMessage("\nSERVER - " + msg);
		}catch(IOException ioe){
			chatWindow.append("\n ERROR: DUDE I CAN'T SEND THAT MESSAGE");
		}
	}
	
	//Method for updating the chat window
	private void showMessage(final String text){
		SwingUtilities.invokeLater(
				new Runnable(){
					 public void run(){
						chatWindow.append(text);
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
