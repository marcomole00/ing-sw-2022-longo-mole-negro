package it.polimi.ingsw;

import java.io.File;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class Log {
    public final Logger logger;
    private FileHandler fh;

    public Log(String file_name) throws SecurityException {
        File f = new File(file_name);
        logger = Logger.getLogger("test");
        try{
            if(!f.exists()) {
                f.createNewFile();
            }
            fh = new FileHandler(file_name, true);
        } catch (IOException e){
            logger.warning("Could not open file");
            logger.severe(e.getMessage());
        }
        logger.setUseParentHandlers(false);
        logger.addHandler(fh);
        SimpleFormatter formatter = new SimpleFormatter();
        fh.setFormatter(formatter);
    }
}
