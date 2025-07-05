package com.spring.boot.bot;

import com.spring.boot.PdfUtil;
import com.spring.boot.WordUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
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

public class CvBot extends TelegramLongPollingBot {

        Map<Long, CvData> userData = new HashMap<>();
    private static final Logger logger = LogManager.getLogger(CvBot.class);


    String[] questions = {
            "Please enter your full name:\n(e.g. Abdulrahman Hussein) (or type 'skip')",
            "Enter your profession: \n(e.g. Java Developer)(or type 'skip')",
            "Enter your email address:\n(e.g. abdo.hussien@gmail.com) (or type 'skip')",
            "Enter your linkedin link:\n(e.g. linkedin.com/in/abdurhman-hussien0/)(or type 'skip')",
            "Enter your phone number:\n(e.g. +201045246746) (or type 'skip')",
            "Write a professional summary:\n(e.g. Junior Java Backend Developer with experience in Spring Boot, REST APIs, Oracle DB...)(or type 'skip')",
            "Enter your education:\n(e.g. Bachelor's Degree in Mechatronics Engineering, Cairo University - GPA: 2.93/4)(or type 'skip')",
            "Enter the date of education (optional):\n(e.g. Sep 2020 – Jun 2024)",
            "Enter your work experience:\n(e.g. Java Developer Intern – EraaSoft)(or type 'skip')",
            "Enter the date of experience (optional):\n(e.g. Dec 2024 – July 2025)",
            "List your projects:\n(e.g. Restaurant Web App, Employee Management System...)(or type 'skip')",
            "List your technical skills:\n(e.g. Java, Spring Boot, REST APIs...)(or type 'skip')",
            "List your soft skills:\n(e.g. Teamwork, Communication...)(or type 'skip')",
            "Mention any courses or certifications:\n(e.g. Java Concurrency – Udemy...)(or type 'skip')",
            "Languages you speak:\n(e.g. Arabic: Native, English: B2)(or type 'skip')",
            "Military status:\n(e.g. Completed)(or type 'skip')"
    };

    @Override
    public void onUpdateReceived(Update update) {
        if (!update.hasMessage() || !update.getMessage().hasText()) return;

        Message msg = update.getMessage();
        Long userId = msg.getFrom().getId();
        String text = msg.getText();
        String userIdentifier = msg.getFrom().getFirstName();
        if (msg.getFrom().getUserName() != null && !msg.getFrom().getUserName().isEmpty()) {
            userIdentifier += " (@" + msg.getFrom().getUserName() + ")";
        } else {
            userIdentifier += " (ID: " + userId + ")";
        }

        CvData data = userData.getOrDefault(userId, new CvData());
        userData.put(userId, data);

        if (text.equals("/start")) {
            data.stage = 0;
            sendText(msg, questions[data.stage]);
            logger.info("User {} started the CV generation process.", userIdentifier);
            return;
        }

        String answer = text.trim();
        switch (data.stage) {
            case 0: if (!answer.equalsIgnoreCase("skip")) data.name = answer; break;
            case 1: if (!answer.equalsIgnoreCase("skip")) data.profession = answer; break;
            case 2: if (!answer.equalsIgnoreCase("skip")) data.email = answer; break;
            case 3: if (!answer.equalsIgnoreCase("skip")) data.linkedin = answer; break;
            case 4: if (!answer.equalsIgnoreCase("skip")) data.phone = answer; break;
            case 5: if (!answer.equalsIgnoreCase("skip")) data.summary = answer; break;
            case 6: if (!answer.equalsIgnoreCase("skip")) data.education = answer; break;
            case 7: if (!answer.equalsIgnoreCase("skip")) data.educationDate = answer; break;
            case 8: if (!answer.equalsIgnoreCase("skip")) data.experience = answer; break;
            case 9: if (!answer.equalsIgnoreCase("skip")) data.experienceDate = answer; break;
            case 10: if (!answer.equalsIgnoreCase("skip")) data.projects = answer; break;
            case 11: if (!answer.equalsIgnoreCase("skip")) data.skills = answer; break;
            case 12: if (!answer.equalsIgnoreCase("skip")) data.softSkills = answer; break;
            case 13: if (!answer.equalsIgnoreCase("skip")) data.courses = answer; break;
            case 14: if (!answer.equalsIgnoreCase("skip")) data.languages = answer; break;
            case 15: if (!answer.equalsIgnoreCase("skip")) data.military = answer; break;
        }

        data.stage++;

        if (data.stage < questions.length) {
            sendText(msg, questions[data.stage]);
        } else {
            sendText(msg, "Generating your CV...");

            try {
                String fileBaseName = data.name != null ? data.name.replaceAll(" ", "_") : "Unknown";

                byte[] pdfBytes = PdfUtil.createSimpleCv(
                        data.name, data.profession, data.email, data.linkedin, data.phone, data.summary,
                        data.education, data.educationDate,
                        data.experience, data.experienceDate,
                        data.projects, data.skills, data.softSkills,
                        data.courses, data.languages, data.military
                );

                sendPdf(msg, pdfBytes, fileBaseName + "_CV.pdf");

                byte[] wordBytes = WordUtil.createSimpleCvWord(
                        data.name, data.profession, data.email, data.linkedin, data.phone, data.summary,
                        data.education, data.educationDate,
                        data.experience, data.experienceDate,
                        data.projects, data.skills, data.softSkills,
                        data.courses, data.languages, data.military
                );

                sendWord(msg, wordBytes, fileBaseName + "_CV.docx");
                logger.info("Successfully generated CV for user {} (Name in CV: {}).", userIdentifier, data.name);
            } catch (IOException e) {
                System.out.println(e.getMessage());
                sendText(msg, "Sorry, an error occurred while generating your CV.");
            } finally {
                userData.remove(userId);
            }
        }
    }

    private void sendText(Message msg, String text) {
        SendMessage sm = new SendMessage();
        sm.setChatId(msg.getChatId().toString());
        sm.setText(text);
        try {
            execute(sm);
        } catch (TelegramApiException e) {
            System.out.println(e.getMessage());
        }
    }

    private void sendPdf(Message msg, byte[] fileData, String fileName) {
        SendDocument doc = new SendDocument();
        doc.setChatId(msg.getChatId().toString());
        doc.setDocument(new InputFile(new ByteArrayInputStream(fileData), fileName));
        doc.setCaption("Here's your CV in PDF format.");
        try {
            execute(doc);
        } catch (TelegramApiException e) {
            System.out.println(e.getMessage());
        }
    }

    private void sendWord(Message msg, byte[] fileData, String fileName) {
        SendDocument doc = new SendDocument();
        doc.setChatId(msg.getChatId().toString());
        doc.setDocument(new InputFile(new ByteArrayInputStream(fileData), fileName));
        doc.setCaption("Here's your CV in Word format.");
        try {
            execute(doc);
        } catch (TelegramApiException e) {
            System.out.println(e.getMessage());
        }
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