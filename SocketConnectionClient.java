import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;


public class SocketConnectionClient extends Thread{
	
	Node node;
	ArrayList<Message> am;
	boolean findSend;
	
	
	public SocketConnectionClient(Node node)
	{
		this.node = node;
	}
	
	public SocketConnectionClient(Node node, ArrayList<Message> am)
	{
		this.node = node;
		this.am = am;
	}
	
	
	
	public void run()
	{
		
		if(node.isRoot() && node.findSend)
		{
			System.out.println("root");
			for(Node n : node.getNeighbors())
			{
				Message m = new Message();
				m.setMessage("find");
				m.setNode(node);
				
				System.out.println("root neighbor id " + n.getId());
				try {
					Socket client = new Socket(n.getHostname(), n.getPortNumber());
					ObjectOutputStream oos = new ObjectOutputStream(client.getOutputStream());
					oos.writeObject(m);
					oos.close();
					//client.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					System.out.println("Error root IOException "+ node.getId());
					e.printStackTrace();
				} catch(Exception e)
				{
					System.out.println("Error root Exception "+ node.getId());
					e.printStackTrace();
				}
			}
		}
		
		else
		{
			
			try {
				System.out.println("request " + node.getId());
				for(int i=0;i<am.size();i++)
				{
					if(am.get(i).getMessage().equalsIgnoreCase("find"))
					{
						Message m1 = new Message();
						m1.setMessage("find");
						m1.setNode(node);
						for(Node n: node.getNeighbors())
						{
							Socket client1 = new Socket(n.getHostname(), n.getPortNumber());
							ObjectOutputStream oos = new ObjectOutputStream(client1.getOutputStream());
							oos.writeObject(m1);
						}
						
					}
					else if(am.get(i).getMessage().equalsIgnoreCase("ack"))
					{
						Message m1 = new Message();
						Node sender = am.get(i).getNode();
						System.out.println("Ack sender id "+ sender.getId() + " Self Node "+ node.getId());
						System.out.println("Self Node "+ node.getId() + " Sender hostname "+ sender.getHostname() + " Sender portNumber "+ sender.getPortNumber());
						m1.setMessage("ack");
						m1.setNode(node);
						InetAddress inet = InetAddress.getByName(sender.getHostname());
						Socket client1 = new Socket(inet, sender.getPortNumber());
						ObjectOutputStream oos = new ObjectOutputStream(client1.getOutputStream());
						oos.writeObject(m1);
						
					}
					else if(am.get(i).getMessage().equalsIgnoreCase("nack"))
					{
						Message m1 = new Message();
						Node sender = am.get(i).getNode();
						System.out.println("Nack sender id "+ sender.getId() + "Self Node "+ node.getId());
						m1.setMessage("nack");
						m1.setNode(node);
						Socket client1 = new Socket(sender.getHostname(), sender.getPortNumber());
						ObjectOutputStream oos = new ObjectOutputStream(client1.getOutputStream());
						oos.writeObject(m1);
					}
					else if(am.get(i).getMessage().equalsIgnoreCase("release"))
					{
						Message m1 = new Message();
						Node sender = am.get(i).getNode();
						System.out.println("release " + "Self Node "+ node.getId() + "sender id "+ sender.getId() );
						m1.setMessage("release");
						m1.setNode(node);
						Socket client1 = new Socket(sender.getHostname(), sender.getPortNumber());
						ObjectOutputStream oos = new ObjectOutputStream(client1.getOutputStream());
						oos.writeObject(m1);
					}
				}
				
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				System.out.println("Error UnknownHostException "+ node.getId());
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				
				System.out.println("Error IOException "+ node.getId());
				e.printStackTrace();
			} catch (Exception e)
			{
				System.out.println("Error exception "+ node.getId());
				e.printStackTrace();
			}
			
		}
		
		
		
		
		
		
	}
	

}
