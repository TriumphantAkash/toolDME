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
	int counter =0;
	
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
		if(node.isRoot())
		{
			node.setDistanceFromRoot(0);
			SocketConnectionClient client = new SocketConnectionClient(node);
			client.start();
		}
		
		try
		{
			
			ServerSocket serverSock = new ServerSocket(node.getPortNumber());
			System.out.println("Server started " + node.getId());
			Socket sock;
			while(true)
			{
				
				sock = serverSock.accept();
				System.out.println("Server Accepted "+ node.getId());
				if(node.isRoot())
				{
					node.findSend=false;
				}
				
				ObjectInputStream ois = new ObjectInputStream(sock.getInputStream());
				Message m = (Message) (ois.readObject());
				ois.close();
				if(m.getMessage().equalsIgnoreCase("find"))
				{
					if(!(node.isRoot() ))
					{
						if(node.getParent().getId()!=9999)
						{
							System.out.println("Node "+node.getId()+" sender DSR"+m.getNode().getDistanceFromRoot());
							System.out.println("Node "+node.getId()+" DistanceFromRoot before condition "+node.getDistanceFromRoot());
							if(node.getDistanceFromRoot() > (m.getNode().getDistanceFromRoot()+1))
							{
								System.out.println("Node" + node.getId() + " Sender Node" + m.getNode().getId());
								System.out.println("Node" + node.getId() + " DistanceFromRoot "+ node.getDistanceFromRoot());
								
								Node currentParent = node.getParent();
								node.setParent(m.getNode());
								node.setDistanceFromRoot(m.getNode().getDistanceFromRoot()+1);
								ArrayList<Message> alm = new ArrayList<Message>();
								Message m1 = new Message();
								m1.setMessage("ack");
								node.findSend = true;
								
								m1.setNode(m.getNode());
								alm.add(m1);
								
								if(node.findSend)
								{
									Message m2 = new Message();
									m2.setMessage("find");
									m2.setNode(node);
									alm.add(m2);
									
									node.findSend = false;
								}
								Message m3 = new Message();
								m3.setMessage("release");
								m3.setNode(currentParent);
								alm.add(m3);
								
								
								SocketConnectionClient client = new SocketConnectionClient(node, alm);
								client.start();
								
							}
							else
							{
								Message m1 = new Message();
								m1.setMessage("nack");
								m1.setNode(m.getNode());
								
								ArrayList<Message> alm = new ArrayList<Message>();
								alm.add(m1);
								
								
								SocketConnectionClient client = new SocketConnectionClient(node, alm);
								client.start();
							}
						}
						else if(node.getParent().getId()==9999)
						{
							node.setParent(m.getNode());
							node.setDistanceFromRoot(m.getNode().getDistanceFromRoot()+1);
							ArrayList<Message> alm = new ArrayList<Message>();
							Message m1 = new Message();
							m1.setMessage("ack");
							
							m1.setNode(m.getNode());
							alm.add(m1);
							
							if(node.findSend)
							{
								Message m2 = new Message();
								m2.setMessage("find");
								m2.setNode(node);
								alm.add(m2);
								
								node.findSend = false;
							}
							
							
							SocketConnectionClient client = new SocketConnectionClient(node, alm);
							client.start();
						}
						
					}
					else
					{
						Message m1 = new Message();
						m1.setMessage("nack");
						m1.setNode(m.getNode());
						
						ArrayList<Message> alm = new ArrayList<Message>();
						alm.add(m1);
						
						
						SocketConnectionClient client = new SocketConnectionClient(node, alm);
						client.start();
						
						
						
					}
					
				}
				else if(m.getMessage().equalsIgnoreCase("ack"))
				{
					
					node.getChild().add(m.getNode());
					counter++;
					if(counter>=node.getNeighbors().size())
					{
						writeFile();
					}
					
				}
				else if(m.getMessage().equalsIgnoreCase("nack"))
				{
					counter++;
					if(counter>=node.getNeighbors().size())
					{
						writeFile();
					}

				}
				else if(m.getMessage().equalsIgnoreCase("release"))
				{
					for(int i=0;i<node.getChild().size();i++)
					{
						if(node.getChild().get(i).getId()==m.getNode().getId())
						{
							node.getChild().remove(i);
						}
					}
					if(counter>=node.getNeighbors().size())
					{
						writeFile();
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
	
	public void writeFile()
	{
		try {
		File file = new File("config_name-"+node.getId()+".out");
		FileWriter fw = new FileWriter(file,false);
		String child = "";
		boolean space = true;
		
		if(node.isRoot())
		{
			fw.write("*"+"\n");
		}
		else
		{
			fw.write(node.getParent().getId()+"\n");
		}
		if(node.getChild().size()!=0)
		{
			for(Node n : node.getChild())
			{
				if(!space)
				{
					child +=" ";
				}
				child +=n.getId();
				space = false;	
			}
			fw.write(child);
		}
		else
			fw.write("*");
		
		fw.close();
		
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	

}
