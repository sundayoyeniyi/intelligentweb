/*
 * @(#)WebCrawlerProtocol.java 20-May-2009
 * 
 * Copyright (c) 2008 - 2009
 * 
 * The Software was written as part of COM6685 Intelligent Web Assignment
 * University of Sheffield, MSC, SSIT, 2008 - 2009.
 */

package com.intelligentweb.webcrawler;
/**
 * <p>
 * A public interface purely for keeping constants 
 * that will be accessible to the implementing classes.  
 * Its function is to keep the location of the robots.txt for the given
 * site.  The location of the robots.txt is fixed for the purpose of this assignment
 * and hence the need to keep it as public, static and final variable.
 * The value of the property to be restricted in the process of crawling
 * is also kept as a public, static and final variable here. 
 * </p>
 * @author sunday oyeniyi - acp08sjo@sheffield.ac.uk
 * @author manoj joseph mathew - acp08mjm@sheffield.ac.uk
 */
public interface WebCrawlerProtocol {
    /**
     * The robot location constant for keeping the location of the robots.txt file.
     * This location is fixed for this assignment and hence the need to keep it 
     * as a constant rather than referring to it in the usual home folder
     * of the web application.
     */
    public static final String ROBOT_LOCATION="http://ext.dcs.shef.ac.uk/~u0082/intelweb/robots.txt"; 
    
    /**
     * The disallow constant for keeping the properly that will be restricted from crawling 
     * if found in the robots.txt file.  Any URL that starts with the value of any of
     * this property will be assumed to be restricted and will be unsafe to crawl and then 
     * excluded from the crawling process.
     */
    public static final String DISALLOW = "Disallow:";
}
