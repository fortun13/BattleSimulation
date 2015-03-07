package utils;

/**
 * Created by KrzysiekH on 2015-02-22.
 */
public class SquareSize {
    private static int value = 20;
    private static SquareSize instance = new SquareSize();

    private SquareSize() {}
    public static SquareSize getInstance() { return instance; }
    public int getValue() { return value; }
}
