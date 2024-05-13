package homeinventory;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;
import org.jdatepicker.impl.JDatePickerImpl;

public class HomeInventory extends JFrame {
    // Toolbar
    JToolBar inventoryToolBar = new JToolBar();
    JButton newButton = new JButton(new ImageIcon("new.gif"));
    JButton deleteButton = new JButton(new ImageIcon("delete.gif"));
    JButton saveButton = new JButton(new ImageIcon("save.gif"));
    JButton previousButton = new JButton(new ImageIcon("previous.gif"));
    JButton nextButton = new JButton(new ImageIcon("next.gif"));
    JButton printButton = new JButton(new ImageIcon("print.gif"));
    JButton exitButton = new JButton();

    // Frame
    JLabel itemLabel = new JLabel();
    JTextField itemTextField = new JTextField();
    JLabel locationLabel = new JLabel();
    JComboBox<String> locationComboBox = new JComboBox<>();
    JCheckBox markedCheckBox = new JCheckBox();
    JLabel serialLabel = new JLabel();
    JTextField serialTextField = new JTextField();
    JLabel priceLabel = new JLabel();
    JTextField priceTextField = new JTextField();
    JLabel dateLabel = new JLabel();
    JDatePickerImpl datePicker; // Declare without initialization
    JLabel storeLabel = new JLabel();
    JTextField storeTextField = new JTextField();
    JLabel noteLabel = new JLabel();
    JTextField noteTextField = new JTextField();
    JLabel photoLabel = new JLabel();
    static JTextArea photoTextArea = new JTextArea();
    JButton photoButton = new JButton();
    JPanel searchPanel = new JPanel();
    JButton[] searchButton = new JButton[26];
    PhotoPanel photoPanel = new PhotoPanel();

    static final int maximumEntries = 300;
    static int numberEntries;
    static InventoryItem[] myInventory = new InventoryItem[maximumEntries];
    int currentEntry;
    static final int entriesPerPage = 2;
    static int lastPage;
private void addLabelAndTextArea(JLabel label, JTextArea textArea, String labelText, int x, int y, int width, int height) {
    addLabel(label, labelText, x, y, width, height);
    addComponent(textArea, x + 1, y, width, height);
}
    public static void main(String args[]) {
        // create frame
        new HomeInventory().setVisible(true);
    }

    public HomeInventory() {
        // frame constructor
        setTitle("Home Inventory Manager");
        setResizable(false);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent evt) {
                exitForm(evt);
            }
        });
        getContentPane().setLayout(new GridBagLayout());
        GridBagConstraints gridConstraints;

        // Add toolbar
        inventoryToolBar.setFloatable(false);
        inventoryToolBar.setBackground(Color.BLUE);
        inventoryToolBar.setOrientation(SwingConstants.VERTICAL);
        gridConstraints = new GridBagConstraints();
        gridConstraints.gridx = 0;
        gridConstraints.gridy = 0;
        gridConstraints.gridheight = 8;
        gridConstraints.fill = GridBagConstraints.VERTICAL;
        getContentPane().add(inventoryToolBar, gridConstraints);
        inventoryToolBar.addSeparator();

        // Initialize buttons
        Dimension bSize = new Dimension(70, 50);
        initToolbarButton(newButton, "New", "Add New Item", "new.gif", bSize, e -> newButtonActionPerformed(e));
        initToolbarButton(deleteButton, "Delete", "Delete Current Item", "delete.gif", bSize, e -> deleteButtonActionPerformed(e));
        initToolbarButton(saveButton, "Save", "Save Current Item", "save.gif", bSize, e -> saveButtonActionPerformed(e));
        initToolbarButton(previousButton, "Previous", "Display Previous Item", "previous.gif", bSize, e -> previousButtonActionPerformed(e));
        initToolbarButton(nextButton, "Next", "Display Next Item", "next.gif", bSize, e -> nextButtonActionPerformed(e));
        initToolbarButton(printButton, "Print", "Print Inventory List", "print.gif", bSize, e -> printButtonActionPerformed(e));
        initToolbarButton(exitButton, "Exit", "Exit Program", null, bSize, e -> exitButtonActionPerformed(e));
        inventoryToolBar.add(exitButton);

        // Add components to the frame
        addLabelAndTextField(itemLabel, itemTextField, "Inventory Item", 1, 0, 10, 0);
        addLabelAndComboBox(locationLabel, locationComboBox, "Location", 1, 1, 10, 0);
        addCheckBox(markedCheckBox, "Marked?", 5, 1, 10, 0);
        addLabelAndTextField(serialLabel, serialTextField, "Serial Number", 1, 2, 10, 0);
        addLabelAndTextField(priceLabel, priceTextField, "Purchase Price", 1, 3, 10, 0);
        addLabel(dateLabel, "Date Purchased", 4, 3, 10, 0);
        addComponent(datePicker, 5, 3, 10, 0);
        addLabelAndTextField(storeLabel, storeTextField, "Store/Website", 1, 4, 10, 0);
        addLabelAndTextField(noteLabel, noteTextField, "Note", 1, 5, 10, 0);
        addLabelAndTextArea(photoLabel, photoTextArea, "Photo", 1, 6, 10, 0);
        addComponent(photoButton, 6, 6, 10, 0);

        // Search panel
        searchPanel.setPreferredSize(new Dimension(240, 160));
        searchPanel.setBorder(BorderFactory.createTitledBorder("Item Search"));
        searchPanel.setLayout(new GridBagLayout());
        gridConstraints = new GridBagConstraints();
        gridConstraints.gridx = 1;
        gridConstraints.gridy = 7;
        gridConstraints.gridwidth = 3;
        gridConstraints.insets = new Insets(10, 0, 10, 0);
        gridConstraints.anchor = GridBagConstraints.CENTER;
        getContentPane().add(searchPanel, gridConstraints);

        int x = 0, y = 0;
        for (int i = 0; i < 26; i++) {
            searchButton[i] = new JButton();
            searchButton[i].setText(String.valueOf((char) (65 + i)));
            searchButton[i].setFont(new Font("Arial", Font.BOLD, 12));
            searchButton[i].setMargin(new Insets(-10, -10, -10, -10));
            sizeButton(searchButton[i], new Dimension(37, 27));
            searchButton[i].setBackground(Color.YELLOW);
            searchButton[i].setFocusable(false);
            gridConstraints = new GridBagConstraints();
            gridConstraints.gridx = x;
            gridConstraints.gridy = y;
            searchPanel.add(searchButton[i], gridConstraints);

            // add action listener
            searchButton[i].addActionListener(e -> searchButtonActionPerformed(e));

            x++;
            if (x > 12) {
                x = 0;
                y++;
            }
        }

        // Set current entry to 0
        if (numberEntries == 0) {
            newButton.setEnabled(false);
            deleteButton.setEnabled(false);
            nextButton.setEnabled(false);
            previousButton.setEnabled(false);
            printButton.setEnabled(false);
        }

        // Initialize photo panel
        photoPanel.setPreferredSize(new Dimension(240, 160));
        gridConstraints = new GridBagConstraints();
        gridConstraints.gridx = 4;
        gridConstraints.gridy = 6;
        gridConstraints.gridheight = 2;
        gridConstraints.insets = new Insets(5, 10, 5, 10);
        getContentPane().add(photoPanel, gridConstraints);

        // Resize frame to fit components
        pack();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension frameSize = getSize();
        setLocation((screenSize.width - frameSize.width) / 2, (screenSize.height - frameSize.height) / 2);
    }

    // Method to handle exiting the program
    private void exitForm(WindowEvent evt) {
        saveInventory();
        dispose();
    }

    // Method to initialize toolbar buttons
    private void initToolbarButton(JButton button, String name, String tooltip, String icon, Dimension size, ActionListener listener) {
        button.setText(name);
        button.setToolTipText(tooltip);
        button.setFocusable(false);
        button.setPreferredSize(size);
        button.addActionListener(listener);
        if (icon != null) {
            button.setIcon(new ImageIcon(icon));
        }
        inventoryToolBar.add(button);
    }

    // Method to add label and text field
    private void addLabelAndTextField(JLabel label, JTextField textField, String labelText, int x, int y, int width, int height) {
        addLabel(label, labelText, x, y, width, height);
        addComponent(textField, x + 1, y, width, height);
    }

    // Method to add label and combo box
    private void addLabelAndComboBox(JLabel label, JComboBox<String> comboBox, String labelText, int x, int y, int width, int height) {
        addLabel(label, labelText, x, y, width, height);
        addComponent(comboBox, x + 1, y, width, height);
    }

    // Method to add label
    private void addLabel(JLabel label, String labelText, int x, int y, int width, int height) {
        label.setText(labelText);
        GridBagConstraints gridConstraints = new GridBagConstraints();
        gridConstraints.gridx = x;
        gridConstraints.gridy = y;
        gridConstraints.insets = new Insets(10, 10, 0, 0);
        gridConstraints.anchor = GridBagConstraints.EAST;
        getContentPane().add(label, gridConstraints);
    }

    // Method to add check box
    private void addCheckBox(JCheckBox checkBox, String labelText, int x, int y, int width, int height) {
        addLabel(new JLabel(), labelText, x, y, width, height);
        GridBagConstraints gridConstraints = new GridBagConstraints();
        gridConstraints.gridx = x + 1;
        gridConstraints.gridy = y;
        gridConstraints.insets = new Insets(10, 0, 0, 10);
        gridConstraints.anchor = GridBagConstraints.WEST;
        getContentPane().add(checkBox, gridConstraints);
    }

    // Method to add component
    private void addComponent(Component component, int x, int y, int width, int height) {
        GridBagConstraints gridConstraints = new GridBagConstraints();
        gridConstraints.gridx = x;
        gridConstraints.gridy = y;
        gridConstraints.gridwidth = width;
        gridConstraints.gridheight = height;
        gridConstraints.insets = new Insets(10, 10, 0, 10);
        gridConstraints.anchor = GridBagConstraints.WEST;
        gridConstraints.fill = GridBagConstraints.BOTH;
if (component != null) {
    getContentPane().add(component, gridConstraints);
}
    }

    // Method to size button
    private void sizeButton(JButton button, Dimension size) {
        button.setPreferredSize(size);
        button.setMinimumSize(size);
        button.setMaximumSize(size);
    }

    // Method to handle New button action
    private void newButtonActionPerformed(ActionEvent e) {
        // code to add new inventory item
    }

    // Method to handle Delete button action
    private void deleteButtonActionPerformed(ActionEvent e) {
        // code to delete inventory item
    }

    // Method to handle Save button action
    private void saveButtonActionPerformed(ActionEvent e) {
        // code to save inventory item
    }

    // Method to handle Previous button action
    private void previousButtonActionPerformed(ActionEvent e) {
        // code to display previous inventory item
    }

    // Method to handle Next button action
    private void nextButtonActionPerformed(ActionEvent e) {
        // code to display next inventory item
    }

    // Method to handle Print button action
    private void printButtonActionPerformed(ActionEvent e) {
        // code to print inventory list
    }

    // Method to handle Exit button action
    private void exitButtonActionPerformed(ActionEvent e) {
        exitForm(null);
    }

    // Method to handle search button action
    private void searchButtonActionPerformed(ActionEvent e) {
        // code to search inventory
    }

    // Method to save inventory
    private void saveInventory() {
        // code to save inventory data to a file
    }
}

// Class representing an inventory item
class InventoryItem implements Serializable {
    String description;
    String location;
    boolean marked;
    String serialNumber;
    String purchasePrice;
    String purchaseDate;
    String purchaseLocation;
    String note;
    String photoFile;
}

// Class representing a photo panel
class PhotoPanel extends JPanel {
    public PhotoPanel() {
        setBackground(Color.BLACK);
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2D = (Graphics2D) g;
        g2D.setColor(Color.WHITE);
        g2D.drawString("Photo goes here", 20, 20);
    }
}
