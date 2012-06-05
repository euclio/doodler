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
    private class DrawingThread extends Thread {
        private BlockingQueue<Point> draggedPoints = new LinkedBlockingQueue<Point>();
        private Point pressedPoint, releasedPoint;
        private final Point END_OF_STREAM = new Point(Integer.MAX_VALUE,
                Integer.MAX_VALUE);

        public void addDraggedPoint(Point p) {
            p.translate(-CANVAS_MARGIN, -CANVAS_MARGIN);
            draggedPoints.add(p);

        }

        public void addPressedPoint(Point p) {
            p.translate(-CANVAS_MARGIN, -CANVAS_MARGIN);
            pressedPoint = p;
            synchronized (this) {
                this.notify();
            }
        }

        public void addReleasedPoint(Point p) {
            p.translate(-CANVAS_MARGIN, -CANVAS_MARGIN);
            releasedPoint = p;
            draggedPoints.add(END_OF_STREAM);
        }

        @Override
        public void run() {
            while (true) {
                try {
                    synchronized (this) {
                        this.wait();
                    }
                    switch (tool) {
                    case PEN:
                        Point lastPoint = pressedPoint;
                        while (true) {
                            Point currentPoint = draggedPoints.take();
                            if (currentPoint.equals(END_OF_STREAM))
                                break;
                            pen(lastPoint, currentPoint);
                            lastPoint = currentPoint;
                        }
                        break;
                    case STAMP:
                        while (true) {
                            Point currentPoint = draggedPoints.take();
                            if (currentPoint.equals(END_OF_STREAM))
                                break;
                            stamp(currentPoint);
                        }
                        stamp(releasedPoint);
                        break;
                    default:
                        throw new IllegalStateException("Bad tool selected");
                    }

                } catch (InterruptedException e) {
                    // Do nothing
                }
            }
        }
    }

    private static final int CANVAS_MARGIN = 10;
    private static final int PEN_SIZE_CONVERSION = 5;

    private BufferedImage image = null;
    private Tool tool;
    private int toolSize;
    private Color color;
    private Shape shape;
    private boolean modified, saved = false;
    private File saveFile = createNewFile();
    private DrawingThread drawWorker = new DrawingThread();
    private String saveDirectory;

    public Canvas(Tool tool, Color color, Shape shape, String directory) {
        this.tool = tool;
        this.color = color;
        this.shape = shape;
        this.saveDirectory = directory;
        this.setBackground(Color.GRAY);

        this.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                drawWorker.addPressedPoint(e.getPoint());
            }

            public void mouseReleased(MouseEvent e) {
                drawWorker.addReleasedPoint(e.getPoint());
            }
        });

        this.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                drawWorker.addDraggedPoint(e.getPoint());
            }
        });

        drawWorker.start();
    }

    public Color getColor() {
        return color;
    }

    public File getSaveFile() {
        return saveFile;
    }

    public BufferedImage getImage() {
        return image;
    }

    public String getSaveDirectory() {
        return saveDirectory;
    }

    public Shape getShape() {
        return shape;
    }

    public int getToolSize() {
        return toolSize;
    }

    public boolean isModified() {
        return modified;
    }

    public boolean isSaved() {
        return saved;
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
        this.color = c;
    }

    public void setCurrentFile(File f) {
        this.saveFile = f;
    }

    public void setImage(BufferedImage b) {
        image = b;
        repaint();
    }

    public void setModified(boolean b) {
        String newTitle;
        if (b) {
            newTitle = "Doodler! - " + saveFile.getName() + " (Modified)";
        } else {
            newTitle = "Doodler! - " + saveFile.getName();
        }

        ((JFrame) this.getTopLevelAncestor()).setTitle(newTitle);
        modified = b;
    }

    public void setSaved(boolean saved) {
        this.saved = saved;
    }

    public void setSaveDirectory(String directory) {
        this.saveDirectory = directory;
    }

    public void setShape(Shape s) {
        this.shape = s;
    }

    public void setShapeSize(int s) {
        this.toolSize = s;
    }

    public void setTool(Tool t) {
        this.tool = t;
    }

    private File createNewFile() {
        String name = "doodle";
        File newFile = new File(saveDirectory + File.separator + name + ".png");
        int count = 1;
        while (newFile.exists()) {
            name = "doodle (" + count + ")";
            newFile = new File(saveDirectory + File.separator + name + ".png");
            ++count;
        }
        return new File(saveDirectory + File.separator + name + ".png");
    }

    private void pen(Point p1, Point p2) {
        Graphics2D tempg = image.createGraphics();
        tempg.setColor(color);
        tempg.setStroke(new BasicStroke(toolSize / PEN_SIZE_CONVERSION,
                BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        tempg.draw(new Line2D.Float(p1, p2));
        setModified(true);
        repaint();
    }

    @SuppressWarnings("unused")
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

    private void stamp(Point p) {
        Graphics2D tempg = image.createGraphics();
        tempg.setColor(color);
        tempg.fill(shape.render(p, toolSize));
        setModified(true);
        repaint();
    }
}