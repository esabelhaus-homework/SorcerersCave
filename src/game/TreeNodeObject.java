package game;

public class TreeNodeObject {
	private int index;
	private String label;
	
	public TreeNodeObject(int thisIndex, String thisLabel) {
		this.index = thisIndex;
		this.label = thisLabel;
	}
	
	public int getIndex() {
		return index;
	}
	
	@Override
	public String toString() {
	    return this.label;
	}
}
