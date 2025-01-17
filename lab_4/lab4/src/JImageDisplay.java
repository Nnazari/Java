import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.nio.Buffer;

public class JImageDisplay extends JComponent {
    private BufferedImage _image;
    public JImageDisplay(int width , int height){
        _image = new BufferedImage(width, height,BufferedImage.TYPE_INT_RGB);

        Dimension size = new Dimension(width, height);
        super.setPreferredSize(size);
    }
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        g.drawImage(_image,0,0,_image.getWidth(), _image.getHeight(), null);
    }
    public void drawPixel(int x,int y ,int rgb){
        _image.setRGB(x,y,rgb);
    }
    public void clearImage(){
        for (int i =0;i < _image.getWidth();i++){
            for (int j =0;j < _image.getHeight();j++){
                drawPixel(i,j,0);
            }
        }
    }
    public BufferedImage getImage() {
        return _image;
    }

}
