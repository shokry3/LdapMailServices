package com.app.service.ldapUtils;

import java.io.IOException;

import java.util.*;

import javax.mail.*;
import javax.mail.internet.*;

public class MailUtil {
    public MailUtil() {
        super();
    }

    public static void main(String[] args) {
        
        Map<String, String> empData =  new LinkedHashMap<String, String>();
        empData.put("Employee Name","\u0645\u062d\u0645\u062f \u0639\u0644\u064a \u0634\u0643\u0631\u064a \u0634\u062d\u0627\u062a\u0647");
        empData.put("Employee Code", "9007050");
        empData.put("Domain Name", "MShehata");
        empData.put("Dir Name", "\u0627\u062f\u0627\u0631\u0629 \u0627\u0644\u0645\u0648\u0627\u0631\u062f \u0627\u0644\u0628\u0634\u0631\u064a\u0629");
        empData.put("Employee Job", "\u0645\u0628\u0631\u0645\u062c \u062d\u0627\u0633\u0628 \u0622\u0644\u064a");
        empData.put("Email Address", "MShehata@alriyadh.gov.sa");
        empData.put("AD Database", "TESTDB");
       //sendAdEmailError(empData);
       //sendAmanaWelcome("MShehata");
    }
    
    public static void sendEmail(String from, String to, String cc, String subject, String body, int authType, String username, String password){
        String[] toArray = new String[0];
        String[] ccArray = new String[0];
        if(to != null){
            toArray = to.split(";");
        }
        if(cc != null){
            ccArray = cc.split(";");
        }
            
        // Recipient's email ID needs to be mentioned.
        //String to = "mshehata@alriyadh.gov.sa";

        // Assuming you are sending email through relay.jangosmtp.net
        String host = "mail.alriyadh.gov.sa";

        Properties props = new Properties();
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", "25");
        //props.put("mail.mime.charset", "UTF-8");
        props.put("Content-Type", "text/html");

        // Get the Session object.
        Session session = null;
        if(authType == 1){
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            session = Session.getInstance(props,
               new javax.mail.Authenticator() {
                  protected PasswordAuthentication getPasswordAuthentication() {
                     return new PasswordAuthentication(username, password);
                 }
               }); 
        }else{
            session = Session.getDefaultInstance(props);
        }

        try {
            // Create a default MimeMessage object.
            MimeMessage message = new MimeMessage(session);

            // Set From: header field of the header.
            message.setFrom(new InternetAddress(from));

            // Set Email: RecipientType.To
            for(int i = 0; i<toArray.length;i++){
                message.addRecipient(Message.RecipientType.TO, new InternetAddress(toArray[i]));
            }
            
            // Set Email: RecipientType.CC
            for(int i = 0; i<ccArray.length;i++){
                message.addRecipient(Message.RecipientType.CC, new InternetAddress(ccArray[i]));
            }

            // Set Subject: header field
            message.setHeader("Content-Type", "text/html; charset=UTF-8");
            message.setSubject(subject, "UTF-8"); //message.setSubject("New user has been created");

            // Now set the actual message
            BodyPart messageBodyPart = new MimeBodyPart(); 
            messageBodyPart.setHeader("Content-Type","text/plain; charset=UTF-8"); 
            messageBodyPart.setContent(body.toString(), "text/html;charset=UTF-8");
            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart( messageBodyPart );
            message.setContent( multipart);
           // message.setContent(body, "text/html;");

            // Send message
            Transport.send(message);

        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    
    public static void sendAdAutocreationEmail(Map<String, String> empData){
        String htmlStart = "<!DOCTYPE html>\n" + 
        "<html>\n" + 
        "<head>\n" + 
        "<style>\n" + 
        "table, th, td {\n" + 
        "  border: 1px solid black;\n" + 
        "  border-collapse: collapse;\n" + 
        "}\n" + 
        "</style>\n" + 
        "<meta  charset=\"utf-8\"/>" +                 
        "</head>\n" + 
        "<body>\n" + 
        "\n" + 
        "<p  style=\"font-size:  1.3em;font-weight: bold;color: #167ec9;\">" + empData.get("Employee Note")+ "</p>\n" +
        "\n" + 
        "<table style=\"width:50%\">\n" + 
        "  <tr style=\"background-color: #a1a8ad;\">\n" + 
        "    <th colspan=\"2\">Employee Domain Details</th>\n" + 
        "  </tr>";
        
        String htmlBody = "";        
        for (Map.Entry<String, String> entry : empData.entrySet()) {
            htmlBody = htmlBody + "<tr>\n" + 
            "    <td style=\"font-weight: bold;background-color: #59b0eb;\">"+entry.getKey()+"</td>\n" + 
            "    <td style=\"text-align:center\">"+entry.getValue()+"</td>\n" + 
            "  </tr>";
        }
        
        String htmlEnd = "</table>\n" + 
        "</body>\n" + 
        "</html>";
        
        String htmlContent = htmlStart + htmlBody + htmlEnd;
        
        String from = "autocreation@alriyadh.gov.sa";
        String to = "thammudeh@alriyadh.gov.sa;mbaju@alriyadh.gov.sa;MShehata@alriyadh.gov.sa;sameht@alriyadh.gov.sa;intranet.support@alriyadh.gov.sa;WQASSEM@alriyadh.gov.sa";
        String cc = "aALkodede@alriyadh.gov.sa;fhamady@alriyadh.gov.sa";
        String subject = "Auto Creation Mail Service";
        sendEmail(from,to,cc,subject,htmlContent,0,null,null);
    }
    
    public static void sendAdEmailError(Map<String, String> empData){
        String htmlStart = "<!DOCTYPE html>\n" +  
        "<head>\n" + 
        "<style>\n" + 
        "table, th, td {\n" + 
        "  border: 1px solid black;\n" + 
        "  border-collapse: collapse;\n" + 
        "}\n" + 
        "</style>\n" + 
        "<meta  charset=\"utf-8\"/>" +                     
        "</head>\n" + 
        "<body>\n" + 
        "\n" + 
        "<p  style=\"font-size:  1.3em;font-weight: bold;color: red;\">Error/Exception in user creation on Active Directory:</p>\n" + 
        "\n" + 
        "<table style=\"width:50%\">\n" + 
        "  <tr style=\"background-color: #a1a8ad;\">\n" + 
        "    <th colspan=\"2\">Employee Details</th>\n" + 
        "  </tr>";
        
        String htmlBody = "";        
        for (Map.Entry<String, String> entry : empData.entrySet()) {
            htmlBody = htmlBody + "<tr>\n" + 
            "    <td style=\"font-weight: bold;background-color: #59b0eb;\">"+entry.getKey()+"</td>\n" + 
            "    <td style=\"text-align:center\">"+entry.getValue()+"</td>\n" + 
            "  </tr>";
        }
        
        String htmlEnd = "</table>\n" + 
        "</body>\n" + 
        "</html>";
        
        String htmlContent = htmlStart + htmlBody + htmlEnd;
        String from = "MShehata@alriyadh.gov.sa";
        String to = "MShehata@alriyadh.gov.sa;sameht@alriyadh.gov.sa;tswidan@alriyadh.gov.sa"; //String to = "MShehata@alriyadh.gov.sa;sameht@alriyadh.gov.sa";
        String subject = "Error in user Domain creation";
        sendEmail(from,to,null,subject,htmlContent,0,null,null);
    }
    
    public static void sendAmanaWelcome(String newDomainUser){
        String htmlStart = "<!DOCTYPE html>\n" +  
        "<head>\n" +  
        "<meta  charset=\"utf-8\"/>" +                     
        "</head>\n" + 
        "<body style=\"text-align: center;font-style: Arial;font-family: Arial;font-size: 25px;color:black; \">\n";
        
        String htmlBody = "<p>\u0645\u0631\u062D\u0628\u0627 \u0628\u0643\u0645 \u0628\u0627\u0645\u0627\u0646\u0629 \u0627\u0644\u0631\u064A\u0627\u0636</p>\n" + 
        "<p>\u062A\u0631\u062D\u0628 \u0628\u0643\u0645 \u0627\u0645\u0627\u0646\u0629 \u0645\u0646\u0637\u0642\u0629 \u0627\u0644\u0631\u064A\u0627\u0636 \u0648\u062A\u062A\u0645\u0646\u064A \u0644\u0643\u0645 \u0628\u062F\u0627\u064A\u0629 \u0645\u0648\u0641\u0642\u0629 </p>\n" + 
        "<p>\u0645\u0639 \u062A\u062D\u064A\u0627\u062A \u0627\u0645\u0627\u0646\u0629 \u0645\u0646\u0637\u0642\u0629 \u0627\u0644\u0631\u064A\u0627\u0636 </p>";
        
        String htmlEnd = "</body>\n" + "</html>";
        
        String htmlContent = htmlStart + htmlBody + htmlEnd;
        String from = "autocreation@alriyadh.gov.sa";
        String to = newDomainUser+"@alriyadh.gov.sa";
        String subject = "\u0645\u0631\u062D\u0628\u0627 \u0628\u0643\u0645 \u0628\u0627\u0645\u0627\u0646\u0629 \u0627\u0644\u0631\u064A\u0627\u0636";
        sendEmail(from,to,null,subject,htmlContent,0,null,null);
    }
    public static void sendAgencyWelcome(String newDomainUser){
        String htmlStart = "<!DOCTYPE html>\n" +  
        "<head>\n" +  
        "<meta  charset=\"utf-8\"/>" +                     
        "</head>\n" + 
        "<body style=\"text-align: center;font-style: Arial;font-family: Arial;font-size: 25px;color:black; \">\n";
        
        String htmlBody = "<p>\u0645\u0631\u062D\u0628\u0627 \u0628\u0643\u0645 \u0628\u0627\u0645\u0627\u0646\u0629 \u0645\u0646\u0637\u0642\u0629 \u0627\u0644\u0631\u064A\u0627\u0636</p>\n" + 
        "<p>\u062A\u0631\u062D\u0628 \u0628\u0643\u0645 \u0648\u0643\u0627\u0644\u0629 \u0627\u0644\u062A\u062D\u0648\u0644 \u0627\u0644\u0631\u0642\u0645\u064A \u2013 \u0627\u0645\u0627\u0646\u0629 \u0645\u0646\u0637\u0642\u0629 \u0627\u0644\u0631\u064A\u0627\u0636  \u0648\u062A\u062A\u0645\u0646\u064A \u0644\u0643\u0645 \u0628\u062F\u0627\u064A\u0629 \u0645\u0648\u0641\u0642\u0629 </p>\n" + 
        "<p>\u0644\u0644\u062F\u0639\u0645 \u0648\u0627\u0644\u062A\u0648\u0627\u0635\u0644 (esupport@alriyadh.gov.sa || 920003218) </p>\n" + 
        "<p>\u0645\u0639 \u062A\u062D\u064A\u0627\u062A \u0648\u0643\u0627\u0644\u0629 \u0627\u0644\u062A\u062D\u0648\u0644 \u0627\u0644\u0631\u0642\u0645\u064A</p>";
        
        String htmlEnd = "</body>\n" + "</html>";
        
        String htmlContent = htmlStart + htmlBody + htmlEnd;
        String from = "autocreation@alriyadh.gov.sa";
        String to = newDomainUser+"@alriyadh.gov.sa";
        String subject = "<p>\u0645\u0631\u062D\u0628\u0627 \u0628\u0643\u0645 \u0628\u0627\u0645\u0627\u0646\u0629 \u0645\u0646\u0637\u0642\u0629 \u0627\u0644\u0631\u064A\u0627\u0636</p>";
        sendEmail(from,to,null,subject,htmlContent,0,null,null);
    }
}
