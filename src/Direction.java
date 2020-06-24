package scrabble;

/**
 * Enum of directions. Contains method to reverse direction.
 * @author Sam Cox
 * @version date 3/6/20
 */
public enum Direction {
    UP,
    DOWN,
    LEFT,
    RIGHT,
    NONE;

    /**
     * Reverses the given direction.
     * @param direction direction to be reversed
     * @return reversed direction
     */
    protected static Direction reverseDirection(Direction direction){
        switch (direction){
            case UP:
                return DOWN;
            case DOWN:
                return UP;
            case LEFT:
                return RIGHT;
            case RIGHT:
                return LEFT;
            default:
                return NONE;
        }
    }
}
