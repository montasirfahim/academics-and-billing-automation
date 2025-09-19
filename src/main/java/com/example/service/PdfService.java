package com.example.service;

import com.example.entity.AssignedCourse;
import com.example.entity.ExamCommittee;
import com.example.entity.Semester;
import com.example.entity.User;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.draw.ILineDrawer;
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.LineSeparator;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Service
public class PdfService {

    public byte[] createPdf(ExamCommittee examCommittee, Semester semester, List<AssignedCourse> assignedCourses) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdfDoc = new PdfDocument(writer);
            pdfDoc.setDefaultPageSize(PageSize.A4);
            //pdfDoc.setDefaultPageSize(PageSize.A4.rotate());

            Document document = new Document(pdfDoc);

            PdfFont font = PdfFontFactory.createFont("src/main/resources/fonts/times.ttf", PdfEncodings.IDENTITY_H);

            document.setFont(font);

            document.setFont(font);

            addHeader(document, "Mawlana Bhashani Science and Technology University\nDept. of Information and Communication Technology, MBSTU");

            LineSeparator ls = new LineSeparator(new SolidLine(1f));
            ls.setWidth(UnitValue.createPercentValue(100));
            document.add(ls);
            addHeader(document, "Exam Committee");
            // Add some space after the divider
            document.add(new Paragraph("\n"));

            addSemesterInfo(document,pdfDoc, semester, examCommittee);
            //addHeader(document, "Committee Body\n");

            addTable(document, examCommittee);
            addFooter(pdfDoc, "Contact: 2nd Floor, 1st Academic Building, MBSTU. Email: chairman-ict@mbstu.ac.bd");

            document.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return baos.toByteArray();
    }

    private void addHeader(Document document, String headerText) {
        Paragraph header = new Paragraph(headerText)
                .setTextAlignment(TextAlignment.CENTER)
                .setFontSize(18f)
                .setBold();
        document.add(header);
    }

    private void addSemesterInfo(Document document, PdfDocument pdfDoc, Semester semester, ExamCommittee examCommittee) throws IOException {
        StringBuilder nsb = new StringBuilder();
       // nsb.append("\n");
        nsb.append("Semester: ");
        nsb.append(semester.getSemesterParity() + " Semester Final Examination - " + semester.getSemesterScheduledYear() + " \n");
        nsb.append("Session: " + examCommittee.getSession() + "\n");
        nsb.append("Held Year: " + semester.getSemesterHeldYear());
        nsb.append("\n\n");

        Paragraph paragraph = new Paragraph(nsb.toString())
                .setTextAlignment(TextAlignment.LEFT)
                .setFontSize(13f); // .setFixedPosition(30, 30, pdfDoc.getDefaultPageSize().getWidth() - 60);

        document.add(paragraph);
    }

//    private void addFooter(Document document, PdfDocument pdfDoc) {
//        Paragraph footer = new Paragraph("Contact: 2nd Floor, 1st Academic Building, MBSTU. Email: chairman-ict@embstu.ac.bd")
//                .setTextAlignment(TextAlignment.CENTER)
//                .setFontSize(10f)
//                .setFixedPosition(30, 30, pdfDoc.getDefaultPageSize().getWidth() - 60);
//        document.add(footer);
//    }

    private void addFooter(PdfDocument pdfDoc, String text) {
        int totalPages = pdfDoc.getNumberOfPages();

        for(int i = 1; i <= totalPages; i++) {
            PdfPage page = pdfDoc.getPage(i);
            Rectangle pageSize = page.getPageSize();
            float y = 30; // footer Y position from bottom

            Canvas canvas = new Canvas(page, pageSize);

            // Contact info (centered)
            Paragraph contact = new Paragraph(text)
                    .setFontSize(10)
                    .setTextAlignment(TextAlignment.CENTER);
            canvas.showTextAligned(contact, pageSize.getWidth() / 2, y, TextAlignment.CENTER);

            // Page number (right aligned)
            Paragraph pageNum = new Paragraph("Page " + i + " of " + totalPages)
                    .setFontSize(8)
                    .setTextAlignment(TextAlignment.RIGHT);
            canvas.showTextAligned(pageNum, pageSize.getWidth() - 30, 15, TextAlignment.RIGHT);

            canvas.close();
        }
    }

    private void addTable(Document document, ExamCommittee committee) {
        Table table = new Table(UnitValue.createPercentArray(new float[]{3, 2, 2, 2, 2}));
        table.setWidth(UnitValue.createPercentValue(100));
        table.addHeaderCell(createStyledCell("Name"));
        table.addHeaderCell(createStyledCell("Designation"));
        table.addHeaderCell(createStyledCell("Dept."));
        table.addHeaderCell(createStyledCell("University"));
        table.addHeaderCell(createStyledCell("Role"));

        for (int i = 0; i < 4; i++) {
            User user = new User();
            String role = "";
            if(i == 0){
                user = committee.getChairman();
                role = "Chairman";
            }
            else if(i == 1){
                user = committee.getInternalMember1();
                role = "Internal Teacher 1";
            }
            else if(i == 2){
                user = committee.getInternalMember2();
                role = "Internal Teacher 2";
            }
            else if(i == 3){
                user = committee.getExternalMember1();
                role = "External Teacher";
            }

            table.addCell(createStyledCell(user.getName()));
            table.addCell(createStyledCell(user.getDesignation()));
            table.addCell(createStyledCell(user.getDepartment()));
            table.addCell(createStyledCell(user.getUniversity()));
            table.addCell(createStyledCell(role));
        }

        document.add(table);
    }

    private Cell createStyledCell(String content) {
        return new Cell().add(new Paragraph(content))
                .setBackgroundColor(ColorConstants.WHITE)
                .setTextAlignment(TextAlignment.CENTER);
    }
}
