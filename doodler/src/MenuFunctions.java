import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class MenuFunctions {
    public static void newFile(JFrame f, Canvas canvas) {
        int choice = JOptionPane.showConfirmDialog(f,
                "Are you sure you want to start over?", "Confirm Reset",
                JOptionPane.YES_NO_OPTION);
        if (choice == JOptionPane.YES_OPTION)
            canvas.reset();
    }

    public static void openFile(JFrame f, Canvas canvas) {
        // OPEN
        int result = 0;
        if (canvas.isModified()) {
            result = JOptionPane
                    .showConfirmDialog(
                            f,
                            "The current doodle is not yet saved.  Would you like to save?",
                            "Warning", JOptionPane.YES_NO_CANCEL_OPTION);
        }

        if (result == JOptionPane.YES_OPTION) {
            canvas.saveImage();
        }

        if (result == JOptionPane.CANCEL_OPTION) {
            return;
        }

        final JFileChooser chooser = new JFileChooser();
        int returnVal = chooser.showOpenDialog(f);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File newFile = chooser.getSelectedFile();
            try {
                BufferedImage newImage = ImageIO.read(newFile);
                canvas.setImage(newImage);
            } catch (IOException exc) {
                System.out.println("Exception thrown");
                // Handle exception
            }
        }
    }
    
    public static void saveFile(JFrame f, Canvas canvas) {
        canvas.saveImage();
    }

    public static void exit(JFrame f, Canvas canvas) {
        int result = 0;
        if (canvas.isModified()) {
            result = JOptionPane
                    .showConfirmDialog(
                            f,
                            "The current doodle is not yet saved.  Would you like to save?",
                            "Warning", JOptionPane.YES_NO_CANCEL_OPTION);
        }

        if (result == JOptionPane.YES_OPTION) {
            canvas.saveImage();
        }

        if (result == JOptionPane.CANCEL_OPTION) {
            return;
        }

        System.exit(0);
    }

    public static void about(JFrame f, Canvas canvas) {
        JOptionPane
                .showMessageDialog(
                        f,
                        "Doodler is a simple drawing program written in Java using Swing/AWT.  Coded with love by Andy Russell.",
                        "About Doodler", JOptionPane.PLAIN_MESSAGE);
    }
}
