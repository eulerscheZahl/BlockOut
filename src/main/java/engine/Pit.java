package engine;

import com.codingame.gameengine.core.GameManager;

import java.util.ArrayList;

public class Pit extends Geometry {
	private BoardModule module;
	private GameManager gameManager;

	public Pit(GameManager gameManager, BoardModule module, int width, int height, int depth) {
		this.gameManager = gameManager;
		this.module = module;
		dimensions = new int[]{width, height, depth};
		module.definePit(width, height, depth);
	}

	public void placeBlock(Block block, int x, int z) {
		block.shift(0, x);
		block.shift(1, dimensions[1]);
		block.shift(2, z);

		int dropDepth = dimensions[1];
		for (Cube c : block.cubes) {
			for (int d = 1; d <= dropDepth; d++) {
				final int d_ = d;
				if (this.cubes.stream().anyMatch(t -> t.pos[0] == c.pos[0] && t.pos[1] == c.pos[1] - d_ && t.pos[2] == c.pos[2]))
					dropDepth = d - 1;
			}
		}
		gameManager.setFrameDuration(Constants.SPAWN_TIME + dropDepth * Constants.DROP_TIME);
		module.spawnBlock(block, (double) Constants.SPAWN_TIME / gameManager.getFrameDuration(), dropDepth);
		block.shift(1, -dropDepth);
		this.cubes.addAll(block.cubes);
	}

	public boolean hasLost() {
		return cubes.stream().anyMatch(c -> c.pos[1] >= this.dimensions[1]);
	}

	public boolean removeLayers() {
		ArrayList<Integer> completeLayers = new ArrayList<>();
		for (int y = 0; y < dimensions[1]; y++) {
			boolean layer = true;
			for (int x = 0; x < dimensions[0]; x++) {
				for (int z = 0; z < dimensions[2]; z++) {
					final int x_ = x, y_ = y, z_ = z;
					layer &= cubes.stream().anyMatch(c -> c.pos[0] == x_ && c.pos[1] == y_ && c.pos[2] == z_);
				}
			}
			if (layer) completeLayers.add(y);
		}
		if (completeLayers.size() == 0) return false;

		double totalTime = Constants.SPAWN_TIME + completeLayers.size() * Constants.DROP_TIME;

		ArrayList<Cube> removeList = new ArrayList<>();
		for (Cube c : cubes) {
			if (completeLayers.contains(c.pos[1])) {
				module.removeCube(c, Constants.SPAWN_TIME / totalTime);
				removeList.add(c);
			}
			else {
				int depth = 0;
				for (int l : completeLayers) {
					if (l < c.pos[1]) depth++;
				}
				if (depth > 0) {
					c.pos[1] -= depth;
					module.moveCube(c, Constants.SPAWN_TIME / totalTime, (Constants.SPAWN_TIME + depth * Constants.DROP_TIME) / totalTime);
				}
			}
		}

		for (Cube cube : removeList) cubes.remove(cube);

		return true;
	}
}
