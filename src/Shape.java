import java.awt.Point;
import java.awt.Polygon;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

public enum Shape {
    CIRCLE, SQUARE, TRIANGLE;

    public String toString() {
        String word = name();
        return word.charAt(0) + word.substring(1).toLowerCase();
    }

    public java.awt.Shape render(Point p, int size) {
        switch (this) {
        case SQUARE:
            return new Rectangle2D.Float(p.x - size / 2, p.y - size / 2, size,
                    size);

        case CIRCLE:
            return new Ellipse2D.Float(p.x - size / 2, p.y - size / 2, size,
                    size);

        case TRIANGLE:
            int[] xPoints = { p.x - size / 2, p.x + size / 2, p.x };
            int[] yPoints = { p.y + size / 2, p.y + size / 2, p.y - size / 2 };
            return new Polygon(xPoints, yPoints, 3);

        default:
            return null;
        }
    }
}
