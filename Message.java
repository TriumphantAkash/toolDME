

public class Message {
	String message;
	Node sourceNode;
	Node destinationNode;
	int timeStamp;
	
	public String getMessage() {
		return message;
	}
	public void setMessage(String messageType) {
		this.message = messageType;
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
