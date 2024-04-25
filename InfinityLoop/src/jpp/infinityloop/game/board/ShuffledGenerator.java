package jpp.infinityloop.game.board;

import jpp.infinityloop.game.tiles.*;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class ShuffledGenerator implements BoardGenerator{

    private int width;
    private int height;

    private int corners = 4;
    private int edges;
    private int centers;

    private HashMap<TileType, Probability> probabilities;

    private ArrayList<Tile>[][] tiles;

    private MatrixGenerator matrix;


    public ShuffledGenerator(int minWidth, int maxWidth, int minHeight, int maxHeight) {

        this.matrix = new MatrixGenerator(minWidth,maxWidth,minHeight,maxHeight);
    }

    private void getOverallProbs(){
        probabilities.put(TileType.CROSS, new Probability(0,0,0.0625,(0.0625*centers)/(width*height)));
        probabilities.put(TileType.TEE, new Probability(0,0.125, 0.25, (0.1250*edges + 0.2500*centers)/(width*height)));
        probabilities.put(TileType.BEND, new Probability(0.25,0.25,0.25, (0.25*corners + 0.25*edges + 0.25*centers)/(width*height)));
        probabilities.put(TileType.STRAIGHT, new Probability(0,0.125,0.125, (0.125*edges + 0.125*centers)/(width*height)));
        probabilities.put(TileType.DEAD_END, new Probability(0.5,0.3750,0.25,(0.5*corners + 0.3750*edges + 0.25*centers)/(width*height)));
        probabilities.put(TileType.EMPTY, new Probability(0.25, 0.1250, 0.0625, (0.25*corners + 0.125*edges + 0.0625*centers)/(width*height)));
    }

    private double getProbability(TileType tileType, Location location){

        for(Map.Entry<TileType, Probability> entry : probabilities.entrySet()){
            if(tileType.equals(entry.getKey())){
                switch (location){
                    case CORNER: return entry.getValue().getCorner();
                    case EDGE: return entry.getValue().getEdge();
                    case CENTER: return entry.getValue().getCenter();
                    default:
                        throw new IllegalStateException("No location found at getProbability");
                }
            }
        }

        throw new IllegalStateException("No tile found at getProbability");
    }


    private void updateProbability(TileType tileType, Location location){

        double newProb = 0.0;
        double oldProb = 0.0;

        int counter = 0;

        for(Map.Entry<TileType, Probability> entry : probabilities.entrySet()){
            if(entry.getKey().equals(tileType)){

                double count;
                int locals;

                switch (location){
                    case CORNER:
                        count = entry.getValue().get(location) * corners;
                        locals = corners;
                        break;
                    case EDGE:
                        count = entry.getValue().get(location) * edges;
                        locals = edges;
                        break;
                    case CENTER:
                        count = entry.getValue().get(location) * centers;
                        locals = centers;
                        break;
                    default:
                        throw new IllegalArgumentException("No location found at ShuffledGenerator.updateProbability");
                }

                count -= 1;

                newProb = count/locals;

                oldProb = entry.getValue().get(location);

                entry.getValue().set(location, newProb);

            } else {
                if(entry.getValue().get(location) != 0){
                    counter++;
                }
            }
        }

        // get the increase
        double increase;
        if(counter != 0) {
            increase = (oldProb - newProb) / counter;
        } else {
            increase = oldProb - newProb;
        }

        for(Map.Entry<TileType, Probability> entry : probabilities.entrySet()){

            if(!entry.getKey().equals(tileType)){
                if(entry.getValue().get(location) != 0){

                    // higher the probability
                    entry.getValue().set(location, entry.getValue().get(location) + increase);

                }

            }
        }
    }

    private TileType nextRandomTileType(Location location){
        HashMap<TileType, Double> currentProbs = new HashMap<>();
        int sum = 0;

        for(Map.Entry<TileType, Probability> entry : probabilities.entrySet()){
            if(entry.getValue().get(location) != 0){
                currentProbs.put(entry.getKey(), entry.getValue().get(location)*100);
                sum += entry.getValue().get(location)*100;
            }
        }

        int pick = ThreadLocalRandom.current().nextInt(1,sum+1);
        int area = 0;

        for(Map.Entry<TileType, Double> entry : currentProbs.entrySet()){

            if(pick > area && pick <= area+entry.getValue() ){
                return entry.getKey();
            }

            area += entry.getValue();
        }

        return null;
    }

    // gets code for orientation
    private int makeCode(int i, int j){
        if(i == 0|| j == 0){
            throw new IllegalArgumentException("not for left/ up side borders");
        }
        String code = "";

        // look at left neighbour
        if(j == 0 || tiles[i][j-1].get(0).getCode()[2] == 0){
            code += "0";
        } else {
            code += "1";
        }

        // top neighbour
        if(i == 0 || tiles[i-1][j].get(0).getCode()[3] == 0){
            code += "0";
        } else {
            code += "1";
        }

        return Integer.parseInt(code);
    }

    // checks if valid
    private boolean isValid(TileType tileType, Location location, int i, int j){

        if(location == Location.CORNER){
            if(tileType == TileType.CROSS || tileType == TileType.TEE || tileType == TileType.STRAIGHT){
                return false;
            }

            if(i == 0 && j == 0){
                if(tileType == TileType.DEAD_END){
                    if(ThreadLocalRandom.current().nextInt(0,1+1) == 1){
                        tiles[i][j].add(new Tile(tileType, Orientation.RIGHT));
                    } else {
                        tiles[i][j].add(new Tile(tileType, Orientation.DOWN));
                    }
                } else {
                    tiles[i][j].add(new Tile(tileType, Orientation.RIGHT));
                }
                updateProbability(tileType, location);
                return true;

            } else if( i == 0 && j+1 == width){
                if(tiles[i][j-1].get(0).getCode()[2] == 1){
                    switch (tileType){
                        case EMPTY: return false;
                        case DEAD_END:
                            tiles[i][j].add(new Tile(tileType, Orientation.LEFT));
                            break;
                        case BEND:
                            tiles[i][j].add(new Tile(tileType, Orientation.DOWN));
                            break;
                    }
                    updateProbability(tileType, location);
                    return true;
                } else {
                    if(tileType == TileType.BEND){
                        return false;
                    } else {
                        tiles[i][j].add(new Tile(tileType, Orientation.DOWN));
                        updateProbability(tileType, location);
                        return true;
                    }
                }

            } else if( i+1 == height && j == 0){
                if(tiles[i-1][j].get(0).getCode()[3] == 1){
                    if(tileType == TileType.EMPTY){
                        return false;
                    } else {
                        tiles[i][j].add(new Tile(tileType, Orientation.UP));
                        updateProbability(tileType, location);
                        return true;
                    }

                } else {
                    if(tileType == TileType.BEND){
                        return false;
                    } else {
                        tiles[i][j].add(new Tile(tileType, Orientation.RIGHT));
                        updateProbability(tileType, location);
                        return true;
                    }
                }

            } else if( i+1 == height && j+1 == width){

                switch (makeCode(i,j)){
                    case 0:
                        if(tileType == TileType.EMPTY){
                            tiles[i][j].add(new Tile(tileType, Orientation.RIGHT));
                            updateProbability(tileType, location);
                            return true;
                        } else {
                            return false;
                        }
                    case 1:
                        if(tileType == TileType.DEAD_END){
                            tiles[i][j].add(new Tile(tileType, Orientation.UP));
                            updateProbability(tileType, location);
                            return true;
                        } else {
                            return false;
                        }
                    case 10:
                        if(tileType == TileType.DEAD_END){
                            tiles[i][j].add(new Tile(tileType, Orientation.LEFT));
                            updateProbability(tileType, location);
                            return true;
                        } else {
                            return false;
                        }
                    case 11:
                        if(tileType == TileType.BEND){
                            tiles[i][j].add(new Tile(tileType, Orientation.LEFT));
                            updateProbability(tileType, location);
                            return true;
                        } else {
                            return false;
                        }
                    default:
                        throw new IllegalArgumentException("Code had an unexpected value:" + makeCode(i,j));
                }

            }

            // corners finished

        } else if(location == Location.EDGE){
            if(tileType == TileType.CROSS){
                return false;
            }

            if(j == 0){
                if(tiles[i-1][j].get(0).getCode()[3] == 1){
                    if(tileType == TileType.EMPTY){
                        return false;
                    } else {
                        tiles[i][j].add(new Tile(tileType, Orientation.UP));
                        updateProbability(tileType, location);
                        return true;
                    }

                } else {
                    if(tileType == TileType.STRAIGHT || tileType == TileType.TEE){
                        return false;
                    }
                    if(tileType == TileType.BEND || tileType == TileType.EMPTY){
                        tiles[i][j].add(new Tile(tileType, Orientation.RIGHT));
                        updateProbability(tileType, location);
                        return true;
                    }
                    if(tileType == TileType.DEAD_END){
                        if(ThreadLocalRandom.current().nextInt(0,1+1) == 1){
                            tiles[i][j].add(new Tile(tileType, Orientation.RIGHT));
                        } else {
                            tiles[i][j].add(new Tile(tileType, Orientation.DOWN));
                        }
                        updateProbability(tileType, location);
                        return true;
                    }

                }

            } else if(i == 0){
                if(tiles[i][j-1].get(0).getCode()[2] == 1){
                    if(tileType == TileType.EMPTY){
                        return false;
                    } else {
                        switch (tileType){
                            case DEAD_END:
                                tiles[i][j].add(new Tile(tileType, Orientation.LEFT));
                                break;
                            case BEND:
                                tiles[i][j].add(new Tile(tileType, Orientation.DOWN));
                                break;
                            case STRAIGHT: case TEE:
                                tiles[i][j].add(new Tile(tileType, Orientation.RIGHT));
                                break;
                            default:
                                throw new IllegalArgumentException("No Tile found at ShuffledGen. Valid");
                        }
                        updateProbability(tileType, location);
                        return true;
                    }

                } else {
                    if(tileType == TileType.STRAIGHT|| tileType == TileType.TEE){
                        return false;
                    }
                    if(tileType == TileType.BEND || tileType == TileType.EMPTY){
                        tiles[i][j].add(new Tile(tileType, Orientation.RIGHT));
                        updateProbability(tileType, location);
                        return true;
                    }
                    if(tileType == TileType.DEAD_END){
                        if(ThreadLocalRandom.current().nextInt(0,1+1) == 1){
                            tiles[i][j].add(new Tile(tileType, Orientation.RIGHT));
                        } else {
                            tiles[i][j].add(new Tile(tileType, Orientation.DOWN));
                        }
                        updateProbability(tileType, location);
                        return true;
                    }
                }


            } else if(j+1 == width){

                switch (makeCode(i, j)){
                    case 0:
                        if(tileType == TileType.EMPTY || tileType == TileType.DEAD_END){
                            tiles[i][j].add(new Tile(tileType, Orientation.DOWN));
                            updateProbability(tileType, location);
                            return true;
                        } else {
                            return false;
                        }
                    case 1:
                        if(tileType == TileType.TEE || tileType == TileType.BEND|| tileType == TileType.EMPTY){
                            return false;
                        } else {
                            tiles[i][j].add(new Tile(tileType, Orientation.UP));
                            updateProbability(tileType, location);
                            return true;
                        }
                    case 10:
                        switch (tileType){
                            case TEE: case STRAIGHT: case EMPTY:
                                return false;
                            case BEND:
                                tiles[i][j].add(new Tile(tileType, Orientation.DOWN));
                                break;
                            case DEAD_END:
                                tiles[i][j].add(new Tile(tileType, Orientation.LEFT));
                                break;
                        }
                        updateProbability(tileType, location);
                        return true;
                    case 11:
                        switch (tileType){
                            case STRAIGHT: case DEAD_END: case EMPTY:
                                return false;
                            case TEE:
                                tiles[i][j].add(new Tile(tileType, Orientation.DOWN));
                                break;
                            case BEND:
                                tiles[i][j].add(new Tile(tileType, Orientation.LEFT));
                                break;
                            default:
                                throw new IllegalArgumentException("No Tile found at ShuffledGen. Valid");
                        }
                        updateProbability(tileType, location);
                        return true;
                    default:
                        throw new IllegalArgumentException("Code had an unexpected value:" + makeCode(i,j));
                }



            } else if(i+1 == height){

                switch (makeCode(i,j)){
                    case 0:
                        if(tileType == TileType.DEAD_END || tileType == TileType.EMPTY){
                            tiles[i][j].add(new Tile(tileType, Orientation.RIGHT));
                            updateProbability(tileType, location);
                            return true;
                        } else {
                            return false;
                        }
                    case 1:
                        if(tileType == TileType.BEND){
                            tiles[i][j].add(new Tile(tileType, Orientation.UP));
                            updateProbability(tileType, location);
                            return true;
                        } else {
                            return false;
                        }
                    case 10:
                        if(tileType == TileType.STRAIGHT || tileType == TileType.DEAD_END){
                            tiles[i][j].add(new Tile(tileType, Orientation.LEFT));
                            updateProbability(tileType, location);
                            return true;
                        } else {
                            return false;
                        }
                    case 11:
                        if(tileType == TileType.TEE|| tileType == TileType.BEND){
                            tiles[i][j].add(new Tile(tileType, Orientation.LEFT));
                            updateProbability(tileType, location);
                            return true;
                        } else {
                            return false;
                        }
                    default:
                        throw new IllegalArgumentException("Code had an unexpected value:" + makeCode(i,j));
                }

            }
            // edges finished

            // center
        } else {

            switch (makeCode(i,j)){
                case 0:
                    switch (tileType){
                        case CROSS: case TEE: case STRAIGHT:
                            return false;
                        case BEND: case EMPTY:
                            tiles[i][j].add(new Tile(tileType, Orientation.RIGHT));
                            updateProbability(tileType, location);
                            return true;
                        case DEAD_END:
                            if(ThreadLocalRandom.current().nextInt(0,1+1) == 1){
                                tiles[i][j].add(new Tile(tileType, Orientation.RIGHT));
                            } else {
                                tiles[i][j].add(new Tile(tileType, Orientation.DOWN));
                            }
                            updateProbability(tileType, location);
                            return true;
                    }
                case 1:
                    switch (tileType){
                        case CROSS: case EMPTY:
                            return false;
                        case TEE: case BEND: case STRAIGHT: case DEAD_END:
                            tiles[i][j].add(new Tile(tileType, Orientation.UP));
                            updateProbability(tileType, location);
                            return true;
                    }
                case 10:
                    switch (tileType){
                        case CROSS: case EMPTY:
                            return false;
                        case TEE: case STRAIGHT:
                            tiles[i][j].add(new Tile(tileType, Orientation.RIGHT));
                            updateProbability(tileType, location);
                            return true;
                        case BEND:
                            tiles[i][j].add(new Tile(tileType, Orientation.DOWN));
                            updateProbability(tileType, location);
                            return true;
                        case DEAD_END:
                            tiles[i][j].add(new Tile(tileType, Orientation.LEFT));
                            updateProbability(tileType, location);
                            return true;
                    }
                case 11:
                    switch (tileType){
                        case EMPTY: case DEAD_END: case STRAIGHT:
                            return false;
                        case CROSS: case TEE:
                            tiles[i][j].add(new Tile(tileType, Orientation.DOWN));
                            updateProbability(tileType, location);
                            return true;
                        case BEND:
                            tiles[i][j].add(new Tile(tileType, Orientation.LEFT));
                            updateProbability(tileType, location);
                            return true;
                    }
                default:
                    throw new IllegalArgumentException("Code had an unexpected value:" + makeCode(i,j));
            }
        }

        return false;
    }


    private void makeBoard(){

        for(int i = 0; i < height; i++){
            for(int j = 0; j < width; j++){

                Location location;
                if((i == 0 && j == 0) || (i == 0 && j+1 == width) || ( i+1 == height && j == 0) || ( i+1 == height && j+1 == width)){
                    location = Location.CORNER;

                } else if(i == 0 || j == 0 || i+1 == height || j+1 == width){
                    location = Location.EDGE;
                } else {
                    location = Location.CENTER;
                }

                boolean found = false;
                TileType tileType;


                /*while(!found){
                    tileType = nextRandomTileType(location);
                    found = isValid(tileType, location, i, j);
                }*/

                stop: for(int t = 0; t < 1; t++){  //3
                    tileType = nextRandomTileType(location);
                    found = isValid(tileType, location, i, j);

                    if(found){
                        break stop;
                    }
                }

                if(!found){
                    // sort the tile by their probabilities at this spot and try if it fits


                    LinkedList<Helper> priority = new LinkedList<>();
                    priority.add(new Helper(TileType.CROSS, getProbability(TileType.CROSS, location)));
                    priority.add(new Helper(TileType.TEE, getProbability(TileType.TEE, location)));
                    priority.add(new Helper(TileType.STRAIGHT, getProbability(TileType.STRAIGHT, location)));
                    priority.add(new Helper(TileType.DEAD_END, getProbability(TileType.DEAD_END, location)));
                    priority.add(new Helper(TileType.EMPTY, getProbability(TileType.EMPTY, location)));
                    priority.add(new Helper(TileType.BEND, getProbability(TileType.BEND, location)));

                    priority.sort(Helper::compareTo);

                    end: while(!found) {
                        for (Helper h : priority) {
                            found = isValid(h.getTileType(), location, i, j);
                            if (found) {
                                break end;
                            }
                        }
                    }

                }



            }
        }
        // get random tile

        // check if it's valid

        // update probabilities
    }




    @Override
    public Board next() {

        this.width = matrix.widthFactory();
        this.height = matrix.heightFactory();

        this.edges = 2*(width-2) + 2*(height-2);
        this.centers = (width-2) * (height-2);

        this.tiles = new ArrayList[height][width];
        for(int i = 0; i < height; i++){
            for(int j = 0; j < width; j++){
                tiles[i][j] = new ArrayList<>();
            }
        }

        probabilities = new HashMap<>();
        getOverallProbs();

        makeBoard();

        Board b = new Board(width, height, tiles);
        b.shuffle();
        return b;
    }

    // maybe a status checker
    /*@Override
    public boolean hasNext() {
        return false;
    }*/

    //test
    public static void main(String[] args) {
        //System.out.println(ThreadLocalRandom.current().nextInt(1,4+1));
        String test = "1101";
        int t = Integer.parseInt(test, 2);
        //System.out.println(t);

        /*BoardGenerator generator = new ShuffledGenerator(10,10,10,10);
        Board b = generator.next();
        System.out.println(b.checkSolved());
        //System.out.println(((ShuffledGenerator) generator).width);
        //System.out.println(((ShuffledGenerator) generator).height);*/

        TreeMap<Double, String> treeMap = new TreeMap<>();
        treeMap.put(0.3, "hello");
        treeMap.put(0.2, "heo");

        //System.out.println(treeMap.firstKey());

        LinkedList<Helper> priority = new LinkedList<>();

        priority.add(new Helper(TileType.TEE, 0.4));
        priority.add(new Helper(TileType.CROSS,0.6));

        Collections.sort(priority);
        priority.sort(Helper::compareTo);

        System.out.println(priority.getFirst().getTileType());

        double a = priority.getFirst().getProbability()*100;
        int b = (int) Math.round(a);
        System.out.println(b);

        System.out.println(priority.getFirst().getProbability()*100);

    }
}
