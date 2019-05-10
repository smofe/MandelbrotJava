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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MandleBrot {

    private ComplexNumber top_left, bottom_right;

    private int max_iterations;

    private double[] smoothfactor = {200,1,1};

    private Color color;

    private boolean multithreading = false;

    private boolean generating = false;

    public MandleBrot(double r, double i, double width, double height){
        this.top_left = new ComplexNumber(r,i);
        this.bottom_right = new ComplexNumber(r+width,i+height);
        color = Color.BLUE;
    }

    public MandleBrot(double r, double i, double width, double height,int max_iterations){
        this(r,i,width,height);
        this.max_iterations = max_iterations;
    }

    public void draw(GraphicsContext gc,int width, int height){
        if (multithreading) {
            ExecutorService es = Executors.newCachedThreadPool();
            double frac_width = (bottom_right.getReal()-top_left.getReal());
            double frac_height = (bottom_right.getImaginary()-top_left.getImaginary());
            int threads = 8;
            for (int x=0; x<threads; x++){
                for (int y=0; y<threads;y++) {
                    es.execute(new DrawingThread(gc,new ComplexNumber(top_left.getReal()+frac_width*x/threads,top_left.getImaginary()+y*frac_height/threads),
                            new ComplexNumber(top_left.getReal()+(x+1)*frac_width/threads, top_left.getImaginary()+(y+1)*frac_height/threads),
                            x*width/threads,y*height/threads,width/threads,height/threads,max_iterations,color,smoothfactor));
                }
            }
            es.shutdown();
            try {
                generating = !(es.awaitTermination(1, TimeUnit.MINUTES));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


        }
        else {
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
                        while (sat > 1) sat-=1.5;
                        while (sat < 0) sat+=1;
                        double bright = color.getBrightness() + smoothfactor[2]/100.0*smooth;
                        while (bright > 1) bright-=1.5;
                        while (bright < 0) bright+=1;
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
        /* Coords of mouse click */
        double r = getTop_left().getReal()+((getBottom_right().getReal()-getTop_left().getReal())*x_pos)/width;
        double i = getTop_left().getImaginary()+((getBottom_right().getImaginary()-getTop_left().getImaginary())*y_pos)/height;
        //System.out.println("Top_left: " + getTop_left().getReal()+","+getTop_left().getImaginary()+ " Bottom_Right: " +
        //        getBottom_right().getReal()+","+getBottom_right().getImaginary() + " r: " + r + " ,i: " + i);
        double width_real = getBottom_right().getReal()-getTop_left().getReal();
        double height_imaginary = getBottom_right().getImaginary()-getTop_left().getImaginary();
        System.out.println(width_real + ", " + height_imaginary);
        ComplexNumber new_top_left = new ComplexNumber(r-(width_real)/(factor*2),
                i-(height_imaginary)/(factor*2));
        setBottom_right(new ComplexNumber(r+(width_real)/(factor*2),
                i+(height_imaginary)/(factor*2)));

        setTop_left(new_top_left);
    }

    public void zoom(double real, double imaginary, double factor){
        factor *=2;
        double r1 = (real-getTop_left().getReal());
        double r2 = (getBottom_right().getReal()-real);
        double i1 = (imaginary-getTop_left().getImaginary());
        double i2 = (getBottom_right().getImaginary()-imaginary);
        ComplexNumber new_top_left = new ComplexNumber(getTop_left().getReal()+r1/factor,getTop_left().getImaginary()+i1/factor);
        setBottom_right(new ComplexNumber(getBottom_right().getReal()-r2/factor,getBottom_right().getImaginary()-i2/factor));
        setTop_left(new_top_left);
        System.out.println("left_r: " + getTop_left().getReal() + " | right_r: " + getBottom_right().getReal()  + " | center_r: " + getTop_left().getReal() + (getBottom_right().getReal() - getTop_left().getReal())/2);
        System.out.println("left_i: " + getTop_left().getImaginary() + " | right_i: " + getBottom_right().getImaginary()  + " | center_i: " + getTop_left().getImaginary()+(getBottom_right().getImaginary() - getTop_left().getImaginary())/2);
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

    public boolean isGenerating() {
        return generating;
    }
}
