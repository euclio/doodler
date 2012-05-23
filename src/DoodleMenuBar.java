import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

@SuppressWarnings("serial")
public class DoodleMenuBar extends JMenuBar {
    public enum MenuChoices {
        // Help
        ABOUT, EXIT, // File
        NEW, OPEN,

        SAVE
    };

    class MenuListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JMenuItem source = (JMenuItem) e.getSource();
            String itemText = source.getText().replaceAll("[^a-zA-Z0-9]", "");

            switch (MenuChoices.valueOf(itemText.toUpperCase())) {
            case NEW:
                newFile(gui, canvas);
                break;
            case OPEN:
                openFile(gui, canvas);
                break;
            case SAVE:
                saveFile(gui, canvas);
                break;
            case EXIT:
                exit(gui, canvas);
                break;

            // Help Menu
            case ABOUT:
                about(gui, canvas);
            }
        }
    }

    private final Canvas canvas;
    private static final String VALID_IMAGES = "([^\\s]+(\\.(?i)(jpg|png|gif|bmp))$)";
    private final DoodleGUI gui;

    public DoodleMenuBar(DoodleGUI gui, Canvas canvas) {
        super();
        this.gui = gui;
        this.canvas = canvas;
        // Class to handle menu events

        ActionListener menuListener = new MenuListener();

        // Build the "File" Menu
        JMenu fileMenu = new JMenu("File");

        JMenuItem newFile = new JMenuItem("New...", KeyEvent.VK_N);
        newFile.addActionListener(menuListener);
        fileMenu.add(newFile);

        JMenuItem openFile = new JMenuItem("Open...");
        openFile.addActionListener(menuListener);
        fileMenu.add(openFile);

        JMenuItem saveFile = new JMenuItem("Save...");
        saveFile.addActionListener(menuListener);
        fileMenu.add(saveFile);

        fileMenu.addSeparator();

        JMenuItem close = new JMenuItem("Exit");
        close.addActionListener(menuListener);
        fileMenu.add(close);

        this.add(fileMenu);

        // Build the "Help" Menu
        JMenu helpMenu = new JMenu("Help");

        JMenuItem about = new JMenuItem("About");
        about.addActionListener(menuListener);
        helpMenu.add(about);

        this.add(helpMenu);
    }

    public static void about(JFrame f, Canvas canvas) {
        JOptionPane
                .showMessageDialog(
                        f,
                        "Doodler is a simple drawing program written in Java using Swing/AWT.  Coded with love by Andy Russell.",
                        "About Doodler", JOptionPane.PLAIN_MESSAGE);
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

    public static void newFile(JFrame f, Canvas canvas) {
        if (canvas.isModified()) {
            int choice = JOptionPane.showConfirmDialog(f,
                    "Are you sure you want to start over?", "Confirm Reset",
                    JOptionPane.YES_NO_OPTION);
            if (choice == JOptionPane.YES_OPTION)
                canvas.reset();
        } else {
            canvas.reset();
        }
    }

    public static void openFile(JFrame f, Canvas canvas) {
        // OPEN
        int result;

        if (canvas.isModified()) {
            result = JOptionPane
                    .showConfirmDialog(
                            f,
                            "The current doodle is not yet saved.  Would you like to save?",
                            "Warning", JOptionPane.YES_NO_CANCEL_OPTION);
            if (result == JOptionPane.YES_OPTION) {
                canvas.saveImage();
            }

            if (result == JOptionPane.CANCEL_OPTION) {
                return;
            }
        }

        final JFileChooser chooser = new JFileChooser();
        int returnVal = chooser.showOpenDialog(f);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File newFile = chooser.getSelectedFile();
            try {
                if (newFile.getName().matches(VALID_IMAGES)) {
                    BufferedImage newImage = ImageIO.read(newFile);
                    canvas.setImage(newImage);
                } else {
                    throw new IOException("This file is not an image.");
                }
            } catch (IOException exc) {
                JOptionPane.showMessageDialog(f, exc.getMessage(),
                        "Error When Opening File", JOptionPane.ERROR_MESSAGE);

            }
        }
    }

    public static void saveFile(JFrame f, Canvas canvas) {
        canvas.saveImage();
    }
}
