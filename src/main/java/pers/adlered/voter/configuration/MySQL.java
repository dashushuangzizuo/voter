package pers.adlered.voter.configuration;

/**
 * If you form this values, SpringBoot will be check the database:
 * DATABASE NAME: Voter
 * And auto create tables if not exist.
 * BUT FIRST: Fill in below values, and CREATE A DATABASE NAMED "Voter", then start the project.
 * Every tables will be auto generate.
 */
public class MySQL {
//    public static String IP = "47.105.71.175";
    public static String IP = "localhost";
//    public static String IP = "106.12.43.242";
    public static Integer Port = 3306;
//    public static String Username = "vote";
//    public static String Password = "vote_6";
    public static String Username = "";
    public static String Password = "";
}
