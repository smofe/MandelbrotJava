package fractem;

import java.math.BigDecimal;

public class ComplexNumber {

    private double real_part, imaginary_part;

    public ComplexNumber(double real_part,double imaginary_part){
        this.real_part = real_part;
        this.imaginary_part = imaginary_part;
    }

    public double getReal(){
        return real_part;
    }

    public double getImaginary(){
        return imaginary_part;
    }

    public void setReal(double r){
        real_part = r;
    }

    public void setImaginary(double i){
        imaginary_part = i;
    }

    public ComplexNumber add(ComplexNumber number){
        if (number != null){
            real_part += number.getReal();
            imaginary_part+=number.getImaginary();
            return this;
        } else throw new NullPointerException("Number must not be null");
    }

    public ComplexNumber subtract(ComplexNumber number){
        if (number != null){
            real_part-=number.getReal();
            imaginary_part-=number.getImaginary();
            return this;
        } else throw new NullPointerException("Number must not be null");
    }

    public ComplexNumber multiply(ComplexNumber number){
        if (number != null){
            double new_real_part = real_part*number.getReal() - imaginary_part*number.getImaginary();
            imaginary_part = real_part*number.getImaginary() + imaginary_part*number.getReal();
            real_part = new_real_part;
            return this;
        } else throw new NullPointerException("Number must not be null");
    }

    public ComplexNumber divide(ComplexNumber number){
        if (number != null){
            double new_real_part = (real_part*number.getReal()+imaginary_part*number.getImaginary())/(Math.pow(number.getReal(),2)+Math.pow(number.getImaginary(),2));
            imaginary_part = (real_part*number.getImaginary()*-1+imaginary_part*number.getReal())/(Math.pow(number.getReal(),2)+Math.pow(number.getImaginary(),2));
            real_part = new_real_part;
            return this;
        } else throw new NullPointerException("Number must not be null");
    }

    public double abs() {
        return Math.hypot(real_part,imaginary_part);
    }

    public String toString(){
        if (imaginary_part >= 0) {
            return real_part + " + " + imaginary_part + "i";
        } else {
            return real_part + " - " + imaginary_part*-1 +"i";
        }
    }




}
