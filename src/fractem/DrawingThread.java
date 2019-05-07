package fractem;


import javafx.application.Platform;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class DrawingThread implements Runnable {

    private GraphicsContext gc;
    private int xpos,ypos,width,height, max_iterations;
    private double[] smoothfactor;
    private Color color;
    private ComplexNumber top_left, bottom_right;
    private WritableImage img;

    public DrawingThread(GraphicsContext gc, ComplexNumber top_left, ComplexNumber bottom_right,int xpos, int ypos,int width, int height, int max_iterations, Color color, double[] smoothfactor){
        this.gc = gc;
        this.top_left = top_left;
        this.bottom_right = bottom_right;
        this.xpos=xpos;
        this.ypos=ypos;
        this.width = width;
        this.height = height;
        this.max_iterations =max_iterations;
        this.color = color;
        this.smoothfactor = smoothfactor;
    }

    public WritableImage getImg() {
        return img;
    }

    @Override
    public void run() {
        img = new WritableImage(width, height);
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
                    while (sat < 0) sat +=1.0;
                    double bright = color.getBrightness() + smoothfactor[2]/100.0*smooth;
                    while (bright > 1) bright-=1.5;
                    while (bright < 0) bright +=1.0;
                    pw.setColor(col,row,Color.hsb(hue,sat,bright));
                } else {
                    pw.setColor(col,row,Color.BLACK);
                }
            }
        }

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                gc.drawImage(img,xpos,ypos);
            }
        });
    }
}
