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
    @Inject private SoloGameManager<Player> gameManager;
    @Inject private BoardModule board;

    private Pit pit;
    private ArrayList<Block> blocks = new ArrayList<>();
    private Random random;
    @Override
    public void init() {
    	String[] parts = gameManager.getTestCaseInput().get(0).split(";");
    	random = new Random(Integer.parseInt(parts[0]));
    	String[] pitDim = parts[1].split(" ");
    	pit = new Pit(gameManager, board, Integer.parseInt(pitDim[0]), Integer.parseInt(pitDim[1]), Integer.parseInt(pitDim[2]));
    	for (int i = 2; i < parts.length; i++) blocks.add(new Block(parts[i]));
    }

    @Override
    public void gameTurn(int turn) {
    	if (pit.removeLayers()) {
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
	        pit.placeBlock(taken, Integer.parseInt(parts[1]), Integer.parseInt(parts[2]));
		} catch (TimeoutException e) {
			gameManager.loseGame("timeout");
			return;
		} catch (Exception e) {
			gameManager.loseGame("command in wrong format");
			return;
		}
    	
    	if (pit.hasLost()) gameManager.loseGame("The pit is full");
    }
}
