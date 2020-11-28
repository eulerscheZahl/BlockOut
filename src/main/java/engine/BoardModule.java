package engine;

import com.codingame.game.Player;
import com.codingame.gameengine.core.Module;
import com.codingame.gameengine.core.SoloGameManager;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class BoardModule implements Module {
	private String toSend = "";
	private SoloGameManager<Player> gameManager;

	@Inject
	public BoardModule(SoloGameManager<Player> gameManager) {
		this.gameManager = gameManager;
		gameManager.registerModule(this);
	}

	@Override
	public void onGameInit() {
	}

	@Override
	public void onAfterGameTurn() {
		if (toSend.length() > 0) gameManager.setViewData("board", toSend.substring(1));
		toSend = "";
	}

	@Override
	public void onAfterOnEnd() {
		// TODO Auto-generated method stub

	}

	public void spawnBlock(Block block, double spawnDuration, int dropDepth) {
		for (Cube c : block.cubes)
			toSend += ";S " + c.id + " " + spawnDuration + " " + c.pos[0] + " " + c.pos[1] + " " + c.pos[2] + " " + dropDepth;
	}

	public void removeCube(Cube c, double endTime) {
		toSend += ";R " + c.id + " " + endTime;
	}

	public void moveCube(Cube c, double startTime, double endTime) {
		toSend += ";M " + c.id + " " + c.pos[1] + " " + startTime + " " + endTime;
	}

	public void definePit(int width, int height, int depth) {
		toSend += ";P " + width + " " + height + " " + depth;
	}
}
