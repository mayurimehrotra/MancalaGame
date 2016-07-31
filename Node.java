import java.util.Arrays;

public class Node {


	private int[] A;
	private int[] B;
	private int depth;
	private int minMaxValue; //=Integer.MIN_VALUE;
	private boolean nextChance=false;
	private boolean root=false;
	private int fromIndex;
	private String nodeName;
	
	public String getNodeName() {
		return nodeName;
	}

	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}

	public int getFromIndex() {
		return fromIndex;
	}

	public void setFromIndex(int fromIndex) {
		this.fromIndex = fromIndex;
	}

	public boolean isRoot() {
		return root;
	}

	public void setRoot(boolean root) {
		this.root = root;
	}

	public Node(int[] a, int[] b, int depth, int minMaxValue, boolean nextChance) {
		super();
		A = a;
		B = b;
		this.depth = depth;
		this.minMaxValue = minMaxValue;
		this.nextChance = nextChance;
	}

	public Node() {
	}
	
	public int[] getA() {
		return A;
	}
	public void setA(int[] a) {
		A = a;
	}
	public int[] getB() {
		return B;
	}
	public void setB(int[] b) {
		B = b;
	}
	public int getDepth() {
		return depth;
	}
	public void setDepth(int depth) {
		this.depth = depth;
	}
	public int getminMaxValue() {
		return minMaxValue;
	}
	public void setminMaxValue(int minMaxValue) {
		this.minMaxValue = minMaxValue;
	}
	public boolean isNextChance() {
		return nextChance;
	}
	public void setNextChance(boolean nextChance) {
		this.nextChance = nextChance;
	}
	@Override
	public String toString() {
		return "Node [A=" + Arrays.toString(A) + ", B=" + Arrays.toString(B)
				+ ", depth=" + depth + ", minMaxValue=" + minMaxValue
				+ ", nextChance=" + nextChance + "]";
	}
	
	public int[] getPitsByPlayer(int player)
	{
		if(player==1)
			return B;
		else
			return A;
	}
	
	public int getPitCoinsByPlayer(int player,int position){
		
		int [] pits;
		if(player==1){
			pits=getPitsByPlayer(1);
			return pits[position];
		}
		else{
			pits=getPitsByPlayer(2);
			return pits[position];
		}
	}
	
	
	public void setPitCoinsByPlayer(int player,int position,int value){
		
		int [] pits;
		if(player==1){
			pits=getPitsByPlayer(1);
			pits[position]=value;
		}
		else{
			pits=getPitsByPlayer(2);
			pits[position]=value;
		}
	}
	
}
