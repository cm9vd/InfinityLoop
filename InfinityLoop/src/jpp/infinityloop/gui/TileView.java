package jpp.infinityloop.gui;

import javafx.animation.Interpolator;
import javafx.animation.RotateTransition;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.effect.Light;
import javafx.scene.effect.Lighting;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import jpp.infinityloop.SolveListener;
import jpp.infinityloop.game.board.Board;
import jpp.infinityloop.game.tiles.Tile;

public class TileView extends ImageView {

    private Tile tile;
    private ContentPane pane;
    private int cols;
    private int rows;
    private Board board;

    private Image image;

    private final RotateTransition rotate;

    public TileView (Tile tile, String color, ContentPane pane, int cols, int rows, Board board, boolean solved){

        this.tile = tile;
        if(solved){
            searchSolved();
        } else {
            searchImage();
        }

        this.setImage(this.image);

        this.pane = pane;
        this.cols = cols;
        this.rows = rows;
        this.board = board;

        // color lighting
        Lighting lighting = new Lighting();
        lighting.setDiffuseConstant(1.0);
        lighting.setSpecularConstant(0.0);
        lighting.setSpecularExponent(0.0);
        lighting.setSurfaceScale(0.0);
        lighting.setLight(new Light.Distant(45,45, Color.web(color))); // tile color

        this.setEffect(lighting);

        //fitting
        this.setPreserveRatio(true);
        this.setSmooth(true);
        //this.setCache(true);

        this.fitHeightProperty().bind(pane.heightProperty().divide(rows));

        // rotation
        this.rotate =  new RotateTransition(Duration.seconds(0.15), this);
        rotate.setByAngle(90);
        rotate.setInterpolator(Interpolator.LINEAR);
        rotate.setCycleCount(0);
    }

    public void playRotate(){
        rotate.play();
    }

    public VBox toBox(){
        VBox vBox = new VBox(this);

        EventHandler eventHandler = new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {

                if(mouseEvent.getClickCount() == 1) {
                    rotate.play();

                    // the board has to know when this happens
                    tile.rotate();

                    rotate.setOnFinished(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent actionEvent) {
                            // check if the board is solved and change the color
                            if (board.checkSolved()) {
                                pane.setColorSolved();
                            }
                        }
                    });
                }
            }
        };

        vBox.setOnMouseClicked(eventHandler);

        return vBox;
    }

    public void tileResizeForHeight(){
        this.fitHeightProperty().bind(pane.heightProperty().subtract(25.0).divide(rows).subtract(1));
    }

    public void tileResizeForWidth(){
        this.fitWidthProperty().bind(pane.widthProperty().divide(cols).subtract(1));
    }

    private void searchImage(){
        StringBuilder binaryString = new StringBuilder();

        for(int c = 0; c < 4; c++){
            binaryString.append(tile.getCode()[c]);
        }
        int code = Integer.parseInt(binaryString.toString(), 2);

        switch(code){
            case 0:
                this.image = new Image("/jpp/infinityloop/images/empty.png");
                break;
            case 1:
                this.image = new Image("/jpp/infinityloop/images/deadend.png");
                this.setRotate(this.getRotate() + 270);
                break;
            case 2:
                this.image = new Image("/jpp/infinityloop/images/deadend.png");
                this.setRotate(this.getRotate() + 180);
                break;
            case 3:
                this.image = new Image("/jpp/infinityloop/images/bend.png");
                this.setRotate(this.getRotate() + 180);
                break;
            case 4:
                this.image = new Image("/jpp/infinityloop/images/deadend.png");
                this.setRotate(this.getRotate() + 90);
                break;
            case 5:
                this.image = new Image("/jpp/infinityloop/images/straight.png");
                this.setRotate(this.getRotate() + 90);
                break;
            case 6:
                this.image = new Image("/jpp/infinityloop/images/bend.png");
                this.setRotate(this.getRotate() + 90);
                break;
            case 7:
                this.image = new Image("/jpp/infinityloop/images/tee.png");
                this.setRotate(this.getRotate() + 90);
                break;
            case 8:
                this.image = new Image("/jpp/infinityloop/images/deadend.png");
                break;
            case 9:
                this.image = new Image("/jpp/infinityloop/images/bend.png");
                this.setRotate(this.getRotate() + 270);
                break;
            case 10:
                this.image = new Image("/jpp/infinityloop/images/straight.png");
                break;
            case 11:
                this.image = new Image("/jpp/infinityloop/images/tee.png");
                this.setRotate(this.getRotate() + 180);
                break;
            case 12:
                this.image = new Image("/jpp/infinityloop/images/bend.png");
                break;
            case 13:
                this.image = new Image("/jpp/infinityloop/images/tee.png");
                this.setRotate(this.getRotate() + 270);
                break;
            case 14:
                this.image = new Image("/jpp/infinityloop/images/tee.png");
                break;
            case 15:
                this.image = new Image("/jpp/infinityloop/images/cross.png");
                break;
            default:
                throw new IllegalStateException("No Image found for code: +" + code + " at TileView.searchImage");

        }
    }

    private void searchSolved(){
        StringBuilder binaryString = new StringBuilder();

        for(int c = 0; c < 4; c++){
            binaryString.append(tile.getCode()[c]);
        }
        int code = Integer.parseInt(binaryString.toString(), 2);

        switch(code){
            case 0:
                this.image = new Image("/jpp/infinityloop/images/empty.png");
                break;
            case 1:
                this.image = new Image("/jpp/infinityloop/images/deadend_o.png");
                this.setRotate(this.getRotate() + 270);
                break;
            case 2:
                this.image = new Image("/jpp/infinityloop/images/deadend_o.png");
                this.setRotate(this.getRotate() + 180);
                break;
            case 3:
                this.image = new Image("/jpp/infinityloop/images/bend_o.png");
                this.setRotate(this.getRotate() + 180);
                break;
            case 4:
                this.image = new Image("/jpp/infinityloop/images/deadend_o.png");
                this.setRotate(this.getRotate() + 90);
                break;
            case 5:
                this.image = new Image("/jpp/infinityloop/images/straight_o.png");
                this.setRotate(this.getRotate() + 90);
                break;
            case 6:
                this.image = new Image("/jpp/infinityloop/images/bend_o.png");
                this.setRotate(this.getRotate() + 90);
                break;
            case 7:
                this.image = new Image("/jpp/infinityloop/images/tee_o.png");
                this.setRotate(this.getRotate() + 90);
                break;
            case 8:
                this.image = new Image("/jpp/infinityloop/images/deadend_o.png");
                break;
            case 9:
                this.image = new Image("/jpp/infinityloop/images/bend_o.png");
                this.setRotate(this.getRotate() + 270);
                break;
            case 10:
                this.image = new Image("/jpp/infinityloop/images/straight_o.png");
                break;
            case 11:
                this.image = new Image("/jpp/infinityloop/images/tee_o.png");
                this.setRotate(this.getRotate() + 180);
                break;
            case 12:
                this.image = new Image("/jpp/infinityloop/images/bend_o.png");
                break;
            case 13:
                this.image = new Image("/jpp/infinityloop/images/tee_o.png");
                this.setRotate(this.getRotate() + 270);
                break;
            case 14:
                this.image = new Image("/jpp/infinityloop/images/tee_o.png");
                break;
            case 15:
                this.image = new Image("/jpp/infinityloop/images/cross_o.png");
                break;
            default:
                throw new IllegalStateException("No Image found for code: +" + code + " at TileView.searchImage");

        }
    }

}
