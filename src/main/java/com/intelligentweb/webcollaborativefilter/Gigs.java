/*
 * @(#)Gigs.java 20-May-2009
 * 
 * Copyright (c) 2008 - 2009
 * 
 * The Software was written as part of COM6685 Intelligent Web Assignment
 * University of Sheffield, MSC, SSIT, 2008 - 2009.
 */
package com.intelligentweb.webcollaborativefilter;

import java.net.URL;

/**
 * <p>
 *  The Gig class used to represent a gig and has as attributes names, URL, date and ranking.
 * </p>
 * <p>
 *  It implements the comparable interface as this will enable a list of gigs to be recommended to a 
 *  a user to be sorted.  It will be sorted using the ranking instance variable and the implementation 
 *  provided for the compareTo method ensures the sorting is done on the ranking variable.
 * </p>
 * @version 1.0
 * @author sunday oyeniyi acp08sjo@sheffield.ac.uk 
 * @author manoj mathew joseph aco08mjm@sheffield.ac.uk
 */
public class Gigs implements Comparable  {
    
    /**
   * <p>
   * A String instance variable holding the name of the gig .
   * </p>
   */ 
    String gigName;
    
    /**
   * <p>
   * A String instance variable holding the date of a particular gig as found in the XML
   * </p>
   */ 
    String gigDate;
    
    /**
   * <p>
   * A URL instance variable holding the URL of that particular gig .
   * </p>
   */ 
    URL gigURL;
    
  /**
   * <p>
   * A Double instance variable holding the Rankings of the particular gig in the dictionary.
   * </p>
   */ 
  Double gigRanking;    
    
  /**
   * <p>
   * The Gigs Constructor - Accepts <em>gigname,gigDate,gigURL,gigRanking</em> and then sets 
   * sets the instance's of the gig name,gig Date,gig URL and gig Ranking. 
   * </p>
   * @param gigname - the name of the gig.
   * @param gigDate - the date at which the date occurs.
   * @param gigURL - the URL of the gig.
   * @param gigRanking - the Ranking of the gig. 
   */
  Gigs(String gigName, String gigDate, URL gigURL, Double gigRanking) {
        this.gigName = gigName;
        this.gigDate = gigDate;
        this.gigURL = gigURL;
        this.gigRanking = gigRanking;
    }
    
  /**
   * An Accessor method for the <em>gigDate</em> instance variable
   *
   * @return gigDate - the date the gig occurs as extracted from the XML file
   */
  public String getGigDate() {
    return gigDate;
  }
    
   /**
    * A mutator methoed for the <em>gigDate</em> instance variable
    *
    */ 	
    public void setGigDate(String gigDate) {
        this.gigDate = gigDate;
    }
    
   /**
    * An accessor method for the <em>gigName</em> instance variable
    *
    * @return gigName the name of the gig as extracted from the XML file
    */
    public String getGigName() {
        return gigName;
    }
    
  /**
    * A mutator method for the <em>gigName</em> instance variable
    *
    */
    public void setGigName(String gigName) {
        this.gigName = gigName;
    }
   
    /**
     * An accessor method for the <em>gigRanking</em> instance variable
     *
     * @return gigRanking - the ranking calculated for this gig that will be used in making top recommendation
     */
    public Double getGigRanking() {
        return gigRanking;
    }
    
  /**
    * A mutator method for the <em>gigRanking</em> instance variable
    *
   */
    public void setGigRanking(Double gigRanking) {
        this.gigRanking = gigRanking;
    } 
    
    /**
    * A mutator method for the <em>gigURL</em> instance variable
    *
    * @return gigURL - the URL of the gig as extracted from the XML file and turned into an ABSOLUTE URL
    */
    public URL getGigURL() {
        return gigURL;
    }

    /**
    * A mutator method for the <em>username</em> gig URL instance variable
    *
    */
    public void setGigURL(URL gigURL) {
        this.gigURL = gigURL;
    }
    
    /**
     * The String representation of a gig object
     * @return A String of the gigName , gigURL, gigDate, gigRankings in a textual form.
     */  
    public String toString() {
        return "\nGig Name : "+gigName+" " +
                "Gig URL : "+gigURL+" " +
                "Gig Date : "+gigDate+" " +
                "Gig Ranking : "+gigRanking;                
    }
    
    /**
    * The compareTo method used for the purpose of providing an implementation for the abstract compareTo in the 
    * Comparable interface.  This is used to sort an object dependent using the ranking variable
    * @return A integer of the instance  for that particular gig which is either negative integer, zero, or a positive integer as this object 
    * is less than, equal to, or greater than the specified object. 
    */
    public int compareTo(Object anotherGig) throws ClassCastException {
        if (!(anotherGig instanceof Gigs)) //Condition to check if the Object anotherGig is an instance of Gigs
          throw new ClassCastException("A Gigs object expected."); //throws an exception when a gig object hasnt been defined in anotherGig
        Double anotherGigRanking = ((Gigs) anotherGig).getGigRanking();     
        return (int) (this.gigRanking - anotherGigRanking);    //returns a integer by finding the difference for the specified object.
  }
}