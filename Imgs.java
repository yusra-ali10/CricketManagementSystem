import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;

/** Small helper to load pictures from the images / photos folders. */
public class Imgs {

    public static BufferedImage image(String name) {
        try {
            File f = new File("images/" + name);
            return f.exists() ? ImageIO.read(f) : null;
        } catch (Exception e) {
            return null;
        }
    }

    public static ImageIcon icon(String name, int w, int h) {
        BufferedImage img = image(name);
        if (img == null) return null;
        return new ImageIcon(img.getScaledInstance(w, h, Image.SCALE_SMOOTH));
    }

    /** Square, centre-cropped player photo. Falls back to the default avatar. */
    public static ImageIcon photo(String path, int size) {
        BufferedImage src = null;
        try {
            File f = new File(path == null || path.isEmpty() ? "images/avatar.png" : path);
            if (!f.exists()) f = new File("images/avatar.png");
            src = ImageIO.read(f);
        } catch (Exception e) { /* ignore */ }
        if (src == null) return null;
        int s = Math.min(src.getWidth(), src.getHeight());
        BufferedImage crop = src.getSubimage((src.getWidth() - s) / 2, (src.getHeight() - s) / 2, s, s);
        return new ImageIcon(crop.getScaledInstance(size, size, Image.SCALE_SMOOTH));
    }
}
