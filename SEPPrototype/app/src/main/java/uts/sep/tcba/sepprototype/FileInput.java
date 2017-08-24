package uts.sep.tcba.sepprototype;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sean Crimmins on 24/08/2017.
 * Class which handles the reading of data from a .CSV file
 * Places the read data into a new array list
 */

public class FileInput {
        InputStream inputStream;

        public FileInput(InputStream inputStream){
            this.inputStream = inputStream;
        }

    /**
     * Code to call read method:
     * InputStream inputStream = getResources().openRawResource(R.raw.stats);
     * CSVFile csvFile = new CSVFile(inputStream);
     * List scoreList = csvFile.readFile();
     * @return array list of each row in a string form
     */
    public List readFile(){
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
