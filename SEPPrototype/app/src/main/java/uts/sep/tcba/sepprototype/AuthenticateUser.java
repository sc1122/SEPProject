package uts.sep.tcba.sepprototype;


public class AuthenticateUser {
    public String database_connection;

    public AuthenticateUser(int ID, String password){
        String SQL = "SELECT FIRSTNAME, LASTNAME, USERTYPE FROM T_USERS WHERE ID = " + ID;
        // Database connection using SQL
    }
}
