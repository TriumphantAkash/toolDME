import java.io.Serializable;


public class Message implements Serializable{
	private String message;
	private Node sourceNode;
	private Node destinationNode;
	
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
}
