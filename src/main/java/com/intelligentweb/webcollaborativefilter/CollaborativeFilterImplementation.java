/*
 *  @(#)CollaborativeFilterImplementation.java 20-May-2009
 * 
 * Copyright (c) 2008 - 2009
 * 
 * The Software was written as part of COM6685 Intelligent Web Assignment
 * University of Sheffield, MSC, SSIT, 2008 - 2009.
 */
package com.intelligentweb.webcollaborativefilter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Properties;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
/**
 * <p>
 * The CollaborativeFilter implementation provided for the collaborative filter interface given in 
 * the assignment brief.  Its purpose is to accept a <strong> 'username' </strong> and it will then
 * do the following:
 * <ul>
 *      <li> Develop a <em>dictionary of user's preferences</em> by scanning through all the 
 *           files that were discovered by the crawler, identifying which context 
 *           are of type xml and that has <strong> 'node' </strong> element(s) and the 
 *           'node' element has two child elements named: <strong> 'gig' and 'vote' </strong>.  The
 *           'gig' child element must have an attribute with name <strong> 'url' </strong>. 
 *           The values of the gig element, vote element and the url attribute of the gig element are
 *           extracted using XML XPath Query Expressions.  The name of the user is extracted from the 
 *           xml file's name that follows pattern above.  The character sequence '/xml' is appended to the 
 *           URL of the gig to identify another xml file where the date of the gig is stored.  This second
 *           xml file is also read using XML XPath Query Expression to extract the date of the gig. 
 *           The four information extracted - username, gig name list, gig url list, gig date list, gig vote list
 *           is used to form the user's preference dictionary. This dictionary is a list of Objects of type '<em>GigUsers</em>
 *      </li>
 *      <li> Calculate <em>Similarity Coefficient</em> between the given user and all the users found in all the qualifying xml files discovered
 *           in above step.  The algorithm adopted for the similarity coefficient is the <em>Pearson Correlation Score.</em>  This algorithm
 *           compares the given user with other users who have voted on the same gigs in the past and then develop a rating between them.
 *           For futher details of the Pearson Correlation Score Algorithm - 
 *           please see <em>Chapter 2, pages 11, 12 13 of Programming Collective Intelligence, ISBN: 0-596-52932-5, Author: Toby Segaran , Publisher: O'Reilly.</em>
 *      </li>
 *      <li> Get a list of Gig Recommendation for the given user by using users' preference dictionary develop in step 1 and 
 *           Pearson Correlation Score developed in step 2.  It is a ranking process that was adopted to get the <em>Recommendations</em>.
 *           For further details of the algorithm developed for this getRecommendation, please see <em>Chapter 2, pages 14, 15, and 16 of 
 *           Programming Collective Intelligence, ISBN: 0-596-52932-5, Author: Toby Segaran , Publisher: O'Reilly.</em>
 *      </li>
 * </ul>      
 * </p>
 * @author sunday oyeniyi - acp08sjo@sheffield.ac.uk
 * @author mathew manoj joseph - acp08mjm@sheffield.ac.uk
 */
public class CollaborativeFilterImplementation implements CollaborativeFilter {
    
    /**
     * <p>
     * A list instance variable for holding the users' preference dictionary.
     * </p>
     */
    private List<GigUsers> gigUsersPreferenceDictionary;
    
    /**
     * <p>
     * A set variable containing the list of distinct gig's URL
     * </p>
     */
    private HashSet<URL> gigsURL;
    
    /**
     * <p>
     * The CollaborativeFilter interface implementation Constructor - sets the instance's users' dictionary and the gigs' lists
     * </p>
     */
    public CollaborativeFilterImplementation() {
        gigUsersPreferenceDictionary = new ArrayList<GigUsers>();
        gigsURL = new HashSet<URL>();
    }
    
    /**
     * <p>
     * The getRecommendGigs implementation provided for the implicitly abstract getRecommendGigs method
     * in the interface provided.  Accepts <em>username</em> from the user of the application and employs 
     * three other helper private methods to achieve the following:
     * <ul>
     *      <li> Derive the users' preference dictionary</li>
     *      <li> Calculate Pearson Correlation Score for all the users in the dictionary<li>
     *      <li> Get Gig's Recommendation for the user by Ranking </li>
     * </li>
     * </p>
     * <p>
     * The list of gig recommendations is finally returned to the user of the application as a list 
     * of Objects of Gigs.
     * </p>
     * @param username - the name of the user that will be compared with other users in the dictionary      *                   
     * @return the list of Objects of type Gigs that was derived for the given username.
     */    
    public List<Gigs> getRecommendGigs(String username){
        System.out.println("Developing the Users' Preference Dictionary ...");
        List<GigUsers> usersDictionary = getGigUsersPreferenceDictionary(); // Building the Users' Preference Dictionary is delegated to a helper private method
        System.out.println("Calculating the Pearson Correlation Coefficient Similarity Scores ...");
        HashMap<String, Double> pearsonCorrelationScore = getPearsonSimilarityRating(username, usersDictionary); //Calculating the Pearson Correlation Score for all users in the dictionary is delegated to a helper private method       
        System.out.println("Finding a recommendation for the given user ...");
        List<Gigs> gigsRecommendation = getRecommendation(username, usersDictionary, pearsonCorrelationScore); // Ranking all the users and getting the list of recommendations is delegated to a helper private method
        System.out.println("Getting recommendation completed for : "+username);
        return gigsRecommendation; 
    }
    
    /**
     * <p>
     * The getGigUsersPreferenceDictionary helper method that creates the user's preference dictionary.
     * <ui>
     *      <li> Reads through all the URLs in the \\output\\spider\\localURLs.txt and look for which of them are xml files</li>
     *      <li> Look for xml files that defines a node element with gig and vote sub elements and the gig sub element has a url attribute </li>
     *      <li> All the elements are extracted and '/xml' appended to the gig url to locate another xml defining the date of the gig.</li>
     *      <li> The name of the user is extracted from the file name containing node elements with gig and vote sub elements. </li>
     *      <li> Above is used to create a list of objects of GigUsers which represent the User's Preference Dictionary. </li>
     * </ui>
     * </p>   
     * @return a list of GigUsers object representing the users' preference dictionary
     */     
    private List<GigUsers> getGigUsersPreferenceDictionary() {
        List<GigUsers> usersPreferenceDictionary = new ArrayList<GigUsers>();
        String localUrlsLocation =  "..\\Output\\Spider\\localURLs.txt"; // assigning the path for the output file
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(new File(localUrlsLocation)));
            String localURLEntry = bufferedReader.readLine();            
            while (localURLEntry!=null) {       //going through thr list of URLs in the localURL store         
                URL discoveredLocalURL = new URL(localURLEntry);
                URLConnection conn=discoveredLocalURL.openConnection(); //opening a connection to the URL discovered
                conn.connect();
                String urlContentType = conn.getContentType(); // getting the type content in the file
                if (urlContentType.startsWith("text/xml")) { //content of file is XML 
                    DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder(); 
                    conn.connect();
                    Document document = documentBuilder.parse(conn.getInputStream()); // getting a Document object on the XML file
                    XPath xpath = XPathFactory.newInstance().newXPath();  // creating an XML xPath to be used in querying the discovered xml file                  
                    String voteXPathQuery="//node/vote/text()"; // an xPath Query for the value of vote element
                    XPathExpression voteXPathExpression = xpath.compile(voteXPathQuery); // compiling an xPath Expression for the vote element
                    String gigNameXPathQuery="//node/gig[@url]/text()"; // an xPath Query for the value of the gig element 
                    XPathExpression gigNameXPathExpression = xpath.compile(gigNameXPathQuery); // compiling an xPath Expression for the gig element
                    String gigURLXPathQuery="//node/gig[@url]"; // an xpath Query for the url attribute of the gig element
                    XPathExpression gigURLXPathExpression = xpath.compile(gigURLXPathQuery); // compiling an xPath Expression for the value of the url attribute of the gig element
                    String gigDateXPathQuery="//node/date/text()"; // an xPath Query for the value of the date element - NB to be discovered in another xml different from above
                    XPathExpression gigDateXPathExpression = xpath.compile(gigDateXPathQuery); // compiling an xPath Expression for the value of the date element
                    Object voteResult =  voteXPathExpression.evaluate(document, XPathConstants.NODESET); // extracting the votes into nodes
                    Object gigNamesResult =  gigNameXPathExpression.evaluate(document, XPathConstants.NODESET); // extracting the gig names into nodes
                    Object gigURLsResult =  gigURLXPathExpression.evaluate(document, XPathConstants.NODESET);  // extracting the gig urls into nodes
                    NodeList votes = (NodeList) voteResult; // extracting the votes from the node vote node
                    NodeList gigNames = (NodeList) gigNamesResult; // extracting the gig names from the gig name node
                    NodeList gigURLs = (NodeList) gigURLsResult; // extracting the gig urls from the gig urls node
                    
                    String filename = discoveredLocalURL.getFile();                    
                    String discoveredUsername = filename.substring(0, filename.lastIndexOf("/"));
                    String user = discoveredUsername.substring(discoveredUsername.lastIndexOf("/")+1); //extracting the username from the url's file name                   
                    GigUsers gigUser = new GigUsers(user); 
                    String gigName = null;
                    String gigDate = null;
                    URL gigURL = null;
                    Integer gigVote = null;                                        
                    for (int i=0; i<gigNames.getLength(); i++){                        
                        NamedNodeMap nodemap = (NamedNodeMap) gigURLs.item(i).getAttributes();                        
                        Node urlNode = (Node) nodemap.item(0);                        
                        try {
                            gigName = gigNames.item(i).getNodeValue(); // extracting the name of the gig
                            gigURL = new URL(discoveredLocalURL, urlNode.getNodeValue()); // creating the gig's URL
                            gigVote = new Integer(votes.item(i).getNodeValue()); //extracting the vote
                            //to add the gig URL to all gigs
                            gigsURL.add(gigURL); //creating a list of all gigs available in the dictionary                      
                            // to now get the gig date from the gig's XML Description file
                            String gigURLXMLDescription = gigURL.toString() +"/xml";                         
                            URL gigXMLURL = new URL(gigURLXMLDescription);
                            URLConnection gigXMLConn=gigXMLURL.openConnection(); // another connection for obtaining the date of the gig
                            DocumentBuilder gigXMLDocBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
                            Document gigXMLDocument = gigXMLDocBuilder.parse(gigXMLConn.getInputStream());
                            Object dateResult =  gigDateXPathExpression.evaluate(gigXMLDocument, XPathConstants.NODESET); 
                            NodeList dates = (NodeList) dateResult;                            
                            gigDate = dates.item(0).getNodeValue();  //extracting the date 
                            //creating a dictionary entry for the current user
                            gigUser.addUsersVoteEntry(gigURL, gigName, gigDate, gigVote); //adding the extracted information into the gigUser object
                        }catch(Exception exp){ // user does not have a complete entry in the xml file
                            //this user could not be added !!!
                            exp.printStackTrace();
                        }                        
                    }
                    //POPULATING THE DICTIONARY WITH VALID DICTIONARY ENTRY
                    if (user.equalsIgnoreCase(gigUser.getUsername()) 
                            && gigUser.getGigNameList().size()>0
                            && gigUser.getGigDateList().size()>0
                            && gigUser.getGigURLList().size()>0
                            && gigUser.getGigVoteList().size()>0
                            && gigUser.getGigNameList().size()==gigUser.getGigURLList().size()
                            && gigUser.getGigURLList().size()==gigUser.getGigDateList().size()
                            && gigUser.getGigDateList().size()==gigUser.getGigVoteList().size()) { //this dictionary entry is valid with upto date information
                        usersPreferenceDictionary.add(gigUser); // populating the dictionary with Objects of GigUsers                       
                    }                    
                }
                localURLEntry = bufferedReader.readLine();                
            }  //end of while loop to read another URL discovered during the crawling process.          
        }catch (Exception exp) {
            exp.printStackTrace();
        }
        return usersPreferenceDictionary;   //returning the dictionary back to the calling public method     
    }
    
    /**
     * <p>
     * The getPearsonSimilarityRating helper method that calculates the Pearson Correlation Score
     * for each user in the dictionary based on the vote they have provided for gigs in the past
     * This similarity rating rating is between the users and all other users who have rated similar 
     * gigs in the past.
     * </p>
     * <p>
     * The details of the algorithm used here can be found in 
     * <em>Chapter 2, pages 11, 12 13 of Programming Collective Intelligence, ISBN: 0-596-52932-5, Author: Toby Segaran , Publisher: O'Reilly.</em>
     * </p>
     * @param username - the given username whose rating will be compared with others who have voted on similar gigs
     * @param usersGigDictionary - the preference dictionary developed earlier
     * @return a hashmap where the key is the name of every user in the dictionary and the value is the pearson correlation score
     */    
    private HashMap<String, Double> getPearsonSimilarityRating(String username, List<GigUsers> usersGigDictionary) {
        HashMap<String, Double> pearsonSimilarityCoefficient = new HashMap<String, Double>();
        GigUsers givenUser = new GigUsers(username);
        for (GigUsers gigUser : usersGigDictionary){
            if (givenUser.getUsername().equalsIgnoreCase(gigUser.getUsername())) {
                givenUser = gigUser;                
                break;
            }
        }
        List<URL> givenUserURLList = givenUser.getGigURLList();
        List<Integer> givenUserVoteList = givenUser.getGigVoteList();
        //to turn given user preferences (gig & vote) into a HashMap to make searchng & retrieval easier
        HashMap<URL, Integer> givenUserPreference = new HashMap<URL, Integer>();
        for (int i=0; i<givenUserURLList.size(); i++){
            givenUserPreference.put(givenUserURLList.get(i), givenUserVoteList.get(i));
        }
        
        //to compare given user with the dictionary
        for (GigUsers gigUser : usersGigDictionary) { 
  
            int score1=0, score2=0, sum1=0, sum2=0, sum1sq=0, sum2sq=0, psum=0, n=0;
            double num=0, den=0, pCoefficient=0.0;          
            List<URL> gigURLList = gigUser.getGigURLList();
            List<Integer> gigVoteList = gigUser.getGigVoteList();            
            HashMap<URL, Integer> currentUserPreference = new HashMap<URL, Integer>();
            for (int i=0; i<gigURLList.size(); i++){
                currentUserPreference.put(gigURLList.get(i), gigVoteList.get(i));
            }
            //to now go through the list of all gigs where given user and current user in dictionary have provided votes
            for (URL k : givenUserPreference.keySet()) {                 
                if (currentUserPreference.containsKey(k)) {
                    //getting the vote for each user
                    score1=givenUserPreference.get(k);
                    score2=currentUserPreference.get(k);                    
                    //Adding up all the preferences
                    sum1+=score1;
                    sum2+=score2;                    
                    //Adding up all the squares
                    sum1sq+=Math.pow(score1,2);
                    sum2sq+=Math.pow(score2,2);                    
                    //Adding up the products
                    psum+=score1*score2;
                    n++;
                }                
            }         
            //to calculate the pearson correlation score
            if (n==0) {
                den=0;
            }else {
                num=psum-(sum1*sum2/n);
                den=Math.sqrt(((sum1sq-Math.pow(sum1,2)/n)) * ((sum2sq-Math.pow(sum2,2)/n)));
            }
            if (den==0) {
                pCoefficient=0;
            }else{
                pCoefficient = num/den;
            }           
            pearsonSimilarityCoefficient.put(gigUser.getUsername(), pCoefficient);
        } 
         return pearsonSimilarityCoefficient; // returning the scores store to the calling method
    }
    
    /**
     * <p>
     * The recommender helper method that calculates the list of recommendations for the given user
     * using the pearson correlation score and the list of gigs seen by the given users and others in the 
     * dictionary.
     * </p>
     * <p>
     * Further details of the recommender algorithm used can be found in <em>Chapter 2, pages 14, 15, and 16 of 
     * Programming Collective Intelligence, ISBN: 0-596-52932-5, Author: Toby Segaran , Publisher: O'Reilly.</em>
     * </p>
     * @param username the user for which recommendation is sought
     * @param gigUsers the users' preference dictionary
     * @param similarity the pearson correlation score calculated for each user in the dictionary
     * @return the top three gigs recommended for this user
     */
    private List<Gigs> getRecommendation(String username, List<GigUsers> gigUsers, HashMap<String, Double> similarity) {
        List<Gigs> gigsRecommended = new ArrayList<Gigs>();
        //to obtain the gigs seen already by the given user and removing them from the list of gigs to derive recommendations for
        HashSet<URL> gigsSeenByUser = new HashSet<URL>();
        for (GigUsers users : gigUsers) {
            if (username.equalsIgnoreCase(users.getUsername())) {               
                for (URL url : users.getGigURLList()) {
                    gigsSeenByUser.add(url); // adding gig's url to gigs already seen by the user
                }
                gigsURL.removeAll(gigsSeenByUser); // removing the gigs already seen by the given user from the list of gigs to be compared
                break;
            }
        }      
        
        for (URL gigURL : gigsURL) {          
            double total = 0d;
            double simTotal = 0d;
            double finalScore = 0d;
            String theGigName = null;
            String theGigDate = null;            
             for (GigUsers users : gigUsers) {
                if (username.equalsIgnoreCase(users.getUsername())) {
                   continue;
                }   
                Double sim = similarity.get(users.getUsername());
                if (sim<=0){
                    continue;
                }                
                //similarity * score
                //total = similarity coefficient with the other person * votes of the other person on this particular gig
                List<URL> gigs = users.getGigURLList();
                List<Integer> votes = users.getGigVoteList();
                List<String> names = users.getGigNameList();
                List<String> dates = users.getGigDateList();
                for (int k=0; k<gigs.size(); k++){
                    if (gigURL.equals(gigs.get(k))){
                        total+=similarity.get(users.getUsername()) * votes.get(k);
                        simTotal+=similarity.get(users.getUsername());
                        theGigName = names.get(k);
                        theGigDate = dates.get(k);
                        break;                        
                    }
                }                
            }
            finalScore = total/simTotal;
            gigsRecommended.add(new Gigs(theGigName, theGigDate, gigURL, finalScore)); //creating the gig object and adding to the recommended list                      
        }        
        // to now sort the gigs to get the ordered list of recommendations             
        Collections.sort(gigsRecommended);
        //to reverse the order of sorting so we get the highest to lowest
        Collections.reverse(gigsRecommended);  
        //to return the top three gigs
        if (gigsRecommended.size()>3){
            gigsRecommended = gigsRecommended.subList(0, 3);
        }
        return gigsRecommended;
    } 
}
