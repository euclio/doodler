import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

@SuppressWarnings("serial")
public class DoodleGUI extends JFrame {
    private static final int SIZE_INIT_VAL = 50;
    private static final int SIZE_MAJOR_TICKS = 20;
    private static final int SIZE_MAX_VAL = 200;
    private static final int SIZE_MIN_VAL = 5;
    private static final int SIZE_MINOR_TICKS = 10;
    private static final int COLOR_PREVIEW_SIZE = 30;
    private static final int WINDOW_HEIGHT = 768;
    private static final int WINDOW_WIDTH = 1024;
    private static final int WINDOW_X = 50;
    private static final int WINDOW_Y = 50;
    private static final int TOOL_H_GAP = 4;
    private static final int TOOL_V_GAP = 2;
    private static final Tool DEFAULT_TOOL = Tool.PEN;
    private static final Color DEFAULT_COLOR = Color.BLACK;
    private static final Shape DEFAULT_SHAPE = Shape.CIRCLE;
    private static final String DEFAULT_SAVE_DIRECTORY = System
            .getProperty("user.home");

    public DoodleGUI(String title) {
        super(title);
        try {
            // Set System L&F
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (UnsupportedLookAndFeelException e) {
            System.err.println("ERROR: " + e.getMessage());
        } catch (ClassNotFoundException e) {
            System.err.println("ERROR: " + e.getMessage());
        } catch (InstantiationException e) {
            System.err.println("ERROR: " + e.getMessage());
        } catch (IllegalAccessException e) {
            System.err.println("ERROR: " + e.getMessage());
        }

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                createAndShowGUI();
            }
        });
    }

    private void createAndShowGUI() {
        // Initialize the main panes of the GUI
        final JPanel options = new JPanel();
        final Canvas canvas = new Canvas(DEFAULT_TOOL, DEFAULT_COLOR,
                DEFAULT_SHAPE, DEFAULT_SAVE_DIRECTORY);
        final DoodleMenuBar menuBar = new DoodleMenuBar(canvas);

        // Add the panels within the options pane
        // Create the Tools Pane
        JPanel tools = new JPanel();
        tools.setBorder(BorderFactory.createTitledBorder("Tools"));
        tools.setLayout(new GridLayout(2, Tool.values().length / 2, TOOL_H_GAP,
                TOOL_V_GAP));
        ButtonGroup toolsGroup = new ButtonGroup();

        // Enumerate through all tools
        for (final Tool t : Tool.values()) {
            // Add the listener for tool changes
            t.getButton().addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    canvas.setTool(t);
                }
            });

            toolsGroup.add(t.getButton());
            tools.add(t.getButton());

            // Select the default tool
            if (t == DEFAULT_TOOL) {
                t.getButton().doClick();
            }
        }

        // Add the tools pane to the options panel
        options.add(tools);

        // Initialize the size panel
        JPanel size = new JPanel();
        size.setBorder(BorderFactory.createTitledBorder("Size"));

        // Create slider for shape size
        JSlider sizeSlider = new JSlider(SIZE_MIN_VAL, SIZE_MAX_VAL,
                SIZE_INIT_VAL);
        sizeSlider.setPaintTicks(true);
        sizeSlider.setMajorTickSpacing(SIZE_MAJOR_TICKS);
        sizeSlider.setMinorTickSpacing(SIZE_MINOR_TICKS);
        sizeSlider.setSnapToTicks(true);

        // Changes the Shape size if the slider is moved
        sizeSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                JSlider source = (JSlider) e.getSource();
                if (!source.getValueIsAdjusting()) {
                    canvas.setShapeSize(source.getValue());
                }
            }
        });

        // Add the size slider to the options pane
        size.add(sizeSlider);
        options.add(size);

        // Initialize the Color panel
        JPanel colors = new JPanel();
        colors.setBorder(BorderFactory.createTitledBorder("Color"));

        // Initialize the color chooser dialog
        final JColorChooser colorChooser = new JColorChooser();
        colorChooser.setPreviewPanel(new JPanel());

        // Initialize JLabel to show current color
        final JLabel colorLabel = new JLabel();
        colorLabel.setBackground(canvas.getColor());
        colorLabel.setOpaque(true);
        colorLabel.setSize(COLOR_PREVIEW_SIZE, COLOR_PREVIEW_SIZE);
        colorLabel.setPreferredSize(new Dimension(COLOR_PREVIEW_SIZE,
                COLOR_PREVIEW_SIZE));

        // Class to listen for color changes
        colorLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                Color newColor = JColorChooser.showDialog(colorChooser,
                        "Choose Color", canvas.getColor());
                if (newColor != null) {
                    canvas.setColor(newColor);
                    colorLabel.setBackground(newColor);
                }
            }
        });

        // Add color selector to options pane
        colors.add(colorLabel);
        options.add(colors);

        // Initialize the Shape ComboBox
        JPanel shapePanel = new JPanel();
        shapePanel.setBorder(BorderFactory.createTitledBorder("Shape"));

        JComboBox shapes = new JComboBox(Shape.values());

        // Class to listen for change of shapes
        shapes.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JComboBox cb = (JComboBox) e.getSource();
                canvas.setShape((Shape) cb.getSelectedItem());
            }
        });

        shapePanel.add(shapes);
        options.add(shapePanel);

        // Set the layout for the options pane
        FlowLayout lay = new FlowLayout();
        lay.setAlignment(FlowLayout.LEFT);
        options.setLayout(lay);

        // Add the options and canvas panes to the main window
        this.setJMenuBar(menuBar);
        this.add(options, BorderLayout.NORTH);
        this.add(canvas, BorderLayout.CENTER);

        // Final settings for the main GUI
        this.setLocation(WINDOW_X, WINDOW_Y);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                // Runs the menu exit routine
                menuBar.exit();
            }
        });
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        this.setMinimumSize(this.getLayout().minimumLayoutSize(this));
        this.setLocationRelativeTo(null);
    }
}
