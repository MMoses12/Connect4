package ce326.hw3;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Iterator;

import org.json.*;

// Make the Connect4 folder in the home folder.
public class fileSystem {
    public static void makeDirectory () {   
        String homePath = System.getProperty("user.home"); // specify the path of the new directory
        File homeDir = new File(homePath, "Connect4");
        
        if (homeDir.exists() == false) { // check if the directory already exists
            homeDir.mkdirs(); // create the new directory
        }
    }

    // Create the file for a currently completed game.
    public static void makeFile() {
        JSONObject obj;
        
        obj = makeString();
		StringBuffer filePath = new StringBuffer("");
		filePath.append("/Connect4/");

        
		String homePath = System.getProperty("user.home");
        // Specify the directory path
        
        String directoryPath = homePath + "/Connect4/";
        File directory = new File(directoryPath);
        File[] files = directory.listFiles();
        int fileCount = files.length;

        filePath.append("Game" + ++fileCount + ".json");

        File fileName = new File(homePath, filePath.toString());
		
		try {
			fileName.createNewFile();
		} catch (IOException ex) {
			return;
		}

		try {
			FileWriter writer = new FileWriter(fileName);
			writer.write(obj.toString(2));
			writer.close();
		} catch (IOException ex) {
			return;
		}
    }

    // Make the JSON string to store the game's data.
    private static JSONObject makeString() {
        JSONObject obj = new JSONObject();

        java.util.Date date = Calendar.getInstance().getTime(); 
        DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss"); 
        obj.put("Date", dateFormat.format(date));

        if (board.playerFirst)
            obj.put("First", "Player");
        else
            obj.put("First", "AI");

        if (board.AIOpp == false)
            obj.put("Diff", "2Player");
        else if (AIPlayer.depth == 1) 
            obj.put("Diff", "Trivial");
        else if (AIPlayer.depth == 3)
            obj.put("Diff", "Medium");
        else
            obj.put("Diff", "Hard");

        JSONArray arr = new JSONArray();
        Iterator<Integer> it = board.moveList.iterator();

        while (it.hasNext()) {
            arr.put(it.next());
        }

        if (board.AIOpp == true) {
            if (board.player == 1)
                obj.put("Winner", "Player");
            else
                obj.put("Winner", "AI");
        }
        else {
            if (board.player == 1)
                obj.put("Winner", "Player1");
            else
                obj.put("Winner", "Player2");
        }

        obj.put("Moves", arr);

        return (obj);
    }
}