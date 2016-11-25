import java.util.UUID;

public class Node{
	private UUID id = UUID.randomUUID();
	private String address;
	
	public Node(String address){
		this.address = address;
	}

	public String getAddress(){
		return this.address;
	}

	public UUID getId(){
		return this.id;
	}

	public long getIdNumber(){
		return this.id.getLeastSignificantBits();
	}
}
