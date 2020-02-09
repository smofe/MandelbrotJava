package fractem;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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

    public static final int FILE_NAME_MODE_DATE = -1;


    private double center_r, center_i, zoomlevel;
    private int width,height;
    private Canvas canvas;
    private MandleBrot mandleBrot;

    @Override
    public void start(Stage primaryStage) throws Exception{
        center_r = -2.5+3.5/2;
        center_i = -1.0+2.0/2;
        zoomlevel = 1;
        mandleBrot = new MandleBrot(-2.5,-1.0,3.5,2.0,1000);
        initUI(primaryStage);
        mandleBrot.draw(canvas.getGraphicsContext2D(),(int)canvas.getWidth(),(int)canvas.getHeight());
    }

    public void initUI(Stage stage){
        StackPane root = new StackPane();
        width=1280;
        height=720;
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
        ComboBox resolution = new ComboBox();
        resolution.getItems().addAll("3840x2160","1920x1080","1080x720","896x504","768x432","640x360","512x288","384x216","256x144","128x72");
        resolution.setValue(String.valueOf(width)+"x"+String.valueOf(height));
        Text center_coord = new Text("r: " + String.valueOf(center_r) + " , i: " + String.valueOf(center_i) );

        Button button_apply = new Button("Apply");
        button_apply.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                mandleBrot.setIterations(Integer.parseInt(textField_iterations.getCharacters().toString()));
                double[] sf = {Double.parseDouble(textField_sf_hue.getCharacters().toString()),Double.parseDouble(textField_sf_sat.getCharacters().toString()),Double.parseDouble(textField_sf_bri.getCharacters().toString())};

                mandleBrot.setSmoothfactor(sf);
                mandleBrot.setColor(colorPicker.getValue());
                mandleBrot.setMultithreading(enableMultithreading.isSelected());

                String res = resolution.getValue().toString();
                String[] parts = res.split("x");
                width = Integer.parseInt(parts[0]);
                height = Integer.parseInt(parts[1]);
                stage.setWidth(width);
                stage.setHeight(height);

                canvas.setWidth(width);
                canvas.setHeight(height);
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
        settingsPane.add(resolution,1,6);
        settingsPane.add(button_screenshot,0,6);
        settingsPane.add(button_apply,0,7);
        settingsPane.add(button_reset,1,7);
        settingsPane.add(center_coord,0,8);

        //Sytling Setting-Panel
        settingsPane.setStyle("-fx-background-color: GREY");

        final Popup settingsPopup = new Popup();
        settingsPopup.setX(300);
        settingsPopup.setY(200);
        settingsPopup.getContent().addAll(settingsPane);

        /* Video Gen Panel */
        GridPane videoPane = new GridPane();
        videoPane.setAlignment(Pos.TOP_RIGHT);
        videoPane.setPadding(new Insets(10,10,10,10));
        videoPane.setHgap(5);
        videoPane.setVgap(5);
        videoPane.setStyle("-fx-background-color: GREY");

        Text videogenTitle = new Text("Generating a video");
        videogenTitle.setUnderline(true);
        Text text_realcoord = new Text("Real Coord: ");
        Text text_imaginarycoord = new Text("Imaginary Coord: ");
        Text text_zoomlevel = new Text("Zoomlevel: ");
        Text text_frames = new Text("Frames: ");
        TextField textField_realcoord = new TextField("-0.644577527046203");
        TextField textField_imaginarycoord = new TextField("-0.44716966417100695");
        TextField textField_zoomlevel = new TextField("4096");
        TextField textField_frames = new TextField("16");
        Button btn_useCurrentCoords = new Button("Current Coords");
        btn_useCurrentCoords.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                textField_realcoord.setText(String.valueOf(center_r));
                textField_imaginarycoord.setText(String.valueOf(center_i));
                textField_zoomlevel.setText(String.valueOf(zoomlevel));
            }
        });
        Button btn_genVideo = new Button("Start Video Generation");
        btn_genVideo.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                mandleBrot = new MandleBrot(-2.5,-1.0,3.5,2.0,Integer.parseInt(textField_iterations.getCharacters().toString()));
                double[] sf = {Double.parseDouble(textField_sf_hue.getCharacters().toString()),Double.parseDouble(textField_sf_sat.getCharacters().toString()),Double.parseDouble(textField_sf_bri.getCharacters().toString())};
                mandleBrot.setSmoothfactor(sf);
                mandleBrot.setColor(colorPicker.getValue());
                mandleBrot.setMultithreading(enableMultithreading.isSelected());
                mandleBrot.draw(canvas.getGraphicsContext2D(),width,height);
                center_r = -2.5+3.5/2;
                center_i = -1.0+2.0/2;
                zoomlevel = 1;

                while (mandleBrot.isGenerating()) {
                    try {
                        wait(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                generateVideo(Double.parseDouble(textField_realcoord.getText()),Double.parseDouble(textField_imaginarycoord.getText()),
                        Integer.parseInt(textField_frames.getText()),Double.parseDouble(textField_zoomlevel.getText()));

            }
        });


        videoPane.add(videogenTitle,0,0);
        videoPane.add(text_realcoord,0,1);
        videoPane.add(text_imaginarycoord,0,2);
        videoPane.add(text_zoomlevel,0,3);
        videoPane.add(text_frames,0,4);
        videoPane.add(textField_realcoord,1,1);
        videoPane.add(textField_imaginarycoord,1,2);
        videoPane.add(textField_zoomlevel,1,3);
        videoPane.add(textField_frames,1,4);
        videoPane.add(btn_useCurrentCoords,0,5);
        videoPane.add(btn_genVideo,0,6);

        final Popup videoPopup = new Popup();
        videoPopup.setX(800);
        videoPopup.setY(200);
        videoPopup.getContent().addAll(videoPane);


        /* HANDLE INPUTS*/
        scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                switch (event.getCode()){
                    case ESCAPE: settingsPopup.show(stage); break;
                    case S: takeScreenshot(FILE_NAME_MODE_DATE); break;
                    case V: videoPopup.show(stage); break;
                }
            }
        });
        scene.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
               double z = 4;
                System.out.println("r: " + center_r + " ,i: " + center_i);

                mandleBrot.zoom((int)event.getX(),(int)event.getY(),width,height,z);
               zoomlevel *= z;
               // generateVideo((int)event.getX(),(int)event.getY(),1500,50);
               mandleBrot.draw(canvas.getGraphicsContext2D(),width,height);
              // System.out.println("r: " + (mandleBrot.getTop_left().getReal()+mandleBrot.getBottom_right().getReal())/2 + " i: " + (mandleBrot.getTop_left().getImaginary()+mandleBrot.getBottom_right().getImaginary())/2);
               center_r = mandleBrot.getTop_left().getReal() + (mandleBrot.getBottom_right().getReal() - mandleBrot.getTop_left().getReal())/2;
               center_i = mandleBrot.getTop_left().getImaginary() + (mandleBrot.getBottom_right().getImaginary() - mandleBrot.getTop_left().getImaginary())/2;
               center_coord.setText("r: " + String.valueOf(center_r) + " , i: " + String.valueOf(center_i) );
            }
        });

        mandleBrot.setColor(colorPicker.getValue());
        mandleBrot.setIterations(Integer.parseInt(textField_iterations.getCharacters().toString()));
        double[] sf = {Double.parseDouble(textField_sf_hue.getCharacters().toString()),Double.parseDouble(textField_sf_sat.getCharacters().toString()),Double.parseDouble(textField_sf_bri.getCharacters().toString())};
        mandleBrot.setSmoothfactor(sf);
        mandleBrot.setMultithreading(true);


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
                 file = new File("." + File.separator + "video3" + File.separator + filenamemode + ".png");
            }
            file.getParentFile().mkdirs();
            RenderedImage renderedImage = SwingFXUtils.fromFXImage(canvas.snapshot(new SnapshotParameters(),new WritableImage(width,height)),null);
            ImageIO.write(renderedImage,"png",file);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("screenshot failed");
        }
    }

    private void generateVideo(double r, double i,double frames,double zoom){
        System.out.println("ZOOM TO: " + r + ", " + i);
        while (mandleBrot.isGenerating()) {
            try {
                wait(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        takeScreenshot(0);
        double speed = Math.pow(zoom,1.0/frames);
        System.out.println("Speed: " + speed);
        //mandleBrot.zoom(r,i,speed);
        mandleBrot.zoom((int)width/2,(int)height/2,width,height,speed);

        mandleBrot.draw(canvas.getGraphicsContext2D(),width,height);
        while (mandleBrot.isGenerating()) {
            try {
                wait(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        for (int f=1; f<frames; f++){
            takeScreenshot(f);
            //mandleBrot.zoom(r,i,speed);
            mandleBrot.zoom((int)width/2,(int)height/2,width,height,speed);
            mandleBrot.draw(canvas.getGraphicsContext2D(),width,height);
            while (mandleBrot.isGenerating()) {
                try {
                    wait(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        //commandline D:\Programme\ffmpeg-20190429-ac551c5-win64-static\bin\ffmpeg.exe -r 30 -f image2 -i %d.png -vcodec libx264 -crf 10 -pix_fmt yuv420p testhq.mp4
        center_r = mandleBrot.getTop_left().getReal() + (mandleBrot.getBottom_right().getReal() - mandleBrot.getTop_left().getReal())/2;
        center_i = mandleBrot.getTop_left().getImaginary() + (mandleBrot.getBottom_right().getImaginary() - mandleBrot.getTop_left().getImaginary())/2;
        System.out.println("Center: " + center_r + " ," + center_i);
    }

}
