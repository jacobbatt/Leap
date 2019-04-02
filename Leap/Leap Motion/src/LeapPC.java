import com.leapmotion.leap.*;
import com.leapmotion.leap.Finger.Type;
import com.leapmotion.leap.Gesture.State;

import java.net.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.*;

import javax.swing.JOptionPane;

public class LeapPC extends Listener{

	private Socket socket;
	private PrintWriter out;
	private static int SERVER_PORT = 6000;

	public void onConnect(Controller controller)
	{
		System.out.println("CONNECTED");
		controller.enableGesture(Gesture.Type.TYPE_CIRCLE);
		controller.enableGesture(Gesture.Type.TYPE_SWIPE);
	}

	public void onFrame(Controller controller)
	{
		Frame frame = controller.frame();
		for (Finger f : frame.fingers())
		{
			if(f.type() == Type.TYPE_INDEX && f.isExtended())
			{
				Vector fingerPos = f.stabilizedTipPosition();
				InteractionBox box = frame.interactionBox();
				fingerPos=box.normalizePoint(fingerPos);
				System.out.println(fingerPos.toString());
				updatePosition(fingerPos.toString());
			}
			else if(f.type() == Type.TYPE_THUMB && !f.isExtended())
			{
				click();
				try {
					Thread.sleep(500);
				} catch(Exception e) { }
			}
		}
		for (Gesture g : frame.gestures())
		{
			if(g.type() == Gesture.Type.TYPE_CIRCLE)
			{
				CircleGesture circle = new CircleGesture(g);
				if(circle.pointable().direction().angleTo(circle.normal())<= Math.PI/4)
				{
					scrollDown();
					try { Thread.sleep(500); } catch (Exception e) {}
				}
				else
				{
					scrollUp();
				}
			}
			else if(g.type() == Gesture.Type.TYPE_SWIPE)
			{
				if(g.state()==State.STATE_STOP)
				{
					swipe();
					try { Thread.sleep(500); } catch (Exception e) {}
				}
			}
		}
	}

	private void connectToServer()
	{
		try
		{
			socket = new Socket(InetAddress.getLocalHost(), SERVER_PORT); //HOSTNAME
			out = new PrintWriter(socket.getOutputStream());
		}catch (IOException e) {
			JOptionPane.showMessageDialog(null, "CLIENT: Cannot connect to the server", "Error", JOptionPane.ERROR_MESSAGE);
			System.exit(-1);
		}
	}

	private void disconnectToServer()
	{
		try
		{
			socket.close();
		}catch (IOException e)
		{
			JOptionPane.showMessageDialog(null, "CLIENT: Cannot disconnect to the server", "Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	private void updatePosition(String position)
	{
		connectToServer();
		out.println(position);
		out.flush();
		disconnectToServer();
	}

	private void click()
	{
		connectToServer();
		out.println("Click");
		out.flush();
		disconnectToServer();
	}

	private void scrollUp()
	{
		connectToServer();
		out.println("Up");
		out.flush();
		disconnectToServer();

	}

	private void scrollDown()
	{
		connectToServer();
		out.println("Down");
		out.flush();
		disconnectToServer();

	}

	private void swipe()
	{
		connectToServer();
		out.println("Swipe");
		out.flush();
		disconnectToServer();

	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		LeapPC leap = new LeapPC();
		Controller controller = new Controller();
		controller.addListener(leap);

		try
		{
			System.in.read();
		} catch (Exception e)
		{

		}
		controller.removeListener(leap);
	}

}
