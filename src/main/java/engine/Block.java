package engine;

import java.util.ArrayList;

public class Block extends Geometry {
	
	public Block(String block) {
		super(block);
	}

	public ArrayList<Block> createRotations() {
		ArrayList<Block> current = new ArrayList<Block>();
		current.add(this);
		ArrayList<Block> rotated = new ArrayList<Block>();
		for (int rotAxis = 0; rotAxis < 3; rotAxis++) {
			for (Block c : current) {
				Block b = new Block(c.toString());
				for (int rot = 0; rot < 4; rot++) {
					final Block b_ = b;
					// I guess I should use a Hashset here
					if (!rotated.stream().anyMatch(r -> r.toString().equals(b_.toString())))
						rotated.add(b);
					b = b.rotate(rotAxis);
				}
			}
			current = new ArrayList<Block>(rotated);
			rotated.clear();
		}
		
		return current;
	}

	private Block rotate(int rotAxis) {
		Block result = new Block(this.toString());
		int d1 = (rotAxis + 1) % 3;
		int d2 = (rotAxis + 2) % 3;
		
		// adjust dimension
		result.dimensions[d1] = this.dimensions[d2];
		result.dimensions[d2] = this.dimensions[d1];
		
		// rotate blocks
		for (int i = 0; i < cubes.size(); i++) {
			result.cubes.get(i).pos[rotAxis] = this.cubes.get(i).pos[rotAxis];
			result.cubes.get(i).pos[d1] = -this.cubes.get(i).pos[d2];
			result.cubes.get(i).pos[d2] = this.cubes.get(i).pos[d1];
		}
		
		// re-adjust to origin of coordinate system
		for (int dim = 0; dim < 3; dim++) {
			final int _dim = dim;
			while (result.cubes.stream().anyMatch(c -> c.pos[_dim] < 0)) {
				result.shift(dim, 1);
			}
			while (!result.cubes.stream().anyMatch(c -> c.pos[_dim] == 0)) {
				result.shift(dim, -1);
			}
		}
		
		return result;
	}

	public void shift(int dimension, int offset) {
		for (Cube c : cubes) c.pos[dimension]+=offset;
	}
}
