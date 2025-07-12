package com.example;
import java.util.Properties;

import jakarta.activation.DataHandler;
import jakarta.activation.DataSource;
import jakarta.activation.FileDataSource;
import jakarta.mail.*;
import jakarta.mail.internet.*;

public class Main {
    public static void main(String[] args) {
        final String senderEmail = "mnddrsddq@gmail.com";
        /**
         * 
         * create a password from https://myaccount.google.com/apppasswords
         * add a custom app name like "MailMailMail"
         * and use that generated password in the code
         * 
         *  */ 
        final String senderPassw = "generated-password-here";
        final String recieverEmails = "reciepient1@gmail.com, reciepient2@gmail.com, reciepient3@gmail.com";

            // smtp server settings of gmail
            Properties props = new Properties();
            props.put("mail.smtp.auth", true);
            props.put("mail.smtp.starttls.enable", true);
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.port", "587");
            
            // create session
            Session session = Session.getInstance(props, new Authenticator(){
                protected PasswordAuthentication getPasswordAuthentication(){
                    return new PasswordAuthentication(senderEmail, senderPassw);
                }
            });

        try {
            // create message
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(senderEmail));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recieverEmails));
            message.setSubject("Trying out jakarta mail");

            // message.setText("Hello, buddy! This is setText");
            // message.setContent("<div style='color:yellow;background-color:red'>"+
            // "<h1>Hello, buddy!</h1>"+
            // "<p>This is setContent</p>"+
            // "</div>", "text/html");

            // to send images/pdfs use MimeBodyPart
            // you can use multiple parts also

            // html part
            MimeBodyPart htmlPart = new MimeBodyPart();
            htmlPart.setContent("<div style='color:yellow;background-color:red'>" +
                                "<h1>Hello, buddy!</h1>" +
                                "<p>This is setContent</p>" +
                                "<img src='cid:myImage'>"+
                                "</div>", "text/html");

            // image part
            MimeBodyPart imagePart = new MimeBodyPart();
            DataSource ds = new FileDataSource("/home/amin/Pictures/screenshot.png");
            imagePart.setDataHandler(new DataHandler(ds));
            imagePart.setHeader("Content-ID", "<myImage>");
            imagePart.setFileName("yo.png");
            imagePart.setDisposition(MimeBodyPart.INLINE);
            
            // pdf part
            MimeBodyPart pdfPart = new MimeBodyPart();
            DataSource ds2 = new FileDataSource("/home/amin/resume.pdf");
            pdfPart.setDataHandler(new DataHandler(ds2));
            pdfPart.setFileName("resume.pdf");
            pdfPart.setDisposition(MimeBodyPart.ATTACHMENT);

            // mimemultipart to combine the parts
            MimeMultipart related = new MimeMultipart("related");
            related.addBodyPart(htmlPart);
            related.addBodyPart(imagePart);

            // wrap the related part into a body part for mixing with attachment
            MimeBodyPart contentBodyPart = new MimeBodyPart();
            contentBodyPart.setContent(related);

            // contentBodyPart + attachment
            MimeMultipart mixed = new MimeMultipart("mixed");
            mixed.addBodyPart(contentBodyPart);
            mixed.addBodyPart(pdfPart);  

            message.setContent(mixed);

            Transport.send(message);
            
            System.out.println("[OK] Email sent successfully!");
        } catch (Exception e) {
            System.err.println("[NOT OK] Error!");
            e.printStackTrace();
        }
    }
}