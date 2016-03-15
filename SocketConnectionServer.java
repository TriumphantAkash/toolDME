import java.io.DataOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Queue;

import org.omg.CORBA.portable.InputStream;


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

				//set logical clock
				if(node.getTimestamp() < m.getSourceNode().getTimestamp()){
					node.setTimestamp(m.getSourceNode().getTimestamp()+1);
				}else {
					node.setTimestamp(node.getTimestamp()+1);
				}

				if(m.getMessage().equalsIgnoreCase("request"))
				{

					if(!node.isGrantFlag())
					{	//if this is first request, grant Flag is false
						node.getQueue().add(m.getSourceNode());
						//send grant to the source of m
						node.setTimestamp(node.getTimestamp()+1);
						Message grantMsg = new Message();
						grantMsg.setSourceNode(node);
						grantMsg.setDestinationNode(m.getSourceNode());
						grantMsg.setMessage("grant");
						node.setGrantFlag(true);
						node.setGrantOwner(m.getSourceNode());

						SocketConnectionClient scc = new SocketConnectionClient(grantMsg);
						scc.start();						
					}
					else 
					{
						if(m.getSourceNode().getTimestamp() > node.getGrantOwner().getTimestamp())
						{	//m's timestamp is more than grant owner's timestamp
							//put this req m into the original priority queue
							node.getQueue().add(m.getSourceNode());
							mn.buildMinHeap(node.getQueue());
							Message sendFailed = new Message();
							sendFailed.setSourceNode(node);
							sendFailed.setDestinationNode(m.getSourceNode());
							sendFailed.setMessage("failed");
							SocketConnectionClient scc = new SocketConnectionClient(sendFailed);
							scc.start();		
						}else
						{
							node.getWaitingForYield().add(m.getSourceNode());	//add this req to waitingForYield list
							if(!node.isInquireFlag()) 
							{//check inquire Flag so that don't send inquire to a node again and again
								//send inquire to grant owner
								node.setTimestamp(node.getTimestamp()+1);
								Message inquireMsg = new Message();
								inquireMsg.setSourceNode(node);
								inquireMsg.setDestinationNode(node.getGrantOwner());
								inquireMsg.setMessage("inquire");
								node.setInquireFlag(true);

								SocketConnectionClient scc = new SocketConnectionClient(inquireMsg);
								scc.start();		
							} 
						}
					}

				}
				else if(m.getMessage().equalsIgnoreCase("release"))
				{
					//1) delete first element from the main queue
					node.getQueue().remove(0);
					mn.minHeapify(node.getQueue(), 0);
					//2) Add waitingForYield list to original queue
					for(Node n:node.getWaitingForYield())
					{
						node.getQueue().add(n);
					}
					node.setWaitingForYield(new ArrayList<Node>());
					mn.buildMinHeap(node.getQueue());
					node.setGrantOwner(node.getQueue().get(0));
					if(node.getQueue().size()>0)
					{
						Message sendGrant = new Message();
						sendGrant.setMessage("grant");
						sendGrant.setSourceNode(node);
						sendGrant.setDestinationNode(node.getQueue().get(0));

						SocketConnectionClient scc = new SocketConnectionClient(sendGrant);
						scc.start();

					}
				}

				else if(m.getMessage().equalsIgnoreCase("grant"))
				{
					//1) update grant arrayList
					node.getGrant().add(m.getSourceNode());
					for(Node n : node.getFailedList())
					{
						if(n.getId() == m.getSourceNode().getId())
						{
							node.getFailedList().remove(n);
						}
					}

					//2) check size of grantArrayList 
					if(node.getGrant().size() == node.getQuorum().size())
					{
						Main.csEnter = true;
						node.setGrant(null);
						node.setGrant(new ArrayList<Node>());
						//go into critical section
					}


				}
				else if(m.getMessage().equalsIgnoreCase("inquire"))
				{
					
					if(node.getFailedList().size()>0)
					{	
			
						//remove these elements from my grantlist (those who have sent me inquire) 
						for(Node g: node.getGrant())
						{
							if(g.getId() == m.getDestinationNode().getId())
							{
								//remove this node from grantList
								node.getGrant().remove(g);
							}
						}
						node.setTimestamp(node.getTimestamp()+1);
						ArrayList<Message>msgList = new ArrayList<Message>();
						for(Node n:node.getInquireQuorum())
						{	//make arrayList of Messages
							Message m1 = new Message();
							m1.setDestinationNode(n);
							m1.setSourceNode(node);
							m1.setMessage("yield");
						}
						//send yield to inquireQuorum list

						SocketConnectionClient scc = new SocketConnectionClient(msgList);
						scc.start();
					}
					else
					{
						node.getInquireQuorum().add(m.getSourceNode());
					}

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
						node.setTimestamp(node.getTimestamp()+1);
						Message send = new Message();
						send.setSourceNode(node);
						send.setDestinationNode(node.getQueue().get(0));
						send.setMessage("grant");

						SocketConnectionClient c = new SocketConnectionClient(send);
						c.start();	

					}

				}
				else if(m.getMessage().equalsIgnoreCase("failed"))
				{
					node.getFailedList().add(m.getSourceNode());
					if(node.getInquireQuorum().size()>0)
					{
						ArrayList<Message> alm = new ArrayList<Message>();
						for(Node n: node.getInquireQuorum())
						{
							Message send = new Message();
							node.setTimestamp(node.getTimestamp()+1);
							send.setDestinationNode(n);
							send.setSourceNode(node);
							send.setMessage("yield");
							for(Node g: node.getGrant())
							{
								if(g.getId() == n.getId())
								{
									node.getGrant().remove(g);
								}
							}
							alm.add(send);
						}
						node.setInquireQuorum(new ArrayList<Node>());
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
