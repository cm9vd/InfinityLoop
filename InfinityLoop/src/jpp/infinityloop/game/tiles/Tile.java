package jpp.infinityloop.game.tiles;

public class Tile {

    private final TileType type;
    private Orientation orientation;

    private int priority;

    public Tile(TileType type, Orientation orientation) {
        this.type = type;
        this.orientation = orientation;
        this.priority = 0;
    }

    public void rotate(){
        switch(orientation){
            case LEFT: orientation = Orientation.UP;
            break;
            case UP: orientation = Orientation.RIGHT;
            break;
            case RIGHT: orientation = Orientation.DOWN;
            break;
            case DOWN: orientation = Orientation.LEFT;
            break;
            default:
                throw new IllegalStateException("No orientation found at Tile.rotate()");
        }
    }

    public int[] getCode(){
        switch (type){
            case CROSS: return new int[]{1,1,1,1};
            case EMPTY: return new int[]{0,0,0,0};
            case DEAD_END:
                switch (orientation){
                    case LEFT: return new int[]{1,0,0,0};
                    case UP: return new int[]{0,1,0,0};
                    case RIGHT: return new int[]{0,0,1,0};
                    case DOWN: return new int[]{0,0,0,1};
                    default: throw new IllegalStateException("No orientation found for DEAD_END at Tile.getCode");
                }
            case TEE:
                switch (orientation){
                    case LEFT: return new int[]{1,1,1,0};
                    case UP: return new int[]{0,1,1,1};
                    case RIGHT: return new int[]{1,0,1,1};
                    case DOWN: return new int[]{1,1,0,1};
                    default: throw new IllegalStateException("No orientation found for TEE at Tile.getCode");
                }
            case BEND:
                switch (orientation){
                    case LEFT: return new int[]{1,1,0,0};
                    case UP: return new int[]{0,1,1,0};
                    case RIGHT: return new int[]{0,0,1,1};
                    case DOWN: return new int[]{1,0,0,1};
                    default: throw new IllegalStateException("No orientation found for BEND at Tile.getCode");
                }
            case STRAIGHT:
                switch (orientation){
                    case LEFT: case RIGHT: return new int[]{1,0,1,0};
                    case UP: case DOWN: return new int[]{0,1,0,1};
                    default: throw new IllegalStateException("No orientation found for STRAIGHT at Tile.getCode");
                }
            default:
                throw new IllegalStateException("No TileType found at Tile.getCode");

        }
    }

    public int getPriority() {
        return priority;
    }

    public TileType getType() {
        return type;
    }

    public Orientation getOrientation() {
        return orientation;
    }

    public void setPriority(int priority) {
        if(this.priority >= 3 && priority < 3){
            throw new IllegalStateException("Illegal tile change");
        }
        this.priority = priority;
    }
}
