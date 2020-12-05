package com.codingame.game;

import java.util.ArrayList;
import java.util.Random;

import com.codingame.gameengine.core.AbstractPlayer.TimeoutException;
import com.codingame.gameengine.core.AbstractReferee;
import com.codingame.gameengine.core.SoloGameManager;
import com.google.inject.Inject;

import engine.Block;
import engine.BoardModule;
import engine.Pit;

public class Referee extends AbstractReferee {
	@Inject
	private SoloGameManager<Player> gameManager;
	@Inject
	private BoardModule board;

	private Pit pit;
	private ArrayList<Block> blocks = new ArrayList<>();
	private Random random;
	private boolean lost = false;

	@Override
	public void init() {
		String[] parts = gameManager.getTestCaseInput().get(0).split(";");
		random = new Random(Integer.parseInt(parts[0]));
		String[] pitDim = parts[1].split(" ");
		pit = new Pit(gameManager, board, Integer.parseInt(pitDim[0]), Integer.parseInt(pitDim[1]), Integer.parseInt(pitDim[2]));
		ArrayList<Block> tooLarge = new ArrayList<>();
		for (int i = 2; i < parts.length; i++) {
			Block block = new Block(parts[i]);
			if (block.maxDimension() <= pit.minDimension()) blocks.add(block);
			else tooLarge.add(block);
		}
		if (blocks.size() == 0) blocks = tooLarge; // tetris testcase
	}

	@Override
	public void onEnd() {
		if (!lost) gameManager.winGame();
	}

	private void loseGame(String message) {
		if (lost) return;
		gameManager.loseGame(message);
		lost = true;
	}

	@Override
	public void gameTurn(int turn) {
		if (pit.removeLayers()) { // create an extra frame in the replay
			gameManager.setMaxTurns(gameManager.getMaxTurns() + 1);
			return;
		}

		ArrayList<String> inputs = new ArrayList<String>();
		inputs.add(pit.toString());
		Block toPlace = blocks.get(random.nextInt(blocks.size()));
		ArrayList<Block> rotations = toPlace.createRotations();
		inputs.add(String.valueOf(rotations.size()));
		for (int i = 0; i < rotations.size(); i++) inputs.add(i + " " + rotations.get(i));

		for (String s : inputs) gameManager.getPlayer().sendInputLine(s);
		gameManager.getPlayer().execute();
		try {
			String output = gameManager.getPlayer().getOutputs().get(0);
			String[] parts = output.split(" ");
			Block taken = rotations.get(Integer.parseInt(parts[0]));
			int x = Integer.parseInt(parts[1]);
			int z = Integer.parseInt(parts[2]);
			pit.placeBlock(taken, x, z);
			if (x < 0 || x + taken.dimensions[0] > pit.dimensions[0]) throw new Exception("x coordinate out of range");
			if (z < 0 || z + taken.dimensions[2] > pit.dimensions[2]) throw new Exception("z coordinate out of range");
		} catch (TimeoutException e) {
			loseGame("timeout");
		} catch (IndexOutOfBoundsException e) {
			loseGame("command in wrong format");
		} catch (NumberFormatException e) {
			loseGame("failed to parse number");
		} catch (Exception e) {
			loseGame(e.getMessage());
		}

		if (pit.hasLost()) loseGame("The pit is full");
	}
}
