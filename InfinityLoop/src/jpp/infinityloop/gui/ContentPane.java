package jpp.infinityloop.gui;

import javafx.animation.*;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import jpp.infinityloop.BlobCoder;
import jpp.infinityloop.Controller;
import jpp.infinityloop.DummyListener;
import jpp.infinityloop.Settings;
import jpp.infinityloop.game.board.Board;
import jpp.infinityloop.game.board.ShuffledGenerator;
import jpp.infinityloop.game.tiles.TileType;
import sun.applet.Main;

import java.io.File;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class ContentPane extends BorderPane {

    private Board board;
    private String[] colors;
    private int colorTheme;
    private int colorMode;
    private Controller controller;

    private FileChooser fileChooser;
    private Stage stage;

    private TileGrid tileGrid;

    private ContentPane contentPane;

    private SequentialTransition sequentialTransition;

    public ContentPane (Board board, Stage stage){
        this.board = board;
        this.stage = stage;
        this.contentPane = this;

        this.sequentialTransition = new SequentialTransition();

        this.controller = new Controller();

        this.fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Level File", "*.bin"));

        colorTheme = 5;
        colorMode = 0;
        chooseColor();

        this.setStyle("-fx-background-color: "+colors[0] +";");

        this.tileGrid = new TileGrid(colors[1], board, this, false);

        this.setCenter(tileGrid.toBox());
        this.setTop(makeMenu());


        //testings
        this.widthProperty().addListener((observableValue, number, t1) -> resize(this, this.getTileGrid()));
        this.heightProperty().addListener((observableValue, number, t1) -> resize(this, this.getTileGrid()));
    }


    public void resize(ContentPane contentPane, TileGrid tileGrid){

        int rows = board.getHeight();
        int cols = board.getWidth();

        if((contentPane.getHeight()/(rows))*cols <= contentPane.getWidth()){

            //old:  tileGrid.prefTileHeightProperty().bind(contentPane.heightProperty().subtract(25.0).divide(rows +0.33)); // 0.33
            tileGrid.prefTileHeightProperty().bind(contentPane.heightProperty().subtract(25.0).divide(rows+0.33).subtract(1)); // 0.33
            tileGrid.prefTileWidthProperty().bind(contentPane.heightProperty().subtract(25.0).divide(rows+0.33).subtract(1));

            for(int i = 0; i < board.getHeight(); i++){
                for(int j = 0; j < board.getWidth(); j++){
                    tileGrid.getTileViews()[i][j].get(0).tileResizeForHeight();
                }
            }

        } else {

            tileGrid.prefTileHeightProperty().bind(contentPane.widthProperty().divide(cols+0.5).subtract(1));
            tileGrid.prefTileWidthProperty().bind(contentPane.widthProperty().divide(cols+0.5).subtract(1)); //cols +0.5

            for(int i = 0; i < board.getHeight(); i++){
                for(int j = 0; j < board.getWidth(); j++){
                    tileGrid.getTileViews()[i][j].get(0).tileResizeForWidth();
                }
            }

        }
    }

    private MenuBar makeMenu(){

        Menu menu = new Menu("Menu");
        //menu.setGraphic(new Text("\u221e"));
        ImageView menuImage = new ImageView("/jpp/infinityloop/images/menu-icon-28.png");
        menuImage.setPreserveRatio(true);
        menuImage.setSmooth(true);
        menuImage.setFitHeight(15);
        menu.setGraphic(menuImage);


        MenuItem load = new MenuItem("Load");
        ImageView loadImage = new ImageView("/jpp/infinityloop/images/folder.png");
        loadImage.setPreserveRatio(true);
        loadImage.setSmooth(true);
        loadImage.setFitHeight(15);
        load.setGraphic(loadImage);

        MenuItem save = new MenuItem("Save");
        ImageView saveImage = new ImageView("/jpp/infinityloop/images/save.png");
        saveImage.setPreserveRatio(true);
        saveImage.setSmooth(true);
        saveImage.setFitHeight(17);
        save.setGraphic(saveImage);

        MenuItem shuffle = new MenuItem("Shuffle");
        ImageView shuffleImage = new ImageView("/jpp/infinityloop/images/shuffle-icon.png");
        shuffleImage.setPreserveRatio(true);
        shuffleImage.setSmooth(true);
        shuffleImage.setFitHeight(17);
        shuffle.setGraphic(shuffleImage);

        MenuItem solve = new MenuItem("Solve");
        ImageView solveImage = new ImageView("/jpp/infinityloop/images/robot-3.png");
        solveImage.setPreserveRatio(true);
        solveImage.setSmooth(true);
        solveImage.setFitHeight(19);
        solve.setGraphic(solveImage);

        Menu settings = new Menu("Color Theme");
        ImageView settingsImage = new ImageView("/jpp/infinityloop/images/settings.png");
        settingsImage.setPreserveRatio(true);
        settingsImage.setSmooth(true);
        settingsImage.setFitHeight(19);
        settings.setGraphic(settingsImage);

        // Submenu for Color Theme
        RadioMenuItem classic = new RadioMenuItem("Classic");
        RadioMenuItem experimental = new RadioMenuItem("Experimental");
        RadioMenuItem blue = new RadioMenuItem("Blue");
        RadioMenuItem green = new RadioMenuItem("Green");
        RadioMenuItem brown = new RadioMenuItem("Brown/Gold");
        RadioMenuItem purple = new RadioMenuItem("Purple");

        classic.setSelected(true);

        ToggleGroup toggleGroup = new ToggleGroup();
        toggleGroup.getToggles().add(classic);
        toggleGroup.getToggles().add(experimental);
        toggleGroup.getToggles().add(blue);
        toggleGroup.getToggles().add(green);
        toggleGroup.getToggles().add(brown);
        toggleGroup.getToggles().add(purple);

        // action for submenu

        classic.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                colorMode = 0;
                changeColor();
            }
        });

        experimental.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                colorMode = 1;
                changeColor();
            }
        });

        blue.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                colorMode = 2;
                colorTheme = 0;
                colors = Settings.getInstance().getColors1();
                changeColor();
            }
        });

        green.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                colorMode = 3;
                colorTheme = 1;
                colors = Settings.getInstance().getColors2();
                changeColor();
            }
        });

        brown.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                colorMode = 4;
                colorTheme = 2;
                colors = Settings.getInstance().getColors3();
                changeColor();
            }
        });

        purple.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                colorMode = 5;
                colorTheme = 3;
                colors = Settings.getInstance().getColors4();
                changeColor();
            }
        });



        SeparatorMenuItem separator3 = new SeparatorMenuItem();

        settings.getItems().add(classic);
        settings.getItems().add(experimental);
        settings.getItems().add(separator3);
        settings.getItems().add(blue);
        settings.getItems().add(green);
        settings.getItems().add(brown);
        settings.getItems().add(purple);
        // end of Submenu

        MenuItem exit = new MenuItem("Exit");
        ImageView exitImage = new ImageView("/jpp/infinityloop/images/running.png");
        exitImage.setPreserveRatio(true);
        exitImage.setSmooth(true);
        exitImage.setFitHeight(20);
        exit.setGraphic(exitImage);

        load.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                fileChooser.setTitle("Select a Level");
                File selectedFile = fileChooser.showOpenDialog(stage);

                if(selectedFile != null){

                    controller.handleOpen(selectedFile.toPath());
                    Board board = controller.handleNext();

                    if(board == null){
                        // show new Window with message
                        Alert alert = new Alert(Alert.AlertType.ERROR, "The selected file could not be processed\ndue to an incompatible format.");
                        alert.setTitle("Wrong format");
                        alert.setHeaderText("Error");
                        alert.show();

                    } else {
                        sequentialTransition.stop();
                        sequentialTransition.getChildren().clear();
                        load(board);
                    }

                }
            }
        });

        save.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                fileChooser.setTitle("Save Level");
                File file = fileChooser.showSaveDialog(stage);

                if(file != null){

                    if(!(file.getName().endsWith(".bin"))) {
                        Alert alert = new Alert(Alert.AlertType.WARNING, "Saving was unsuccessful.\nThe file type needs to be declared as '.bin'");
                        alert.setTitle("Saving unsuccessful");
                        alert.setHeaderText("Warning!");
                        alert.show();
                    } else {

                        controller.handleSave(file, board);
                    }
                }

            }
        });

        shuffle.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                sequentialTransition.stop();
                sequentialTransition.getChildren().clear();

                shuffle();
            }
        });

        solve.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                Board solved = controller.handleSolve(board);

                if(solved == null){
                    Alert alert = new Alert(Alert.AlertType.ERROR, "This Board seems to be unsolvable,\ntherefore it could not be solved.");
                    alert.setTitle("Solving unsuccessful");
                    alert.setHeaderText("Error");
                    alert.show();

                } else {

                    sequentialTransition.stop();
                    sequentialTransition.getChildren().clear();

                    for(int i = 0; i < board.getHeight(); i++){
                        for(int j= 0; j < board.getWidth(); j++){

                            if(board.getTiles()[i][j].get(0).getType() != TileType.EMPTY && board.getTiles()[i][j].get(0).getType() != TileType.CROSS) {

                                while (board.getTiles()[i][j].get(0).getOrientation() != solved.getTiles()[i][j].get(0).getOrientation()) {

                                    board.getTiles()[i][j].get(0).rotate();

                                    RotateTransition rotate =  new RotateTransition(Duration.seconds(0.15), tileGrid.getTileViews()[i][j].get(0));
                                    rotate.setByAngle(90);
                                    rotate.setInterpolator(Interpolator.LINEAR);
                                    rotate.setCycleCount(0);
                                    sequentialTransition.getChildren().add(rotate);

                                }
                            }
                        }
                    }

                    sequentialTransition.play();

                    sequentialTransition.setOnFinished(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent actionEvent) {
                            contentPane.setColorSolved();
                        }
                    });

                }

            }
        });

        settings.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                // optional
                // color stuff only
            }
        });

        exit.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                Platform.exit();
            }
        });

        SeparatorMenuItem separator = new SeparatorMenuItem();
        SeparatorMenuItem separator1 = new SeparatorMenuItem();
        SeparatorMenuItem separator2 = new SeparatorMenuItem();

        menu.getItems().add(load);
        menu.getItems().add(save);
        menu.getItems().add(separator);
        menu.getItems().add(shuffle);
        menu.getItems().add(solve);
        menu.getItems().add(separator1);
        menu.getItems().add(settings);
        menu.getItems().add(separator2);
        menu.getItems().add(exit);

        MenuBar menuBar = new MenuBar();
        menuBar.getMenus().add(menu);

        return menuBar;
    }

    private void chooseColor(){

        if(colorMode == 0) {

            int newColor = ThreadLocalRandom.current().nextInt(0, 3 + 1);
            while (newColor == colorTheme) {
                newColor = ThreadLocalRandom.current().nextInt(0, 3 + 1);
            }
            colorTheme = newColor;
            switch (colorTheme) {
                case 0:
                    this.colors = Settings.getInstance().getColors1();
                    break;
                case 1:
                    this.colors = Settings.getInstance().getColors2();
                    break;
                case 2:
                    this.colors = Settings.getInstance().getColors3();
                    break;
                case 3:
                    this.colors = Settings.getInstance().getColors4();
                    break;
                default:
                    throw new IllegalStateException("No color found at ContentPane.chooseColor");
            }

        } else if(colorMode == 1){

            int newColor = ThreadLocalRandom.current().nextInt(0, 3 + 1);
            while (newColor == colorTheme) {
                newColor = ThreadLocalRandom.current().nextInt(0, 3 + 1);
            }
            colorTheme = newColor;

            switch (colorTheme) {
                case 0:
                    this.colors = Settings.getInstance().getExp1();
                    break;
                case 1:
                    this.colors = Settings.getInstance().getExp2();
                    break;
                case 2:
                    this.colors = Settings.getInstance().getExp3();
                    break;
                case 3:
                    this.colors = Settings.getInstance().getExp4();
                    break;
                default:
                    throw new IllegalStateException("No color found at ContentPane.chooseColor");
            }

        }
    }


    public void setColorSolved(){

        this.setStyle("-fx-background-color: "+ colors[2] +";");

        this.setTileGrid(new TileGrid(colors[3], board, this, true));
        this.setCenter(this.getTileGrid().toBox());
        resize(this, this.tileGrid);
    }


    private void shuffle(){
        controller.handleShuffle();
        this.board = controller.handleNext();
        chooseColor();

        this.setStyle("-fx-background-color: "+colors[0] +";");

        this.setTileGrid(new TileGrid(colors[1], board, this, false));
        this.setCenter(this.getTileGrid().toBox());


        if(stage.getHeight() < board.getHeight()*50){
            stage.setHeight(board.getHeight()*50);

        }
        if(stage.getWidth() < board.getWidth()*50){
            stage.setWidth(board.getWidth()*50);
        }

        stage.setMinWidth(board.getWidth()*50);
        stage.setMinHeight(board.getHeight()*50);

        resize(this, this.tileGrid);

    }

    private void load(Board board){
        this.board = board;
        chooseColor();

        this.setStyle("-fx-background-color: "+colors[0]+ ";");

        this.setTileGrid(new TileGrid(colors[1], board, this, false));
        this.setCenter(this.getTileGrid().toBox());


        if(stage.getHeight() < board.getHeight()*50){
            stage.setHeight(board.getHeight()*50);

        }
        if(stage.getWidth() < board.getWidth()*50){
            stage.setWidth(board.getWidth()*50);
        }

        stage.setMinWidth(board.getWidth()*50);
        stage.setMinHeight(board.getHeight()*50);

        resize(this, this.tileGrid);
    }

    private void changeColor(){
        chooseColor();
        this.setStyle("-fx-background-color: "+colors[0]+ ";");
        this.setTileGrid(new TileGrid(colors[1], board, this, false));
        this.setCenter(this.getTileGrid().toBox());
        resize(this, this.tileGrid);

    }


    public TileGrid getTileGrid() {
        return tileGrid;
    }

    public void setTileGrid(TileGrid tileGrid) {
        this.tileGrid = tileGrid;
    }

    public Board getBoard() {
        return board;
    }
}
