import java.awt.Dimension;
import java.awt.event.*;

import javax.swing.*;

@SuppressWarnings("serial")
public class DoodleFrame extends JFrame {
    private Dimension minimumSize;

    public DoodleFrame(String title) {
        super(title);
    }

    public DoodleFrame() {
        super();
    }

    public void setMinimumSize(Dimension d) {
        minimumSize = d;
        // Ensures user cannot resize frame to be smaller than this
        final int origX = (int) minimumSize.getWidth();
        final int origY = (int) minimumSize.getHeight();
        addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent event) {
                setSize((getWidth() < origX) ? origX : getWidth(),
                        (getHeight() < origY) ? origY : getHeight());
            }
        });
    }
}
