/*
 * @(#)WebCrawlerImplementation.java 20-May-2009
 * 
 * Copyright (c) 2008 - 2009
 * 
 * The Software was written as part of COM6685 Intelligent Web Assignment
 * University of Sheffield, MSC, SSIT, 2008 - 2009.
 */

package com.intelligentweb.webcrawler;

import java.net.URL;
import java.net.URLConnection;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * <p>
 *  The Web Crawler implementation class that implements the given interface for the web crawler
 *  part of this assignment.  It implements the Crawler interface and provide concrete methods
 *  for all <strong> <i>implicitly</i></strong> abstract methods of the crawler interface.  It also
 *  implements the WebCrawlerProtocol which provide a set of constants (specifically the robot's file descriptipn).
 *  It is also Runnable as it implements the Runnable interface which enables the crwaler implementation 
 *  to be executed as a thread and then make use of Java's provision to handle stop, resume and kill thread  
 *  to implements the specifics of the COM6865 Assignment 
 * </p>
 * @version 1.0
 * @author sunday oyeniyi acp08sjo@sheffield.ac.uk 
 * @author manoj mathew joseph aco08mjm@sheffield.ac.uk
 */
public class WebCrawlerImplementation implements Crawler, WebCrawlerProtocol, Runnable {
    
    /**
     * <p>A generic instance List of URLs that will hold all the URLs that are <strong>LOCAL</strong> to the given seed URL.</p>
     */
    protected List<URL> localUrls;
    
    /**
     * <p>A generic instance List of URLs that will hold all the URLs that are <strong>EXTERNAL</strong> to the given seed URL.</p>
     */
    protected List<URL> externalUrls;
    
    /**
     * <p>A generic instance list of URLs holds a working load of all discovered URLs that are booked to be visited by the crawler.</p>
     */
    protected List<URL> urlsToVisit;
    
    /**
     * <p>A generic instance list of URLS that holds all discovered URLs that have been visited by the crawler.</p>
     */
    protected List<URL> urlsVisited;
    
    /**
     * <p>A generic instance list of URLs that will hold all discovered URLs that are invalid. i.e. could not be opened</p>
     */
    protected List<String> invalidUrls;
    
    /**
     * <p>A generic instance list of URLs <strong>that fail ROBOT's test.</strong> i.e. sites that re restricted based on the ROBOT's file definition.</p>
     */
    protected List<URL> unSafeUrls;
    
    /**
     * <p>
     * A URL instance variable that is the very first point of call for the crawler and from where other URLS will
     * be discovered.  Its the seed in the sense that its the startin point for the crawler.
     * </p>
     */    
    protected URL seedUrl;
    
    /**
     * <p>
     * A boolean instance variable that will be used to determine the whether the crawler thread should 
     * continue to run or it should stop.  This concept was adopted from Java approach to handling thread's
     * stop, resume and kill since the suspend(), resume() and kill methods have been deprecared.
     * </p>
     */
    protected boolean stop; 
    
    /**
     * <p> A thread class variable which will be used to kick start the crawler thread.  It's a volatile variable</p>
     */
    protected volatile Thread crawlerThread;
    
    /**
     * <p>
     * The constructor for the web crawler implemntation takes the seed URL at construction and assign to the 
     * instance seed URL and also prepares the various holding area for temporary storage.
     * <p>
     * @param seedUrl - the URL that will be supplied by the user of the application.  This indicates the implementation can be used to search any URL and its not hard coded.
     */
    public WebCrawlerImplementation(URL seedUrl) {
            urlsToVisit=new ArrayList<URL>();
            urlsVisited=new ArrayList<URL>();
            localUrls=new ArrayList<URL>();
            externalUrls=new ArrayList<URL>();
            invalidUrls=new ArrayList<String>();
            unSafeUrls=new ArrayList<URL>();
            this.seedUrl=seedUrl;  //    sets the instance seed URL here so that its available to all the class methods      
    }
    
    /**
     * <p>
     * A method to confirm if the crawler is still alive or not.  This is required since the implemented class 
     * will be tested using a different GUI application and they will both be running as separate threads...
     * </p>
     * @return a boolean variable indicating whether the thread is alive (true) or is dead (false).
     */
    public boolean isCrawlerAlive() {
        return crawlerThread.isAlive();
    }  
    
    /**
     * <p>
     * A start method that starts a thread of execution for the crawler. The purpose is to make the crawler
     * a thread and when called the the Java Virtual Machine calls the run method of this thread.
     * Also, <strong>note that this is an overloaded method as it has a different signature from the implicitly
     * abstract start method provided for the crawler interface in the assignment brief</strong>.
     * </p>
     */ 
    public void start() {       
       urlsVisited.clear();
       urlsToVisit.clear();
       localUrls.clear();
       externalUrls.clear();
       invalidUrls.clear();
       unSafeUrls.clear();
       crawlerThread = new Thread(this);
       crawlerThread.start();    
    }    

    /**
     * <p>
     * The run method that override the run method provided by the Runnable interface.
     * Its purpose is to initiate the start method for the crawler which will start
     * crawling the given seed URL.  The start method to be called is the implementation 
     * provided for the given crawler interface.
     * </p>
     */
    public void run() {        
        start(seedUrl);
    }
    
    /**
     * <p>
     * The stop method that is used to <strong> 'PAUSE' </strong> i.e. suspend the running crawler thread 
     * for later resumption. Its purpose is to set the <strong> 'stop' </strong> instance boolean variable
     * to true and the crawler will detect this and then temporarily stop crawling by calling its 
     * <strong>wait()</strong> method until it is set back to false.
     * We have used this approach because the suspend() method of the thread class in the Java API has been
     * deprecated for thread safety.  For more information on this approach please check:
     * <a href="http://java.sun.com/j2se/1.4.2/docs/guide/misc/threadPrimitiveDeprecation.html">
     * WHY THREAD'S SUSPEND, RESUME AND STOP ARE DEPRECATED </a> 
     * </p>
     */    
    public void stop() {
        stop=true;
        System.out.println("crawler has been stopped ... i.e. suspended");
    }
    
    /**
     * <p>
     * The resume method that is used to <strong> 'RESUME' </strong> the thread of crawler execution after it 
     * has been paused by the user.  It will reset the <strong> 'stop' </strong> instance boolean variable
     * back to 'false' and call the <strong> notify() </strong> method within a <strong> synchronized </strong>
     * code snippet.  This is to ensure it is accessible only by the currently running object that 
     * owns the monitor.
     * We have used this approach because the resume() method of the thread class in the Java API has been
     * deprecated for thread safety.  For more information on this approach please check:
     * <a href="http://java.sun.com/j2se/1.4.2/docs/guide/misc/threadPrimitiveDeprecation.html">
     * WHY THREAD'S SUSPEND, RESUME AND STOP ARE DEPRECATED </a>
     * </p>
     */    
    public void resume() {
        stop=false;
        synchronized(this) {
            notify();
        }
        System.out.println("crawler has been resumed ...");
    }
    
    /**
     * <p>
     * The kill method that is used to terminate the thread of execution for the crawler.
     * The crawlerThread instance will be set to null and the implementation of the start
     * method will detect this and then halts execution by gracefully getting to the end of
     * its run method which will cause the Java Virtual Machine to now terminate the thread.
     * We have used this approach because the stop() method of the thread class in the Java API has been
     * deprecated for thread safety.  For more information on this approach please check:
     * <a href="http://java.sun.com/j2se/1.4.2/docs/guide/misc/threadPrimitiveDeprecation.html">
     * WHY THREAD'S SUSPEND, RESUME AND STOP ARE DEPRECATED </a>
     * </p>
     */  
    public void kill() {
        crawlerThread=null;
        System.out.println("crawler has been killed ...");
    }
 
    /**
     * <p>
     * The implementation to confirm that it is safe to crawl a given url using the restrictions
     * that are provided in the robots.txt file for the site.
     * The approach downloads the robots.txt file and build tokens around the values of the DISALLOW properties
     *  and it was adapted from the work of <strong> Thom Blum, Doug Keislar, Jim Wheaton, 
     * and Erling Wold of Muscle Fish, LLC January 1998.</strong>  For more information, please see
     * <a href="http://java.sun.com/developer/technicalArticles/ThirdParty/WebCrawler/WebCrawler.java">
     * Writing a Web Crawler in the Java Programming Language </a>
     * </p>
     * <p>
     * <strong> ASSUMPTIONS </strong>
     * </p>
     * <p>
     * <ul>
     *      <li> The given URL is assumed to be <em>unsafe</em> to crawl if the robots.txt file is <em>MALFORMED</em></li>
     *      <li> The given URL is assumed to be <em>safe</em> to crawl if the robots.txt file throws an IOException.</li> 
     *      <li> The given URL is not safe to crawl if the given URL starts with a path that is found as a value of the DISALLOW properties of the robots.txt file.</li>
     * </ul>
     * </p> 
     * @param url is the url to be compared against the list of exceptions if any in robots.txt file     * 
     * @return a boolean value stating true or false to crawl the given url
     */
    public boolean isRobotSafe(final URL url){        
	URL siteRobot;
        String robotRestrictions;        
	try { 
	    siteRobot = new URL(ROBOT_LOCATION); // the robot location is kept in the WebCrawlerProtocol interface class since it is a constant
	} catch (MalformedURLException mue) {
	    // robot is malformed thus assumed to be unsafe to crawl.
	    return false;
	}
	
	try {
	    InputStream siteRobotStream = siteRobot.openStream();
	    // to download the robot file
	    byte robotDataChunk[] = new byte[1000];
	    int dataChunk = siteRobotStream.read(robotDataChunk);
	    robotRestrictions = new String(robotDataChunk, 0, dataChunk);
	    while (dataChunk != -1) {
		dataChunk = siteRobotStream.read(robotDataChunk);
		if (dataChunk != -1) {		   
		    robotRestrictions += new String(robotDataChunk, 0, dataChunk);                 
		}
	    }
	    siteRobotStream.close();
	} catch (IOException e) {
	    // Site is assumed to be safe for crawling if there is an IOException on the robots file
	    return true;
	}

	// To apply the disallow commands under the assumptions that all the restrictions 
        // are meant for this crawler by looking for 'Disallow:' restrictions	
	String strURL = url.getFile();
	int index = 0;
	while ((index = robotRestrictions.indexOf(DISALLOW, index)) != -1) {
	    index += DISALLOW.length();
	    String robotRestrictionPath = robotRestrictions.substring(index);
	    StringTokenizer robotRestrictionTokenizer = new StringTokenizer(robotRestrictionPath);
	    if (!robotRestrictionTokenizer.hasMoreTokens())
		break;	    
	    String strBadPath = robotRestrictionTokenizer.nextToken();     
	    // if the URL starts with a disallowed path, it is not safe
	    if (strURL.indexOf(strBadPath) == 0)    
		return false;
	}
	return true;        
    }
    
    /**
     * <p>
     * This is the implementation for the start method that was provided in 
     * crawler interface given in the assignment brief.  It actually kick start the process
     * of visiting the seed url and discovering all links and scheduling them for visit.
     * </p>
     * <p>
     * The task of going through the web pages and scanning it is delegated to supporting 
     * private method and the instance variables that hold urlsVisited, ursToVisit, invalidUrls,
     * unSafeUrls are managed appropriately in the supporting private method.
     * </p>
     * @param seedUrl - the base url to start crawling from.
     */    
    public void start(final URL seedUrl) {         
        Thread runningCrawler=Thread.currentThread();
        try {     
            urlsToVisit.add(seedUrl);
            int fileCount=0;
            long timenow=System.currentTimeMillis(); 
            while (!urlsToVisit.isEmpty() && (runningCrawler==crawlerThread)) {               
                synchronized(this) {
                    while (stop) {
                        System.out.println("The Web Crawler is waiting ...."); 
                        wait(); // the crawler will block here until the notify() method is called.  Used to implement 'pausing' the crawler
                    }
                }

                Iterator iter=urlsToVisit.iterator(); // an iterator to go through the workload of URLs to visit
                fileCount++;  // a counter to keep track of number of files downloaded in order not to exceed the five files per second throttle rate
                URL presentUrl=(URL)iter.next(); 
                
                // to set the properties of the URLConnection that will be used in crawling the given URLs
                // this is to ensure it behaves responsibly and identifiable by webmasters.
                URLConnection conn=presentUrl.openConnection();
                conn.setRequestProperty("User-Agent", "COM6865 Web Crawler Assignment 2009 Module");
                conn.setRequestProperty("From", "acp08sjo@sheffield.ac.uk; acp08mjm@sheffield.ac.uk; com6865@crawler.com");
                conn.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
                conn.setRequestProperty("accept-charset", "ISO-8859-1,utf-8;q=0.7,*;q=0.7");
                conn.setRequestProperty("accept-encoding", "gzip,deflate");                
                conn.setRequestProperty("accept-language", "en-gb,en;q=0.5");
                conn.setRequestProperty("connection", "keep-alive");
                conn.setRequestProperty("keep-alive", "300");                
                conn.setRequestProperty("accept-language", "en-gb,en;q=0.5");
                conn.setRequestProperty("Content-Type", "application/xml");
                conn.setRequestProperty("Referer", "Site Discocered in COM6865 Assignment Handout by Professor Fabio Ciravegna");
                
                //To test that the URL to download is VALID
                InputStream inputStream=null;
                try {
                    inputStream=conn.getInputStream();            
                }catch(Exception exp){
                    urlsToVisit.remove(presentUrl);  //removing it from URLs to Visit list since it throws an exception
                    invalidUrls.add(presentUrl.toString()); // adding it to INVALID URLs list since it throws an exception                                    
                    continue;
                }
                
                //To test if the URL has already been visited so as to avoid downloading it twice.
                if (urlsVisited.contains(presentUrl)) {
                    urlsToVisit.remove(presentUrl); //removing the URL from the list of URLs to visit since it appears in the list of URLs that has been visited.                    
                    continue;
                }

                //To test if url has already been restricted by the robot engine so as to avoid wasting time testing it a second time
                if (unSafeUrls.contains(presentUrl)) {
                    urlsToVisit.remove(presentUrl); //removing it from the list of URLs to visit since it has been marked unsafe
                    continue;
                }
                
                //Io test if the given URL is already marked as invalid to avoid wasting time testing it again
                if (invalidUrls.contains(presentUrl)) {                   
                   urlsToVisit.remove(presentUrl); //removint it from the list of URLs to visit since it has been earlier confirmed to be invalid
                   continue;
                }                 
                  
                //To test if it is robot safe to process this URL
                if (!isRobotSafe(presentUrl)) {
                   unSafeUrls.add(presentUrl );  //to add the URL to list of unsafe URLs since it's been denied by the robot.txt
                   urlsToVisit.remove(presentUrl); //to remove it from the list of URLs to visit.
                   continue;
                }           

                //TO process using Regular Expression    
                System.out.println("about to process :"+presentUrl);
                processURL(presentUrl, inputStream);
                inputStream.close();
                System.out.println("url processed :"+presentUrl);
                
                //To mark the URL as visited and remove it from list of URLs to be visited
                urlsVisited.add(presentUrl);
                urlsToVisit.remove(presentUrl);
               
                //To manage the server throttle rate for this crawler ( Five (5) download per second 5dps)
                if (fileCount>4 & (System.currentTimeMillis()-timenow)<1000) {
                    try {
                        runningCrawler.sleep(1000); //crawler will sleep for 1 second when fileCount>4 within a second
                        fileCount=0;
                    }catch (InterruptedException ie) {
                        //
                    }
                    timenow=System.currentTimeMillis();                    
                }                
            }
            //Producing output files for the crawler since it has completed all workload of URLs to visits
            String localSites = outputLocalUrls(); //calls a method that prints the local URLs in the specified location
            String externalSites = outputExternalUrls(); //calls a method that prints the external URLs in the specified location
            
            System.out.println("\n\n\n The Web Crawler has finished crawling the URL :"+seedUrl.toString());
            System.out.println("\n The Web Crawling statistics is as follows : \n"+this.toString());
            System.out.println("\n The URLs local to this site are stored in : '"+localSites+"'");
            System.out.println("\n The URLs external to this site are stored in : '"+externalSites+"'");
            System.out.println("\n THE END");  
        }catch (Exception exp) {
            exp.printStackTrace();
            System.out.println("There is a general exception with trace above that is causing thecrawler to halt ...");
        }finally{
            crawlerThread=null;
        }        
    }    
        
    /**
     * <p>
     * A public method which is an implementation of the implicitly abstract getLocalUrls() method
     * of the crawler interface.
     * </p>
     * @return a list object that contain the list of local URLs that were discovered during the crawling exercise     * 
     */
    public List<URL> getLocalUrls() {
        return localUrls;
    }

   /**
     * <p>
     * A public method which is an implementation of the implicitly abstract getExternalUrls() method
     * of the crawler interface.
     * </p>
     * @return a list object that contain the list of external URLs that were discovered during the crawling exercise     * 
     */    
    public List<URL> getExternalURLs() {
        return externalUrls;
    }
    
    /**
     * 
     * @param url
     * @param conn
     * @param inputStream
     */    
    private void processURL(URL url, InputStream inputStream) {
        try {                    
            BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(inputStream));            
            String line;
            String allLines = "";          
            while ((line=bufferedReader.readLine())!=null) {
                allLines = allLines+" "+line;                
            }  
              
            Pattern p1=Pattern.compile("\\s*href\\s*=\\s*[\"][^\"]*[\"]");
            Matcher m1=p1.matcher(allLines);
            Pattern p2=Pattern.compile("\"[^\"]*\"");
            while (m1.find()) {
                String href=allLines.substring(m1.start(), m1.end());                
                Matcher m2=p2.matcher(href);
                while(m2.find()) {                    
                    href=href.substring(m2.start(), m2.end()).replace("\"", "");                
                    //To strip off urls with anchor reference to itself
                    if (href.indexOf('#')!=-1){
                        href=href.substring(0, href.indexOf('#'));
                    }                    
                    if (href.contains("&amp;")) {
                        href=href.replace("&amp;", "&");
                    }
                    try {
                        URL discoveredURL = new URL (url, href);
                        addUrl(discoveredURL, url);
                    }catch(Exception exp){
                        invalidUrls.add(href);
                    }                   
                }
            }            
         }catch(IOException ioe){
            invalidUrls.add(url.toString());            
        }            
    }
    
    /**
     * 
     * @param discoveredUrl
     * @param baseUrl
     */    
    private void addUrl (URL discoveredUrl, URL baseUrl) {
        if (urlsToVisit.contains(discoveredUrl)) {
            return;
        }
        if (invalidUrls.contains(discoveredUrl)) {
            return;
        }
        if (urlsVisited.contains(discoveredUrl)) {
            return;
        }        
        if (externalUrls.contains(discoveredUrl)) {
            return;
        } 
        if (localUrls.contains(discoveredUrl)) {
            return;
        }
        if (unSafeUrls.contains(discoveredUrl)) {
            return;
        }        
         if (baseUrl.getHost().equalsIgnoreCase(discoveredUrl.getHost())) {            
            urlsToVisit.add(discoveredUrl);
            localUrls.add(discoveredUrl);
          } else {
            externalUrls.add(discoveredUrl);
          }
     }    

  /**
   * <p>
   * A helper method that is responsible for outputting the discovered local URLs 
   * into a specified file and specified folder in the hand in folder.
   * </p>
   * @return the full path indicating the name and path to the output file containing the list of discovered local URLs
   */
  private String outputLocalUrls() {
        String spiderOutputFolder = "..\\Output\\Spider\\"; // assigning the path for the output file
        String fileLocation = spiderOutputFolder + "localURLS.txt"; //assigning a name for the ouput file
        File localUrlsOutputFile = new File (fileLocation);
        File spiderFolder = new File (spiderOutputFolder);
        try {            
            if (localUrlsOutputFile.exists()) { // output file already exist.
                localUrlsOutputFile.delete(); // delete the existing output file
                localUrlsOutputFile.createNewFile(); //re-create the output file that was deleted above.
            }else{ //output file does not exits
                if (!spiderFolder.exists()){
                    spiderFolder.mkdirs(); //output folder does not exist, thus create it here.
                }
                localUrlsOutputFile.createNewFile(); // create the output file since it does not exist.
            }
            FileWriter fw = new FileWriter(localUrlsOutputFile);
            BufferedWriter bw = new BufferedWriter(fw);            
            for (URL url : localUrls) {
                bw.write(url.toString(), 0, url.toString().length()); // buffered writer to write the list of local URLs to the output file.
                bw.newLine();
            }        
            bw.close(); // close buffered writer
            fw.close(); // close file writer
            fileLocation = localUrlsOutputFile.getAbsolutePath(); // get the path to the output folder 
        }catch (Exception exp) {
            exp.printStackTrace();            
            fileLocation = "EXCEPTION in generating localURLs file ...";
        }
        return fileLocation; // returns the output folder path
  }

  /**
   * <p>
   * A helper method that is responsible for outputting the discovered exteranl URLs 
   * into a specified file and specified folder in the hand in folder.
   * </p>
   * @return the full path indicating the name and path to the output file containing the list of discovered external URLs
   */  
    public String outputExternalUrls() {
        String spiderOutputFolder = "..\\Output\\Spider\\"; // assigning the path for the output file
        String fileLocation = spiderOutputFolder + "externalURLS.txt"; //assigning a name for the ouput file
        File externalUrlsOutputFile = new File (fileLocation);
        File spiderFolder = new File (spiderOutputFolder);        
        try {
            if (externalUrlsOutputFile.exists()) { // output file already exist
                externalUrlsOutputFile.delete(); //delete existing file
                externalUrlsOutputFile.createNewFile(); // create a new one
            }else{ //output file does not exit
                if (!spiderFolder.exists()){ // output folder does not exist
                    spiderFolder.mkdirs(); //create the output folder
                }
                externalUrlsOutputFile.createNewFile(); // cearte the output file.
            }
            FileWriter fw = new FileWriter(externalUrlsOutputFile);
            BufferedWriter bw = new BufferedWriter(fw);            
            for (URL url : externalUrls) {
                bw.write(url.toString(), 0, url.toString().length()); //write the list of external URLs to the output folder here
                bw.newLine();
            }
            bw.close(); //close the buffered writer
            fw.close(); //close the file writer
            fileLocation = externalUrlsOutputFile.getAbsolutePath();
        }catch (Exception exp) {
            exp.printStackTrace();            
            fileLocation = "EXCEPTION in generating externalURLs file ...";
        }
        return fileLocation; // return the output folder path 
    }

    /**
     * <p>
     * Overrriding the toString method to provide a String representation for the crawler.
     * The string representation returned contains the current state of the crawler and states
     * the size of URLs discovered in each category (local, external, unsafe, invalid).
     * </p>
     * @return a string representation of the crawler
     */
    public String toString() {
        return "\n Local URLS Count :"+localUrls.size()
                + "\n External URLS Count :"+externalUrls.size()                
                + "\n Visited URLS Count :"+urlsVisited.size()
                + "\n To Be Visited URLS Count :"+urlsToVisit.size()
                + "\n Unsafe URLS Count :"+unSafeUrls.size()
                + "\n Invalid URLS Count :"+invalidUrls.size();
    }
}

