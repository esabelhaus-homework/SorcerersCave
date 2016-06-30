package game;

public class Resource {
	private String name;
	private int needed;
	
	public Resource(String myName, int howMany) {
		this.name = myName;
		this.needed = howMany;
	}
	
	public int howManyNeeded() {
		return needed;
	}
	
	public String getName() {
		return name;
	}
 	
}
