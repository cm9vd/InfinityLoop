package jpp.infinityloop.gui;

import javafx.geometry.Pos;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import jpp.infinityloop.game.board.Board;

import java.util.ArrayList;

public class TileGrid extends TilePane{

    private Board board;

    private ArrayList<TileView>[][] tileViews;
    private boolean solved;

    public TileGrid(String contentColor, Board board, ContentPane pane, boolean solved) {
        this.board = board;
        this.solved = solved;

        this.setAlignment(Pos.CENTER);

        this.setPrefColumns(board.getWidth());
        this.setPrefRows(board.getHeight());

        this.tileViews = new ArrayList[board.getHeight()][board.getWidth()];
        for(int i = 0; i < board.getHeight(); i++){
            for(int j = 0; j < board.getWidth(); j++){
                tileViews[i][j] = new ArrayList<>();
            }
        }

        fillTilePane(pane, contentColor);
    }


    private void fillTilePane(ContentPane pane, String contentColor){
        for(int i = 0; i < board.getHeight(); i++){
            for(int j = 0; j < board.getWidth(); j++){
                TileView tileView = new TileView(board.getTiles()[i][j].get(0), contentColor, pane, board.getWidth(), board.getHeight(), board, solved);
                tileViews[i][j].add(tileView);
                this.getChildren().add(tileView.toBox());
            }
        }
    }

    public VBox toBox(){
        VBox vBox = new VBox(this);
        vBox.setAlignment(Pos.CENTER);
        vBox.setFillWidth(false);

        return vBox;
    }

    public ArrayList<TileView>[][] getTileViews() {
        return tileViews;
    }
}
