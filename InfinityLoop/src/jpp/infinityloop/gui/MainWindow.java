package jpp.infinityloop.gui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.TilePane;
import javafx.stage.Stage;
import jpp.infinityloop.Controller;
import jpp.infinityloop.game.board.Board;
import jpp.infinityloop.game.tiles.Tile;

import java.util.ArrayList;

public class MainWindow extends Application {

    private Board board;

    private int rows;
    private int cols;

    private ContentPane contentPane;

    private Stage primaryStage;


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

        this.primaryStage = primaryStage;

        Controller controller = new Controller();
        controller.handleShuffle();
        this.board = controller.handleNext();

        this.rows = board.getHeight();
        this.cols = board.getWidth();


        this.contentPane = new ContentPane(board, primaryStage);

        primaryStage.setMinHeight(rows*50);       // rows *50
        primaryStage.setMinWidth(cols*50);        // columns *50

        Scene scene  = new Scene(contentPane, cols*75,rows*75);
        primaryStage.setScene(scene);
        primaryStage.setTitle("\u221e Infinity Loop");
        primaryStage.show();

    }
}
