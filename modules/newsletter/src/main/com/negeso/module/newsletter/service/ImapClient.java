package com.negeso.module.newsletter.service;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.mail.Address;
import javax.mail.Authenticator;
import javax.mail.Folder;
import javax.mail.Header;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Flags.Flag;

import org.apache.log4j.Logger;

import com.negeso.framework.Env;
import com.negeso.module.newsletter.Configuration;

public class ImapClient{
	
	private static final Logger logger = Logger.getLogger(ImapClient.class);
	
	private static int MAX_NUMBER_READ_LINES = 10;//per 512b
	
	private Properties connectionProperties;
	
	public ImapClient() {

//		java.security.Security.setProperty( "ssl.SocketFactory.provider",
//		"com.negeso.module.newsletter.service.DummySSLSocketFactory");

//		java.security.Provider provider = new com.sun.net.ssl.internal.ssl.Provider();
//		java.security.Security.addProvider(provider);
		
		connectionProperties = new Properties();
        connectionProperties.putAll(System.getProperties());
        
        connectionProperties.put(Configuration.IMAP_HOST_PROPERTY,
				Configuration.getIMAPHost());
		connectionProperties.put(Configuration.IMAP_PORT_PROPERTY,
				Configuration.getIMAPPort());
		
		connectionProperties.setProperty("mail.imaps.socketFactory.class", 
				"com.negeso.module.newsletter.service.DummySSLSocketFactory");
		
		connectionProperties.setProperty("mail.debug", "true");
	}
	
	private boolean createTempFolder(){
		File folder = new File(Env.getRealPath(MailBounceService.MAIL_CACHE_PATH));

		return !(!folder.exists() && !folder.mkdirs());
	}
    
	public void archiveMails(){
    	logger.debug("+");
    	
    	if (!createTempFolder()){
    		logger.error("- cron interrupted, reason: statistics folder cannot be created");
			return;
		}
    	
    	Authenticator auth = new MyAuth(Configuration.getIMAPUser(), Configuration.getIMAPPass());
    	
        Session session=Session.getInstance(connectionProperties, auth);
        
		Folder folder = null;
		Store store = null;
        try{
            store = session.getStore(Configuration.IMAP_STORE);
            store.connect();
            folder=store.getFolder(Configuration.getImapsFolder());
            folder.open(Folder.READ_WRITE);
            Message[] m = folder.getMessages();
            if (folder.getMessageCount() > 0){
	            m = folder.getMessages();
	            for (int i=0, n=m.length; i<n; i++){
	            	if (isBouncedMail(m[i])){
	            		archiveMail(m[i]);
	            		m[i].setFlag(Flag.DELETED, true);
	            	}
	            }
            }
        }catch(Exception e){
        	logger.error("imap server connection error: "
        			 + e.getMessage(), e);
        }finally{
        	try {
        		folder.close(true);
				store.close();
			} catch (MessagingException e1) {
				logger.error("unexpected error");
			}
        }
        logger.debug("-");
    }
    
    private boolean isBouncedMail(Message message) throws Exception{
    	if (message.getSubject().trim().toLowerCase().contains(
    			Configuration.getBouncedSubject()) ||
    			isFromAddressBounced(message.getFrom())){
    		return true;
    	}
		return false;
    }
    
    @SuppressWarnings("unchecked")
	private void archiveMail(Message m) throws Exception{
    	
    	File f = new File(Env.getRealPath(MailBounceService.MAIL_CACHE_PATH + "/" + 
    			Env.formatRoundDate(new Date()) + "-" + 
    			new Date().getTime() % 1000 + "-" + m.getSubject().substring(0, 20)));

    	FileOutputStream fd = new FileOutputStream(f);
    	InputStream isr = new DataInputStream(m.getInputStream());

    	try{
	    	for (Map.Entry entry : (Set<Map.Entry>)getMessageProperies(m).entrySet())
				fd.write((entry.getKey() + " : " + entry.getValue() + "\n").getBytes());
	    	int counter = 0;
	    	while (counter++ < MAX_NUMBER_READ_LINES){
	    		byte[] b = new byte[512];
		    	isr.read(b);
		    	fd.write(b);
	    	}
    	}catch(Exception e){
    		logger.error(e.getMessage());
    	} finally{
    		fd.close();
    		isr.close();
    	}
    }
    
    private boolean isFromAddressBounced(Address[] addresses){
    	for (int i = 0; i < addresses.length; i++){
    		if (addresses[i].toString().toLowerCase().trim().contains(
    				Configuration.getMailDaemonAddress()))
    			return true;
    	}
		return false;
    }
    
    @SuppressWarnings("unchecked")
	private Map getMessageProperies(Message message) throws Exception{
    	Map<String, Object> properties = new HashMap<String, Object>();
    	Enumeration<Header> en = message.getAllHeaders();
    	while(en.hasMoreElements()){
    		Header h = en.nextElement();
    		properties.put(h.getName(), h.getValue());
    	}
    	return properties;
    }

    private class MyAuth extends Authenticator {
        private String username, password;

        MyAuth(String name, String passwd) {
            username = name;
            password = passwd;
        }

        protected PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(username, password);
        }
    }
}
