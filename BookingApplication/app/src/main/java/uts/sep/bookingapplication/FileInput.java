package uts.sep.bookingapplication;

/**
 * Created by seant on 24/08/2017.
 */

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.io.*;


public class FileInput {
    InputStream inputStream;

    public FileInput(InputStream inputStream){
        this.inputStream = inputStream;
    }

    public List read(){
        List fileList = new ArrayList();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        try {
            String fileLine;
            while ((fileLine = reader.readLine()) != null){
                String[] row = fileLine.split(",");
                fileList.add(row);
            }
        }
        catch (IOException exception){
            throw new RuntimeException("Error in reading file: "+exception);
        }
        finally {
            try {
                inputStream.close();
            }
            catch(IOException exception){
                throw  new RuntimeException("Error closing file: "+exception);
            }
        }
        return fileList;
    }

}
