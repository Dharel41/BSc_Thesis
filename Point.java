package sample;
public class Point{
    int x,y;
    boolean merge,inOrder;
    Point(int arg1,int arg2){
        x=arg1;
        y=arg2;
        merge=false;
        inOrder=false;
    }

    double distance(int argx,int argy){
        return Math.sqrt((argy - y) * (argy - y) + (argx - x) * (argx - x));
    }

    public String toString(){
        return x+","+y;
    }

}
