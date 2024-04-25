package jpp.infinityloop.game.board;


import com.sun.org.apache.xpath.internal.operations.Or;
import jpp.infinityloop.Controller;
import jpp.infinityloop.game.tiles.Orientation;
import jpp.infinityloop.game.tiles.Tile;
import jpp.infinityloop.game.tiles.TileType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

public class BoardSolver implements Solver{

    private ArrayList<Tile>[][] tiles;

    public BoardSolver() {}

    private LinkedList<Orientation> possibilities (int i, int j, Board board){

        StringBuilder code  = new StringBuilder();
        Tile tile = tiles[i][j].get(0);
        TileType tileType = tile.getType();
        //ArrayList<Orientation> possList = new ArrayList<>();
        LinkedList<Orientation> possList = new LinkedList<>();

        // look at left neighbour
        if(j == 0 || tiles[i][j-1].get(0).getCode()[2] == 0){
            code.append("0");
        } else {
            code.append("1");
        }

        // top neighbour
        if(i == 0 || tiles[i-1][j].get(0).getCode()[3] == 0){
            code.append("0");
        } else {
            code.append("1");
        }

        switch (Integer.parseInt(code.toString())){

            case 0:
                switch (tileType){
                    case DEAD_END:
                        if(j+1 != board.getWidth() && !(tiles[i][j+1].get(0).getType().equals(TileType.EMPTY))){
                            possList.add(Orientation.RIGHT);
                        }
                        if(i+1 != board.getHeight() && !(tiles[i+1][j].get(0).getType().equals(TileType.EMPTY))){
                            possList.add(Orientation.DOWN);
                        }
                        break;
                    case BEND:
                        if((j+1 != board.getWidth() && !(tiles[i][j+1].get(0).getType().equals(TileType.EMPTY)))
                                && (i+1 != board.getHeight() && !(tiles[i+1][j].get(0).getType().equals(TileType.EMPTY)))){
                            possList.add(Orientation.RIGHT);
                        }
                        break;
                    case EMPTY:
                        possList.add(Orientation.UP);
                        break;
                }
                break;
            case 1:
                switch (tileType){
                    case DEAD_END:
                        possList.add(Orientation.UP);
                        break;
                    case BEND:
                        if(j+1 != board.getWidth() && !(tiles[i][j+1].get(0).getType().equals(TileType.EMPTY))){
                            possList.add(Orientation.UP);
                        }
                        break;
                    case STRAIGHT:
                        if(i+1 != board.getHeight() && !(tiles[i+1][j].get(0).getType().equals(TileType.EMPTY))){
                            possList.add(Orientation.UP);
                        }
                        break;
                    case TEE:
                        if((j+1 != board.getWidth() && !(tiles[i][j+1].get(0).getType().equals(TileType.EMPTY)))
                                && (i+1 != board.getHeight() && !(tiles[i+1][j].get(0).getType().equals(TileType.EMPTY)))){
                            possList.add(Orientation.UP);
                        }
                        break;
                }
                break;
            case 10:
                switch (tileType){
                    case DEAD_END:
                        possList.add(Orientation.LEFT);
                        break;
                    case BEND:
                        if(i+1 != board.getHeight() && !(tiles[i+1][j].get(0).getType().equals(TileType.EMPTY))){
                            possList.add(Orientation.DOWN);
                        }
                        break;
                    case STRAIGHT:
                        if(j+1 != board.getWidth() && !(tiles[i][j+1].get(0).getType().equals(TileType.EMPTY))){
                            possList.add(Orientation.LEFT);
                        }
                        break;
                    case TEE:
                        if((j+1 != board.getWidth() && !(tiles[i][j+1].get(0).getType().equals(TileType.EMPTY)))
                                && (i+1 != board.getHeight() && !(tiles[i+1][j].get(0).getType().equals(TileType.EMPTY)))){
                            possList.add(Orientation.RIGHT);
                        }
                        break;
                }
                break;
            case 11:
                switch (tileType){
                    case BEND:
                        possList.add(Orientation.LEFT);
                        break;
                    case TEE:
                        if(j+1 != board.getWidth() && !(tiles[i][j+1].get(0).getType().equals(TileType.EMPTY))){
                            possList.add(Orientation.LEFT);
                        }
                        if(i+1 != board.getHeight() && !(tiles[i+1][j].get(0).getType().equals(TileType.EMPTY))){
                            possList.add(Orientation.DOWN);
                        }
                        break;
                    case CROSS:
                        if((j+1 != board.getWidth() && !(tiles[i][j+1].get(0).getType().equals(TileType.EMPTY)))
                                && (i+1 != board.getHeight() && !(tiles[i+1][j].get(0).getType().equals(TileType.EMPTY)))){
                            possList.add(Orientation.RIGHT);
                        }
                        break;
                }

                break;
            default:
                throw new IllegalStateException("Code went out of bounce at BoardSolver.possibilities.");
        }
        return possList;
    }


    private HashMap<String, LinkedList> refreshPossMap(Board board, int a, int b, HashMap possMap){

        if(b+1 == board.getWidth()){
            a += 1;
            b = 0;
        } else {
            b += 1;
        }

        if(a != board.getHeight()){

            String d = Integer.toString(a);
            d += Integer.toString(b);
            possMap.put(d, possibilities(a,b,board));

        }

        return possMap;
    }

    @Override
    public boolean solve(Board board) {
        if(board == null){
            throw new IllegalArgumentException("Missing parameter.");
        }

        if(board.checkSolved()){
            return true;
        }

        this.tiles = board.getTiles();
        HashMap<String, LinkedList> possMap = new HashMap<>();

        possMap.put("00", possibilities(0,0,board));


        for(int i = 0; i < board.getHeight(); i++) {
            for (int j = 0; j < board.getWidth(); j++) {

                if(possMap.get(Integer.toString(i)+Integer.toString(j)).isEmpty()){

                    if(j == 0 && i > 0){
                        j = board.getWidth()-2;
                        i = i-1;
                    } else if( j == 0 && i == 0){
                        return false;
                    } else {
                        j -= 2;
                    }


                } else {

                    Orientation orientation = (Orientation) possMap.get(Integer.toString(i)+Integer.toString(j)).pollFirst();

                    while (tiles[i][j].get(0).getOrientation() != orientation) {
                        tiles[i][j].get(0).rotate();
                    }

                    possMap = refreshPossMap(board, i, j, possMap);

                }



            }   // for loop
        }   // for loop


        return board.checkSolved();
    }

    //test
    public static void main(String[] args) {
        Controller controller = new Controller();
        controller.handleShuffle();
        Board board = controller.handleNext();

        BoardSolver solver = new BoardSolver();

        /*System.out.println("test results:");

        System.out.println(solver.solve(board));
        System.out.println(board.checkSolved());

        Board test = solver.solveHelper(controller.handleNext());
        System.out.println(test.checkSolved());

        for(int i = 0; i < 100*10; i++){
            solver.solve(controller.handleNext());
        }*/


        //solver.solve(board);
        //System.out.println(board.checkSolved());
        //System.out.println(solver.solveHelper(board));

        System.out.println(solver.solve(board));

    }
}
