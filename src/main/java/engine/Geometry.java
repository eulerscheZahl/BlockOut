package engine;

import java.util.ArrayList;

public class Geometry {
	public int[] dimensions = new int[3];
	protected ArrayList<Cube> cubes = new ArrayList<>();

	protected Geometry() {
	}

	public Geometry(String geometry) {
		String[] parts = geometry.split(" ");
		for (int i = 0; i < dimensions.length; i++) dimensions[i] = Integer.parseInt(parts[i]);

		int x = 0, y = 0, z = 0;
		for (char c : parts[3].toCharArray()) {
			if (c == '#') cubes.add(new Cube(new int[]{x, y, z}));
			if (++x == dimensions[0]) {
				x = 0;
				y++;
			}
			if (y == dimensions[1]) {
				y = 0;
				z++;
			}
		}
	}

	public int minDimension() {
		return Math.min(Math.min(dimensions[0], dimensions[1]), dimensions[2]);
	}

	public int maxDimension() {
		return Math.max(Math.max(dimensions[0], dimensions[1]), dimensions[2]);
	}

	@Override
	public String toString() {
		String result = dimensions[0] + " " + dimensions[1] + " " + dimensions[2] + " ";
		for (int z = 0; z < dimensions[2]; z++) {
			for (int y = 0; y < dimensions[1]; y++) {
				for (int x = 0; x < dimensions[0]; x++) {
					final int x_ = x, y_ = y, z_ = z;
					if (cubes.stream().anyMatch(c -> c.pos[0] == x_ && c.pos[1] == y_ && c.pos[2] == z_)) result += '#';
					else result += '.';
				}
			}
		}
		return result;
	}
}
