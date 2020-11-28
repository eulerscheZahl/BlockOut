import java.util.Random;
import java.util.Scanner;

public class Agent1 {
    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);
        Random random = new Random(0);

        // game loop
        while (true) {
            int pitWidth = in.nextInt();
            int pitHeight = in.nextInt();
            int pitDepth = in.nextInt();
            String pitShape = in.next();
            int blockCount = in.nextInt();
            for (int i = 0; i < blockCount; i++) {
                int blockIndex = in.nextInt();
                int width = in.nextInt();
                int height = in.nextInt();
                int depth = in.nextInt();
                String shape = in.next();
            }

            // Write an action using System.out.println()
            // To debug: System.err.println("Debug messages...");

            System.out.println(random.nextInt(blockCount) + " " + random.nextInt(3) + " " + random.nextInt(3));
        }
    }
}