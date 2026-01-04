package com.example.service;

import com.example.entity.*;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import static java.awt.SystemColor.text;

@Service
public class PdfService {

    public byte[] createCommitteePdf(ExamCommittee examCommittee, Semester semester, List<Course> assignedCourses) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try (PdfWriter writer = new PdfWriter(baos);
             PdfDocument pdfDoc = new PdfDocument(writer);
             Document document = new Document(pdfDoc)) {

            pdfDoc.setDefaultPageSize(PageSize.A4);
//            document.setMargins(70, 36, 50, 36);

            PdfFont font = PdfFontFactory.createFont("src/main/resources/fonts/times.ttf", PdfEncodings.IDENTITY_H);
            document.setFont(font);

            String logoPath = "src/main/resources/static/logo.PNG";
            ImageData imageData = ImageDataFactory.create(logoPath);
            Image logo = new Image(imageData);
            logo.setWidth(60);
            logo.setHeight(60);
           // logo.setAutoScale(true);
            logo.setHorizontalAlignment(HorizontalAlignment.CENTER);
            document.add(logo);

            addHeader(document, "Mawlana Bhashani Science and Technology University\nDept. of Information and Communication Technology, MBSTU");

            LineSeparator ls = new LineSeparator(new SolidLine(1f));
            ls.setWidth(UnitValue.createPercentValue(100));
            document.add(ls);
            addHeader(document, "Exam Committee");
            document.add(new Paragraph("\n"));

            addSemesterInfo(document, pdfDoc, semester, examCommittee);
            addCommitteeMemberTable(document, examCommittee);

            addSubHeader(document, "\nCourses\n");
            addCommitteeCourseTable(document, assignedCourses);

            addSignatureSpace(document, "-----------------------\nChairman\nDepartmental Academic Committee, Dept. of ICT, MBSTU");

            addFooter(pdfDoc);

        } catch (IOException e) {
            e.printStackTrace();
            throw e; //Re-throw the exception to let the controller handle it
        }

        //after the try block, the document is guaranteed to be closed
        return baos.toByteArray();
    }

    public byte[] createTaDaBillPdf(TourAllowanceBill bill) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try (PdfWriter writer = new PdfWriter(baos);
             PdfDocument pdfDoc = new PdfDocument(writer);
             Document document = new Document(pdfDoc)) {

            pdfDoc.setDefaultPageSize(PageSize.LEGAL.rotate());
            document.setMargins(20f, 20f, 20f, 20f);

            PdfFont font = PdfFontFactory.createFont("src/main/resources/fonts/times.ttf", PdfEncodings.IDENTITY_H);
            document.setFont(font);

            String logoPath = "src/main/resources/static/logo.PNG";
            ImageData imageData = ImageDataFactory.create(logoPath);
            Image logo = new Image(imageData);
            logo.setWidth(45);
            logo.setHeight(45);
            logo.setHorizontalAlignment(HorizontalAlignment.CENTER);
            document.add(logo);

            addTaDaHeader(document, "Mawlana Bhashani Science and Technology University\nDept. of Information and Communication Technology\nTour Allowance Bill");

            LineSeparator ls = new LineSeparator(new SolidLine(1f));
            ls.setWidth(UnitValue.createPercentValue(100));
            document.add(ls);

            User billUser = bill.getUser();
            Table infoTable = new Table(UnitValue.createPercentArray(new float[]{10, 3}));
            infoTable.useAllAvailableWidth();
            infoTable.setMarginTop(10f);


            infoTable.addCell(createLabelValueCell("Name: ", billUser.getName()));
            infoTable.addCell(createLabelValueCell("Cash Voucher No.: ", "________________"));


            infoTable.addCell(createLabelValueCell("Designation: ", billUser.getDesignation()));
            infoTable.addCell(createLabelValueCell("Date: ", "________________"));


            infoTable.addCell(createLabelValueCell("Dept/Office: ", billUser.getDepartment() + ", " + billUser.getUniversity()));
            infoTable.addCell(new Cell().setBorder(Border.NO_BORDER));


            infoTable.addCell(createLabelValueCell("Basic Salary: ",  ""));
            infoTable.addCell(new Cell().setBorder(Border.NO_BORDER));

            document.add(infoTable);


            float[] columnWidths = {11, 9, 4, 10, 8, 4, 4, 4, 4, 4, 4, 8, 7, 5, 6, 8, 6};
            Table mainTable = new Table(UnitValue.createPercentArray(columnWidths));
            mainTable.useAllAvailableWidth();
            mainTable.setMarginTop(15f);

            //1st row

            //columns 1-6: Travel Details
            mainTable.addCell(new Cell(1, 6)
                    .add(new Paragraph("Travel Details"))
                    .setTextAlignment(TextAlignment.CENTER));

            //Column 7: Travel Type
            mainTable.addCell(new Cell(3, 1)
                    .add(new Paragraph("Transport Type"))
                    .setTextAlignment(TextAlignment.CENTER));

            //Columns 8-11: Bus/Train Fare
            mainTable.addCell(new Cell(1, 4)
                    .add(new Paragraph("Bus/Train Fare"))
                    .setTextAlignment(TextAlignment.CENTER));

            //Column 12: Kilometer
            mainTable.addCell(new Cell(3, 1)
                    .add(new Paragraph("Kilometer"))
                    .setTextAlignment(TextAlignment.CENTER));

            //Column 13: Daily Allowance
            mainTable.addCell(new Cell(3, 1)
                    .add(new Paragraph("Daily Allowance"))
                    .setTextAlignment(TextAlignment.CENTER));

            //Columns 14-15
            mainTable.addCell(new Cell(1, 2)
                    .add(new Paragraph("Actual Cost"))
                    .setTextAlignment(TextAlignment.CENTER));

            //Column 16
            mainTable.addCell(new Cell(3, 1)
                    .add(new Paragraph("Travel Intention"))
                    .setTextAlignment(TextAlignment.CENTER));

            mainTable.addCell(new Cell(3, 1)
                    .add(new Paragraph("Comment"))
                    .setTextAlignment(TextAlignment.CENTER));

            //2nd row
            mainTable.addCell(new Cell(1, 3).add(new Paragraph("Departure")).setTextAlignment(TextAlignment.CENTER));
            mainTable.addCell(new Cell(1, 3).add(new Paragraph("Arrival")).setTextAlignment(TextAlignment.CENTER));
            mainTable.addCell(new Cell().add(new Paragraph("Class")).setTextAlignment(TextAlignment.CENTER));
            mainTable.addCell(new Cell().add(new Paragraph("Unit Fare")).setTextAlignment(TextAlignment.CENTER));
            mainTable.addCell(new Cell().add(new Paragraph("Availability")).setTextAlignment(TextAlignment.CENTER));
            mainTable.addCell(new Cell().add(new Paragraph("Total Fare")).setTextAlignment(TextAlignment.CENTER));
            mainTable.addCell(new Cell().add(new Paragraph("Details")).setTextAlignment(TextAlignment.CENTER));
            mainTable.addCell(new Cell().add(new Paragraph("Amount (BDT)")).setTextAlignment(TextAlignment.CENTER));

            //3rd row
            mainTable.addCell(new Cell().add(new Paragraph("Place")).setTextAlignment(TextAlignment.CENTER));
            mainTable.addCell(new Cell().add(new Paragraph("Date")).setTextAlignment(TextAlignment.CENTER));
            mainTable.addCell(new Cell().add(new Paragraph("Time")).setTextAlignment(TextAlignment.CENTER));
            mainTable.addCell(new Cell().add(new Paragraph("Place")).setTextAlignment(TextAlignment.CENTER));
            mainTable.addCell(new Cell().add(new Paragraph("Date")).setTextAlignment(TextAlignment.CENTER));
            mainTable.addCell(new Cell().add(new Paragraph("Time")).setTextAlignment(TextAlignment.CENTER));
            mainTable.addCell(new Cell().add(new Paragraph("")).setTextAlignment(TextAlignment.CENTER));
            mainTable.addCell(new Cell().add(new Paragraph("")).setTextAlignment(TextAlignment.CENTER));
            mainTable.addCell(new Cell().add(new Paragraph("")).setTextAlignment(TextAlignment.CENTER));
            mainTable.addCell(new Cell().add(new Paragraph("")).setTextAlignment(TextAlignment.CENTER));
            mainTable.addCell(new Cell().add(new Paragraph("")).setTextAlignment(TextAlignment.CENTER));
            mainTable.addCell(new Cell().add(new Paragraph("")).setTextAlignment(TextAlignment.CENTER));


            //4th row
            for(int i = 1; i <= 17; i++){
                mainTable.addCell(new Cell()
                        .add(new Paragraph(String.valueOf(i)))
                        .setTextAlignment(TextAlignment.CENTER)
                        .setFontSize(10f));
            }

            //5th row
            mainTable.addCell(new Cell().add(new Paragraph(billUser.getUniversity())).setTextAlignment(TextAlignment.CENTER));
            mainTable.addCell(new Cell().add(new Paragraph(bill.getDepartureTimeFromHisUniversity().toString().split("T")[0])).setTextAlignment(TextAlignment.CENTER));
            mainTable.addCell(new Cell().add(new Paragraph(bill.getDepartureTimeFromHisUniversity().toString().split("T")[1])).setTextAlignment(TextAlignment.CENTER));

            mainTable.addCell(new Cell().add(new Paragraph("Tangail")).setTextAlignment(TextAlignment.CENTER));
            mainTable.addCell(new Cell().add(new Paragraph(bill.getArrivalTimeAtTangail1().toString().split("T")[0])).setTextAlignment(TextAlignment.CENTER));
            mainTable.addCell(new Cell().add(new Paragraph(bill.getArrivalTimeAtTangail1().toString().split("T")[1])).setTextAlignment(TextAlignment.CENTER));

            for(int i = 7; i <= 11; i++){
                mainTable.addCell(new Cell().add(new Paragraph("")));
            }
            mainTable.addCell(new Cell().add(new Paragraph(billUser.getDistanceFromMBSTU().toString() + " Km")).setTextAlignment(TextAlignment.CENTER));
            mainTable.addCell(new Cell().add(new Paragraph(String.valueOf(bill.getDailyAllowance())).setTextAlignment(TextAlignment.CENTER)));
            mainTable.addCell(new Cell().add(new Paragraph(bill.getTotalDayCount().toString()).setTextAlignment(TextAlignment.CENTER)));
            mainTable.addCell(new Cell().add(new Paragraph(String.valueOf(bill.getTotalDayCount()*bill.getDailyAllowance())).setTextAlignment(TextAlignment.CENTER)));
            mainTable.addCell(new Cell(1, 2).add(new Paragraph(bill.getTravelIntention())).setTextAlignment(TextAlignment.CENTER));

            //6th row
            mainTable.addCell(new Cell().add(new Paragraph("Tangail")).setTextAlignment(TextAlignment.CENTER));
            mainTable.addCell(new Cell().add(new Paragraph(bill.getArrivalTimeAtTangail1().toString().split("T")[0])).setTextAlignment(TextAlignment.CENTER));
            mainTable.addCell(new Cell().add(new Paragraph(bill.getArrivalTimeAtTangail1().toString().split("T")[1])).setTextAlignment(TextAlignment.CENTER));
            mainTable.addCell(new Cell().add(new Paragraph("MBSTU")).setTextAlignment(TextAlignment.CENTER));
            mainTable.addCell(new Cell().add(new Paragraph(bill.getArrivalTimeAtMbstu().toString().split("T")[0])).setTextAlignment(TextAlignment.CENTER));
            mainTable.addCell(new Cell().add(new Paragraph(bill.getArrivalTimeAtMbstu().toString().split("T")[1])).setTextAlignment(TextAlignment.CENTER));
            for(int i = 7; i <= 11; i++){
                mainTable.addCell(new Cell().add(new Paragraph("")));
            }
            mainTable.addCell(new Cell().add(new Paragraph("5 Km")).setTextAlignment(TextAlignment.CENTER));
            for(int i = 13; i <= 17; i++){
                mainTable.addCell(new Cell().add(new Paragraph("")));
            }

            //7th row
            mainTable.addCell(new Cell().add(new Paragraph("MBSTU")).setTextAlignment(TextAlignment.CENTER));
            mainTable.addCell(new Cell().add(new Paragraph(bill.getDepartureTimeFromMbstu().toString().split("T")[0])).setTextAlignment(TextAlignment.CENTER));
            mainTable.addCell(new Cell().add(new Paragraph(bill.getDepartureTimeFromMbstu().toString().split("T")[1])).setTextAlignment(TextAlignment.CENTER));
            mainTable.addCell(new Cell().add(new Paragraph("Tangail")).setTextAlignment(TextAlignment.CENTER));
            mainTable.addCell(new Cell().add(new Paragraph(bill.getArrivalTimeAtTangail2().toString().split("T")[0])).setTextAlignment(TextAlignment.CENTER));
            mainTable.addCell(new Cell().add(new Paragraph(bill.getArrivalTimeAtTangail2().toString().split("T")[1])).setTextAlignment(TextAlignment.CENTER));
            for(int i = 7; i <= 11; i++){
                mainTable.addCell(new Cell().add(new Paragraph("")));
            }
            mainTable.addCell(new Cell().add(new Paragraph("5 Km")).setTextAlignment(TextAlignment.CENTER));
            for(int i = 13; i <= 17; i++){
                mainTable.addCell(new Cell().add(new Paragraph("")));
            }

            //8th row
            mainTable.addCell(new Cell().add(new Paragraph("Tangail")).setTextAlignment(TextAlignment.CENTER));
            mainTable.addCell(new Cell().add(new Paragraph(bill.getArrivalTimeAtTangail2().toString().split("T")[0])).setTextAlignment(TextAlignment.CENTER));
            mainTable.addCell(new Cell().add(new Paragraph(bill.getArrivalTimeAtTangail2().toString().split("T")[1])).setTextAlignment(TextAlignment.CENTER));

            mainTable.addCell(new Cell().add(new Paragraph(billUser.getUniversity())).setTextAlignment(TextAlignment.CENTER));
            mainTable.addCell(new Cell().add(new Paragraph(bill.getArrivalTimeAtHisUniversity().toString().split("T")[0])).setTextAlignment(TextAlignment.CENTER));
            mainTable.addCell(new Cell().add(new Paragraph(bill.getArrivalTimeAtHisUniversity().toString().split("T")[1])).setTextAlignment(TextAlignment.CENTER));

            for(int i = 7; i <= 11; i++){
                mainTable.addCell(new Cell().add(new Paragraph("")));
            }
            mainTable.addCell(new Cell().add(new Paragraph(String.valueOf(billUser.getDistanceFromMBSTU()))).setTextAlignment(TextAlignment.CENTER));
            for(int i = 13; i <= 17; i++){
                mainTable.addCell(new Cell().add(new Paragraph("")));
            }

            //9th row
            for(int i = 1; i <= 17; i++){
                if(i == 12) mainTable.addCell(new Cell().add(new Paragraph("/")).setTextAlignment(TextAlignment.CENTER));
                else mainTable.addCell(new Cell().add(new Paragraph("")));
            }

            //10th
            for(int i = 1; i <= 11; i++){
                mainTable.addCell(new Cell().add(new Paragraph("")));
            }
            mainTable.addCell(new Cell().add(new Paragraph(String.valueOf(bill.getTotalTravelDistance())).setTextAlignment(TextAlignment.CENTER)));
            mainTable.addCell(new Cell().add(new Paragraph("x " + String.valueOf(bill.getPerKmFareRate())).setTextAlignment(TextAlignment.CENTER)));
            mainTable.addCell(new Cell().add(new Paragraph("")));
            mainTable.addCell(new Cell().add(new Paragraph(String.valueOf(bill.getPerKmFareRate()*bill.getTotalTravelDistance())).setTextAlignment(TextAlignment.CENTER)));
            for(int i = 16; i <= 17; i++){
                mainTable.addCell(new Cell().add(new Paragraph("")));
            }

            //11th row
            for(int i = 1; i <= 13; i++){
                mainTable.addCell(new Cell().add(new Paragraph("")));
            }
            mainTable.addCell(new Cell(1, 2).add(new Paragraph("Total: " + String.valueOf(bill.getTotalBillAmount())).setTextAlignment(TextAlignment.CENTER)));
            for(int i = 16; i <= 17; i++){
                mainTable.addCell(new Cell().add(new Paragraph("")));
            }


            document.add(mainTable);


        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }

        return baos.toByteArray();
    }

    private void addTaDaHeader(Document document, String text) {
        Paragraph p = new Paragraph(text)
                .setTextAlignment(TextAlignment.CENTER)
                .setBold()
                .setFontSize(16f)
                .setMultipliedLeading(1.2f);
        document.add(p);
    }

    private void addTaDaSignatureSpace(Document document, String headerText) {
        Paragraph header = new Paragraph(headerText)
                .setTextAlignment(TextAlignment.LEFT)
                .setFontSize(13f)
                .setFixedPosition(40, 30, 400);
        document.add(header);
    }

    private Cell createLabelValueCell(String label, String value) {
        Paragraph p = new Paragraph()
                .add(new Text(label).setBold())
                .add(new Text(value != null ? value : ""));

        return new Cell().add(p)
                .setBorder(Border.NO_BORDER)
                .setPaddingBottom(2f); //tightens the vertical space between lines
    }

    private void addHeader(Document document, String headerText) {
        Paragraph header = new Paragraph(headerText)
                .setTextAlignment(TextAlignment.CENTER)
                .setFontSize(18f)
                .setBold();
        document.add(header);
    }

    private void addSubHeader(Document document, String headerText) {
        Paragraph header = new Paragraph(headerText)
                .setTextAlignment(TextAlignment.CENTER)
                .setFontSize(15f)
                .setBold();
        document.add(header);
    }

    private void addSemesterInfo(Document document, PdfDocument pdfDoc, Semester semester, ExamCommittee examCommittee) throws IOException {
        StringBuilder nsb = new StringBuilder();
       // nsb.append("\n");
        nsb.append("Exam: ");
        nsb.append( examCommittee.getSemesterYearName() + " " + semester.getSemesterParity() + " Semester Final Examination - " + semester.getSemesterScheduledYear() + " \n");
        nsb.append("Session: " + examCommittee.getSession() + "\n");
        nsb.append("Held Year: " + semester.getSemesterHeldYear());
        nsb.append("\n\n");

        Paragraph paragraph = new Paragraph(nsb.toString())
                .setTextAlignment(TextAlignment.LEFT)
                .setFontSize(13f); // .setFixedPosition(30, 30, pdfDoc.getDefaultPageSize().getWidth() - 60);

        document.add(paragraph);
    }


    private void addFooter(PdfDocument pdfDoc) {
        int totalPages = pdfDoc.getNumberOfPages();

        for(int i = 1; i <= totalPages; i++) {
            PdfPage page = pdfDoc.getPage(i);
            Rectangle pageSize = page.getPageSize();
            float y = 20; //footer Y position from bottom

            Canvas canvas = new Canvas(page, pageSize);

            //main footer info (centered)
//            Paragraph contact = new Paragraph(text)
//                    .setFontSize(10)
//                    .setTextAlignment(TextAlignment.CENTER);
//            canvas.showTextAligned(contact, pageSize.getWidth() / 2, y, TextAlignment.CENTER);

            //Page number (right aligned)
            Paragraph pageNum = new Paragraph("Page " + i + " of " + totalPages)
                    .setFontSize(8)
                    .setTextAlignment(TextAlignment.RIGHT);
            canvas.showTextAligned(pageNum, pageSize.getWidth() - 30, 7, TextAlignment.RIGHT);

            canvas.close();
        }
    }

    private void addCommitteeMemberTable(Document document, ExamCommittee committee) {
        Table table = new Table(UnitValue.createPercentArray(new float[]{3, 2, 2, 2, 2}));
        table.setWidth(UnitValue.createPercentValue(100));
        table.addHeaderCell(createStyledHeaderCell("Name"));
        table.addHeaderCell(createStyledHeaderCell("Designation"));
        table.addHeaderCell(createStyledHeaderCell("Dept."));
        table.addHeaderCell(createStyledHeaderCell("University"));
        table.addHeaderCell(createStyledHeaderCell("Role"));

        for (int i = 0; i < 4; i++) {
            User user = new User();
            String role = "";
            if(i == 0){
                user = committee.getChairman();
                role = "Chairman";
            }
            else if(i == 1){
                user = committee.getInternalMember1();
                role = "Internal Member";
            }
            else if(i == 2){
                user = committee.getInternalMember2();
                role = "Internal Member";
            }
            else if(i == 3){
                user = committee.getExternalMember1();
                role = "External Member";
            }

            table.addCell(createStyledCell(user.getName()));
            table.addCell(createStyledCell(user.getDesignation()));
            table.addCell(createStyledCell(user.getDepartment()));
            table.addCell(createStyledCell(user.getUniversity()));
            table.addCell(createStyledCell(role));
        }

        document.add(table);
    }

    private void addCommitteeCourseTable(Document document, List<Course> courseList) {
        Table table = new Table(UnitValue.createPercentArray(new float[]{1.5F, 4.5F, 1.5F, 3.5F}));
        table.setWidth(UnitValue.createPercentValue(100));
        table.addHeaderCell(createStyledHeaderCell("Course Code"));
        table.addHeaderCell(createStyledHeaderCell("Course Name"));
        table.addHeaderCell(createStyledHeaderCell("Credit Hour"));
        table.addHeaderCell(createStyledHeaderCell("Course Teacher"));

        //Collections.sort(courseList, course_name );
        for (int i = 0; i < courseList.size(); i++) {
            Course assignedCourse = courseList.get(i);

            table.addCell(createStyledCell(assignedCourse.getCourseCode()));
            table.addCell(createStyledCell(assignedCourse.getCourseName()));
            table.addCell(createStyledCell(String.valueOf(assignedCourse.getCourseCredit())));
            table.addCell(createStyledCell((assignedCourse.getCourseTeacher() == null) ? "Not Assigned" : assignedCourse.getCourseTeacher().getName()));
        }

        document.add(table);
    }

    private void addSignatureSpace(Document document, String headerText) {
        Paragraph header = new Paragraph(headerText)
                .setTextAlignment(TextAlignment.LEFT)
                .setFontSize(13f)
                .setFixedPosition(40, 45, 400);
        document.add(header);
    }

    private Cell createStyledCell(String content) {
        return new Cell().add(new Paragraph(content))
                .setBackgroundColor(ColorConstants.WHITE)
                .setTextAlignment(TextAlignment.CENTER);
    }

    private Cell createStyledHeaderCell(String content) {
        return new Cell().add(new Paragraph(content))
                .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                .setTextAlignment(TextAlignment.CENTER).setBold();
    }


}
