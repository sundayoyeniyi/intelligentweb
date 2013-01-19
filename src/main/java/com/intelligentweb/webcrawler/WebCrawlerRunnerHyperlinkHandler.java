/*
 * @(#)WebCrawlerRunnerHyperlinkhandler.java 20-May-2009
 * 
 * Copyright (c) 2008 - 2009
 * 
 * The Software was written as part of COM6685 Intelligent Web Assignment
 * University of Sheffield, MSC, SSIT, 2008 - 2009.
 */

package com.intelligentweb.webcrawler;
import javax.swing.JEditorPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLFrameHyperlinkEvent;

/**
 * <p>
 * The WebCrawlerRunnerHyperlinkHandler is a helper class for the GUI that is used to run
 * the crawler.  It's  purpose is just to display the seed URL to be
 * crawled properly on the testing GUI before the crawling exericse starts
 * </p>
 * <p>
 * It is the implementation for the Sun's Hyperlink Listener Interface.
 * It's  purpose it to handle hyperlinks in HTML documents that are displayed inside the Web Index Browser.
 * It handles the process of opening a hypertext link that is displayed inside
 * a JEditorPane component and then open the hyperlinked document/page/URL.
 * JEditorPane has been used to display the URL and a HyperlinkListener has been added to it
 * to update the page when a link inside the browser is clicked.
 * </p>
 * <p>
 * It is adapted from Sun Micro System Java API Documentation interface definition
 * for adding a HyperlinkListener to a Swing Component.  For further details of the Sun's 
 * HyperlinkListener interface please see 
 * <a href="http://java.sun.com/j2se/1.5.0/docs/api/javax/swing/event/HyperlinkListener.html"> Hyperlink Listener Interface </a>
 * </p>
 * <p>
 * The implementation of the hyperlinkUpdate method has been adapted from Sun Java API Documentation.
 * For further details of Sun's implementation for a hyperlink listener interface, please see
 * <a href="http://java.sun.com/j2se/1.5.0/docs/api/javax/swing/JEditorPane.html#addHyperlinkListener(javax.swing.event.HyperlinkListener)"> 
 * JEditorPane and HyperlinkListener </a>
 * </p>
 * @author sunday oyeniyi acp08sjo@sheffield.ac.uk
 * @author manoj mathew joseph acp08mjm@sheffield.ac.uk
 */
class WebCrawlerRunnerHyperlinkHandler implements HyperlinkListener {
    
    /**
     * The constructor for the HyperlinkHandler
     */
    
     WebCrawlerRunnerHyperlinkHandler(){
     }
     
     /**
      * the implementation of the hyperlinkUpdate method which handles the hyperlink event
      * @param e an event that is triggered when a hyperlink on an html is clicked.
      */
 
     public void hyperlinkUpdate(HyperlinkEvent e) {
         if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
             JEditorPane pane = (JEditorPane) e.getSource();
             if (e instanceof HTMLFrameHyperlinkEvent) {
                 HTMLFrameHyperlinkEvent  evt = (HTMLFrameHyperlinkEvent)e;
                 HTMLDocument doc = (HTMLDocument)pane.getDocument();
                 doc.processHTMLFrameHyperlinkEvent(evt);
             } else {
                 try {
                     pane.setPage(e.getURL());                         
                  } catch (Throwable t) {
                     t.printStackTrace();
                 } finally {
                     //
                 }
             }
         }
     }
}
