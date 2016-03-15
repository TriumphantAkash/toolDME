import java.util.ArrayList;


public class Node {

	int id;
	int portNumber;
	String hostname;
	ArrayList<Node> quorum;
	ArrayList<Node> queue;
	Node grantOwner;
	boolean failedReceived;
	boolean grantFlag;
	boolean inquireFlag;
	int timestamp =0;
	ArrayList<Node> grant;
	ArrayList<Node> inquire;
	
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getPortNumber() {
		return portNumber;
	}
	public void setPortNumber(int portNumber) {
		this.portNumber = portNumber;
	}
	
	public String getHostname() {
		return hostname;
	}
	public void setHostname(String hostname) {
		this.hostname = hostname;
	}
	public ArrayList<Node> getQuorum() {
		return quorum;
	}
	public void setQuorum(ArrayList<Node> quorum) {
		this.quorum = quorum;
	}
	public ArrayList<Node> getQueue() {
		return queue;
	}
	public void setQueue(ArrayList<Node> queue) {
		this.queue = queue;
	}
	public Node getGrantOwner() {
		return grantOwner;
	}
	public void setGrantOwner(Node grantOwner) {
		this.grantOwner = grantOwner;
	}
	public boolean isFailedReceived() {
		return failedReceived;
	}
	public void setFailedReceived(boolean failedReceived) {
		this.failedReceived = failedReceived;
	}
	public boolean isGrantFlag() {
		return grantFlag;
	}
	public void setGrantFlag(boolean grantFlag) {
		this.grantFlag = grantFlag;
	}
	public boolean isInquireFlag() {
		return inquireFlag;
	}
	public void setInquireFlag(boolean inquireFlag) {
		this.inquireFlag = inquireFlag;
	}
	public int getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(int timestamp) {
		this.timestamp = timestamp;
	}
	public ArrayList<Node> getGrant() {
		return grant;
	}
	public void setGrant(ArrayList<Node> grant) {
		this.grant = grant;
	}
	public ArrayList<Node> getInquire() {
		return inquire;
	}
	public void setInquire(ArrayList<Node> inquire) {
		this.inquire = inquire;
	}
	
	
}
