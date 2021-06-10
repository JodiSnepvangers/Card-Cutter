package program;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class DirectoryLoader {
    /**
     *
     * loads all directories found in the root folder of the program, and creates a list of them!
     *
     *
     */

    public List<File> folderList = new ArrayList<>(); //a list of folders found in the program!
    public File[] directories;

    public DirectoryLoader(){
        //get base path of the program
        File currentDir = new File("");
        String basePath = currentDir.getAbsolutePath();
        directories = new File(basePath).listFiles(File::isDirectory);



        if(directories.length == 0){
            System.out.println("no directories found in '" + basePath + "'. generating default");
            File folder = new File(basePath + "/input");
            if(folder.exists() == false){
                folder.mkdir();
                directories = new File[]{folder};
            }
        } else {
            //some directories found!
            System.out.println("directories found in '" + basePath + "'");
        }

        for(File file : directories){
            folderList.add(file);
            System.out.println(file.getName());
        }
    }
}
