package icons;

import java.awt.Image;
import java.util.Objects;
import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.evosuite.intellij.EvoAction;

/**
 * @author XenoAmess
 */
public class EvosuiteIcons {

    public static final Icon EVOSUITE_ICON = loadIcon();

    private static Icon loadIcon() {
        try {
            Image image = ImageIO.read(
                    Objects.requireNonNull(
                            EvoAction.class.getClassLoader().getResourceAsStream("icons/evosuite.png"),
                            "getResourceAsStream result be null"
                    )
            );
            image = image.getScaledInstance(16, 16, java.awt.Image.SCALE_SMOOTH);

            return new ImageIcon(image);
        } catch (Exception e) {
            throw new RuntimeException("EvoAction.loadIcon failed", e);
        }
    }

}
