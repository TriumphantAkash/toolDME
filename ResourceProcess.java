import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class ResourceProcess {
	public static boolean resourceUseFlag = false;

	public static boolean dmeHolds = true;
	//port number passed as command line argument
	public static ArrayList<Integer> csEnterList = new ArrayList<Integer>();
	
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub

		ServerSocket serverSock;
		try {
			serverSock = new ServerSocket(Integer.parseInt(args[0]));
		
		System.out.println("Resource Server started at port: "+args[0]);
		
		while(true)
		{
			Socket sock = serverSock.accept();
			ObjectInputStream ois = new ObjectInputStream(sock.getInputStream());
			Message m = (Message) (ois.readObject());
			ois.close();
			
			if(m.getMessage().equalsIgnoreCase("csenter")){
				csEnterList.add(m.getSourceNode().getId());
				
				if(resourceUseFlag == true){
					//Mutual exclusion doesn't holds
					//print no here
					if(dmeHolds == true){
						System.out.println("[DME RESULT] \"DME Doesnt Hold\" [DME RESULT]");
						dmeHolds = false;
					}else {
						//we don't want to print again
					}
					
				}else {
					resourceUseFlag = true;
				}
			}
			
			else if(m.getMessage().equalsIgnoreCase("csexit")){
				if(csEnterList.contains(m.getSourceNode().getId()))
				{
					//remove this node from th arraylist
					csEnterList.remove(m.getSourceNode().getId());
					if(csEnterList.isEmpty())
					{
						resourceUseFlag = false;
					}
					else
					{
						resourceUseFlag = true;
					}
				}
				else
				{
					//do nothing
				}
				
			}
			//either cs enter or cs exit
		}
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}