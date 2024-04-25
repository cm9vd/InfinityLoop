package jpp.infinityloop.game.board;

import jpp.infinityloop.game.tiles.Tile;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class MatrixGenerator{

    private int minWidth;
    private int maxWidth;

    private int minHeight;
    private int maxHeight;

    public MatrixGenerator(int minWidth, int maxWidth, int minHeight, int maxHeight) {

        if(minWidth > maxWidth){
            throw new IllegalArgumentException("Illegal width boarders. (MatrixGenerator)");
        }
        if(minHeight > maxHeight){
            throw new IllegalArgumentException("Illegal height boarders. (MatrixGenerator)");
        }
        this.minWidth = minWidth;
        this.maxWidth = maxWidth;
        this.minHeight = minHeight;
        this.maxHeight = maxHeight;
    }


    public int widthFactory(){
        return ThreadLocalRandom.current().nextInt(minWidth, maxWidth+1);
    }

    public int heightFactory(){
        return ThreadLocalRandom.current().nextInt(minHeight, maxHeight+1);
    }


    public ArrayList<int[]>[][] getStartGrid(int width, int height){

        ArrayList<int[]>[][] startGrid = new ArrayList[height][width];

        for(int i = 0; i < height; i++){
            for(int j = 0; j < width; j++){
                startGrid[i][j] = new ArrayList<>();

                // catch the top edge
                if(i == 0){

                    if(j == 0){
                        startGrid[i][j].add(new int[]{0,0,1,1});
                    } else if(j+1 == width){
                        startGrid[i][j].add(new int[]{1,0,0,1});
                    } else {
                        startGrid[i][j].add(new int[]{1,0,1,1});
                    }

                // catch the down edge
                } else if(i+1 == height) {

                    if(j == 0){
                        startGrid[i][j].add(new int[]{0,1,1,0});
                    } else if(j+1 == width){
                        startGrid[i][j].add(new int[]{1,1,0,0});
                    } else {
                        startGrid[i][j].add(new int[]{1,1,1,0});
                    }

                // the middle
                } else {

                    if(j == 0){
                        startGrid[i][j].add(new int[]{0,1,1,1});
                    } else if(j+1 == width){
                        startGrid[i][j].add(new int[]{1,1,0,1});
                    } else {
                        startGrid[i][j].add(new int[]{1,1,1,1});
                    }
                }
            }
        }

        return startGrid;
    }



    /*// just methods from the UML
    // not in use yet
    public void fromWidth(int minWidth, int maxWidth){
        if(minWidth > maxWidth){
            throw new IllegalArgumentException("Illegal width boarders.");
        }

        this.minWidth = minWidth;
        this.maxWidth = maxWidth;
    }

    public void fromHeight(int minHeight, int maxHeight){
        if(minHeight > maxHeight){
            throw new IllegalArgumentException("Illegal height boarders.");
        }

        this.maxHeight = maxHeight;
        this.minHeight = minHeight;
    }*/



    //test
    public static void main(String[] args) {
        System.out.println(ThreadLocalRandom.current().nextInt(5, 5+1));
    }





}

