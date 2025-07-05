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
            Font nameFont = new Font(baseFont, 22, Font.BOLD, BaseColor.BLACK);
            Font infoFont = new Font(baseFont, 12, Font.NORMAL, BaseColor.BLACK);
            Font titleFont = new Font(baseFont, 13, Font.BOLD, BaseColor.BLACK);
            Font contentFont = new Font(baseFont, 11, Font.NORMAL, BaseColor.BLACK);


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

        // العنوان + التاريخ في سطر واحد
        PdfPTable headerTable = new PdfPTable(2);
        headerTable.setWidthPercentage(100);
        headerTable.setWidths(new int[]{80, 20});
        headerTable.setSpacingBefore(15);
        headerTable.setSpacingAfter(5);

        PdfPCell titleCell = new PdfPCell(new Phrase(title, titleFont));
        titleCell.setBorder(Rectangle.NO_BORDER);

        PdfPCell dateCell = new PdfPCell(new Phrase(
                (date != null && !date.trim().isEmpty() && !date.equalsIgnoreCase("skip")) ? date : "", textFont));
        dateCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        dateCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        dateCell.setBorder(Rectangle.NO_BORDER);

        headerTable.addCell(titleCell);
        headerTable.addCell(dateCell);

        doc.add(headerTable);

        // المحتوى نفسه
        Paragraph contentPara = new Paragraph(content, textFont);
        contentPara.setSpacingAfter(10);
        contentPara.setLeading(16); // تباعد أسطر مريح
        doc.add(contentPara);

        // خط فاصل ومسافة بعده
        LineSeparator separator = new LineSeparator();
        separator.setLineColor(BaseColor.LIGHT_GRAY);
        doc.add(separator);
        doc.add(Chunk.NEWLINE);
    }


}
