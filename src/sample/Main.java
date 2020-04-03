package sample;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.Random;

public class Main extends Application {


    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Gra w Życie");
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));
        Label x = new Label("Wymiar X:");
        grid.add(x, 0, 0);
        TextField xTextField = new TextField();
        grid.add(xTextField, 0, 1);
        xTextField.setText("12");
        Label y = new Label("Wymiar Y:");
        grid.add(y, 1, 0);
        TextField yTextField = new TextField();
        grid.add(yTextField, 1, 1);
        yTextField.setText("12");
        Label stanPoczatkowy = new Label("Stan początkowy:");
        grid.add(stanPoczatkowy, 2, 0);
        ChoiceBox stanPoczatkowyChoiceBox = new ChoiceBox();
        stanPoczatkowyChoiceBox.setMinWidth(50.);
        grid.add(stanPoczatkowyChoiceBox, 2, 1);
        stanPoczatkowyChoiceBox.getItems().add("");
        stanPoczatkowyChoiceBox.getItems().add("Niezmienne");
        stanPoczatkowyChoiceBox.getItems().add("Oscylator");
        stanPoczatkowyChoiceBox.getItems().add("Glider");
        stanPoczatkowyChoiceBox.getItems().add("Losowy");
        stanPoczatkowyChoiceBox.setValue("");

        //......................................................

        Button zastosuj = new Button("Zastosuj");
        zastosuj.setMinWidth(65);
        grid.add(zastosuj, 3, 1);

        Canvas canvas = new Canvas(600, 600);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        GridPane.setColumnSpan(canvas, 4);

        Timeline play=new Timeline();

        zastosuj.setOnAction(actionEvent ->  {

            gc.clearRect(0,0,600,600);

            play.stop();
            play.getKeyFrames().removeAll(play.getKeyFrames());

            String initialState = (String) stanPoczatkowyChoiceBox.getValue();
            int width=Integer.parseInt(xTextField.getText());
            int height = Integer.parseInt(yTextField.getText());

            boolean[][] gd=new boolean[height][width];
            for(int i =0; i< width; i++){
                for (int j = 0; j < height; j++) {
                    gd[j][i]=false;
                }
            }

            switch(initialState){
                case "Niezmienne":
                    gd[3][4]=true;
                    gd[3][5]=true;
                    gd[4][3]=true;
                    gd[4][6]=true;
                    gd[5][4]=true;
                    gd[5][5]=true;
                    break;
                case "Oscylator":
                    gd[3][4]=true;
                    gd[4][4]=true;
                    gd[5][4]=true;
                    break;
                case "Glider":
                    gd[3][4]=true;
                    gd[3][5]=true;
                    gd[4][3]=true;
                    gd[4][4]=true;
                    gd[5][5]=true;
                    break;
                case "Losowy":
                    Random r;
                    for(int i =0; i< width; i++){
                        for (int j = 0; j < height; j++) {
                            r = new Random();
                            gd[j][i]= r.nextBoolean();
                        }
                    }
            }

            draw(gc, gd, width, height);

            canvas.setOnMouseClicked(canvasEvent-> {

                int size;
                if(width<height)
                    size=height;
                else
                    size=width;
                int xValue=(int)((canvasEvent.getX()/600)*size);
                int yValue=(int)((canvasEvent.getY()/600)*size);
                if(xValue>=0 && yValue>=0 && xValue<width && yValue<height){
                    gd[yValue][xValue]= !gd[yValue][xValue];
                    draw(gc, gd, width, height);
                }

            });

            Button start = new Button("Start");
            start.setMinWidth(65);
            GridPane.setColumnSpan(canvas, 5);
            grid.add(start, 4, 0);

            Button stop = new Button("Stop");
            stop.setMinWidth(65);
            grid.add(stop, 4, 1);

            stop.setOnAction(stopEvent ->{

                play.stop();
                play.getKeyFrames().removeAll(play.getKeyFrames());

            });

            start.setOnAction(startEvent -> {

                play.stop();
                play.getKeyFrames().removeAll(play.getKeyFrames());

                boolean[][] temp =new boolean[height][width];
                for (int k = 0; k < 100; k++)
                {

                    KeyFrame kf = new KeyFrame(Duration.seconds(k+1), actionEvent2 -> {

                        for (int i = 0; i < width; i++) {
                            for (int j = 0; j < height; j++) {
                                temp[j][i] = gd[j][i];
                            }
                        }

                        for (int i = 0; i < height ; i++) {
                            for (int j = 0; j < width ; j++) {

                                int nb = getNeighbours(temp, j, i);
                                if (temp[j][i]) {
                                    if (nb < 2 || nb > 3)
                                        gd[j][i] = false;
                                } else if (nb == 3)
                                    gd[j][i] = true;

                            }
                        }

                        draw(gc, gd, width, height);

                    });


                    play.getKeyFrames().add(kf);
                }

                play.play();
            });

        });

        grid.add(canvas, 0, 3);

        Scene scene = new Scene(grid, 650, 720);
        primaryStage.setScene(scene);
        primaryStage.show();


    }
    private int getNeighbours(boolean[][]grid, int y, int x){
        int neighbours=0;
        int a, b;
        for(int i = -1; i<=1; i++){
            for(int j = -1; j<=1; j++){
                b=x+i;
                if(b==-1)
                    b=grid.length-1;
                else if(b==grid.length)
                    b=0;
                a=y+j;
                if(a==-1)
                    a=grid.length-1;
                else if(a==grid[0].length)
                    a=0;
                if(grid[a][b]&& (i!=0 || j!=0))
                    neighbours++;
            }
        }
        return neighbours;
    }

    private void draw(GraphicsContext gc, boolean[][] grid, int width, int height) {
        gc.clearRect(0,0,600, 600);
        int a;
        if(width<height)
            a=600/height;
        else
            a=600/width;
        gc.setFill(Color.RED);
        for(int i =0; i< height; i++){
            for (int j = 0; j < width; j++) {
                if(grid[i][j])
                    gc.fillRect(j*a, i*a, a, a);
            }
        }

    }
    public static void main(String[] args) {
        launch(args);
    }
}
