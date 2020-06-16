package sample;

public class Pixel {
    int r,g,b,int_rgb;
    boolean boundary;
    boolean bold_boundary;
    boolean visited_boundary;

    Pixel(){
        this.boundary=false;
        this.bold_boundary=false;
        this.visited_boundary=false;
    }
    Pixel(int a1,int a2,int a3){
        r=a1;
        g=a2;
        b=a3;
    }

    public void equal(Pixel a){
        this.r=a.r;
        this.g=a.g;
        this.b=a.b;
        this.int_rgb=a.int_rgb;
        this.boundary=a.boundary;
        this.bold_boundary=a.bold_boundary;
        this.visited_boundary=a.visited_boundary;

    }
}
