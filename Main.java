import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;


public class Main {

	Node node;
	int totalNode;
	int interRequestDelay;
	int csExecutionTime;
	int numberOfRequest;
	public static volatile boolean csEnter = false;

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
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//while(m.numberOfRequest>0)
		int counter = 2;
		while(counter>0)
		{
			m.csEnter();
			m.csExecution();
			m.csExit();
			//m.numberOfRequest = m.numberOfRequest - 1;
			counter--;
			double lambda = 1.0 / m.interRequestDelay; 
	        Random defaultR = new Random();
	        try {        	
	        	long l = (long) m.getRandom(defaultR, lambda);
				Thread.sleep(l);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}	
		}
	}

	public void readConfigFile(int nodeNumber, File f)
	{
		FileReader fileReader;
		try {
			fileReader = new FileReader(f);
			BufferedReader br = new BufferedReader(fileReader);
			ArrayList<Node> aln = new ArrayList<Node>();
			HashMap<Integer,Node> hostNameHM = new HashMap<Integer, Node>();

			String line1 = br.readLine();
			String[] words = line1.split("\\s+");
			totalNode = Integer.parseInt(words[0]);
			interRequestDelay = Integer.parseInt(words[1]);
			csExecutionTime = Integer.parseInt(words[2]);
			numberOfRequest = Integer.parseInt(words[3]);
			
			for(int i=0;i<totalNode;i++)
			{
				String line2= br.readLine();

				String[] hostNameLine = line2.split("\\s+");
				Node n = new Node();
				n.setId(Integer.parseInt(hostNameLine[0]));
				n.setHostname(hostNameLine[1]);
				
				n.setPortNumber(Integer.parseInt(hostNameLine[2]));
				hostNameHM.put(n.getId(), n);
				//aln.add(n);
			}
			
			HashMap<Integer,ArrayList<Node>> hm = new HashMap<Integer,ArrayList<Node>>();

			for(int i=0;i<totalNode;i++)
			{
				ArrayList<Node> quorum = new ArrayList<Node>();
				String line2= br.readLine();

				String[] childLine = line2.split("\\s+");
				for(int j=0;j<childLine.length;j++)
				{
						Node n = new Node();
						n.setId(Integer.parseInt(childLine[j]));
						n.setHostname(hostNameHM.get(n.getId()).getHostname());
						n.setPortNumber(hostNameHM.get(n.getId()).getPortNumber());
						quorum.add(n);
				
				}

				hm.put(i, quorum);
			}

			node.setHostname(hostNameHM.get(nodeNumber).getHostname());
			node.setPortNumber(hostNameHM.get(nodeNumber).getPortNumber());
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
		ArrayList<Message> alm = new ArrayList<Message>();
		for(Node n : node.getQuorum())
		{
			Message m = new Message();
			m.setSourceNode(node);
			m.setDestinationNode(n);
			m.setMessage("request");
			alm.add(m);
		}
		SocketConnectionClient scc = new SocketConnectionClient(alm);
		scc.start();
		while(!Main.csEnter)
		{
		}
		System.out.println("Exit CS Enter function");
		
	}
	
	public void csExecution()
	{
        double lambda = 1.0 / csExecutionTime; 
        Random defaultR = new Random();
        try {        	
        	long l = (long) getRandom(defaultR, lambda);
			Thread.sleep(l);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
        System.out.println("CSExecution "+ node.getId());
        Main.csEnter = false;
		
	}
	
	public void csExit()
	{
		ArrayList<Message> alm = new ArrayList<Message>();
		for(Node n : node.getQuorum())
		{
			Message m = new Message();
			m.setSourceNode(node);
			m.setDestinationNode(n);
			m.setMessage("release");
			alm.add(m);
		}
		SocketConnectionClient scc = new SocketConnectionClient(alm);
		scc.start();
	}
	
	public double getRandom(Random r, double p) { 
        double d = -(Math.log(r.nextDouble()) / p);
        return d;
    }
	

}
