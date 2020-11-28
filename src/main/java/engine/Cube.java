package engine;

public class Cube {
	private static int idCounter = 0;
	public int id;
	public int[] pos = new int[3];
	
	public Cube(int[] pos) {
		this.id = idCounter++;
		this.pos = pos;
	}
}
