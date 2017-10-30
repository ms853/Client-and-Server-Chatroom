import javax.swing.JFrame;

public class ClientTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Client mike;
		mike = new Client("127.0.0.1"); //localhost - the computer that I am at right now.
		mike.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mike.startRunning();
	}

	/*
	 * This can be turned into a jar file which can be distributed to 
	 * all the clients. Then there is the separate server program. Both program need to run,
	 * in order for the instant messaging service to work. 
	 * */
}
