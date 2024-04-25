package jpp.infinityloop.game.board;

import jpp.infinityloop.Controller;
import jpp.infinityloop.game.tiles.Orientation;
import jpp.infinityloop.game.tiles.Tile;
import jpp.infinityloop.game.tiles.TileType;

import java.util.ArrayList;
import java.util.Random;

public class Board {

    private int width;
    private int height;

    private ArrayList<Tile>[][] tiles;

    public Board(int width, int height, ArrayList<Tile>[][] tiles) {
        this.width = width;
        this.height = height;

        this.tiles = tiles;
    }


    public void rotate(int x, int y){
        tiles[y][x].get(0).rotate();
    }


    public void shuffle(){
        Random rd = new Random();
        int rot;

        for(int i = 0; i < height; i ++){
            for(int j = 0; j < width; j++){
                rot = rd.nextInt(4);
                for(int k = 0; k < rot; k++){
                    tiles[i][j].get(0).rotate();
                }
            }
        }
    }


    public boolean checkSolved(){

        int[] code;
        int[] neighbour;

        // get every field
        for(int i = 0; i < height; i++){
            for(int j = 0; j < width; j++){

                code = tiles[i][j].get(0).getCode();

                // check its code
                for(int c = 0; c < 4; c++){

                    if(code[c] == 1){
                        switch (c){

                            case 0:
                                if(j == 0){
                                    return false;
                                } else {
                                    neighbour = tiles[i][j-1].get(0).getCode();
                                    if(neighbour[2] != 1){
                                        return false;
                                    }
                                }
                                break;
                            case 1:
                                if(i == 0){
                                    return false;
                                } else {
                                    neighbour = tiles[i-1][j].get(0).getCode();
                                    if(neighbour[3] != 1){
                                        return false;
                                    }
                                }
                                break;
                            case 2:
                                if(j+1 == width){
                                    return false;
                                } else {
                                    neighbour = tiles[i][j+1].get(0).getCode();
                                    if(neighbour[0] != 1){
                                        return false;
                                    }
                                }
                                break;
                            case 3:
                                if(i+1 == height){
                                    return false;
                                } else {
                                    neighbour = tiles[i+1][j].get(0).getCode();
                                    if(neighbour[1] != 1){
                                        return false;
                                    }
                                }
                                break;

                            default:
                                throw new IllegalStateException("Went out of neighbour's code bounce. (Board.checkSolved)");
                        }
                        // switch end
                    }
                    // check next code position
                }
                // got through all positions
            }
            // next row
        }
        // finished check

        return true;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public ArrayList<Tile>[][] getTiles() {
        return tiles;
    }

    //test
    public static void main(String[] args) {
        Random rd = new Random();
        int rot = rd.nextInt(4);
        //System.out.println(rot);
        Controller controller = new Controller();
        controller.handleShuffle();
        Board board = controller.handleNext();

        for(int i = 0; i < 10000*10; i++){
            board.checkSolved();
        }

        System.out.println("stop");
    }



}
