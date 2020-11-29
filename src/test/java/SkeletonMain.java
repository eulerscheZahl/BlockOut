import com.codingame.gameengine.runner.SoloGameRunner;

public class SkeletonMain {
    public static void main(String[] args) {

        SoloGameRunner gameRunner = new SoloGameRunner();
        gameRunner.setAgent(Agent2.class);
        gameRunner.setTestCase("test1.json");

        gameRunner.start();
    }
}
