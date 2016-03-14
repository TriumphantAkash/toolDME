import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;


public class Main {
	
	Node node;
	int totalNodes;
	int interRequestDelay;
	int csExecutionTime;
	int numberOfRequest;
	
	public Main()
	{
		node = new Node();
	}

	public static void main(String[] args) {
	
		
		
		int nodeNumber = Integer.parseInt(args[0]);
		File f = new File(args[1]);
		
		Main m = new Main();
		m.node.setId(nodeNumber);
		m.readConfigFile(nodeNumber,f);
		
		SocketConnectionServer server = new SocketConnectionServer(m.node);
		server.start();
		
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		while(m.numberOfRequest>0)
		{
			m.csEnter();
			m.csExecution();
			m.csExit();
			m.numberOfRequest--;
			
		}
		
		

	}
	
	public void readConfigFile(int nodeNumber, File f)
	{
		FileReader fileReader;
		try {
			fileReader = new FileReader(f);
			BufferedReader br = new BufferedReader(fileReader);
			ArrayList<Node> aln = new ArrayList<Node>();
			
			String line1 = br.readLine();
			String[] words = line1.split("\t",-1);
			for(int i=0;i<words.length;i++)
			{
				words[i] = words[i].trim();
			}
			totalNodes = Integer.parseInt(words[0]);
			interRequestDelay = Integer.parseInt(words[1]);
			csExecutionTime = Integer.parseInt(words[2]);
			numberOfRequest = Integer.parseInt(words[3]);
						
			for(int i=0;i<totalNodes;i++)
			{
				String line2= br.readLine();
				
				String[] hostNameLine = line2.split("\t",-1);
				for(int j=0;j<hostNameLine.length;j++)
				{
				
					hostNameLine[j]=hostNameLine[j].trim();
				}
				
				
				Node n = new Node();
				n.setId(Integer.parseInt(hostNameLine[0]));
				n.setHostname(hostNameLine[1]);
				n.setPortNumber(Integer.parseInt(hostNameLine[2]));
				
				
				
				aln.add(n);
			}
			
			
			HashMap<Integer,ArrayList<Node>> hm = new HashMap<Integer,ArrayList<Node>>();
	
			for(int i=0;i<totalNodes;i++)
			{
				ArrayList<Node> quorum = new ArrayList<Node>();
				String line2= br.readLine();
				
				String[] childLine = line2.split("\t",-1);
				for(int j=0;j<childLine.length;j++)
				{
					childLine[j]=childLine[j].trim();
					if(childLine[j].equalsIgnoreCase(""))
						childLine[j] = "a";
					else
						childLine[j] = childLine[j];
				}
				for(int j=0;j<childLine.length;j++)
				{
					if(!childLine[j].equalsIgnoreCase("a"))
					{				
						Node n = new Node();
						n.setId(Integer.parseInt(childLine[j]));
						
						n.setHostname(aln.get(n.getId()).getHostname());
						n.setPortNumber(aln.get(n.getId()).getPortNumber());
						
						quorum.add(n);
					}
				}
				
				hm.put(i, quorum);
			}
			
			node.setHostname(aln.get(nodeNumber).getHostname());
			node.setPortNumber(aln.get(nodeNumber).getPortNumber());
			node.setQuorum(hm.get(nodeNumber));

			
			
			
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	public Node getNode() {
		return node;
	}

	public void setNode(Node node) {
		this.node = node;
	}
	
	public void csEnter()
	{
		
	}
	
	public void csExecution()
	{
		
	}
	
	public void csExit()
	{
		
	}
	
	

}
