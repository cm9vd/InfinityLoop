package jpp.infinityloop.adapter;

import jpp.infinityloop.BlobCoder;
import jpp.infinityloop.DummyListener;
import jpp.infinityloop.game.board.*;
import jpp.infinityloop.game.tiles.Tile;

import java.util.ArrayList;

public class TestAdapter implements ITestAdapter<Board> {

    private BoardGenerator boardGenerator;

    public TestAdapter() {
    }

    public Board decode(byte[] data){
        if(data == null){
            throw new IllegalArgumentException("Missing parameter.");
        }
        Board board = BlobCoder.decode(data);
        if(board == null){
            throw new IllegalArgumentException("Wrong file format.");
        }
        return board;
    }

    public byte[] encode(Board board){
        if(board == null){
            throw new IllegalArgumentException("Missing parameter.");
        }
        return BlobCoder.encode(board);
    }

    public boolean solve(Board board){
        if(board == null){
            throw new IllegalArgumentException("Missing parameter.");
        }
        BoardSolver solver = new BoardSolver();
        return solver.solve(board);
    }

    public void initGenerator(int minWidth, int maxWidth, int minHeight, int maxHeight){
        this.boardGenerator = new ShuffledGenerator(minWidth, maxWidth, minHeight, maxHeight);
    }

    public Board generate(){
        if(boardGenerator == null){
            throw new IllegalStateException("No generator initialized.");
        }
        return boardGenerator.next();
    }

    public void rotate(Board board, int column, int row){
        ArrayList<Tile>[][] tiles = board.getTiles();
        tiles[row][column].get(0).rotate();
    }
}
