import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Line2D;
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
                    stamp(points.take());
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
    private boolean modified, saved = false;
    private File currentFile = createNewFile();
    private DrawingThread drawWorker = new DrawingThread();
    public static final String DEFAULT_SAVE_DIR = System.getProperty("user.home");

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
    
    public boolean isSaved() {
        return saved;
    }
    
    public void setSaved(boolean saved) {
        this.saved = saved;
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D tempg = (Graphics2D) g;
        if (image == null) {
            reset();
        }

        tempg.drawImage(image, null, CANVAS_MARGIN, CANVAS_MARGIN);
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
        this.setModified(false);
        repaint();
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
        String newTitle;
        if (b) {
            newTitle = "Doodler! - " + currentFile.getName() + " (Modified)";
        } else {
            newTitle = "Doodler! - " + currentFile.getName();
        }
        
        ((JFrame)this.getTopLevelAncestor()).setTitle(newTitle);
        modified = b;
    }

    public void setShape(Shape s) {
        shape = s;
    }

    public void setShapeSize(int s) {
        this.shapeSize = s;
    }
    
    private void pen(Point p1, Point p2) {
        int size = shapeSize;
        Graphics2D tempg = image.createGraphics();
        tempg.setColor(color);
        tempg.setStroke(new BasicStroke(size));
        tempg.draw(new Line2D.Float(p1, p2));
    }

    private void stamp(Point p) {
        p.translate(-CANVAS_MARGIN, -CANVAS_MARGIN);
        Graphics2D tempg = image.createGraphics();
        tempg.setColor(color);
        tempg.fill(shape.render(p, shapeSize));
        setModified(true);
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
    
    private File createNewFile() {
        String name = "doodle";
        File newFile = new File (DEFAULT_SAVE_DIR + File.separator + name + ".png");
        int count = 1;
        while (newFile.exists()) {
            name = "doodle (" + count + ")";
            newFile = new File (DEFAULT_SAVE_DIR + File.separator + name + ".png");
            ++count;
        }
        return new File (DEFAULT_SAVE_DIR + File.separator + name + ".png");
    }
}