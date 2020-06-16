package sample;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.transform.Scale;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.stream.Collectors;

import static java.awt.image.BufferedImage.TYPE_INT_RGB;


public class ResultController {
    public Canvas canvas;
    public TextField nzrTextField,nzcTextField,leTextField;
    public Slider slider = new Slider();
    private int r,g,b;
    public Pixel[][] pixelArray;
    public int [] neighbor_x={-1,0,0,1,-1,-1,1,1};
    public int [] neighbor_y={0,-1,1,0,-1,1,-1,1};
    private BufferedImage bufferedImage;
    private BufferedImage bufferedImage2;
    private GraphicsContext gc;
    public static File selectedFile;
    @FXML
    void detectBoundary(){
        try {
             bufferedImage = ImageIO.read(selectedFile);
             gc = canvas.getGraphicsContext2D();

            canvas.setWidth(bufferedImage.getWidth());
            canvas.setHeight(bufferedImage.getHeight());
            gc.clearRect(0,0,canvas.getWidth(),canvas.getHeight());
            slider.setMin(0);
            slider.setMax(255);
            double detectorValue=slider.getValue();
            //detectorValue=150;


            //***************READ IMAGE PIXEL BY PIXEL***************

            pixelArray=new Pixel[bufferedImage.getWidth()][bufferedImage.getHeight()];
            for(int i=0;i<pixelArray.length;i++)
                for(int j=0;j<pixelArray[i].length;j++)
                    pixelArray[i][j]=new Pixel();

                            for (int i=0; i < bufferedImage.getWidth(); i++) {
                                for (int j=0; j < bufferedImage.getHeight(); j++) {
                                    r = (bufferedImage.getRGB(i, j) & 0x00ff0000) >> 16;
                                    g = (bufferedImage.getRGB(i, j) & 0x0000ff00) >> 8;
                                    b = bufferedImage.getRGB(i, j) & 0x000000ff;

                                    pixelArray[i][j].int_rgb=bufferedImage.getRGB(i, j);
                                    pixelArray[i][j].r=r;
                                    pixelArray[i][j].g=g;
                                    pixelArray[i][j].b=b;

                                    if (r > detectorValue || g > detectorValue || b > detectorValue) {
                                        bufferedImage.setRGB(i, j, 16777215);
                                    }
                                    else{
                                        pixelArray[i][j].boundary=true;
                                    }
                                }
                            }




             //***************READ IMAGE PIXEL BY PIXEL***************[END]

            gc.drawImage(SwingFXUtils.toFXImage(bufferedImage, null), 0,0);

                           // for(int i=0;i<pixelArray.length;i++)
                                //for(int j=0;j<pixelArray[i].length;j++)
                                    //System.out.println(i+" "+j+"  "+pixelArray[i][j].boundary +" "+ pixelArray[i][j].r+" "+pixelArray[i][j].g+" "+pixelArray[i][j].b+" "+pixelArray[i][j].int_rgb);



        }

        catch(Exception e){
            e.printStackTrace();
        }





        //***************EVENTS***************

        canvas.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                gc.setFill(Color.BLACK);
                gc.fillRect((int) (event.getX()), (int) (event.getY()), 1,1);
                pixelArray[(int) (event.getX())][(int) (event.getY())].boundary=true;
                pixelArray[(int) (event.getX())][(int) (event.getY())].r=0;
                pixelArray[(int) (event.getX())][(int) (event.getY())].r=0;
                pixelArray[(int) (event.getX())][(int) (event.getY())].r=0;
                pixelArray[(int) (event.getX())][(int) (event.getY())].int_rgb=-16777216;
            }
        });

        canvas.setOnScroll(event -> {
            Scale newScale = new Scale();
            double zoomFactor=1;
            double deltaY = event.getDeltaY();

            if (deltaY < 0) {
                zoomFactor *= 1.05;
                newScale.setPivotX(event.getSceneX());
                newScale.setPivotY(event.getSceneY());
            }
            else {
                zoomFactor *= 0.95;
                newScale.setPivotX(canvas.getWidth()/2);
                newScale.setPivotY(canvas.getHeight()/2);
            }

            newScale.setX( canvas.getScaleX() * zoomFactor);
            newScale.setY( canvas.getScaleY() * zoomFactor);
            canvas.getTransforms().add(newScale);
            event.consume();

        });

        //***************EVENTS***************[END]
    }

    @FXML
    void completeBoundary(){
        try {

            gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
            bufferedImage2 = new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(), TYPE_INT_RGB);

            Pixel[][] copyPixelArray = new Pixel[bufferedImage.getWidth()][bufferedImage.getHeight()];

            for (int i = 0; i < copyPixelArray.length; i++)
                for (int j = 0; j < copyPixelArray[i].length; j++) {
                    copyPixelArray[i][j] = new Pixel();
                    copyPixelArray[i][j].equal(pixelArray[i][j]);
                }

            for (int i = 0; i < bufferedImage.getWidth(); i++)
                for (int j = 0; j < bufferedImage.getHeight(); j++)
                    bufferedImage2.setRGB(i, j, bufferedImage.getRGB(i, j));


            //***************BOLD BORDERS***************
            for (int r = 0; r < 1; r++) {
                for (int i = 0; i < pixelArray.length; i++)
                    for (int j = 0; j < pixelArray[i].length; j++) {
                        for (int k = 0; k < neighbor_x.length; k++) {
                            if (pixelArray[i][j].boundary && i > 0 && j > 0 && i < pixelArray.length - 1 && j < pixelArray[i].length - 1 && !pixelArray[i][j].bold_boundary) {
                                copyPixelArray[i + neighbor_x[k]][j + neighbor_y[k]].boundary = true;
                                copyPixelArray[i + neighbor_x[k]][j + neighbor_y[k]].r = 0;
                                copyPixelArray[i + neighbor_x[k]][j + neighbor_y[k]].g = 0;
                                copyPixelArray[i + neighbor_x[k]][j + neighbor_y[k]].b = 0;
                                copyPixelArray[i + neighbor_x[k]][j + neighbor_y[k]].int_rgb = -16777216;
                                if(r==1){
                                    copyPixelArray[i + neighbor_x[k]][j + neighbor_y[k]].bold_boundary = true;
                                    copyPixelArray[i][j].bold_boundary = true;
                                }

                            }
                            if (i == 0 || j == 0 || i == pixelArray.length - 1 || j == pixelArray[i].length - 1) {
                                copyPixelArray[i][j].boundary = true;
                                copyPixelArray[i][j].r = 0;
                                copyPixelArray[i][j].g = 0;
                                copyPixelArray[i][j].b = 0;
                                copyPixelArray[i][j].int_rgb = -16777216;
                                copyPixelArray[i][j].bold_boundary = true;
                            }
                        }
                    }

                for (int i = 0; i < pixelArray.length; i++)
                    for (int j = 0; j < pixelArray[i].length; j++) {
                        if(copyPixelArray[i][j].boundary)
                            pixelArray[i][j].equal(copyPixelArray[i][j]);
                    }

            }
            //***************BOLD BORDERS***************[END]

                for (int i = 0; i < bufferedImage2.getWidth(); i++) {
                    for (int j = 0; j < bufferedImage2.getHeight(); j++) {
                        pixelArray[i][j].equal(copyPixelArray[i][j]);
                        if (pixelArray[i][j].boundary) {
                            bufferedImage2.setRGB(i, j, 0);
                        } else {
                            //bufferedImage2.setRGB(i,j,pixelArray[i][j].int_rgb);
                        }
                    }
                }




            gc.drawImage(SwingFXUtils.toFXImage(bufferedImage2, null), 0, 0);

        }
        catch (Exception e){
            e.printStackTrace();
        }


    }

    @FXML
    void generate() throws Exception{


        for(int i=0;i<pixelArray.length;i++)
            for(int j=0;j<pixelArray[0].length;j++)
                pixelArray[i][j].visited_boundary=false;




        int nzr=0,nzc=0,le=0,node_x,node_y,accuracy=25;
        String grainsColor="";
        List<Point> nodes = new ArrayList<>();
        List<List<Point>> grains=new ArrayList<>();
        List<List<Point>> finalGrains=new ArrayList<>();
        List<List<Point>> finalGrainsInOrder=new ArrayList<>();
        String fileText="";

        try {
            nzr = Integer.parseInt(nzrTextField.getText());
            nzc = Integer.parseInt(nzcTextField.getText());
            le = Integer.parseInt(leTextField.getText());
            }
        catch(Exception e){
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setHeaderText("Input is valid");
            errorAlert.setContentText("Number of points or length is incorrect");
            errorAlert.showAndWait();
        }

        int nzxArray[]=new int[nzr*nzc];
        int nzyArray[]=new int[nzr*nzc];
        int neighbour_x,neighbour_y;

        //***************ROZMIESZCZENIE WEZLOW POMOCNICZYCH***************
        for(int i=0;i<nzc;i++){
            for(int j=0;j<nzr;j++){
                node_y= (int)(i*((double)pixelArray[0].length/nzc)+(pixelArray[0].length/nzc/2));
                node_x=(int)(j*((double)pixelArray.length/nzr)+(pixelArray.length/nzr/2));

                if(!pixelArray[node_x][node_y].boundary){
                    nzyArray[i*nzr+j]=node_y;
                    nzxArray[i*nzr+j]=node_x;
                }

                if(nzyArray[i*nzr+j]>pixelArray[0].length-1)
                    nzyArray[i*nzr+j]=pixelArray[0].length-1;
                if(nzxArray[i*nzr+j]>pixelArray.length-1)
                    nzxArray[i*nzr+j]=pixelArray.length-1;
            }
        }
        //***************ROZMIESZCZENIE WEZLOW POMOCNICZYCH***************[END]

        //***************SEARCHING FOR THE BORDER***************
        for(int h=0;h<nzr*nzc;h++) {
            //gc.setFill(Color.GREEN);
            //gc.fillRect(nzxArray[h], nzyArray[h], 2, 2);
            while (!pixelArray[nzxArray[h]][nzyArray[h]].boundary) {
                //gc.setFill(Color.LIGHTGREEN);
                //gc.fillRect(nzxArray[h], nzyArray[h], 1, 1);
                nzxArray[h]--;
            }
            if(!pixelArray[nzxArray[h]][nzyArray[h]].visited_boundary){
                //gc.setFill(Color.RED);
                //gc.fillRect(nzxArray[h], nzyArray[h], 1, 1);
                pixelArray[nzxArray[h]][nzyArray[h]].visited_boundary=true;
                nodes.add(new Point(nzxArray[h],nzyArray[h]));
                for(int i=0;i<neighbor_x.length;i++){
                    neighbour_x = nzxArray[h] + neighbor_x[i];
                    neighbour_y = nzyArray[h] + neighbor_y[i];
                    if(neighbour_x>0 && neighbour_y>0 && neighbour_x<pixelArray.length && neighbour_y<pixelArray[0].length && !pixelArray[nzxArray[h]+1+neighbor_x[i]][nzyArray[h]+neighbor_y[i]].boundary){
                        pixelArray[nzxArray[h]+1][nzyArray[h]].r=(pixelArray[nzxArray[h]+1][nzyArray[h]].r+pixelArray[nzxArray[h]+1+neighbor_x[i]][nzyArray[h]+neighbor_y[i]].r)/2;
                        pixelArray[nzxArray[h]+1][nzyArray[h]].g=(pixelArray[nzxArray[h]+1][nzyArray[h]].g+pixelArray[nzxArray[h]+1+neighbor_x[i]][nzyArray[h]+neighbor_y[i]].g)/2;
                        pixelArray[nzxArray[h]+1][nzyArray[h]].b=(pixelArray[nzxArray[h]+1][nzyArray[h]].b+pixelArray[nzxArray[h]+1+neighbor_x[i]][nzyArray[h]+neighbor_y[i]].b)/2;
                    }
                }
                grainsColor+=pixelArray[nzxArray[h]+1][nzyArray[h]].r+","+pixelArray[nzxArray[h]+1][nzyArray[h]].g+","+pixelArray[nzxArray[h]+1][nzyArray[h]].b+"x";
            }
         //***************SEARCHING FOR THE BORDER***************[END]

            while (true) {
                for (int i = 0; i < neighbor_x.length; i++) {
                    neighbour_x = nzxArray[h] + neighbor_x[i];
                    neighbour_y = nzyArray[h] + neighbor_y[i];

                    if (neighbour_x >= 0 && neighbour_y >= 0 && neighbour_x <= pixelArray.length - 1 && neighbour_y <= pixelArray[0].length - 1
                            && pixelArray[neighbour_x][neighbour_y].boundary && !pixelArray[neighbour_x][neighbour_y].visited_boundary) {
                        for (int j = 0; j < neighbor_x.length; j++) {
                            neighbour_x = nzxArray[h] + neighbor_x[i] + neighbor_x[j];
                            neighbour_y = nzyArray[h] + neighbor_y[i] + neighbor_y[j];
                            if (neighbour_x >= 0 && neighbour_y >= 0 && neighbour_x <= pixelArray.length - 1 && neighbour_y <= pixelArray[0].length - 1
                                    && !pixelArray[neighbour_x][neighbour_y].boundary) {
                                nzxArray[h] += neighbor_x[i];
                                nzyArray[h] += neighbor_y[i];



                                if(nodes.size()>0 && new Point(nzxArray[h],nzyArray[h]).distance(nodes.get(nodes.size()-1).x,nodes.get(nodes.size()-1).y)>=le) {
                                    nodes.add(new Point(nzxArray[h],nzyArray[h]));
                                    //gc.setFill(Color.RED);
                                    //gc.fillRect(nzxArray[h], nzyArray[h], 1, 1);
                                }
                                else{
                                    //gc.setFill(Color.GREEN);
                                    //gc.fillRect(nzxArray[h], nzyArray[h], 1, 1);
                                }


                                i = neighbor_x.length;
                                break;

                            }
                        }
                    }

                }
                if (pixelArray[nzxArray[h]][nzyArray[h]].visited_boundary) {
                    break;
                }
                pixelArray[nzxArray[h]][nzyArray[h]].visited_boundary = true;
            }

            if(nodes.size()>0) {
                grains.add(new ArrayList<Point>(nodes));
            }
            nodes.clear();

        }

        //***************MERGE THE NODES ON BORDER***************

       int finalNodesX,finalNodesY,mergePoint=0;
        double distance=canvas.getWidth();
        gc.setFill(Color.ORANGE);
        System.out.println(pixelArray.length);
        for(int i=0;i<grains.size();i++)
            for (int j = 0; j < grains.size(); j++) {
                if(i==j)
                    continue;
                for(int k=0;k<grains.get(i).size();k++) {
                    for (int l = 0; l < grains.get(j).size(); l++) {
                        if (!grains.get(i).get(k).merge && !grains.get(j).get(l).merge && grains.get(i).get(k).distance(grains.get(j).get(l).x, grains.get(j).get(l).y) < accuracy &&
                                grains.get(i).get(k).x!=0 && grains.get(i).get(k).x!=canvas.getWidth()-1 &&
                                grains.get(i).get(k).y!=0 && grains.get(i).get(k).y!=canvas.getHeight()-1 && grains.get(j).get(l).x!=0 && grains.get(j).get(l).x!=canvas.getWidth()-1 &&
                                grains.get(j).get(l).y!=0 && grains.get(j).get(l).y!=canvas.getHeight()-1 &&
                                theSameBorder(pixelArray,grains.get(i).get(k),grains.get(j).get(l) )) {
                            if (distance > grains.get(i).get(k).distance(grains.get(j).get(l).x, grains.get(j).get(l).y)) {
                                distance = grains.get(i).get(k).distance(grains.get(j).get(l).x, grains.get(j).get(l).y);
                                mergePoint = l;
                            }
                        }
                    }

                    if(distance!=canvas.getWidth()){
                        finalNodesX=(grains.get(i).get(k).x+grains.get(j).get(mergePoint).x)/2;
                        finalNodesY=(grains.get(i).get(k).y+grains.get(j).get(mergePoint).y)/2;
                        gc.fillRect(finalNodesX,finalNodesY, 2, 2);

                        grains.get(i).get(k).x=finalNodesX;
                        grains.get(j).get(mergePoint).x=finalNodesX;
                        grains.get(i).get(k).y=finalNodesY;
                        grains.get(j).get(mergePoint).y=finalNodesY;
                        grains.get(i).get(k).merge=true;
                        grains.get(j).get(mergePoint).merge=true;
                        distance=canvas.getWidth();
                    }


                }

            }
        //***************MERGE THE NODES ON BORDER***************[END]


        //***************ADD ALL MERGED NODE TO NEW LIST***************



        for(int i=0;i<grains.size();i++) {
            for (int j = 0; j < grains.get(i).size();j++) {
                if(grains.get(i).get(j).merge || grains.get(i).get(j).x==0|| grains.get(i).get(j).y==0 ||grains.get(i).get(j).x==canvas.getWidth()-1|| grains.get(i).get(j).y==canvas.getHeight()-1 ){
                    nodes.add(grains.get(i).get(j));
                }
            }
            nodes=nodes.stream().distinct().collect(Collectors.toList());
            finalGrains.add(new ArrayList<Point>(nodes));
            nodes.clear();
        }
        //***************ADD ALL MERGED NODE TO NEW LIST***************[END]


        //***************FINAL GRAINS IN ORDER***************
        distance=accuracy;
        for(int i=0;i<finalGrains.size();i++) {
            for (int j = 0; j < finalGrains.get(i).size();j++) {
                if(j==0) {
                    finalGrains.get(i).get(j).inOrder = true;
                    nodes.add(finalGrains.get(i).get(j));
                }

                for (int k = 0; k < finalGrains.get(i).size();k++) {
                    if(i==11){
                        //System.out.println(nodes.get(nodes.size()-1)+"    "+finalGrains.get(i).get(k)+"     "+finalGrains.get(i).get(k).inOrder+"    "+nodes.get(nodes.size()-1).distance(finalGrains.get(i).get(k).x,finalGrains.get(i).get(k).y));
                    }
                    if(nodes.get(nodes.size()-1).x==finalGrains.get(i).get(k).x && nodes.get(nodes.size()-1).y==finalGrains.get(i).get(k).y ){
                        continue;
                    }
                    if(distance>nodes.get(nodes.size()-1).distance(finalGrains.get(i).get(k).x,finalGrains.get(i).get(k).y) && !finalGrains.get(i).get(k).inOrder){
                        distance=nodes.get(nodes.size()-1).distance(finalGrains.get(i).get(k).x,finalGrains.get(i).get(k).y);
                        mergePoint=k;
                    }
                }

                if(mergePoint<finalGrains.get(i).size() && finalGrains.get(i).get(mergePoint)==nodes.get(0)){
                    distance = accuracy;
                    break;
                }
                if(distance<accuracy) {
                    finalGrains.get(i).get(mergePoint).inOrder = true;
                    nodes.add(finalGrains.get(i).get(mergePoint));
                    distance = accuracy;
                }
            }
            nodes=nodes.stream().distinct().collect(Collectors.toList());
            finalGrainsInOrder.add(new ArrayList<Point>(nodes));
            nodes.clear();
        }
        //***************FINAL GRAINS IN ORDER***************[END]


        //***************SAVE TO FILE***************
        for(int i=0;i<finalGrainsInOrder.size();i++){
            if(finalGrainsInOrder.get(i).size()>0) {
                finalGrainsInOrder.get(i).add(finalGrainsInOrder.get(i).get(0));
                fileText += finalGrainsInOrder.get(i).size()-1 + "x";
                for (int j = 0; j < finalGrainsInOrder.get(i).size(); j++) {
                        fileText += finalGrainsInOrder.get(i).get(j).x;
                        fileText += ",";
                        fileText += finalGrainsInOrder.get(i).get(j).y;
                        fileText += "x";

                }
            }


        }


        BufferedWriter writer = new BufferedWriter(new FileWriter("C:/Users/Dharel/Desktop/grain.txt"));
        writer.write(grains.size()+"");
        writer.newLine();
        String[] points = fileText.split("x");
        for (String point: points) {
            writer.write(point);
            writer.newLine();
        }
        writer.close();

        writer = new BufferedWriter(new FileWriter("C:/Users/Dharel/Desktop/grainColor.txt"));
        String[] grainColor = grainsColor.split("x");
        for (String s: grainColor) {
            writer.write(s);
            writer.newLine();
        }
        writer.close();
        gc.clearRect(0,0,canvas.getWidth(),canvas.getHeight());
        drawStructure(finalGrainsInOrder);
        System.out.println("Done");

        //***************SAVE TO FILE***************[END]

    }

    @FXML
    void readFromTxt() throws Exception{
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Txt File With Coordinates");
        File selectedFileTxt = fileChooser.showOpenDialog(new Stage());
        Scanner sc = new Scanner(selectedFileTxt);

        List<Point> nodes = new ArrayList<>();
        List<List<Point>> grains=new ArrayList<>();

        System.out.println("Loaded " + sc.next()+" grains");
        String point="";
        try {
            while (sc.hasNextLine()) {
                int w = Integer.parseInt(sc.next()) + 1;
                for (int i = 0; i < w; i++) {
                    point=sc.next();
                    String pointCoordinates[] =point.split(",");
                    nodes.add(new Point(Integer.parseInt(pointCoordinates[0]),Integer.parseInt(pointCoordinates[1])));
                }
                grains.add(new ArrayList<Point>(nodes));
                nodes.clear();
            }

        }
        catch (NoSuchElementException e){
            try {
                gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
            }
            catch(Exception ex){

            }
            drawStructure(grains);
        }

        List<Pixel> pixels = new ArrayList<>();
        try {
        fileChooser.setTitle("Open Txt File With Grains Colors");
        selectedFileTxt = fileChooser.showOpenDialog(new Stage());
        sc = new Scanner(selectedFileTxt);


            while (sc.hasNextLine()) {
                    String pixelrgb[] =sc.next().split(",");
                    pixels.add(new Pixel(Integer.parseInt(pixelrgb[0]),Integer.parseInt(pixelrgb[1]),Integer.parseInt(pixelrgb[2])));
            }
        }
        catch(NoSuchElementException e){
            drawStructure(grains, pixels);
            drawStructure(grains);
        }
        catch (NullPointerException e) {
            System.out.println("Draw structure withour colors");
        }
        catch (Exception e){

        }



}
    @FXML
    void saveImage()throws Exception{

               canvas.getTransforms().clear();

        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("PNG", "*.png"),
                new FileChooser.ExtensionFilter("GIF", "*.gif"),
                new FileChooser.ExtensionFilter("TIF", "*.tif")
        );
        File saveFile = fileChooser.showSaveDialog(new Stage());
        if(saveFile!=null) {
            WritableImage writableImage = new WritableImage((int) canvas.getWidth(), (int) canvas.getHeight());
            canvas.snapshot(null, writableImage);
            RenderedImage renderedImage = SwingFXUtils.fromFXImage(writableImage, null);
            ImageIO.write(renderedImage, saveFile.getAbsolutePath().substring(saveFile.getAbsolutePath().length()-3), saveFile);
        }
    }

    static void passImage(File passFile){
        selectedFile=passFile;
    }
    private boolean theSameBorder( Pixel[][] pixelArray,Point p,Point p2){
        int pX=p.x;
        int pY=p.y;

        while (true){
            if(pX-p2.x<0)
                pX++;
            if(pY-p2.y<0)
                pY++;
            if(pX-p2.x>0)
                pX--;
            if(pY-p2.y>0)
                pY--;
            if(pX==p2.x && pY==p2.y)
                return true;
            if(!pixelArray[pX][pY].boundary)
                break;

        }
        return false;
    }
    private void drawStructure(List<List<Point>> grains,List<Pixel> pixels)throws Exception{
        try {
            bufferedImage = ImageIO.read(selectedFile);
            gc = canvas.getGraphicsContext2D();
            canvas.setWidth(bufferedImage.getWidth());
            canvas.setHeight(bufferedImage.getHeight());
            gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        }
        catch (Exception e){
            gc = canvas.getGraphicsContext2D();
            canvas.setWidth(1600);
            canvas.setHeight(900);
        }

        double[] xPoints;
        double[] yPoints;

        for(int i=0;i<grains.size();i++){
            xPoints=new double[grains.get(i).size()];
            yPoints=new double[grains.get(i).size()];
            for (int j = 0; j < grains.get(i).size(); j++) {
                xPoints[j]=grains.get(i).get(j).x;
                yPoints[j]=grains.get(i).get(j).y;
            }
            gc.setFill(Color.rgb(pixels.get(i).r,pixels.get(i).g,pixels.get(i).b));
            gc.fillPolygon(xPoints,yPoints,xPoints.length);
        }
    }

    private void drawStructure(List<List<Point>> grains)throws Exception{
        try {
            bufferedImage = ImageIO.read(selectedFile);
            gc = canvas.getGraphicsContext2D();
            canvas.setWidth(bufferedImage.getWidth());
            canvas.setHeight(bufferedImage.getHeight());
        }
        catch(Exception e){
            gc = canvas.getGraphicsContext2D();
            canvas.setWidth(1600);
            canvas.setHeight(900);
        }

        for(int i=0;i<grains.size();i++){
            for (int j = 0; j < grains.get(i).size()-1; j++) {
                gc.strokeLine(grains.get(i).get(j).x,grains.get(i).get(j).y,grains.get(i).get(j+1).x,grains.get(i).get(j+1).y);
            }
        }
    }

}
