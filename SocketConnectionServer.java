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
