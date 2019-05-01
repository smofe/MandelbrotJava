package fractem;

import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Popup;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Main extends Application {

    public static final int FILE_NAME_MODE_DATE = 0;


    private ComplexNumber c_x, c_y;
    private int width,height;
    private Canvas canvas;
    private MandleBrot mandleBrot;

    @Override
    public void start(Stage primaryStage) throws Exception{
        initUI(primaryStage);
        mandleBrot = new MandleBrot(-2.5,-1.0,3.5,2.0,1000);
        mandleBrot.draw(canvas.getGraphicsContext2D(),(int)canvas.getWidth(),(int)canvas.getHeight());
    }

    public void initUI(Stage stage){
        StackPane root = new StackPane();
        width=1920;
        height=1080;
        Scene scene = new Scene(root, width, height);
        canvas = new Canvas(width,height);
        root.getChildren().add(canvas);

        //Creating Setting-Panel
        Text text_iterations = new Text("Number of Iterations: ");
        TextField textField_iterations = new TextField("1000");
        Text text_base_color = new Text("Base Color: ");
        ColorPicker colorPicker = new ColorPicker(Color.BLUE);
        Text text_sf_hue = new Text("Smooth Factor Hue: ");
        TextField textField_sf_hue = new TextField("200");
        Text text_sf_sat = new Text("Smooth Factor Saturation: ");
        TextField textField_sf_sat = new TextField("1");
        Text text_sf_bri = new Text("Smooth Factor Brightness: ");
        TextField textField_sf_bri = new TextField("1");
        CheckBox enableMultithreading = new CheckBox("Enable Multithreading");
        enableMultithreading.setSelected(true);

        Button button_apply = new Button("Apply");
        button_apply.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                mandleBrot.setIterations(Integer.parseInt(textField_iterations.getCharacters().toString()));
                double[] sf = {Double.parseDouble(textField_sf_hue.getCharacters().toString()),Double.parseDouble(textField_sf_sat.getCharacters().toString()),Double.parseDouble(textField_sf_bri.getCharacters().toString())};

                mandleBrot.setSmoothfactor(sf);
                mandleBrot.setColor(colorPicker.getValue());
                mandleBrot.setMultithreading(enableMultithreading.isSelected());
                mandleBrot.draw(canvas.getGraphicsContext2D(),width,height);
            }
        });
        Button button_reset = new Button("Reset");
        button_reset.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                mandleBrot = new MandleBrot(-2.5,-1.0,3.5,2.0,Integer.parseInt(textField_iterations.getCharacters().toString()));
                double[] sf = {Double.parseDouble(textField_sf_hue.getCharacters().toString()),Double.parseDouble(textField_sf_sat.getCharacters().toString()),Double.parseDouble(textField_sf_bri.getCharacters().toString())};
                mandleBrot.setSmoothfactor(sf);
                mandleBrot.setColor(colorPicker.getValue());
                mandleBrot.setMultithreading(enableMultithreading.isSelected());
                mandleBrot.draw(canvas.getGraphicsContext2D(),width,height);
                button_apply.onMouseClickedProperty();
            }
        });
        Button button_screenshot = new Button("Screenshot");
        button_screenshot.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                takeScreenshot(FILE_NAME_MODE_DATE);
            }
        });



        GridPane settingsPane = new GridPane();
        settingsPane.setAlignment(Pos.TOP_LEFT);
        settingsPane.setPadding(new Insets(10,10,10,10));
        settingsPane.setHgap(5);
        settingsPane.setVgap(5);

        settingsPane.add(text_iterations,0,0);
        settingsPane.add(textField_iterations,1,0);
        settingsPane.add(text_base_color,0,1);
        settingsPane.add(colorPicker,1,1);
        settingsPane.add(text_sf_hue,0,2);
        settingsPane.add(textField_sf_hue,1,2);
        settingsPane.add(text_sf_sat,0,3);
        settingsPane.add(textField_sf_sat,1,3);
        settingsPane.add(text_sf_bri,0,4);
        settingsPane.add(textField_sf_bri,1,4);
        settingsPane.add(enableMultithreading,0,5);
        settingsPane.add(button_screenshot,0,6);
        settingsPane.add(button_apply,0,7);
        settingsPane.add(button_reset,1,7);

        //Sytling Setting-Panel
        settingsPane.setStyle("-fx-background-color: GREY");

        final Popup popup = new Popup();
        popup.setX(300);
        popup.setY(200);
        popup.getContent().addAll(settingsPane);

        /* HANDLE INPUTS*/
        scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                switch (event.getCode()){
                    case ESCAPE: popup.show(stage); break;
                    case S: takeScreenshot(FILE_NAME_MODE_DATE); break;
                }
            }
        });
        scene.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
              // mandleBrot.zoom((int)event.getX(),(int)event.getY(),width,height,5);
                generateVideo((int)event.getX(),(int)event.getY(),1500,50);
              // mandleBrot.draw(canvas.getGraphicsContext2D(),width,height);
              // System.out.println("r: " + (mandleBrot.getTop_left().getReal()+mandleBrot.getBottom_right().getReal())/2 + " i: " + (mandleBrot.getTop_left().getImaginary()+mandleBrot.getBottom_right().getImaginary())/2);
            }
        });


        stage.setTitle("Fractem");
        stage.setScene(scene);
        stage.show();
    }




    public static void main(String[] args) {
        launch(args);
    }

    private void takeScreenshot(int filenamemode){
        try {
            Date date = new Date();
            File file;
            if (filenamemode == FILE_NAME_MODE_DATE) {
                SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy_HH-mm-ss");
                 file = new File("." + File.separator + "screenshots" + File.separator + formatter.format(date) + ".png");
            } else {
                 file = new File("." + File.separator + "video2" + File.separator + filenamemode + ".png");
            }
            file.getParentFile().mkdirs();
            RenderedImage renderedImage = SwingFXUtils.fromFXImage(canvas.snapshot(new SnapshotParameters(),new WritableImage(width,height)),null);
            ImageIO.write(renderedImage,"png",file);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("screenshot failed");
        }
    }

    private void generateVideo(int x, int y,int img_count,double speed){
        takeScreenshot(0);
        //mandleBrot.zoom(x,y,width,height,speed);
        mandleBrot.zoom(-1.3952110016851083,-0.018571491266314463,speed);
        mandleBrot.draw(canvas.getGraphicsContext2D(),width,height);
        while (mandleBrot.isMultithreading()) {
            try {
                wait(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        for (int i=1; i<img_count; i++){
            takeScreenshot(i);
            //mandleBrot.zoom(width/2,height/2,width,height,speed);
            mandleBrot.zoom(-1.3952110016851083,-0.018571491266314463,speed);
            mandleBrot.draw(canvas.getGraphicsContext2D(),width,height);
            while (mandleBrot.isMultithreading()) {
                try {
                    wait(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        //commandline D:\Programme\ffmpeg-20190429-ac551c5-win64-static\bin\ffmpeg.exe -r 30 -f image2 -i %d.png -vcodec libx264 -crf 10 -pix_fmt yuv420p testhq.mp4
    }

}
