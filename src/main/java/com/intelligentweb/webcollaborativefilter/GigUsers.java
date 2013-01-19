/*
 * @(#)GigUsers.java 26-May-2009 10H00 AM Absolute Deadline!
 * 
 * Copyright (c) 2008 - 2009
 * 
 * The Software was written as part of COM6685 Intelligent Web Assignment
 * University of Sheffield, MSC, SSIT, 2008 - 2009.
 */
package com.intelligentweb.webcollaborativefilter;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 *  The GigUsers class representing an object of users containing the username, the list of gig names he's been to,
 *  the list of gigs URL he's been to, the list of gig dates, the list of votes for each gigs, 
 *  and a method for adding to the list of gig names, gig URLs and gig votes for a user.
 * </p>
 * @version 1.0
 * @author sunday oyeniyi - acp08sjo@sheffield.ac.uk 
 * @author manoj mathew joseph - acp08mjm@sheffield.ac.uk
 */
public class GigUsers {

 /**
   * <p>
   * A String instance variable holding the name of the user .
   * </p>
   */    
    private String username;
    
     /**
     * <p>
     * A list instance variable for holding the gig url's.
     * </p>
     */
    private List<URL> gigURLList;
    
     /**
     * <p>
     * A list instance variable for holding the gig's names.
     * </p>
     */
    private List<String> gigNameList;
    
     /**
     * <p>
     * A list instance variable for holding the dates of the gigs.
     * </p>
     */
    private List<String> gigDateList;
    
     /**
     * <p>
     * A list instance variable for holding the users' vote for all gig's.
     * </p>
     */
    private List<Integer> gigVoteList;
           
   /**
     * <p>
     * The GigUsers class constructor which accepts <em>username</em> and then 
     * sets the instance's username, creates an empty URL List, Name of the gig, Date and Votes for the
     * given user. 
     * </p>
     * @param username - the name of the user for which a dictionary entry will be created.
     */
    public GigUsers(String username) {
        this.username=username;
        gigURLList = new ArrayList<URL>();
        gigNameList = new ArrayList<String>();
        gigDateList = new ArrayList<String>();
        gigVoteList = new ArrayList<Integer>();
    }
    
    
   
     /**
     * Accessor Method for <em>username</em> instance variable
     *
     * @return A String of the instance of the username .
     */
    public String getUsername() {
        return this.username;
    }
    
     /**
      * Accessor Method for the <em> GigURLList </em> instance variable
      *
      * @return A generic list of URLs the user has been to/seen
      */
    public List<URL> getGigURLList() {
        return gigURLList;
    }
    
   /**
    * Accessor Method for the <em> GigNameList </em> instance variable
    *
    * @return A generic list of names of gigs the user has been to/seen
    */
    public List<String> getGigNameList() {
        return gigNameList;
    }
    
   /**
     * Accessor Method for the <em> GigDateList </em> instance variable
     *
     * @return A generic list of dates of gigs the user has been to/seen
     */
    public List<String> getGigDateList() {
        return gigDateList;
    }
    
   /**
     * Accessor Method for the <em> GigVoteList </em> instance variable
     *
     * @return A generic list of votes / ratings for the gigs the user has been to/seen
     */    
    public List<Integer> getGigVoteList() {
        return gigVoteList;
    }
    
    /**
      * The addUsersVoteEntry method that acts as a mutator method for the elements of gigURL list, gigName list, gigDate list and gigVote list.
      * @param gigURL contains the URL for a particular gig.
      * @param gigName contains the Name for that partcular gig.
      * @param gigDate contains the Date at which the particular event will be occurring.
      * @param gigVote contains the votes made by a particular user for a gig he has seen.
      */
    public void addUsersVoteEntry(URL gigURL, String gigName, String gigDate, Integer gigVote) {        
        if (gigURL!=null && gigName!=null && gigDate!=null && gigVote!=null){ //Condition to check if any of the parameters specified is not null.
            gigURLList.add(gigURL); //adds the particular gig URL into the ArrayList in gigURLList.
            gigNameList.add(gigName); //adds the gigName to the ArrayList in gigNameList.
            gigDateList.add(gigDate); //adds the gigDate to the ArrayList in gigDate List.
            gigVoteList.add(gigVote); //adds the gigVote to the ArrayList in gigVote List.
        }
    }
    
   /**
     * The String representation of a Gig User returning the name of the user, the number of
     * gig Name, gig URL, gig Dates and gig Votes he has seen / been to.
     * @return A String of the username,size of the gigURLList,gigNameList,gigDateList 
     *         and the gigVoteListin atextual form.
     */ 
    public String toString() {
        return "USER : "+username+" " +
                "\n URL Count : " +gigURLList.size()+"" +
                "\n Gig Name Count : "+ gigNameList.size()+"" +
                "\n Gig Date Count : "+ gigDateList.size()+"" +
                "\n Vote Count : "+gigVoteList.size();                
    }    
}
