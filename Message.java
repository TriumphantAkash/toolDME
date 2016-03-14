package toolDME;

public class Message {
	String messageType;
	Node sourceNode;
	Node destinationNode;
	int timeStamp;
	public String getMessageType() {
		return messageType;
	}
	public void setMessageType(String messageType) {
		this.messageType = messageType;
	}
	public Node getSourceNode() {
		return sourceNode;
	}
	public void setSourceNode(Node sourceNode) {
		this.sourceNode = sourceNode;
	}
	public Node getDestinationNode() {
		return destinationNode;
	}
	public void setDestinationNode(Node destinationNode) {
		this.destinationNode = destinationNode;
	}
	public int getTimeStamp() {
		return timeStamp;
	}
	public void setTimeStamp(int timeStamp) {
		this.timeStamp = timeStamp;
	}
	
	
}
