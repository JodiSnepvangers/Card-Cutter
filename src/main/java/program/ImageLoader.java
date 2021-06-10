package program;


import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ImageLoader {
    //takes in a folder, and retrieves all images found in it! filters out only png and jpg!

    public boolean isFolder(File folder){
        return folder.isDirectory();
    }

    public List<File> retrieveAllImageFiles(File folder){
        if(isFolder(folder) == false)return null; //folder is not a folder! nothing to return here!

        //folder is real. retrieve files and return them!. filters out all folders and non compatible files
        List<File> fileList = new ArrayList<>();
        for(File file : folder.listFiles()){
            if(file.isDirectory())continue; //file is folder! not allowed!
            if(isIncompatible(file))continue; //file not compatible! now allowed!
            fileList.add(file);
        }

        //all files loaded and checked!
        return fileList;
    }

    private boolean isIncompatible(File file){
        String extension = getFileExtension(file);
        if(extension.toLowerCase().equals(".jpg") || extension.toLowerCase().equals(".jpeg") || extension.toLowerCase().equals(".png")){
            //extention is compatible!
            return false;
        }
        //extention not compatible
        return true;
    }

    private String getFileExtension(File file) {
        String name = file.getName();
        int lastIndexOf = name.lastIndexOf(".");
        if (lastIndexOf == -1) {
            return ""; // empty extension
        }
        return name.substring(lastIndexOf);
    }

}
