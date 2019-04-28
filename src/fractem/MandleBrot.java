package fractem;

import javafx.application.Platform;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class MandleBrot {

    private ComplexNumber top_left, bottom_right;

    private int max_iterations;

    private double[] smoothfactor = {200,1,1};

    private Color color;

    private boolean multithreading = false;

    public MandleBrot(double r, double i, double width, double height){
        this.top_left = new ComplexNumber(r,i);
        this.bottom_right = new ComplexNumber(r+width,i+height);
        color = Color.RED;
    }

    public MandleBrot(double r, double i, double width, double height,int max_iterations){
        this(r,i,width,height);
        this.max_iterations = max_iterations;
    }

    public void draw(GraphicsContext gc,int width, int height){
        if (multithreading) {
            Thread[][] threads = new Thread[16][16];
            Runnable[][] runnables = new Runnable[threads.length][threads[0].length];
            double frac_width = (bottom_right.getReal()-top_left.getReal());
            double frac_height = (bottom_right.getImaginary()-top_left.getImaginary());
            for (int x=0; x<threads.length; x++){
                for (int y=0; y<threads[x].length;y++){
                    runnables[x][y]=new DrawingThread(gc,new ComplexNumber(top_left.getReal()+frac_width*x/threads.length,top_left.getImaginary()+y*frac_height/threads[x].length),
                            new ComplexNumber(top_left.getReal()+(x+1)*frac_width/threads.length, top_left.getImaginary()+(y+1)*frac_height/threads[x].length),
                            x*width/threads.length,y*height/threads[x].length,width/threads.length,height/threads[x].length,max_iterations,color,smoothfactor);

                    threads[x][y] = new Thread(runnables[x][y]);
                    threads[x][y].start();

                    Platform.runLater(threads[x][y]);
                }
            }


        } else
            {
        WritableImage img = new WritableImage(width, height);
        int iteration;


        double pixelwidth_x = (bottom_right.getReal()-top_left.getReal())/width;
        double pixelwidth_y = (bottom_right.getImaginary()-top_left.getImaginary())/height;
        PixelWriter pw = img.getPixelWriter();

        for (int row=0; row < height; row++){
            for (int col=0; col < width; col++){
                double c_re = top_left.getReal()+col*pixelwidth_x;
                double c_im = top_left.getImaginary()+row*pixelwidth_y;
                double x=0, y=0;
                iteration = 0;

                while (x*x+y*y < 4 && iteration < max_iterations){
                    double x_new = x*x-y*y+c_re;
                    y = 2*x*y+c_im;
                    x = x_new;
                    iteration++;
                }

                if (iteration < max_iterations) {
                    double smooth = iteration + 1.0 - Math.log(Math.abs(x)+Math.abs(y))/Math.log(2.0);

                    double hue = color.getHue() - smoothfactor[0]/100.0* smooth;
                    double sat = color.getSaturation() + smoothfactor[1]/100.0*smooth;
                    while (sat > 1) sat-=1.0;
                    while (sat < 0) sat +=1.0;
                    double bright = color.getBrightness() + smoothfactor[2]/100.0*smooth;
                    while (bright > 1) bright-=1.0;
                    while (bright < 0) bright +=1.0;
                    pw.setColor(col,row,Color.hsb(hue,sat,bright));
                } else {
                    pw.setColor(col,row,Color.BLACK);
                }
            }
        }

        gc.drawImage(img,0,0);

            }

    }

    public void zoom(int x_pos, int y_pos, int width, int height, double factor){
        double r = getTop_left().getReal()+((getBottom_right().getReal()-getTop_left().getReal())*x_pos)/width;
        double i = getTop_left().getImaginary()+((getBottom_right().getImaginary()-getTop_left().getImaginary())*y_pos)/height;
        ComplexNumber new_top_left = new ComplexNumber(r-(getBottom_right().getReal()-getTop_left().getReal())/factor,
                i-(getBottom_right().getImaginary()-getTop_left().getImaginary())/factor);
        setBottom_right(new ComplexNumber(r+(getBottom_right().getReal()-getTop_left().getReal())/factor,
                i+(getBottom_right().getImaginary()-getTop_left().getImaginary())/factor));

        setTop_left(new_top_left);
    }

    public ComplexNumber getTop_left() {
        return top_left;
    }

    public void setTop_left(ComplexNumber top_left) {
        this.top_left = top_left;
    }

    public ComplexNumber getBottom_right() {
        return bottom_right;
    }

    public void setBottom_right(ComplexNumber bottom_right) {
        this.bottom_right = bottom_right;
    }

    public int getIterations() {
        return max_iterations;
    }

    public void setIterations(int iterations) {
        this.max_iterations = iterations;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public double[] getSmoothfactor() {
        return smoothfactor;
    }

    public void setSmoothfactor(double[] smoothfactor) {
        this.smoothfactor = smoothfactor;
    }


    public boolean isMultithreading() {
        return multithreading;
    }

    public void setMultithreading(boolean multithreading) {
        this.multithreading = multithreading;
    }
}