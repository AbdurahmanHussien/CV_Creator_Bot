package com.spring.boot;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.itextpdf.text.pdf.draw.LineSeparator;

import java.io.ByteArrayOutputStream;

public class PdfUtil {

    public static byte[] createSimpleCv(String name, String profession, String email,String linkedin, String phone, String summary,
                                        String education, String experience, String projects,
                                        String skills, String softSkills, String courses,
                                        String languages, String military) {

        ByteArrayOutputStream output = new ByteArrayOutputStream();

        try {
            Document document = new Document(PageSize.A4, 50, 50, 50, 50);
            PdfWriter.getInstance(document, output);
            document.open();

            // Fonts
            BaseFont baseFont = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.WINANSI, BaseFont.EMBEDDED);
            Font headerFont = new Font(baseFont, 20, Font.BOLD);
            Font subHeaderFont = new Font(baseFont, 12, Font.NORMAL);
            Font sectionTitleFont = new Font(baseFont, 14, Font.BOLD, BaseColor.DARK_GRAY);
            Font sectionTextFont = new Font(baseFont, 11, Font.NORMAL);

            // Header
            Paragraph header = new Paragraph(name + "\n" + profession, headerFont);
            header.setAlignment(Element.ALIGN_CENTER);
            document.add(header);

            Paragraph subHeader = new Paragraph(email + " | " +linkedin + " | " +phone, subHeaderFont);
            subHeader.setAlignment(Element.ALIGN_CENTER);
            document.add(subHeader);

            LineSeparator line = new LineSeparator();
            line.setLineColor(BaseColor.LIGHT_GRAY);
            document.add(Chunk.NEWLINE);
            document.add(line);
            document.add(Chunk.NEWLINE);

            // Sections
            addSection(document, "Professional Summary", summary, sectionTitleFont, sectionTextFont);
            addSection(document, "Education", education, sectionTitleFont, sectionTextFont);
            addSection(document, "Experience", experience, sectionTitleFont, sectionTextFont);
            addSection(document, "Projects", projects, sectionTitleFont, sectionTextFont);
            addSection(document, "Technical Skills", skills, sectionTitleFont, sectionTextFont);
            addSection(document, "Soft Skills", softSkills, sectionTitleFont, sectionTextFont);
            addSection(document, "Courses & Certifications", courses, sectionTitleFont, sectionTextFont);
            addSection(document, "Languages", languages, sectionTitleFont, sectionTextFont);
            addSection(document, "Military Status", military, sectionTitleFont, sectionTextFont);

            document.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return output.toByteArray();
    }

    private static void addSection(Document doc, String title, String content, Font titleFont, Font textFont) throws DocumentException {
        Paragraph sectionTitle = new Paragraph(title, titleFont);
        sectionTitle.setSpacingBefore(10);
        sectionTitle.setSpacingAfter(5);
        doc.add(sectionTitle);

        Paragraph sectionContent = new Paragraph(content, textFont);
        sectionContent.setSpacingAfter(10);
        doc.add(sectionContent);

        LineSeparator separator = new LineSeparator();
        separator.setLineColor(BaseColor.LIGHT_GRAY);
        doc.add(separator);
    }
}
