package agents;

/**
 * Created by Jakub Fortunka on 07.03.15
 *
 * Enum responsible for representing types of agents that are in the battle (including obstacles)
 *
 */
public enum AgentType {
    WARRIOR("main/resources/images/warrior.png",20, "Warrior"),
    ARCHER("main/resources/images/archer.png",20, "Archer"),
    COMMANDER("main/resources/images/commander.png",20, "Commander"),
    OBSTACLE("main/resources/images/obstacle.png",20, "Obstacle");

    private String imagePath;
    private int size;
    private String name;

    private AgentType(String pathToImage, int size, String name) {
        imagePath = pathToImage;
        this.size = size;
        this.name = name;
    }

    public String getImagePath() {
        return imagePath;
    }

    public int getSize() {
        return size;
    }

    @Override
    public String toString() {
        return name;
    }
}
