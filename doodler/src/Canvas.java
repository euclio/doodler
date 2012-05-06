import javax.imageio.ImageIO;
import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;

import java.io.*;

@SuppressWarnings("serial")
public class Canvas extends JPanel {
    private BufferedImage image = null;
    private int shapeSize;
    private Color color = Color.BLACK;
    private Shape shape = Shape.CIRCLE;
    private boolean modified;
    private File currentFile = null;

    public Canvas() {
        addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                resizeCanvas();
                repaint();
            }
        });

        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                drawShape(image.createGraphics(), shape, e.getX(), e.getY());
                repaint();
            }
        });

        addMouseMotionListener(new MouseAdapter() {
            public void mouseDragged(MouseEvent e) {
                drawShape(image.createGraphics(), shape, e.getX(), e.getY());
                repaint();
            }
        });
    }

    public BufferedImage getImage() {
        return image;
    }

    public void setImage(BufferedImage b) {
        image = b;
        repaint();
    }

    public File saveImage() {
        if (currentFile == null) {
            JFileChooser chooser = new JFileChooser();
            chooser.setSelectedFile(new File("doodle.png"));
            int returnVal = chooser.showSaveDialog(null);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                currentFile = chooser.getSelectedFile();
            }
        }

        try {
            ImageIO.write(image, "png", currentFile);
        } catch (IOException exc) {
            System.err.println("Exception thrown during write");
            // Handle exception
        }
        return currentFile;
    }

    public File getCurrentFile() {
        return currentFile;
    }

    public void setCurrentFile(File f) {
        currentFile = f;
    }

    public boolean isModified() {
        return modified;
    }

    public void setModified(boolean b) {
        modified = b;
    }

    public Shape getShape() {
        return shape;
    }

    public void setShape(Shape s) {
        shape = s;
    }

    public int getShapeSize() {
        return shapeSize;
    }

    public void setShapeSize(int s) {
        this.shapeSize = s;
    }

    public void setColor(Color c) {
        color = c;
    }

    public Color getColor() {
        return color;
    }

    private void resizeCanvas() {
        if (this.getWidth() <= 0 || this.getHeight() <= 0) {
            return;
        }
        BufferedImage oldImage = image;
        image = new BufferedImage(this.getWidth(), this.getHeight(),
                oldImage.getType());
        Graphics2D tempg = image.createGraphics();
        tempg.setColor(Color.WHITE);
        tempg.fillRect(0, 0, this.getWidth(), this.getHeight());
        tempg.drawImage(oldImage, null, 0, 0);
    }

    private void drawShape(Graphics2D tempg, Shape s, int x, int y) {
        tempg.setColor(color);
        switch (s) {
        case SQUARE:
            tempg.fillRect(x - shapeSize / 2, y - shapeSize / 2, shapeSize,
                    shapeSize);
            break;

        case CIRCLE:
            tempg.fillOval(x - shapeSize / 2, y - shapeSize / 2, shapeSize,
                    shapeSize);
            break;

        case TRIANGLE:
            int[] xPoints = { x - shapeSize / 2, x + shapeSize / 2, x };
            int[] yPoints = { y + shapeSize / 2, y + shapeSize / 2,
                    y - shapeSize / 2 };
            tempg.fillPolygon(xPoints, yPoints, 3);
            break;
        }
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D tempg = (Graphics2D) g;
        if (image == null) {
            int w = this.getWidth();
            int h = this.getHeight();
            image = (BufferedImage) this.createImage(w, h);
            Graphics2D gc = image.createGraphics();
            gc.setColor(Color.white);
            gc.fillRect(0, 0, w, h);
        }

        tempg.drawImage(image, null, 0, 0);
        setModified(true);
    }

    public void reset() {
        int w = this.getWidth();
        int h = this.getHeight();
        image = (BufferedImage) this.createImage(w, h);
        Graphics2D gc = image.createGraphics();
        gc.setColor(Color.white);
        gc.fillRect(0, 0, w, h);
        repaint();
    }
}