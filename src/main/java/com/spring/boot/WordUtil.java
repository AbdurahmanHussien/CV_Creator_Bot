package com.spring.boot;

import org.apache.poi.xwpf.usermodel.*;

import org.openxmlformats.schemas.wordprocessingml.x2006.main.STTabJc;

import java.io.ByteArrayOutputStream;
import java.io.IOException;


public class WordUtil {

    public static byte[] createSimpleCvWord(
            String name, String profession, String email, String linkedin, String phone, String summary,
            String education, String educationDate,
            String experience, String experienceDate,
            String projects, String skills, String softSkills,
            String courses, String languages, String military
    ) throws IOException {

        try (XWPFDocument document = new XWPFDocument();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            addParagraph(document, name, 20, true, ParagraphAlignment.CENTER);
            addParagraph(document, profession, 12, false, ParagraphAlignment.CENTER);
            addParagraph(document, email + " | " + linkedin + " | " + phone, 12, false, ParagraphAlignment.CENTER);
            addSeparatorLine(document);

            addSection(document, "Professional Summary", summary, null);
            addSection(document, "Education", education, educationDate);
            addSection(document, "Experience", experience, experienceDate);
            addSection(document, "Projects", projects, null);
            addSection(document, "Technical Skills", skills, null);
            addSection(document, "Soft Skills", softSkills, null);
            addSection(document, "Courses & Certifications", courses, null);
            addSection(document, "Languages", languages, null);
            addSection(document, "Military Status", military, null);

            document.write(out);
            return out.toByteArray();
        }
    }

    private static void addParagraph(XWPFDocument document, String text, int fontSize, boolean bold, ParagraphAlignment align) {
        if (text == null || text.trim().isEmpty() || text.equalsIgnoreCase("skip")) return;

        XWPFParagraph para = document.createParagraph();
        para.setAlignment(align);
        XWPFRun run = para.createRun();
        run.setText(text);
        run.setFontSize(fontSize);
        run.setBold(bold);
        para.setSpacingAfter(100);
    }

    private static void addSection(XWPFDocument document, String title, String content, String dateText) {
        if (content == null || content.trim().isEmpty() || content.equalsIgnoreCase("skip")) return;

        addNewLine(document);

        XWPFParagraph titlePara = document.createParagraph();
        titlePara.setAlignment(ParagraphAlignment.BOTH);

        titlePara.setSpacingAfter(100);
        titlePara.getCTP().getPPr().addNewTabs().addNewTab().setVal(STTabJc.RIGHT);
        titlePara.setAlignment(ParagraphAlignment.BOTH);
        titlePara.setVerticalAlignment(TextAlignment.TOP);
        titlePara.setWordWrap(true);

        XWPFRun run = titlePara.createRun();
        run.setBold(true);
        run.setFontSize(14);
        run.setColor("333333");
        run.setText(title);

        if (dateText != null && !dateText.trim().isEmpty() && !dateText.equalsIgnoreCase("skip")) {
            run.addTab();
            run.setText("\t" + dateText);
        }

        addSeparatorLine(document);

        XWPFParagraph contentPara = document.createParagraph();
        contentPara.setAlignment(ParagraphAlignment.LEFT);
        XWPFRun contentRun = contentPara.createRun();
        contentRun.setText(content);
        contentRun.setFontSize(11);
        contentRun.setColor("000000");
        contentRun.setBold(false);
        contentPara.setSpacingAfter(200);
    }

    private static void addSeparatorLine(XWPFDocument document) {
        XWPFParagraph sep = document.createParagraph();
        sep.setSpacingBefore(100);
        sep.setSpacingAfter(100);

        sep.setBorderBottom(Borders.SINGLE);
    }
    private static void addNewLine(XWPFDocument document) {
        XWPFParagraph paragraph = document.createParagraph();
        XWPFRun run = paragraph.createRun();
        run.setText("\n");
    }

}
