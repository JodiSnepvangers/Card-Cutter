package program;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ImageCutter {

    /**
     *
     * this class provides the tools to cut the cards out of each image!~ takes a input of images, and processes them!~
     * calls controlInterface to keep progress bar updated!
     *
     */
    ControlInterface controlInterface; //control interface linked to this class

    public ImageCutter(ControlInterface controlInterface){
        //update internal variables:
        this.controlInterface = controlInterface;
    }

    public void cutImages(File folder){
        CutterThread cutterThread = new CutterThread(folder, controlInterface.cardsHorizontal, controlInterface.cardsVertical, controlInterface.progressBar,controlInterface);
        cutterThread.start();
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
