import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class SocketConnectionClient extends Thread{

	ArrayList<Message> am;
	Message m;

	public SocketConnectionClient(ArrayList<Message> am)
	{
		this.am = am;
	}
	public SocketConnectionClient(Message m)
	{
		am = new ArrayList<Message>();
		this.m = m;
	}

	public void run()
	{
		try 
		{
			if(am.size()>0)
			{				
				for(Message m1 : am)
				{
					Socket client = new Socket(m1.getDestinationNode().getHostname(), m1.getDestinationNode().getPortNumber());
					ObjectOutputStream oos = new ObjectOutputStream(client.getOutputStream());
					oos.writeObject(m1);
					oos.close();
					client.close();
				}
			}
			else
			{
				synchronized(this)
				{					
					
					Socket client = new Socket(m.getDestinationNode().getHostname(), m.getDestinationNode().getPortNumber());
					ObjectOutputStream oos = new ObjectOutputStream(client.getOutputStream());
					oos.writeObject(m);
					oos.close();
					client.close();
				}
			}

		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
