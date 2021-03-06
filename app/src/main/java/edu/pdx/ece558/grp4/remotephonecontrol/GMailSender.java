package edu.pdx.ece558.grp4.remotephonecontrol;

//Adapted from https://medium.com/@ssaurel/how-to-send-an-email-with-javamail-api-in-android-2fc405441079

/////////////////////
// Android Imports //
/////////////////////

import java.security.Security;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;

import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

/////////////////
// GMailSender //
/////////////////

public class GMailSender extends javax.mail.Authenticator {

    // Private members
    private String mailhost = "smtp.gmail.com";
    private String user;
    private String password;
    private Session session;

    static {
        Security.addProvider(new JSSEProvider());
    }

    /////////////////
    // Constructor //
    /////////////////

    public GMailSender(String user, String password) {
        this.user = user;
        this.password = password;

        Properties props = new Properties();
        props.setProperty("mail.transport.protocol", "smtp");
        props.setProperty("mail.host", mailhost);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.socketFactory.fallback", "false");
        props.setProperty("mail.smtp.quitwait", "false");

        session = Session.getDefaultInstance(props, this);

    } // Constructor

    ///////////////////////////////
    // getPasswordAuthentication //
    ///////////////////////////////

    protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(user, password);
    } // getPasswordAuthentication

    //////////////
    // sendMail //
    //////////////

    public synchronized void sendMail(String subject, String body,
                                      String sender, String recipients, String filename) throws Exception {
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(sender));
        if (recipients.indexOf(',') > 0)
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipients));
        else
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(recipients));
        message.setSubject(subject);
        // Create the message part
        BodyPart messageBodyPart = new MimeBodyPart();
        messageBodyPart.setText(body);
        // Create a multipart message
        Multipart multipart = new MimeMultipart();
        // Set text message part
        multipart.addBodyPart(messageBodyPart);

        if (filename != null) {
            // Part two is attachment
            messageBodyPart = new MimeBodyPart();
            DataSource source = new FileDataSource(filename);
            messageBodyPart.setDataHandler(new DataHandler(source));
            messageBodyPart.setFileName(filename);
            multipart.addBodyPart(messageBodyPart);
        }
        // Include all message parts
        message.setContent(multipart);
        // Send message
        Transport.send(message);

    } // sendMail

} // GMailSender