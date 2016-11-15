package environment;
 
import java.io.IOException;
import java.util.Properties;

import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.search.BodyTerm;
import javax.mail.search.SearchTerm;
import javax.mail.search.SubjectTerm;

import common.Log;
 
/**
 * Backend Mail Manipulation
 * @author Administrator
 *
 */
public class Mail {
	
	final static String port = "993";
	public static Folder folderInbox = null;
	public static Store store = null;
	
	
	/**
	 * Connection establishment 
	 * @param host
	 * @param userName
	 * @param password
	 * @return
	 */
	
	public static Folder mailConfig(String host, String userName, String password) {
		
		Properties properties = new Properties();
	    // server setting
        properties.put("mail.imap.host", host);
        properties.put("mail.imap.port", port);
 
        // SSL setting
        properties.setProperty("mail.imap.socketFactory.class",
                "javax.net.ssl.SSLSocketFactory");
        properties.setProperty("mail.imap.socketFactory.fallback", "false");
        properties.setProperty("mail.imap.socketFactory.port",
                String.valueOf(port));
 
        Session session = Session.getDefaultInstance(properties);
    
        try {
            // connects to the message store
            store = session.getStore("imap");
            store.connect(userName, password);
 
            // opens the inbox folder
            folderInbox = store.getFolder("INBOX");
            folderInbox.open(Folder.READ_WRITE);
 
        } catch (NoSuchProviderException ex) {
            Log.info("No provider.");
            ex.printStackTrace();
        } catch (MessagingException ex) {
            Log.info("Could not connect to the message store.");
            ex.printStackTrace();
        }
        
        
		return folderInbox;
		
		
		
	}
	
	
	/**
	 * Wait for the specified seconds
	 * @param integer Value
	 * @throws InterruptedException
	 */
	public static void wait(int i) {
		Log.info("Waiting for time: " + i + " seconds ");
		try {
			java.util.concurrent.TimeUnit.SECONDS.sleep(i);
		} catch (InterruptedException e) {
			Log.error(e.getMessage());
		}
	}

	
	
   /**
     * Search an Email with particular subject and body
     * @throws MessagingException 
     * @throws IOException 
     */
    public static boolean searchEmail(String subject,String body) throws MessagingException, IOException {
    	
        // performs search through the folder
    	SearchTerm subjectTerm   = new SubjectTerm(subject);
        SearchTerm bodyTerm      = new BodyTerm(body);
        Message[] subjectMessage = folderInbox.search(subjectTerm);
        Message[] bodyMessage    = folderInbox.search(bodyTerm);
        
        Integer resultSubject = subjectMessage.length;
        Integer resultBody    = bodyMessage.length;
        
        if((resultSubject > 0) && (resultBody > 0)) {
        	return true;
        }else{
        	return false;
        }
        
           
    }
    
    
    /**
     * Search an Email with particular subject
     * @throws MessagingException 
     * @throws IOException 
     */
    public static boolean searchEmail(String subject) throws MessagingException, IOException {
    	
        // performs search through the folder
    	SearchTerm term   = new SubjectTerm(subject);
    	Message[] message = folderInbox.search(term);
    	
    	if (message.length == 0) {
    		return false;
    	}else{
    		return true;
    	}
    	
	}
    
    
   /**
    * Get subject from the given mail
    * @param host
    * @param userName
    * @param password
    * @param keyword
    * @return subject
    * @throws MessagingException
    */
    public static String getSubject() throws MessagingException {
    	
        // performs search through the folder
        int messageCount = folderInbox.getMessageCount();
        Message message = folderInbox.getMessage(messageCount);
        String result = message.getSubject();
        return result;
           
    }
    
    

    /**
     * Get Body Content from the given mail
     * @return body result
     * @throws MessagingException
     * @throws IOException 
     */
     public static String getBody() throws MessagingException, IOException {
     	
         // Gets body part of the email
         int messageCount = folderInbox.getMessageCount();
         Message message = folderInbox.getMessage(messageCount);
         String result = message.getContent().toString();
         return result;
            
     }
     
     
     public static void deleteMessage() throws MessagingException{
    	 
    	 int messageCount = folderInbox.getMessageCount();
    	 Message message = folderInbox.getMessage(messageCount);
         message.setFlag(Flags.Flag.DELETED, true);
     }
     
     
     /**
     * @throws MessagingException 
      * 
      */
     public void closeMail() throws MessagingException {
    	 folderInbox.close(false);
    	 store.close();
     }
 
}