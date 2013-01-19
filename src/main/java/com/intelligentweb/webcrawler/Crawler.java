/* 
 * @(#)Crawler.java 20-May-2009
 * 
 * Copyright (c) 2008 - 2009
 * 
 * The Software was written as part of COM6685 Intelligent Web Assignment
 * University of Sheffield, MSC, SSIT, 2008 - 2009.
*/

package com.intelligentweb.webcrawler;
import java.net.URL;
import java.util.List;

/**
 * <p>
 * The interface class given for the Web Crawler.
 * There are seven implicitly abstract methods with no implementation
 * and for which the concrete class will provide implementation.
 * </p>
 * @author sunday oyeniyi acp08sjo@sheffield.ac.uk
 * @author mathew manoj joseph acp08mjm@sheffield.ac.uk
 */
public interface Crawler {
    /**
     * A method that will start the crawling process
     * @param seedUrl - the starting point for the crawler and the cralwer will be restricted to the domain given
     */
    public void start(final URL seedUrl);
    
    /**
     * A method that will confirm if it is safe to crawl the given URL based on 
     * restrictions specified in the robots.txt file
     * @param url - the URL to be confirm with the list of restrictions listed in the robots.txt filee
     * @return a boolean value indicating whether its OK to crawl the given URL or not.
     */
    public boolean isRobotSafe(final URL url); 
    
    /**
     * A method that will temporarily pause the crawler and it can be resumed by other methods
     */
    public void stop(); 
    
    /**
     * A method that will resume the crawler if paused
     */
    public void resume(); 
    
    /**
     * A method that will cause the JVM to stop running the crawler
     */
    public void kill();
    
    /**
     * A method that will return the list of local URLs discovered during the crawling process
     * @return a generic list of URLs that are local or found on the same host as the seed URL
     */
    public List<URL> getLocalUrls(); 
    
    /**
     * A method that will return the list of external URLs discovered during the crawling process
     * @return a generic list of URLs that are external or found to be on another host different from the host of the seed URL
     */
    public List<URL> getExternalURLs(); 
}
