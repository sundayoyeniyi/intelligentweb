/* 
 * @(#)CollaborativeFilter.java 20-May-2009
 * 
 * Copyright (c) 2008 - 2009
 * 
 * The Software was written as part of COM6685 Intelligent Web Assignment
 * University of Sheffield, MSC, SSIT, 2008 - 2009.
 */

package com.intelligentweb.webcollaborativefilter;
import java.util.List;

/**
 * <p>
 * The interface class given for the CollaborativeFilter.
 * The interface has one method which returns a list of objects.
 * The exact type of object to be returned will be determined
 * by the implementation class.  The purpose of the method is to 
 * a username and then returns a list of recommendations that are arrived
 * at by comparing the user with other users who have rated similar things 
 * in the past.
 * </p>
 * @author sunday oyeniyi acp08sjo@sheffield.ac.uk
 * @author mathew manoj joseph acp08mjm@sheffield.ac.uk
 */
public interface CollaborativeFilter {
    /**
     * The getRecommendGigs method implementation for the given interface.
     * 
     * @param username
     * @return a list of objects of Gigs stating the gig name, url and the date.
     */
    public List<Gigs> getRecommendGigs(String username); 
}
