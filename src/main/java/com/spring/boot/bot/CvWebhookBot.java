package com.spring.boot.bot;

import com.spring.boot.PdfUtil;
import com.spring.boot.WordUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class CvWebhookBot extends TelegramWebhookBot {

    Map<Long, CvData> userData = new HashMap<>();
    private static final Logger logger = LogManager.getLogger(CvWebhookBot.class);

    String[] questions = {
            "Please enter your full name:\n(e.g. Abdulrahman Hussein) (or type 'skip')",
            "Enter your profession: \n(e.g. Java Developer)(or type 'skip')",
            "Enter your email address:\n(e.g. abdo.hussien@gmail.com) (or type 'skip')",
            "Enter your linkedin link:\n(e.g. linkedin.com/in/abdurhman-hussien0/)(or type 'skip')",
            "Enter your phone number:\n(e.g. +201045246746) (or type 'skip')",
            "Write a professional summary:\n(e.g. Junior Java Backend Developer with experience in Spring Boot, REST APIs, Oracle DB...)(or type 'skip')",
            "Enter your education:\n(e.g. Bachelor's Degree in Mechatronics Engineering, Cairo University - GPA: 2.93/4)(or type 'skip')",
            "Enter your work experience:\n(e.g. Java Developer Intern – EraaSoft, Dec 2024 – July 2025...)(or type 'skip')",
            "List your projects(just headlines):\n(e.g. Restaurant Web App, Employee Management System, Online Store using JSP/Servlet)(or type 'skip')",
            "List your technical skills:\n(e.g. Java, Spring Boot, REST APIs, JPA, Oracle, Git, Docker, Angular Basics...)(or type 'skip')",
            "List your soft skills:\n(e.g. Problem-solving, Fast learner, Teamwork, Communication...)(or type 'skip')",
            "Mention any courses or certifications:\n(e.g. Spring Boot & Security – Udemy, Java Concurrency – Udemy...)(or type 'skip')",
            "Languages you speak:\n(e.g. Arabic: Native, English: B2)(or type 'skip')",
            "Military status:\n(e.g. Completed)(or type 'skip')"
    };

    @Override
    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
        if (!update.hasMessage() || !update.getMessage().hasText()) return null;

        Message msg = update.getMessage();
        Long userId = msg.getFrom().getId();
        String text = msg.getText();
        String userIdentifier = msg.getFrom().getFirstName();
        if (msg.getFrom().getUserName() != null && !msg.getFrom().getUserName().isEmpty()) {
            userIdentifier += " (@" + msg.getFrom().getUserName() + ")";
            System.out.println(userIdentifier);
        } else {
            userIdentifier += " (ID: " + userId + ")";
            System.out.println(userIdentifier);
        }

        CvData data = userData.getOrDefault(userId, new CvData());
        userData.put(userId, data);

        SendMessage replyMessage = new SendMessage();
        replyMessage.setChatId(msg.getChatId().toString());

        if (text.equals("/start")) {
            data.stage = 0;
            userData.put(userId, new CvData());
            replyMessage.setText(questions[data.stage]);
            logger.info("User {} started the CV generation process.", userIdentifier);
            System.out.println("started the CV generation process"+ userIdentifier);

            return replyMessage;
        }

        switchMethod(text, data);

        data.stage++;

        if (data.stage < questions.length) {
            replyMessage.setText(questions[data.stage]);

        } else {
            replyMessage.setText("Generating your CV in both PDF and Word formats...");
            try {
                execute(replyMessage);
            } catch (TelegramApiException e) {
                System.out.println(e.getMessage());
            }

            try {
                String cvUserName = data.name != null && !data.name.isEmpty() ? data.name : "Unknown_User";

                String fileNameBase = data.name != null && !data.name.isEmpty() ?
                        data.name.replaceAll("[^a-zA-Z0-9.-]", "_") : "Unknown";


                byte[] pdfBytes = PdfUtil.createSimpleCv(
                        data.name, data.profession, data.email, data.linkedin, data.phone, data.summary,
                        data.education, data.experience, data.projects,
                        data.skills, data.softSkills, data.courses,
                        data.languages, data.military
                );
                sendDocument(msg.getChatId().toString(), pdfBytes, "CV_" + fileNameBase + ".pdf", "Here's your CV in PDF format.");

                byte[] wordBytes = WordUtil.createSimpleCvWord(
                        data.name, data.profession, data.email, data.linkedin, data.phone, data.summary,
                        data.education, data.experience, data.projects,
                        data.skills, data.softSkills, data.courses,
                        data.languages, data.military
                );
                sendDocument(msg.getChatId().toString(), wordBytes, "CV_" + fileNameBase + ".docx", "Here's your CV in Word (DOCX) format, which you can edit.");

                logger.info("Successfully generated CV for user {} (Name in CV: {}).", userIdentifier, cvUserName);
                System.out.println("Successfully generated CV for user"+ userIdentifier + " Name in CV" + cvUserName );
            } catch (IOException e) {
                System.out.println(e.getMessage());

                SendMessage errorReply = new SendMessage();
                errorReply.setChatId(msg.getChatId().toString());
                errorReply.setText("Sorry, an error occurred while generating your CV. Please try again.");
                try {
                    execute(errorReply);
                } catch (TelegramApiException e2) {
                    System.out.println(e2.getMessage());
                }
            } catch (TelegramApiException e) {
                System.out.println(e.getMessage());

                SendMessage errorReply = new SendMessage();
                errorReply.setChatId(msg.getChatId().toString());
                errorReply.setText("Sorry, an error occurred while sending your CVs. Please try again.");
                try {
                    execute(errorReply);
                } catch (TelegramApiException e2) {
                    System.out.println(e2.getMessage());
                }
            } finally {
                userData.remove(userId);
            }
            return null;
        }

        return replyMessage;
    }

    private void sendDocument(String chatId, byte[] fileData, String fileName, String caption) throws TelegramApiException {
        SendDocument doc = new SendDocument();
        doc.setChatId(chatId);
        doc.setDocument(new InputFile(new ByteArrayInputStream(fileData), fileName));
        doc.setCaption(caption);
        execute(doc);
    }

    public void switchMethod(String text, CvData data) {
        String answer = text.trim();
        switch (data.stage) {
            case 0:
                if (!answer.equalsIgnoreCase("skip")) data.name = answer;
                break;
            case 1:
                if (!answer.equalsIgnoreCase("skip")) data.profession = answer;
                break;
            case 2:
                if (!answer.equalsIgnoreCase("skip")) data.email = answer;
                break;
            case 3:
                if (!answer.equalsIgnoreCase("skip")) data.linkedin = answer;
                break;
            case 4:
                if (!answer.equalsIgnoreCase("skip")) data.phone = answer;
                break;
            case 5:
                if (!answer.equalsIgnoreCase("skip")) data.summary = answer;
                break;
            case 6:
                if (!answer.equalsIgnoreCase("skip")) data.education = answer;
                break;
            case 7:
                if (!answer.equalsIgnoreCase("skip")) data.experience = answer;
                break;
            case 8:
                if (!answer.equalsIgnoreCase("skip")) data.projects = answer;
                break;
            case 9:
                if (!answer.equalsIgnoreCase("skip")) data.skills = answer;
                break;
            case 10:
                if (!answer.equalsIgnoreCase("skip")) data.softSkills = answer;
                break;
            case 11:
                if (!answer.equalsIgnoreCase("skip")) data.courses = answer;
                break;
            case 12:
                if (!answer.equalsIgnoreCase("skip")) data.languages = answer;
                break;
            case 13:
                if (!answer.equalsIgnoreCase("skip")) data.military = answer;
                break;
        }
    }

    @Override
    public String getBotPath() {
        return "/webhook";
    }

    @Override
    public String getBotUsername() {
        return "CV_creator0_bot";
    }

    @Override
    public String getBotToken() {
        return System.getenv("BOT_TOKEN");
    }
}