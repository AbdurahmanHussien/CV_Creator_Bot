package com.spring.boot;

import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.*;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTBorder;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STBorder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class WordUtil {

    public static byte[] createSimpleCvWord(
            String name, String profession, String email, String linkedin, String phone, String summary,
            String education, String experience, String projects,
            String skills, String softSkills, String courses,
            String languages, String military
    ) throws IOException {
        try (XWPFDocument document = new XWPFDocument();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            addParagraph(document, name != null && !name.isEmpty() ? name : "Applicant Name", 20, true, ParagraphAlignment.CENTER);
            addParagraph(document, profession != null && !profession.isEmpty() ? profession : "Profession", 12, false, ParagraphAlignment.CENTER);

            String contactInfo = (email != null && !email.isEmpty() ? email : "N/A") +
                    " | " + (linkedin != null && !linkedin.isEmpty() ? linkedin : "N/A") +
                    " | " + (phone != null && !phone.isEmpty() ? phone : "N/A");
            addParagraph(document, contactInfo, 12, false, ParagraphAlignment.CENTER);

            addSeparatorLine(document);

            addSection(document, "Professional Summary", summary);
            addSection(document, "Education", education);
            addSection(document, "Experience", experience);
            addSection(document, "Projects", projects);
            addSection(document, "Technical Skills", skills);
            addSection(document, "Soft Skills", softSkills);
            addSection(document, "Courses & Certifications", courses);
            addSection(document, "Languages", languages);
            addSection(document, "Military Status", military);

            document.write(out);
            return out.toByteArray();
        }
    }


    private static void addParagraph(XWPFDocument document, String text, int fontSize, boolean isBold, ParagraphAlignment alignment) {
        XWPFParagraph paragraph = document.createParagraph();
        paragraph.setAlignment(alignment);
        XWPFRun run = paragraph.createRun();
        run.setText(text);
        run.setFontSize(fontSize);
        run.setBold(isBold);
        paragraph.setSpacingAfter(100);
    }


    private static void addSection(XWPFDocument document, String title, String content) {
        if (content != null && !content.isEmpty() && !content.equalsIgnoreCase("skip")) {
            addNewLine(document, 1);

            XWPFParagraph titleParagraph = document.createParagraph();
            titleParagraph.setAlignment(ParagraphAlignment.LEFT);
            XWPFRun titleRun = titleParagraph.createRun();
            titleRun.setText(title);
            titleRun.setFontSize(14);
            titleRun.setBold(true);
            titleRun.setColor("404040");

            addSeparatorLine(document);

            XWPFParagraph contentParagraph = document.createParagraph();
            contentParagraph.setAlignment(ParagraphAlignment.LEFT);
            XWPFRun contentRun = contentParagraph.createRun();
            contentRun.setText(content);
            contentRun.setFontSize(11);
            contentRun.setBold(false);
            contentParagraph.setSpacingAfter(100); // Spacing after content
        }
    }


    private static void addSeparatorLine(XWPFDocument document) {
        XWPFParagraph separatorParagraph = document.createParagraph();
        separatorParagraph.setSpacingBefore(100); // Small space before the line
        separatorParagraph.setSpacingAfter(100);

        CTBorder border = separatorParagraph.getCTPPr().addNewPBdr().addNewBottom();
        border.setVal(STBorder.SINGLE);
        border.setColor("D3D3D3");
        border.setSz(new java.math.BigInteger("8"));
    }



    private static void addNewLine(XWPFDocument document, int count) {
        for (int i = 0; i < count; i++) {
            document.createParagraph().createRun().setText("\n");
        }
    }
}