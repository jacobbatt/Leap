import java.net.*;
import java.io.*;
import javax.swing.JOptionPane;
import java.awt.Robot;
import java.awt.Dimension;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

public class LeapPI {
	public static int SERVER_PORT = 6000;
	public Robot robot;
	
	public LeapPI()
	{
		try {
			robot = new Robot();
			
		}catch(Exception e) {
			
		}
		
		
	}
	
	private ServerSocket goOnline()
	{
		ServerSocket serverSocket= null;
		try {
			serverSocket= new ServerSocket(SERVER_PORT);
		}catch(IOException e)
		{
			JOptionPane.showMessageDialog(null, "Server: Error creating network connection", "Error", JOptionPane.ERROR_MESSAGE);
		}
		try
		{
			System.out.println("SERVER Online\nWaiting for gestures on: " +InetAddress.getLocalHost() +":" + SERVER_PORT +"\n");
		}catch(Exception e)
		{
			
		}
		return serverSocket;
	}
		
	
	
	private void readInput(ServerSocket serverSocket)
	{
		while(true)
		{
			Socket socket = null;
			BufferedReader in =null;
			try
			{
				socket=serverSocket.accept();
				in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			}catch (IOException e)
			{
				JOptionPane.showMessageDialog(null, "Server: Error connecting to client", "Error", JOptionPane.ERROR_MESSAGE);
				System.exit(-1);
			}
			try
			{
				String request = in.readLine();
				if(request.startsWith(" ("))
				{
					String subStr = request.substring(1, request.length()-1);
					String[] coordinates = subStr.split(",");
					Dimension screen = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
					Float xPos= Float.parseFloat(coordinates[0]);
					Float yPos = Float.parseFloat(coordinates[1]);
					robot.mouseMove((int)(screen.width * xPos), (int) (screen.height - yPos*screen.height));
				}
				else if(request.equals("Click"))
				{
					robot.mousePress(InputEvent.BUTTON1_MASK);
					robot.mouseRelease(InputEvent.BUTTON1_MASK);
				}
				else if(request.equals("Up"))
				{
					robot.mouseWheel(-1);
				}
				else if(request.equals("Down"))
				{
					robot.mouseWheel(1);
				}
				else if(request.equals("Swipe"))
				{
					robot.keyPress(KeyEvent.VK_WINDOWS);
					robot.keyRelease(KeyEvent.VK_WINDOWS);
				}
			}catch (IOException e)
			{
				JOptionPane.showMessageDialog(null, "Server: Error communicating to client", "Error", JOptionPane.ERROR_MESSAGE);
			}
		}
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		LeapPI leap = new LeapPI();
		ServerSocket ss = leap.goOnline();
		if(leap!=null)
		{
			leap.readInput(ss);
		}

	}

}
