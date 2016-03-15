import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class SocketConnectionClient extends Thread{

	ArrayList<Message> am;

	public SocketConnectionClient(ArrayList<Message> am)
	{
		this.am = am;
	}

	public void run()
	{
		try 
		{
			for(Message m : am)
			{
				Socket client = new Socket(m.getDestinationNode().getHostname(), m.getDestinationNode().getPortNumber());
				ObjectOutputStream oos = new ObjectOutputStream(client.getOutputStream());
				oos.writeObject(m);
				oos.close();
				client.close();
			}

		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
