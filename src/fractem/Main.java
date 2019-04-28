package fractem;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
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

import java.math.BigDecimal;

public class Main extends Application {

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
        width=1280;
        height=720;
        Scene scene = new Scene(root, width, height);
        canvas = new Canvas(width,height);
        root.getChildren().add(canvas);

        //Creating Setting-Panel
        Text text_iterations = new Text("Number of Iterations: ");
        TextField textField_iterations = new TextField("1000");
        Text text_base_color = new Text("Base Color: ");
        ColorPicker colorPicker = new ColorPicker();
        Text text_sf_hue = new Text("Smooth Factor Hue: ");
        TextField textField_sf_hue = new TextField("200");
        Text text_sf_sat = new Text("Smooth Factor Saturation: ");
        TextField textField_sf_sat = new TextField("1");
        Text text_sf_bri = new Text("Smooth Factor Brightness: ");
        TextField textField_sf_bri = new TextField("1");
        CheckBox enableMultithreading = new CheckBox("Enable Multithreading");

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
                mandleBrot.setColor(colorPicker.getValue());
                double[] sf = {Double.parseDouble(textField_sf_hue.getCharacters().toString()),Double.parseDouble(textField_sf_sat.getCharacters().toString()),Double.parseDouble(textField_sf_bri.getCharacters().toString())};
                mandleBrot.setSmoothfactor(sf);
                mandleBrot.setMultithreading(enableMultithreading.isSelected());
                mandleBrot.draw(canvas.getGraphicsContext2D(),width,height);
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
        settingsPane.add(button_apply,0,6);
        settingsPane.add(button_reset,1,6);

        //Sytling Setting-Panel
        settingsPane.setStyle("-fx-background-color: BEIGE");


        //testcommentlol
        final Popup popup = new Popup();
        popup.setX(300);
        popup.setY(200);
        popup.getContent().addAll(settingsPane);

        /* HANDLE INPUTS*/
        scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                switch (event.getCode()){
                    case ESCAPE: popup.show(stage);
                }
            }
        });
        scene.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                mandleBrot.zoom((int)event.getX(),(int)event.getY(),width,height,5);
                mandleBrot.draw(canvas.getGraphicsContext2D(),width,height);
            }
        });


        stage.setTitle("Fractem");
        stage.setScene(scene);
        stage.show();
    }




    public static void main(String[] args) {
        launch(args);
    }
}
