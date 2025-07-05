package com.spring.boot;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.itextpdf.text.pdf.draw.LineSeparator;
import java.io.ByteArrayOutputStream;



public class PdfUtil {

    public static byte[] createSimpleCv(String name, String profession, String email, String linkedin, String phone, String summary,
                                        String education, String educationDate,
                                        String experience, String experienceDate,
                                        String projects, String skills, String softSkills, String courses,
                                        String languages, String military)
    {

        ByteArrayOutputStream output = new ByteArrayOutputStream();

        try {
            Document document = new Document(PageSize.A4, 50, 50, 50, 50);
            PdfWriter.getInstance(document, output);
            document.open();

            BaseFont baseFont = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.WINANSI, BaseFont.EMBEDDED);
            Font nameFont = new Font(baseFont, 20, Font.BOLD);
            Font infoFont = new Font(baseFont, 12, Font.NORMAL, BaseColor.DARK_GRAY);
            Font titleFont = new Font(baseFont, 14, Font.BOLD, BaseColor.BLACK);
            Font contentFont = new Font(baseFont, 11, Font.NORMAL, BaseColor.DARK_GRAY);

            addCenteredText(document, name, nameFont);
            addCenteredText(document, profession, infoFont);
            addCenteredText(document, email + " | " + linkedin + " | " + phone, infoFont);
            document.add(Chunk.NEWLINE);
            document.add(new LineSeparator());

            addSection(document, "Professional Summary", summary, titleFont, contentFont, null);
            addSection(document, "Education", education, titleFont, contentFont, educationDate);
            addSection(document, "Experience", experience, titleFont, contentFont, experienceDate);
            addSection(document, "Projects", projects, titleFont, contentFont, null);
            addSection(document, "Technical Skills", skills, titleFont, contentFont, null);
            addSection(document, "Soft Skills", softSkills, titleFont, contentFont, null);
            addSection(document, "Courses & Certifications", courses, titleFont, contentFont, null);
            addSection(document, "Languages", languages, titleFont, contentFont, null);
            addSection(document, "Military Status", military, titleFont, contentFont, null);

            document.close();

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return output.toByteArray();
    }

    private static void addCenteredText(Document document, String text, Font font) throws DocumentException {
        if (text == null || text.trim().isEmpty() || text.equalsIgnoreCase("skip")) return;

        Paragraph p = new Paragraph(text, font);
        p.setAlignment(Element.ALIGN_CENTER);
        p.setSpacingAfter(5);
        document.add(p);
    }

    private static void addSection(Document doc, String title, String content, Font titleFont, Font textFont, String date) throws DocumentException {
        if (content == null || content.trim().isEmpty() || content.equalsIgnoreCase("skip")) return;

        Paragraph titlePara = new Paragraph(title, titleFont);
        titlePara.setSpacingBefore(10);
        titlePara.setSpacingAfter(5);
        doc.add(titlePara);

        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setWidths(new int[]{80, 20}); // 80% for content, 20% for date

        PdfPCell contentCell = new PdfPCell(new Phrase(content, textFont));
        PdfPCell dateCell = new PdfPCell(new Phrase((date != null && !date.equalsIgnoreCase("skip")) ? date : "", textFont));

        contentCell.setBorder(Rectangle.NO_BORDER);
        dateCell.setBorder(Rectangle.NO_BORDER);
        dateCell.setHorizontalAlignment(Element.ALIGN_RIGHT);

        table.addCell(contentCell);
        table.addCell(dateCell);

        doc.add(table);

        LineSeparator separator = new LineSeparator();
        separator.setLineColor(BaseColor.LIGHT_GRAY);
        doc.add(separator);
        doc.add(Chunk.NEWLINE);

    }

}
