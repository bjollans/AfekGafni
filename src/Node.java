import java.util.UUID;

public class Node{
	private UUID id = UUID.randomUUID();
	private int indexInProcArray;
	private int level;
	
	public Node(int indexInProcArray, UUID id, int level){
		this.indexInProcArray = indexInProcArray;
		this.id = id;
		this.level = level;
	}

	public int getIndexInProcArray(){
		return this.indexInProcArray;
	}

	public UUID getId(){
		return this.id;
	}

	public long getIdNumber(){
		return this.id.getLeastSignificantBits();
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}
}
