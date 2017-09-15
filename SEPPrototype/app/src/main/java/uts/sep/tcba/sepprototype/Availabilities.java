package uts.sep.tcba.sepprototype;

/**
 * Created by seant on 15/09/2017.
 * Class which handles the availabilities of the tutor
 * Done in a linked list of the different availabilities
 */

public class Availabilities {

    private String availDate;
    private double startTime;
    private double endTime;
    private int studentLimit;
    private String location;

    public Availabilities(Tutor tutor){
        //in full implementation, pull records entered into database to here
        availDate = "08/10/17";
        startTime = 10.00;
        endTime = 16.00;
        studentLimit = 3;
        location = "CB11.04.401";
    }


}
