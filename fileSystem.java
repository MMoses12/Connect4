package ce326.hw3;

import java.io.File;

public class fileSystem {
    public static void makeDirectory () {   
        String homePath = System.getProperty("user.home"); // specify the path of the new directory
        File homeDir = new File(homePath, "Connect4");
        
        if (homeDir.exists() == false) { // check if the directory already exists
            homeDir.mkdirs(); // create the new directory
        }

        
    }
}