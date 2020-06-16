package sample;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

public class SampleController {
    public Image selectedImage;
    public File selectedFile;
    public ImageView imageView;

    @FXML
    void pickImageButtonAction(){
        String extension="";
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose Input Argument");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("All Files", "*.*"),
                new FileChooser.ExtensionFilter("JPG", "*.jpg"),
                new FileChooser.ExtensionFilter("PNG", "*.png"),
                new FileChooser.ExtensionFilter("BMP", "*.bmp"),
                new FileChooser.ExtensionFilter("GIF", "*.gif"),
                new FileChooser.ExtensionFilter("TIF", "*.tif")
        );
        selectedFile = fileChooser.showOpenDialog(new Stage());

        //***************GET FILE EXTENSION***************
        if(selectedFile!=null) {
            int i = selectedFile.getPath().lastIndexOf('.');
            if (i > 0) {
                extension = selectedFile.getPath().substring(i + 1);
            }
        }
        //***************GET FILE EXTENSION***************[END]

        //***************PUT IMAGE TO IMAGEVIEW***************
        if(selectedFile!=null) {
            if (!extension.equals("tif")) {
                selectedImage = new Image("file:" + selectedFile.getAbsolutePath());
                imageView.setImage(selectedImage);
            }

            if (extension.equals("tif")) {
                File pngFile = new File("temporary.png");
                try {
                    BufferedImage image = ImageIO.read(selectedFile);  //read tif file
                    ImageIO.write(image, "png", pngFile);  //convert to png
                    selectedImage = new Image("file:" + pngFile.getAbsolutePath());
                    imageView.setImage(selectedImage);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
            //***************PUT IMAGE TO IMAGE VIEW***************[END]

        }

    }

    @FXML
    void nextButtonAction()throws Exception{
        if(selectedFile!=null) {
            Stage secondStage = new Stage();
            Parent root = FXMLLoader.load(ResultController.class.getResource("result.fxml"));
            secondStage.setScene(new Scene(root, 640,360));

            secondStage.show();
            ResultController.passImage(selectedFile);
        }
    }
    @FXML
    void readFromFile()throws Exception{
            Stage secondStage = new Stage();
            Parent root = FXMLLoader.load(ResultController.class.getResource("result.fxml"));
            secondStage.setScene(new Scene(root, 640,360));
            secondStage.show();
    }
}
