import java.awt.*; 
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

@SuppressWarnings("serial")
public class DoodleGUI extends JFrame {
    private static final int SIZE_INIT_VAL = 50;
    private static final int SIZE_MAX_VAL = 200;
    private static final int SIZE_MIN_VAL = 5;
    private static final int SIZE_MINOR_TICKS = 10;
    private static final int SIZE_MAJOR_TICKS = 20;

    public enum MenuChoices {
        // File
        NEW, OPEN, SAVE, EXIT,

        // Help
        ABOUT
    };

    public DoodleGUI(String title) {
        super(title);
        try {
            // Set System L&F
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (UnsupportedLookAndFeelException e) {
            // handle exception
        } catch (ClassNotFoundException e) {
            // handle exception
        } catch (InstantiationException e) {
            // handle exception
        } catch (IllegalAccessException e) {
            // handle exception
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
        final Canvas canvas = new Canvas();
        final JMenuBar menuBar = new JMenuBar();

        // Class to handle menu events
        class MenuListener implements ActionListener {
            @Override
            public void actionPerformed(ActionEvent e) {
                JMenuItem source = (JMenuItem) e.getSource();
                String itemText = source.getText().replaceAll("[^a-zA-Z0-9]",
                        "");

                switch (MenuChoices.valueOf(itemText.toUpperCase())) {
                case NEW:
                    MenuFunctions.newFile(DoodleGUI.this, canvas);
                    break;
                case OPEN:
                    MenuFunctions.openFile(DoodleGUI.this, canvas);
                    break;
                case SAVE:
                    MenuFunctions.saveFile(DoodleGUI.this, canvas);
                    break;
                case EXIT:
                    MenuFunctions.exit(DoodleGUI.this, canvas);
                    break;

                // Help Menu
                case ABOUT:
                    MenuFunctions.about(DoodleGUI.this, canvas);
                }
            }
        }

        MenuListener menuListener = new MenuListener();

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

        menuBar.add(fileMenu);

        // Build the "Help" Menu
        JMenu helpMenu = new JMenu("Help");

        JMenuItem about = new JMenuItem("About");
        about.addActionListener(menuListener);
        helpMenu.add(about);

        menuBar.add(helpMenu);

        // Add the panels within the options pane
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

        // Class to listen for slider changes
        class SliderListener implements ChangeListener {
            @Override
            public void stateChanged(ChangeEvent e) {
                JSlider source = (JSlider) e.getSource();
                if (!source.getValueIsAdjusting()) {
                    canvas.setShapeSize(source.getValue());
                }
            }
        }

        sizeSlider.addChangeListener(new SliderListener());

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
        final Dimension labelDimension = new Dimension(30, 30);
        colorLabel.setSize(labelDimension);
        colorLabel.setPreferredSize(labelDimension);

        // Class to listen for color changes
        colorLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                Color newColor = JColorChooser.showDialog(colorChooser,
                        "Choose Color", canvas.getColor());
                canvas.setColor(newColor);
                colorLabel.setBackground(newColor);
            }
        });

        // Add color selector to options pane
        colors.add(colorLabel);
        options.add(colors);

        // Initialize the Shape ComboBox
        JPanel shapePanel = new JPanel();
        shapePanel.setBorder(BorderFactory.createTitledBorder("Shape"));

        JComboBox<Shape> shapes = new JComboBox<Shape>(Shape.values());

        // Class to listen for change of shapes
        shapes.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                @SuppressWarnings("unchecked")
                JComboBox<Shape> cb = (JComboBox<Shape>)e.getSource();
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
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(1024, 768);
        this.setMinimumSize(this.getLayout().minimumLayoutSize(this));
        this.setVisible(true);
    }
}
