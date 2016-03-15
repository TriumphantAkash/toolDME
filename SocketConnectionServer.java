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
			while(true)
			{
				sock = serverSock.accept();
				
				
				
				
				ObjectInputStream ois = new ObjectInputStream(sock.getInputStream());
				Message m = (Message) (ois.readObject());
				ois.close();
				
				//set logical clock
				if(node.timestamp < m.getTimeStamp()){
					node.timestamp = m.getTimeStamp()+1;
				}else {
					node.timestamp += 1;
				}
					
				if(m.getMessage().equalsIgnoreCase("request"))
				{
					
					if(!node.grantFlag){	//if this is first request, grant Flag is false
						node.queue.add(m.getSourceNode());
						//send grant to the source of m
						Message grantMsg = new Message();
						grantMsg.sourceNode = node;
						grantMsg.destinationNode = m.getSourceNode();
						grantMsg.setTimeStamp(node.timestamp);
						grantMsg.setMessage("GRANT");
						
						SocketConnectionClient scc = new SocketConnectionClient(grantMsg);
						scc.start();
						////////////////////////////////////////////////////////////////////
						
						node.grantFlag = true;
						node.grantOwner = m.sourceNode;
						
					} else {
						if(m.timeStamp > node.grantOwner.timestamp){	//m's timestamp is more than grant owner's timestamp
							//put this req m into the original priority queue
							node.queue.add(m.sourceNode);
						}else{
							node.waitingForYield.add(m.sourceNode);	//add this req to waitingForYield list
							if(!node.inquireFlag) {//check inquire Flag so that don't send inquire to a node again and again
								//send inquire to grant owner
								Message inquireMsg = new Message();
								inquireMsg.sourceNode = node;
								inquireMsg.destinationNode = node.grantOwner;
								inquireMsg.setTimeStamp(node.timestamp);
								inquireMsg.setMessage("INQUIRE");
								
								SocketConnectionClient scc = new SocketConnectionClient(inquireMsg);
								scc.start();
								
								node.inquireFlag = true;
							
							
							} else {
								//don't send inquire message again to the Grant Owner
							}
							
							}
						}
						
				}
				else if(m.getMessage().equalsIgnoreCase("release"))
				{
					//1) delete first element from the main queue
					node.queue.remove(0);
					//2) Add waitingForYield list to original queue
					for(Node n:node.waitingForYield){
						node.queue.add(n);
					}
					
					//3) sort main priority queue //boss's method
					//Collections.sort(node.queue);
					
					//4) send grant to first if there
					//SocketConnectionClient scc = new SocketConnectionClient(node);
					
				}
				else if(m.getMessage().equalsIgnoreCase("grant"))
				{
					//1) update grant arrayList
					node.grant.add(m.getSourceNode());
					
					//2) check size of grantArrayList 
					if(node.grant.size() == node.quorum.size()){
						csEnter = true;
						//go into critical section
					}

				}
				else if(m.getMessage().equalsIgnoreCase("inquire"))
				{
					node.inquireQuorum.add(m.sourceNode);
					if(node.failedReceived){
						
						//remove these elements from my grantlist (those who have sent me inquire) 
						for(Node i:node.inquireQuorum){
							for(Node g: node.grant){
								if(g.id == i.id){
									//remove this node from grantList
									node.grant.remove(g);
								}
							}
						}
						
						
						//send yield to inquireQuorum list
						node.timestamp = node.timestamp + 1;
						ArrayList<Message>msgList = new ArrayList<Message>();
						for(Node n:node.inquireQuorum){	//make arrayList of Messages
							Message m = new Message();
							m.destinationNode = n;
							m.sourceNode = node;
							m.message = "YIELD";							
						}
						//send this to ClientTHread
						SocketConnectionClient scc = new SocketConnectionClient(msgList);
					}

				}
				else if(m.getMessage().equalsIgnoreCase("yield"))
				{
					

				}
				else if(m.getMessage().equalsIgnoreCase("failed"))
				{
					

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
