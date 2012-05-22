import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;

import java.io.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@SuppressWarnings("serial")
public class Canvas extends JPanel {
    private BlockingQueue<Point> points = new LinkedBlockingQueue<Point>();

    private class DrawingThread extends Thread {
        public void add(Point p) {
            points.add(p);
        }

        @Override
        public void run() {
            while (true) {
                try {
                    drawShape(points.take());
                } catch (InterruptedException e) {
                    // Do nothing
                }
            }
        }
    }

    private static final int CANVAS_MARGIN = 10;

    private BufferedImage image = null;
    private int shapeSize;
    private Color color = Color.BLACK;
    private Shape shape = Shape.CIRCLE;
    private boolean modified;
    private File currentFile = null;
    private DrawingThread drawWorker = new DrawingThread();

    public Canvas() {
        this.setBackground(Color.GRAY);
        drawWorker.start();

        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                drawWorker.add(e.getPoint());
            }
        });

        addMouseMotionListener(new MouseAdapter() {
            public void mouseDragged(MouseEvent e) {
                drawWorker.add(e.getPoint());
            }
        });
    }

    public Color getColor() {
        return color;
    }

    public File getCurrentFile() {
        return currentFile;
    }

    public BufferedImage getImage() {
        return image;
    }

    public Shape getShape() {
        return shape;
    }

    public int getShapeSize() {
        return shapeSize;
    }

    public boolean isModified() {
        return modified;
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D tempg = (Graphics2D) g;
        if (image == null) {
            reset();
        }

        tempg.drawImage(image, null, CANVAS_MARGIN, CANVAS_MARGIN);
        setModified(true);
    }

    public void reset() {
        int w, h;
        
        if (image == null) {
            w = this.getWidth() - CANVAS_MARGIN * 2;
            h = this.getHeight() - CANVAS_MARGIN * 2;
        } else {
            w = image.getWidth();
            h = image.getHeight();
        }
        
        image = (BufferedImage) this.createImage(w, h);
        Graphics2D gc = image.createGraphics();
        gc.setColor(Color.WHITE);
        gc.fillRect(0, 0, w, h);
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

    public void setColor(Color c) {
        color = c;
    }

    public void setCurrentFile(File f) {
        currentFile = f;
    }

    public void setImage(BufferedImage b) {
        image = b;
        repaint();
    }

    public void setModified(boolean b) {
        modified = b;
    }

    public void setShape(Shape s) {
        shape = s;
    }

    public void setShapeSize(int s) {
        this.shapeSize = s;
    }

    private void drawShape(Point p) {
        Graphics2D tempg = image.createGraphics();
        tempg.setColor(color);
        switch (shape) {
        case SQUARE:
            tempg.fillRect(p.x - shapeSize / 2, p.y - shapeSize / 2, shapeSize,
                    shapeSize);
            break;

        case CIRCLE:
            tempg.fillOval(p.x - shapeSize / 2, p.y - shapeSize / 2, shapeSize,
                    shapeSize);
            break;

        case TRIANGLE:
            int[] xPoints = { p.x - shapeSize / 2, p.x + shapeSize / 2, p.x };
            int[] yPoints = { p.y + shapeSize / 2, p.y + shapeSize / 2,
                    p.y - shapeSize / 2 };
            tempg.fillPolygon(xPoints, yPoints, 3);
            break;
        }

        repaint();
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
}