package jpp.infinityloop;

import javafx.stage.FileChooser;
import jpp.infinityloop.game.board.*;
import jpp.infinityloop.game.tiles.Orientation;
import jpp.infinityloop.game.tiles.Tile;
import jpp.infinityloop.game.tiles.TileType;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Set;

public class Controller {

    private BoardGenerator generator;

    public Controller(BoardGenerator generator) {
        this.generator = generator;
    }

    public Controller() {
    }

    public void handleSave(File file, Board board){
        byte[] code = BlobCoder.encode(board);


        try{
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(code);
            fos.close();
        } catch (IOException io) {
            System.err.println("Unable to write to file.");
        }


        //String fileName = file.toString();
        //Path path = Paths.get(fileName);
        //Path path = file.toPath();

        /*try {
            Files.write(path, code);

        } catch(IOException write){
            // create the file

            try{
                Files.createDirectories(path.getParent());
                Files.createFile(path);
            } catch(IOException create){
                System.err.println("Creating new File was unsuccessful.");
            }

            // write in new file
            try{
                Files.write(path,code);
            } catch (IOException writeFresh){
                System.err.println("Couldn't write in fresh file.");
            }

        }*/

    }

    public void handleOpen(Path path){
        this.generator = new FilesGenerator(path);
    }

    public void handleShuffle(){
        int minWidth = Settings.getInstance().getMinWidth();
        int maxWidth = Settings.getInstance().getMaxWidth();
        int minHeight = Settings.getInstance().getMinHeight();
        int maxHeight = Settings.getInstance().getMaxHeight();
        this.generator = new ShuffledGenerator(minWidth, maxWidth, minHeight, maxHeight);
    }

    public Board handleNext(){
        return generator.next();
    }

    public Board handleSolve(Board board){

        // make a fresh board
        ArrayList<Tile>[][] tiles = new ArrayList[board.getHeight()][board.getWidth()];
        for(int i = 0; i < board.getHeight(); i++){
            for(int j = 0; j < board.getWidth(); j++){
                tiles[i][j] = new ArrayList<>();
                tiles[i][j].add(new Tile(board.getTiles()[i][j].get(0).getType(), board.getTiles()[i][j].get(0).getOrientation()));
            }
        }
        Board fresh = new Board(board.getWidth(), board.getHeight(), tiles);

        BoardSolver solver = new BoardSolver();

        if(!solver.solve(fresh)){
            return null;
        } else {
            return fresh;
        }
    }



    //test
    public static void main(String[] args) {

        ArrayList<Tile>[][] tiles = new ArrayList[2][3];
        for(int i = 0; i < 2; i++){
            for(int j = 0; j < 3; j++){
                tiles[i][j] = new ArrayList<>();
            }
        }
        tiles[0][0].add(new Tile(TileType.BEND, Orientation.LEFT));
        tiles[0][1].add(new Tile(TileType.TEE, Orientation.RIGHT));
        tiles[0][2].add(new Tile(TileType.BEND, Orientation.UP));
        tiles[1][0].add(new Tile(TileType.BEND, Orientation.DOWN));
        tiles[1][1].add(new Tile(TileType.TEE, Orientation.LEFT));
        tiles[1][2].add(new Tile(TileType.BEND, Orientation.RIGHT));

        Board b = new Board(3,2,tiles);

        Controller controller = new Controller();


    }
}
