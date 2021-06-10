package program;

public class Main {

    public static void main(String[] args){
        DirectoryLoader directoryLoader = new DirectoryLoader();
        ControlInterface controlInterface = new ControlInterface(directoryLoader.folderList);
        
    }
}
