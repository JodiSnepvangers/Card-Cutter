package program;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.RasterFormatException;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CutterThread extends Thread{
    //runs all the heavy calculations off the program! should always tick off to the progress bar!

    File intakeFolder;
    int cardsHorizontal;
    int cardsVertical;
    JProgressBar progressBar;
    ControlInterface controlInterface;

    public CutterThread(File folder, int cardsHorizontal, int cardsVertical, JProgressBar progressBar, ControlInterface controlInterface){
        this.intakeFolder = folder;
        this.cardsHorizontal = cardsHorizontal;
        this.cardsVertical = cardsVertical;
        this.progressBar = progressBar;
        this.controlInterface = controlInterface;
    }

    boolean majorError = false; //this can be set to true if a big error happened and progress must be halted!

    @Override
    public void run() {
        if(intakeFolder == null){
            System.out.println("INTAKE FOLDER NOT SET!");
            return;
        }

        //first of all, load all images:
        List<BufferedImage> imageList = loadAllImages();
        if(majorError)return;
        // cut all images
        imageList = cutImages(imageList);
        if(majorError)return;
        //cut off all borders
        if(controlInterface.borderField.getNumberValue() > 0){
            imageList = cutBorders(imageList);
        }
        if(majorError)return;
        if(controlInterface.allowResizing){
            imageList = resizeImages(imageList);
        }
        if(majorError)return;
        //save all images to disk!~
        saveImages(imageList);
        if(majorError)return;
        JOptionPane.showMessageDialog(null, "All images created successfully!");
        controlInterface.enableDisplay(true);
    }

    private List<BufferedImage> loadAllImages(){
        List<File> fileList = new ImageLoader().retrieveAllImageFiles(intakeFolder);
        List<BufferedImage> imageList = new ArrayList<>();

        startProgress(fileList.size(), "Loading Images...");

        boolean error = false;
        for(File file : fileList){
            BufferedImage fileImage;
            try {
                fileImage = ImageIO.read(new File(file.getPath()));
                BufferedImage newImage = new BufferedImage(fileImage.getWidth() + 200, fileImage.getHeight() + 200, fileImage.getType());
                Graphics2D graphics2D = newImage.createGraphics();
                graphics2D.setColor(Color.black);
                graphics2D.fillRect(0,0,newImage.getWidth(), newImage.getHeight());
                graphics2D.drawImage(fileImage, 0,0,null);
                graphics2D.dispose();
                imageList.add(newImage);
            } catch (IOException e) {
                error = true;
                e.printStackTrace();
            }
            tickProgressBar();
        }

        if(error){
            //show a error message if a error happened!
            majorError = true;
            JOptionPane.showMessageDialog(null, "There was a error loading one or multiple images!");
        }
        return imageList;
    }

    private List<BufferedImage> cutImages(List<BufferedImage> imageList) {
        List<BufferedImage> finalImageList = new ArrayList<>();

        startProgress((cardsHorizontal * cardsVertical) * imageList.size(), "Cutting Images...");

        //loop though images and cut them up!
        for (BufferedImage image : imageList) {
            //initialise all variables:
            int offsetX = 0;
            int offsetY = 0;

            int cardWidth = (int) Math.round((double)(image.getWidth() - 200) / cardsHorizontal);
            int cardHeight = (int) Math.round((double)(image.getHeight() - 200) / cardsVertical);

            boolean cuttingImage = true;

            //loop though image until all cards have been extraced:
            while (cuttingImage) {
                //generate a new buffered image:
                BufferedImage card = new BufferedImage(cardWidth, cardHeight, BufferedImage.TYPE_4BYTE_ABGR);
                // generate graphics object
                Graphics2D graphics2D = card.createGraphics();
                //perform graphic calculations:
                try{
                    graphics2D.drawImage(image.getSubimage(cardWidth * offsetX, cardHeight * offsetY, cardWidth, cardHeight), null, 0, 0);
                } catch (RasterFormatException e) {
                    e.printStackTrace();
                }

                graphics2D.dispose();

                //save image to list
                finalImageList.add(card);

                //increment counters:
                offsetX++;

                //check if offsets need ajusting:
                if (offsetX >= cardsHorizontal) {
                    offsetX = 0;
                    offsetY++;
                }

                if (offsetY >= cardsVertical) {
                    //end is reached of image! stop cutting!
                    cuttingImage = false;
                }

                tickProgressBar();
            }
        }
        return finalImageList;
    }

    private List<BufferedImage> cutBorders(List<BufferedImage> imageList) {
        List<BufferedImage> finalImageList = new ArrayList<>();
        int borderSize = controlInterface.borderField.getNumberValue();
        startProgress(imageList.size(), "Cutting Borders...");
        for(BufferedImage original : imageList){
            BufferedImage image = new BufferedImage(original.getWidth() - (borderSize * 2), original.getHeight() - (borderSize * 2), BufferedImage.TYPE_4BYTE_ABGR);
            Graphics2D graphics2D = image.createGraphics();
            graphics2D.drawImage(original, 0 - borderSize, 0 - borderSize, null);
            graphics2D.dispose();
            tickProgressBar();
            finalImageList.add(image);
        }

        return finalImageList;
    }

    private void saveImages(List<BufferedImage> imageList){
        //now save all to the output folder
        startProgress(imageList.size(), "saving Images...");
        //generate output folder:
        File outputFolder = new File(new File("").getAbsolutePath() + "/output/" + intakeFolder.getName());
        if(outputFolder.exists() == false){
            outputFolder.mkdirs();
        }

        boolean error = false;
        int cardNumber = 1;
        for(BufferedImage image : imageList){
            File outputFile = new File(outputFolder + "/" +  cardNumber + ".png");
            try {
                ImageIO.write(image, "png", outputFile);
            } catch (IOException e) {
                e.printStackTrace();
                error = true;
                controlInterface.endNumberLabel.setText("" + (cardNumber));
                cardNumber++;
                tickProgressBar();
                continue;
            }
            tickProgressBar();
            controlInterface.endNumberLabel.setText("" + (cardNumber));
            cardNumber++;
        }

        if(error){
            //show a error message if a error happened!
            JOptionPane.showMessageDialog(null, "There was a error saving one or multiple images!");
            majorError = true;
        }
    }

    private void startProgress(int goalAmount, String text){
        if(progressBar == null)return; //if no progress bar connected, do nothing
        progressBar.setString(text);
        progressBar.setValue(0);
        progressBar.setMaximum(goalAmount);
    }

    private void tickProgressBar(){
        if(progressBar == null)return; //if no progress bar connected, do nothing
        progressBar.setValue(progressBar.getValue() + 1);
    }

    private List<BufferedImage> resizeImages(List<BufferedImage> imageList){
        List<BufferedImage> newImageList = new ArrayList<>();
        int cardWidth = controlInterface.widthField.getNumberValue();
        int cardHeight = controlInterface.heightField.getNumberValue();
        startProgress(imageList.size(), "Resizing Images...");
        for(BufferedImage image : imageList){
            BufferedImage newImage = resize(image, cardWidth, cardHeight);
            newImageList.add(newImage);
            tickProgressBar();
        }
        return newImageList;
    }

    private BufferedImage resize(BufferedImage image, int width, int height) {
        BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
        Graphics2D g2d = bi.createGraphics();
        g2d.addRenderingHints(new RenderingHints(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY));
        g2d.drawImage(image, 0, 0, width, height, null);
        g2d.dispose();
        return bi;
    }
}
