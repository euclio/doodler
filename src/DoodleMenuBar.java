import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

@SuppressWarnings("serial")
public class DoodleMenuBar extends JMenuBar {
    public enum MenuChoices {
        // File
        NEW, OPEN, SAVE, SAVEAS, EXIT,

        // Help
        ABOUT
    };

    class MenuListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JMenuItem source = (JMenuItem) e.getSource();
            String itemText = source.getText().replaceAll("[^a-zA-Z0-9]", "");

            switch (MenuChoices.valueOf(itemText.toUpperCase())) {
            case NEW:
                newFile();
                break;
            case OPEN:
                openFile();
                break;
            case SAVE:
                saveFile();
                break;
            case SAVEAS:
                saveAs();
                break;
            case EXIT:
                exit();
                break;

            // Help Menu
            case ABOUT:
                about();
            }
        }
    }

    private final Container rootPane = this.getTopLevelAncestor();
    private final Canvas canvas;
    private static final String VALID_IMAGES = "([^\\s]+(\\.(?i)(jpg|png|gif|bmp))$)";
    
    
    public DoodleMenuBar(Canvas canvas) {
        super();
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
        
        JMenuItem saveAs = new JMenuItem("Save As...");
        saveAs.addActionListener(menuListener);
        fileMenu.add(saveAs);

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

    public void about() {
        JOptionPane
                .showMessageDialog(
                        rootPane,
                        "Doodler is a simple drawing program written in Java using Swing/AWT.  Coded with love by Andy Russell.",
                        "About Doodler", JOptionPane.PLAIN_MESSAGE);
    }

    public void exit() {
        int result = 0;
        if (canvas.isModified()) {
            result = JOptionPane
                    .showConfirmDialog(
                            rootPane,
                            "The current doodle is not saved.  Would you like to save it?",
                            "Warning", JOptionPane.YES_NO_CANCEL_OPTION);
            if (result == JOptionPane.YES_OPTION) {
                saveFile();
            }

            if (result == JOptionPane.CANCEL_OPTION) {
                return;
            }
        }
        System.exit(0);
    }

    public void newFile() {
        if (canvas.isModified()) {
            int choice = JOptionPane.showConfirmDialog(rootPane,
                    "Are you sure you want to start over?", "Confirm Reset",
                    JOptionPane.YES_NO_OPTION);
            if (choice == JOptionPane.YES_OPTION)
                canvas.reset();
        } else {
            canvas.reset();
        }
    }

    public void openFile() {
        // OPEN
        int result;

        if (canvas.isModified()) {
            result = JOptionPane
                    .showConfirmDialog(
                            rootPane,
                            "The current doodle is not saved.  Would you like to save it?",
                            "Warning", JOptionPane.YES_NO_CANCEL_OPTION);
            if (result == JOptionPane.YES_OPTION) {
                saveFile();
            }

            if (result == JOptionPane.CANCEL_OPTION) {
                return;
            }
        }

        final JFileChooser chooser = new JFileChooser(canvas.getSaveDirectory());
        int returnVal = chooser.showOpenDialog(rootPane);
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
                JOptionPane.showMessageDialog(rootPane, exc.getMessage(),
                        "Error When Opening File", JOptionPane.ERROR_MESSAGE);

            }
        }
    }

    public void saveAs() {
        JFileChooser chooser = new JFileChooser(canvas.getSaveDirectory());
        chooser.setSelectedFile(canvas.getSaveFile());
        int returnVal = chooser.showSaveDialog(rootPane);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            if (chooser.getSelectedFile().exists()) {
                returnVal = JOptionPane.showConfirmDialog(rootPane, chooser.getSelectedFile().getName() + " already exists.  Overwrite?", "Overwrite File?", JOptionPane.YES_NO_OPTION);
                if (returnVal == JOptionPane.NO_OPTION) {
                    return;
                }
            }
            canvas.setCurrentFile(chooser.getSelectedFile());
            canvas.setSaved(true);
            saveFile();
        }
    }

    public void saveFile() {
        if (canvas.isSaved()) {
            try {
                ImageIO.write(canvas.getImage(), "png", canvas.getSaveFile());
                canvas.setModified(false);
            } catch (IOException exc) {
                System.err.println("Exception thrown during write");
                // Handle exception
            }
        } else {
            saveAs();
        }
        return;
    }
}