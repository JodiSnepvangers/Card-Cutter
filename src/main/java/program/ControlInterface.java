package program;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ControlInterface extends JFrame {
    /**
     *
     * creates the user interface!~
     *
     *
     */

    public enum test {
        TEST,
    }

    JNumberTextField horizontalField = new JNumberTextField(10);
    JNumberTextField verticalField = new JNumberTextField(7);
    JLabel horizontalLabel = new JLabel("Cards Horizontally:");
    JLabel verticalLabel = new JLabel("Cards Vertically:");
    JLabel folderLabel = new JLabel("Input Folder:");
    JButton confirmButton = new JButton("Confirm");
    JButton calculateButton = new JButton("Refresh");

    JLabel cardLabel = new JLabel("Amount of Cards:");
    JLabel numberLabel = new JLabel("Un-calculated!");
    JLabel endCardLabel = new JLabel("Border Cut:");
    JLabel endNumberLabel = new JLabel("Un-calculated!");
    JLabel fileLabel = new JLabel("Compatible Files:");
    JLabel fileNumberLabel = new JLabel("0");

    JLabel widthLabel = new JLabel("New Width:");
    JLabel heightLabel = new JLabel("New Height:");
    JNumberTextField widthField = new JNumberTextField(400);
    JNumberTextField heightField = new JNumberTextField(600);
    JCheckBox resizeChecker = new JCheckBox("Resize Cards");


    JNumberTextField borderField = new JNumberTextField(0);



    JComboBox comboBox;

    JProgressBar progressBar = new JProgressBar(0, 100);


    //internal classesL
    ImageLoader imageLoader = new ImageLoader();
    ImageCutter imageCutter = new ImageCutter(this);
    List<File> folderList = new ArrayList<>();

    int cardsHorizontal = 0; //amount of cards horzontal per image
    int cardsVertical = 0;//amount of cards vertical per image
    int cardAmount = 0;//maximum amount of cards
    int cardsFinished = 0; //current cards that have been finished cutting

    boolean allowResizing = false;

    public ControlInterface(List<File> directories){
        //generate the combo box used to display all folders
        List<String> folderNameList = new ArrayList<>();
        for(File file : directories){
            folderNameList.add(file.getName());
            folderList.add(file);
        }

        setVisible(true);

        String string[] = new String[folderNameList.size()];
        for (int i = 0; i < folderNameList.size(); i++) {
            string[i] = folderNameList.get(i);
        }

        comboBox = new JComboBox(string);

        //set up frame parameters:
        setLayout(new BorderLayout());
        setLayout(null);
        setMinimumSize(new Dimension(450, 300));
        setTitle("Card Cutter V1.1");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);

        //set component parameters:
        //deal with border
        Border border = BorderFactory.createLineBorder(Color.GRAY, 2, true);
        numberLabel.setBorder(border);
        numberLabel.setOpaque(true);
        numberLabel.setBackground(new Color(200, 200, 200));

        endNumberLabel.setBorder(border);
        endNumberLabel.setOpaque(true);
        endNumberLabel.setBackground(new Color(200, 200, 200));

        fileNumberLabel.setBorder(border);
        fileNumberLabel.setOpaque(true);
        fileNumberLabel.setBackground(new Color(200, 200, 200));

        progressBar.setString("Progress...");
        progressBar.setStringPainted(true);
        progressBar.setVisible(false);

        resizeChecker.setSelected(false);
        heightField.setEnabled(false);
        widthField.setEnabled(false);

        //add components:
        folderLabel.setBounds(20, 5, 120, 20);
        add(folderLabel);
        comboBox.setBounds(20, 30, 120, 20);
        add(comboBox);
        fileLabel.setBounds(167, 5, 120, 20);
        add(fileLabel);
        fileNumberLabel.setBounds(167, 30, 100, 20);
        add(fileNumberLabel);

        horizontalField.setBounds(20, 90, 120, 30);
        add(horizontalField);
        horizontalLabel.setBounds(20, 60, 120, 30);
        add(horizontalLabel);

        verticalField.setBounds(20, 150, 120, 30);
        add(verticalField);
        verticalLabel.setBounds(20, 120, 120, 30);
        add(verticalLabel);

        progressBar.setBounds(20, 210, 395, 30);
        add(progressBar);
        confirmButton.setBounds(20, 200, 120, 50);
        add(confirmButton);
        calculateButton.setBounds(295, 200, 120, 50);
        add(calculateButton);

        cardLabel.setBounds(167, 60, 180, 30);
        add(cardLabel);
        numberLabel.setBounds(167, 91, 100, 27);
        add(numberLabel);

        endCardLabel.setBounds(167, 120, 180, 30);
        add(endCardLabel);
        borderField.setBounds(167, 150, 100, 30);
        add(borderField);

        heightField.setBounds(295, 150, 120, 30);
        add(heightField);
        heightLabel.setBounds(295, 120, 120, 30);
        add(heightLabel);

        resizeChecker.setBounds(295, 25, 120, 30);
        add(resizeChecker);

        widthField.setBounds(295, 90, 120, 30);
        add(widthField);
        widthLabel.setBounds(295, 60, 120, 30);
        add(widthLabel);

        //add mouse listeners:
        comboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateFileNumber();
                update(); //updates internal variables
                File folder = folderList.get(comboBox.getSelectedIndex());
                int fileAmount = imageLoader.retrieveAllImageFiles(folder).size();
                calculateCardAmount(fileAmount);
            }
        });

        calculateButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                update(); //updates internal variables
                File folder = folderList.get(comboBox.getSelectedIndex());
                int fileAmount = imageLoader.retrieveAllImageFiles(folder).size();
                calculateCardAmount(fileAmount);
            }
        });

        confirmButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                update(); //updates internal variables
                File folder = folderList.get(comboBox.getSelectedIndex());
                List<File> imageList = imageLoader.retrieveAllImageFiles(folder);
                if(imageList.size() == 0)return; //no images to handle! return!
                calculateCardAmount(imageList.size());

                //lock out interface and reveal progress bar
                enableDisplay(false);

                //update progress bar
                progressBar.setMaximum(cardAmount);

                //cut images!
                imageCutter.cutImages(folder);
            }
        });

        resizeChecker.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                boolean state = resizeChecker.isSelected();
                heightField.setEnabled(state);
                widthField.setEnabled(state);
                allowResizing = state;
            }
        });




        //update
        updateFileNumber();
        update(); //updates internal variables
        File folder = folderList.get(comboBox.getSelectedIndex());
        int fileAmount = imageLoader.retrieveAllImageFiles(folder).size();
        calculateCardAmount(fileAmount);

        //reveal!
        setVisible(true);
    }

    public void enableDisplay(boolean enabled){
        comboBox.setEnabled(enabled);
        verticalField.setEnabled(enabled);
        horizontalField.setEnabled(enabled);
        borderField.setEnabled(enabled);
        confirmButton.setVisible(enabled);
        calculateButton.setVisible(enabled);
        progressBar.setVisible(!enabled);
        resizeChecker.setEnabled(enabled);
        if(enabled){
            boolean state = resizeChecker.isSelected();
            heightField.setEnabled(state);
            widthField.setEnabled(state);
        } else {
            heightField.setEnabled(false);
            widthField.setEnabled(false);
        }
    }

    private void updateFileNumber(){
        List<File> fileList = imageLoader.retrieveAllImageFiles(folderList.get(comboBox.getSelectedIndex()));
        fileNumberLabel.setText("" + fileList.size());
    }

    private void update(){
        //updates internal variables with the parameters of the menu!
        this.cardsHorizontal = horizontalField.getNumberValue();
        this.cardsVertical = verticalField.getNumberValue();
    }

    private void tickProgress(){
        cardAmount++;
        progressBar.setValue(cardsFinished);
    }

    private void calculateCardAmount(int amountOfFiles){
        int cardTotal = (cardsHorizontal * cardsVertical) * amountOfFiles;

        numberLabel.setText("" + cardTotal);
        endNumberLabel.setText("0");

        this.cardAmount = cardTotal;
    }

    final float dash1[] = {10.0f};
    final BasicStroke dashed = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash1, 0.0f);

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        if(true)return;
        Graphics2D graphics2D = (Graphics2D) g;
        graphics2D.setColor(Color.black);
        graphics2D.setStroke(dashed);
        graphics2D.drawLine(-4, 90, 400, 90);
        graphics2D.drawLine(180, 90, 180, 220);
        graphics2D.drawLine(-4, 220, 400, 220);
    }
}
