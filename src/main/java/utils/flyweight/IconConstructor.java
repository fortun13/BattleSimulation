package utils.flyweight;

import utils.Prototype;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.List;

/**
 * Created by Marek on 21.02.2015.
 * Prototype interface implementation for BufferedImage
 */
public class IconConstructor implements Prototype<String, IconConstructor> {
    private BufferedImage image;

    @Override
    public IconConstructor clone() throws CloneNotSupportedException {
        super.clone();
        return new IconConstructor();
    }

    @Override
    public void setup(List<String> strings) {
        String path = strings.get(0);
        URL resource = getClass().getClassLoader().getResource(path);
        try {
            if (resource == null) throw new IOException();
            image = ImageIO.read(resource);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("No such file found: " + path);
        }
    }

    public BufferedImage get() {
        return image;
    }
}