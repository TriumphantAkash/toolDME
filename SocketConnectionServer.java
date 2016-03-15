import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;


public class SocketConnectionServer extends Thread{
	
	Node node ;
	
	public SocketConnectionServer(Node node)
	{
		this.node = node;
	}
	
	
	public void run()
	{
		go();
	}
	
	public void go()
	{
		try{
			ServerSocket serverSock = new ServerSocket(node.getPortNumber());
			System.out.println("Server started " + node.getId());
			Socket sock;
			MinHeap mn = new MinHeap();
			while(true)
			{
				sock = serverSock.accept();
				ObjectInputStream ois = new ObjectInputStream(sock.getInputStream());
				Message m = (Message) (ois.readObject());
				ois.close();
				if(m.getMessage().equalsIgnoreCase("request"))
				{
					
				}
				else if(m.getMessage().equalsIgnoreCase("release"))
				{
					
				}
				else if(m.getMessage().equalsIgnoreCase("grant"))
				{
					

				}
				else if(m.getMessage().equalsIgnoreCase("inquire"))
				{
					

				}
				else if(m.getMessage().equalsIgnoreCase("yield"))
				{
					if(node.getWaitingForYield().size()>0)
					{
						for(Node n : node.getWaitingForYield())
						{
							node.getQueue().add(n);
						}
						mn.buildMinHeap(node.getQueue());
						ArrayList<Message> alm = new ArrayList<Message>();
						Message send = new Message();
						node.setTimestamp(node.getTimestamp()+1);
						send.setTimeStamp(node.getTimestamp());
						send.setSourceNode(node);
						send.setDestinationNode(node.getQueue().get(0));
						send.setMessage("grant");
						alm.add(send);
						SocketConnectionClient c = new SocketConnectionClient(alm);
						c.start();	
						
					}

				}
				else if(m.getMessage().equalsIgnoreCase("failed"))
				{
					node.setFailedReceived(true);
					if(node.getInquireQuorum().size()>0)
					{
						ArrayList<Message> alm = new ArrayList<Message>();
						for(Node n: node.getInquireQuorum())
						{
							Message send = new Message();
							node.setTimestamp(node.getTimestamp()+1);
							send.setTimeStamp(node.getTimestamp());
							send.setDestinationNode(n);
							send.setSourceNode(node);
							send.setMessage("yield");
							alm.add(send);
						}
					SocketConnectionClient c = new SocketConnectionClient(alm);
					c.start();	
					}
				}
			}

		}
		catch(IOException ex)
		{
			ex.printStackTrace();
			
		}
		catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
}
