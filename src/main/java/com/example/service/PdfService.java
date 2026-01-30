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
import com.itextpdf.layout.borders.DottedBorder;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.*;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Service
public class PdfService {


    private final ExamCommitteeService examCommitteeService;

    public PdfService(ExamCommitteeService examCommitteeService) {
        this.examCommitteeService = examCommitteeService;
    }

    public byte[] createCommitteePdf(ExamCommittee examCommittee, Semester semester, List<Course> assignedCourses) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try (PdfWriter writer = new PdfWriter(baos);
             PdfDocument pdfDoc = new PdfDocument(writer);
             Document document = new Document(pdfDoc)) {

            pdfDoc.setDefaultPageSize(PageSize.A4);
//            document.setMargins(70, 36, 50, 36);

            InputStream fontStream = getClass().getResourceAsStream("/fonts/times.ttf");

            if (fontStream == null) {
                throw new IOException("Font file not found! Check if it is in src/main/resources/fonts/times.ttf");
            }

            byte[] fontBytes = fontStream.readAllBytes();

            PdfFont font = PdfFontFactory.createFont(fontBytes, PdfEncodings.IDENTITY_H);
            document.setFont(font);

            InputStream is = getClass().getResourceAsStream("/static/logo.PNG");

            if (is == null) {
                throw new IOException("Logo not found in classpath! Check path: /static/logo.PNG");
            }

            byte[] imageBytes = is.readAllBytes();

            ImageData imageData = ImageDataFactory.create(imageBytes);
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

            InputStream fontStream = getClass().getResourceAsStream("/fonts/times.ttf");

            if (fontStream == null) {
                throw new IOException("Font file not found! Check if it is in src/main/resources/fonts/times.ttf");
            }

            byte[] fontBytes = fontStream.readAllBytes();

            PdfFont font = PdfFontFactory.createFont(fontBytes, PdfEncodings.IDENTITY_H);
            document.setFont(font);

            InputStream is = getClass().getResourceAsStream("/static/logo.PNG");

            if (is == null) {
                throw new IOException("Logo not found in classpath! Check path: /static/logo.PNG");
            }

            byte[] imageBytes = is.readAllBytes();

            ImageData imageData = ImageDataFactory.create(imageBytes);
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
            document.add(new AreaBreak(AreaBreakType.NEXT_PAGE)); //to jump on next page

            //new page
            float[] columnWidth2 = {6, 3, 6, 10};
            Table summaryTable = new Table(UnitValue.createPercentArray(columnWidth2));
            summaryTable.useAllAvailableWidth();
            summaryTable.setMarginTop(15f);

            //1st row
            summaryTable.addCell(new Cell().add(new Paragraph("i. Travel Distance " + bill.getTotalTravelDistance() + " Km (as per column 12)\nii. " + bill.getDailyAllowance() + " BDT, daily allowance (as per column 13)\n04. Actual cost (as per column 15)").setTextAlignment(TextAlignment.LEFT)));
            summaryTable.addCell(new Cell().add(new Paragraph(bill.getTotalTravelDistance() * bill.getPerKmFareRate() + "\n\n\n\n" + bill.getDailyAllowance()*bill.getTotalDayCount()).setTextAlignment(TextAlignment.CENTER)));

            Cell column3 = new Cell();


            Table photoBox = new Table(1);
            photoBox.setWidth(80f);
            photoBox.setHeight(80f);
            photoBox.setHorizontalAlignment(HorizontalAlignment.CENTER);

            Cell boxCell = new Cell()
                    .setHeight(100f)
                    .setBorder(new SolidBorder(1f));

            photoBox.addCell(boxCell);


            column3.add(photoBox);

            Paragraph sigPara = new Paragraph("\n\nSignature of Traveler: ____________\n(Official Seal)                      Date: _________")
                    .setTextAlignment(TextAlignment.LEFT);
            column3.add(sigPara);

            summaryTable.addCell(column3);

            //summaryTable.addCell(new Cell(1, 4).add(new Paragraph("Attestation\n1. This is being attested that, the class in which I have traveled and that travel was made in the interest of the university.\n2. I didn't receive the claimed amount of this bill before.\n3. I have stayed at Rest House, Circuit House or Dak Bungalow. True : False\n4. If any additional money received against this bill, I will be obliged to return it.").setTextAlignment(TextAlignment.LEFT)));
            //COLUMN 4: Attestation
            Cell column4 = new Cell(4, 1);

            Paragraph attHeader = new Paragraph("Attestation")
                    .setTextAlignment(TextAlignment.CENTER)
                    .setBold()
                    .setMarginBottom(5f);
            column4.add(attHeader);


            Paragraph attBody = new Paragraph()
                    .setTextAlignment(TextAlignment.LEFT)
                    .add("1. This is being attested that, the class in which I have traveled and that travel was made in the interest of the university.\n")
                    .add("2. I didn't receive the claimed amount of this bill before.\n")
                    .add("3. I have stayed at Rest House, Circuit House or Dak Bungalow. True : False\n")
                    .add("4. If any additional money received against this bill, I'll be obliged to return it.");
            column4.add(attBody);

            Paragraph signature = new Paragraph().setTextAlignment(TextAlignment.RIGHT).add("\n\nSignature of Traveler")
                            .add("\nDate: ____________");
            column4.add(signature);

            Paragraph rulesHeading = new Paragraph("\nProcedures of Preparing Tour Allowance Bill").setTextAlignment(TextAlignment.CENTER).setBold();
            column4.add(rulesHeading);
            Paragraph rulesBody = new Paragraph().setTextAlignment(TextAlignment.LEFT)
                            .add("1. Tour allowance bill must be submitted within 03 month of travel.\n")
                    .add("2. Different types of travels and halts should not shown on the same row.\n");

            column4.add(rulesBody);



            summaryTable.addCell(column4);

            summaryTable.addCell(new Cell().add(new Paragraph("Total Payable").setTextAlignment(TextAlignment.LEFT)));
            summaryTable.addCell(new Cell().add(new Paragraph("Tk: " + bill.getTotalBillAmount() + " BDT").setTextAlignment(TextAlignment.LEFT)));
            summaryTable.addCell(new Cell().add(new Paragraph()));

            summaryTable.addCell(new Cell().add(new Paragraph("Advance (if exist)\nCash voucher no. ______________\nDate: __________").setTextAlignment(TextAlignment.LEFT)));
            summaryTable.addCell(new Cell().add(new Paragraph("Tk: " + bill.getTotalBillAmount() + " BDT").setTextAlignment(TextAlignment.LEFT)));
            summaryTable.addCell(new Cell().add(new Paragraph()));



            summaryTable.addCell(new Cell().add(new Paragraph("Total Payable").setTextAlignment(TextAlignment.LEFT)));
            summaryTable.addCell(new Cell().add(new Paragraph("Tk: " + bill.getTotalBillAmount() + " BDT").setTextAlignment(TextAlignment.LEFT)));
            summaryTable.addCell(new Cell().add(new Paragraph()));


            document.add(summaryTable);

            String billAmountInWords = UtilityService.formatBDT(bill.getTotalBillAmount());

            Paragraph p = new Paragraph();
            p.add("\n\n\nTK " + bill.getTotalBillAmount() + " BDT, In Words: " + billAmountInWords);
            p.add(", advance/expenditure approved.");
            document.add(p);

            document.add(new Paragraph("Budget Office").setTextAlignment(TextAlignment.CENTER));

            Paragraph p2 = new Paragraph();
            p2.addTabStops(new TabStop(400));
            p2.addTabStops(new TabStop(600));
            p2.add("Tk " + bill.getTotalBillAmount() + " BDT, In Words: " + billAmountInWords);
            p2.add(", Let it be paid. ");
            p2.add(new Tab());
            p2.add("     expenditure sector ______ in ______ allocated amount: __________");
            document.add(p2);

            document.add(new Paragraph("Total expenditure till current bill: ___________ BDT").setTextAlignment(TextAlignment.RIGHT));
            document.add(new Paragraph("Remaining Tk: ___________ BDT").setTextAlignment(TextAlignment.RIGHT));

            document.add(new Paragraph("\nHead of Department / Head of Office\n").setTextAlignment(TextAlignment.CENTER));
            document.add(new Paragraph("Assistant Accountant                Section Officer/Assistant Account Officer               Assistant Director                  Deputy Director                 Director").setTextAlignment(TextAlignment.CENTER));


        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }

        return baos.toByteArray();
    }

    public byte[] createTaDaReportPdf(TourAllowanceBill bill) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try (PdfWriter writer = new PdfWriter(baos);
             PdfDocument pdfDoc = new PdfDocument(writer);
             Document document = new Document(pdfDoc)) {

            pdfDoc.setDefaultPageSize(PageSize.LEGAL);

            InputStream fontStream = getClass().getResourceAsStream("/fonts/times.ttf");

            if (fontStream == null) {
                throw new IOException("Font file not found! Check if it is in src/main/resources/fonts/times.ttf");
            }

            byte[] fontBytes = fontStream.readAllBytes();

            PdfFont font = PdfFontFactory.createFont(fontBytes, PdfEncodings.IDENTITY_H);
            document.setFont(font);

            InputStream is = getClass().getResourceAsStream("/static/logo.PNG");

            if (is == null) {
                throw new IOException("Logo not found in classpath! Check path: /static/logo.PNG");
            }

            byte[] imageBytes = is.readAllBytes();

            ImageData imageData = ImageDataFactory.create(imageBytes);
            Image logo = new Image(imageData);
            logo.setWidth(45);
            logo.setHeight(45);
            logo.setHorizontalAlignment(HorizontalAlignment.CENTER);
            document.add(logo);

            addTaDaHeader(document, "Mawlana Bhashani Science and Technology University\nDept. of Information and Communication Technology\nTour Report");

            LineSeparator ls = new LineSeparator(new SolidLine(1f));
            ls.setWidth(UnitValue.createPercentValue(100));
            document.add(ls);

            User billUser = bill.getUser();
            Table infoTable = new Table(UnitValue.createPercentArray(new float[]{10, 3}));
            infoTable.useAllAvailableWidth();
            infoTable.setMarginTop(10f);


            infoTable.addCell(createLabelValueCell("Name: ", billUser.getName()));
            infoTable.addCell(createLabelValueCell("Designation: ", billUser.getDesignation()));

            infoTable.addCell(createLabelValueCell("Dept: ", billUser.getDepartment() + ", " + billUser.getUniversity()));
            infoTable.addCell(createLabelValueCell("Basic Salary: ", ""));
            //infoTable.addCell(new Cell().setBorder(Border.NO_BORDER));

            infoTable.addCell(createLabelValueCell("From: ", bill.getDepartureTimeFromHisUniversity().toString().split("T")[0]));
            infoTable.addCell(createLabelValueCell("To: ", bill.getDepartureTimeFromMbstu().toString().split("T")[0]));


            infoTable.addCell(createLabelValueCell("Transport: ", "Public"));
            infoTable.addCell(createLabelValueCell("Class: ", "First Class"));

            infoTable.addCell(createLabelValueCell("Bank Acc & Routing No: ", ""));
            infoTable.addCell(new Cell().setBorder(Border.NO_BORDER));
            infoTable.addCell(createLabelValueCell("Travel Intention: ", bill.getTravelIntention()));


            document.add(infoTable);


            float[] columnWidths = {4, 4, 6, 4, 4, 6, 5};
            Table mainTable = new Table(UnitValue.createPercentArray(columnWidths));
            mainTable.useAllAvailableWidth();
            mainTable.setMarginTop(15f);

            //1st
            mainTable.addCell(new Cell(1, 3).add(new Paragraph("Departure").setTextAlignment(TextAlignment.CENTER)));
            mainTable.addCell(new Cell(1, 3).add(new Paragraph("Arrival").setTextAlignment(TextAlignment.CENTER)));
            mainTable.addCell(new Cell().add(new Paragraph("Location").setTextAlignment(TextAlignment.CENTER)));

            //2nd
            mainTable.addCell(new Cell().add(new Paragraph("Date")).setTextAlignment(TextAlignment.CENTER));
            mainTable.addCell(new Cell().add(new Paragraph("Time")).setTextAlignment(TextAlignment.CENTER));
            mainTable.addCell(new Cell().add(new Paragraph("Place")).setTextAlignment(TextAlignment.CENTER));
            mainTable.addCell(new Cell().add(new Paragraph("Date")).setTextAlignment(TextAlignment.CENTER));
            mainTable.addCell(new Cell().add(new Paragraph("Time")).setTextAlignment(TextAlignment.CENTER));
            mainTable.addCell(new Cell().add(new Paragraph("Place")).setTextAlignment(TextAlignment.CENTER));
            mainTable.addCell(new  Cell().add(new Paragraph("From - To (Time)")).setTextAlignment(TextAlignment.CENTER));


            //3rd
            mainTable.addCell(new Cell().add(new Paragraph(bill.getDepartureTimeFromHisUniversity().toString().split("T")[0])).setTextAlignment(TextAlignment.CENTER));
            mainTable.addCell(new Cell().add(new Paragraph(bill.getDepartureTimeFromHisUniversity().toString().split("T")[1])).setTextAlignment(TextAlignment.CENTER));
            mainTable.addCell(new Cell().add(new Paragraph(billUser.getUniversity())).setTextAlignment(TextAlignment.CENTER));

            mainTable.addCell(new Cell().add(new Paragraph(bill.getArrivalTimeAtTangail1().toString().split("T")[0])).setTextAlignment(TextAlignment.CENTER));
            mainTable.addCell(new Cell().add(new Paragraph(bill.getArrivalTimeAtTangail1().toString().split("T")[1])).setTextAlignment(TextAlignment.CENTER));
            mainTable.addCell(new Cell().add(new Paragraph("Tangail")).setTextAlignment(TextAlignment.CENTER));
            mainTable.addCell(new Cell().add(new Paragraph("")));

            //4th
            mainTable.addCell(new Cell().add(new Paragraph(bill.getArrivalTimeAtTangail1().toString().split("T")[0])).setTextAlignment(TextAlignment.CENTER));
            mainTable.addCell(new Cell().add(new Paragraph(bill.getArrivalTimeAtTangail1().toString().split("T")[1])).setTextAlignment(TextAlignment.CENTER));
            mainTable.addCell(new Cell().add(new Paragraph("Tangail")).setTextAlignment(TextAlignment.CENTER));
            mainTable.addCell(new Cell().add(new Paragraph(bill.getArrivalTimeAtMbstu().toString().split("T")[0])).setTextAlignment(TextAlignment.CENTER));
            mainTable.addCell(new Cell().add(new Paragraph(bill.getArrivalTimeAtMbstu().toString().split("T")[1])).setTextAlignment(TextAlignment.CENTER));
            mainTable.addCell(new Cell().add(new Paragraph("MBSTU")).setTextAlignment(TextAlignment.CENTER));
            mainTable.addCell(new Cell().add(new Paragraph("")));

            //5th
            mainTable.addCell(new Cell().add(new Paragraph(bill.getDepartureTimeFromMbstu().toString().split("T")[0])).setTextAlignment(TextAlignment.CENTER));
            mainTable.addCell(new Cell().add(new Paragraph(bill.getDepartureTimeFromMbstu().toString().split("T")[1])).setTextAlignment(TextAlignment.CENTER));
            mainTable.addCell(new Cell().add(new Paragraph("MBSTU")).setTextAlignment(TextAlignment.CENTER));
            mainTable.addCell(new Cell().add(new Paragraph(bill.getArrivalTimeAtTangail2().toString().split("T")[0])).setTextAlignment(TextAlignment.CENTER));
            mainTable.addCell(new Cell().add(new Paragraph(bill.getArrivalTimeAtTangail2().toString().split("T")[1])).setTextAlignment(TextAlignment.CENTER));
            mainTable.addCell(new Cell().add(new Paragraph("Tangail")).setTextAlignment(TextAlignment.CENTER));
            mainTable.addCell(new Cell().add(new Paragraph("")));

            //6th row
            mainTable.addCell(new Cell().add(new Paragraph(bill.getArrivalTimeAtTangail2().toString().split("T")[0])).setTextAlignment(TextAlignment.CENTER));
            mainTable.addCell(new Cell().add(new Paragraph(bill.getArrivalTimeAtTangail2().toString().split("T")[1])).setTextAlignment(TextAlignment.CENTER));
            mainTable.addCell(new Cell().add(new Paragraph("Tangail")).setTextAlignment(TextAlignment.CENTER));

            mainTable.addCell(new Cell().add(new Paragraph(bill.getArrivalTimeAtHisUniversity().toString().split("T")[0])).setTextAlignment(TextAlignment.CENTER));
            mainTable.addCell(new Cell().add(new Paragraph(bill.getArrivalTimeAtHisUniversity().toString().split("T")[1])).setTextAlignment(TextAlignment.CENTER));
            mainTable.addCell(new Cell().add(new Paragraph(billUser.getUniversity())).setTextAlignment(TextAlignment.CENTER));
            mainTable.addCell(new Cell().add(new Paragraph("")));

            for(int i = 1; i <= 35; i++){
                mainTable.addCell(new Cell().setHeight(19f).add(new Paragraph("")));
            }
            document.add(mainTable);

            document.add(new Paragraph("Total Travelling Days(Arrival & Departure): ").setTextAlignment(TextAlignment.LEFT));
            document.add(new Paragraph("Total Spent Days: " + bill.getTotalDayCount()).setTextAlignment(TextAlignment.LEFT));
            document.add(new Paragraph("Signature of Teacher/Officer").setTextAlignment(TextAlignment.RIGHT));
            document.add(new Paragraph("Date: _____________\n\n").setTextAlignment(TextAlignment.RIGHT));

            document.add(ls);

            document.add(new Paragraph("This tour report has been approved for preparation and payment of TA/DA bill.").setTextAlignment(TextAlignment.LEFT));
            document.add(new Paragraph("\nHead Of Department/Office").setTextAlignment(TextAlignment.RIGHT));

        }catch (Exception e){
            e.printStackTrace();
        }

        return baos.toByteArray();
    }

    public byte[] createGratuityBillPdf(User billUser, ExamCommittee examCommittee, List<GratuityBill> gratuityBillList) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try (PdfWriter writer = new PdfWriter(baos);
             PdfDocument pdfDoc = new PdfDocument(writer);
             Document document = new Document(pdfDoc)) {

            pdfDoc.setDefaultPageSize(PageSize.LEGAL);

            InputStream fontStream = getClass().getResourceAsStream("/fonts/times.ttf");

            if (fontStream == null) {
                throw new IOException("Font file not found! Check if it is in src/main/resources/fonts/times.ttf");
            }

            byte[] fontBytes = fontStream.readAllBytes();

            PdfFont font = PdfFontFactory.createFont(fontBytes, PdfEncodings.IDENTITY_H);
            document.setFont(font);

            document.add(new Paragraph("Bill No: __________").setTextAlignment(TextAlignment.RIGHT));

            InputStream is = getClass().getResourceAsStream("/static/logo.PNG");

            if (is == null) {
                throw new IOException("Logo not found in classpath! Check path: /static/logo.PNG");
            }

            float[] columnWidths = {1, 4};
            Table headerTable = new Table(UnitValue.createPercentArray(columnWidths));
            headerTable.setWidth(UnitValue.createPercentValue(100));

            byte[] imageBytes = is.readAllBytes();
            ImageData imageData = ImageDataFactory.create(imageBytes);
            Image logo = new Image(imageData).setWidth(45).setHeight(45);

            Cell logoCell = new Cell(2, 1)
                    .add(logo)
                    .setHorizontalAlignment(HorizontalAlignment.CENTER)
                    .setVerticalAlignment(VerticalAlignment.MIDDLE)
                    .setBorder(Border.NO_BORDER);
            headerTable.addCell(logoCell);

            Cell univCell = new Cell()
                    .add(new Paragraph("Mawlana Bhashani Science and Technology University")
                            .setBold()
                            .setFontSize(16)
                            .setTextAlignment(TextAlignment.CENTER))
                    .setBorder(Border.NO_BORDER)
                    .setVerticalAlignment(VerticalAlignment.BOTTOM);
            headerTable.addCell(univCell);

            Cell titleCell = new Cell()
                    .add(new Paragraph("Examination-Related Remuneration Bill Form")
                            .setFontSize(12)
                            .setTextAlignment(TextAlignment.CENTER))
                    .setBorder(Border.NO_BORDER)
                    .setVerticalAlignment(VerticalAlignment.TOP);
            headerTable.addCell(titleCell);

            document.add(headerTable);

            document.add(new Paragraph("\nThe relevant documents must be submitted to the Office of the Controller of Examinations through the Chairman of the Examination Committee upon completion of all examination-related duties. \nBill must be submitted for each course separately.").setTextAlignment(TextAlignment.CENTER).setFontSize(9));


            Table infoTable = new Table(UnitValue.createPercentArray(new float[]{10, 3}));
            infoTable.useAllAvailableWidth();
            infoTable.setMarginTop(10f);


            infoTable.addCell(createLabelValueCell("Name of Teacher: ", billUser.getName()));
            infoTable.addCell(createLabelValueCell("", ""));
            infoTable.addCell(createLabelValueCell("Designation & Address: ", billUser.getDesignation() + ", Dept. of " + billUser.getDepartment() + ", " + billUser.getUniversity()));

            document.add(infoTable);

            document.add(new Paragraph("\n"));

            document.add(new Paragraph("Details of Examination-Related Duties Given Below:").setFontSize(15).setTextAlignment(TextAlignment.CENTER).setBold());
            document.add(new Paragraph("Duration of Examination: " + examCommittee.getSemester().getSemesterHeldMonths() + ", " + examCommittee.getSemester().getSemesterHeldYear()).setTextAlignment(TextAlignment.CENTER));

            document.add(new Paragraph("\n"));

            float[] columnWidths2 = {0.8f, 3.5f, 2f, 1f, 1.5f, 1.5f, 1.5f, 1f, 1f, 1f, 1.5f, 1f};
            Table table = new Table(UnitValue.createPercentArray(columnWidths2));
            table.setWidth(UnitValue.createPercentValue(100));
            table.setFontSize(8.5f);

            String[] headers = {
                    "SL", "Nature of Work", "Exam Name", "Year", "Dept.", "Course Code",
                    "No. of Scripts/Students", "Total Days/Members", "Credit Hours", "No. of Class Tests", "Taka", "Paisa"
            };

            for(String header : headers){
                table.addHeaderCell(new Cell()
                        .add(new Paragraph(header).setBold())
                        .setTextAlignment(TextAlignment.CENTER));
            }


            String[] categories = {
                    "Question Setting", "Question Moderation", "Script Evaluation",
                    "Oral/Comprehensive Exam", "Practical/Lab/Sessional", "Tabulation",
                    "Chairman Honorarium","Member Honorarium", "Question Typing & Checking", "Thesis/Research Project",
                    "Special Topic Seminar/Term Paper/Industrial Training & Viva", "Internship/Monograph/Presentation & Viva-Voce", "Class Test"
            };

            int sl = 1;
            double totalQuesSettingBill = 0;
            double totalScriptEvaluationBill = 0;
            double grossTotal = 0;
            for(String category : categories){
                table.addCell(new Cell().add(new Paragraph(String.valueOf(sl))));
                table.addCell(new Cell().add(new Paragraph(category)));
                for(int i = 0; i < 10; i++) {
                    if(sl == 1){
                        if(i == 0) table.addCell(new Cell().add(new Paragraph(examCommittee.getSemesterYearName() + " " + examCommittee.getSemester().getSemesterParity() + " Semester")));
                        else if(i == 1)  table.addCell(new Cell().add(new Paragraph(String.valueOf(examCommittee.getSemester().getSemesterScheduledYear())).setTextAlignment(TextAlignment.CENTER)));
                        else if(i == 2) table.addCell(new Cell().add(new Paragraph("ICT").setTextAlignment(TextAlignment.CENTER)));
                        else if(i == 3){
                            StringBuilder courseCodes = new StringBuilder();
                            for(GratuityBill gratuityBill : gratuityBillList){
                                if(gratuityBill.getTaskName().equals("Question Setting")){
                                    totalQuesSettingBill += gratuityBill.getTotalBillAmount();
                                    grossTotal += gratuityBill.getTotalBillAmount();
                                    System.out.println(gratuityBill.getCourseCode() + " Ques Setting: " + gratuityBill.getTotalBillAmount());
                                    if(courseCodes.isEmpty()) courseCodes.append(gratuityBill.getCourseCode());
                                    else courseCodes.append("\n").append(gratuityBill.getCourseCode());
                                }
                            }
                            table.addCell(new Cell().add(new Paragraph(courseCodes.toString()).setTextAlignment(TextAlignment.CENTER)));
                        }
                        else if(i == 8)  table.addCell(new Cell().add(new Paragraph(String.valueOf(totalQuesSettingBill)).setTextAlignment(TextAlignment.CENTER)));
                        else table.addCell(new Cell().add(new Paragraph("").setTextAlignment(TextAlignment.CENTER)));
                        continue;
                    }
                    else if(sl == 2){
                        if(!examCommitteeService.isMember(billUser, examCommittee)){
                            table.addCell(new Cell().add(new Paragraph("").setTextAlignment(TextAlignment.CENTER)));
                            continue;
                        }
                        if(i == 8){
                            System.out.println(billUser.getName() + " is a member of this committee!");
                            double modBill = 0;
                            for(GratuityBill gratuityBill : gratuityBillList) {
                                if(gratuityBill.getTaskName().equals("Question Moderation")){
                                    grossTotal += gratuityBill.getTotalBillAmount();
                                    modBill = gratuityBill.getTotalBillAmount();
                                }
                            }
                            table.addCell(new Cell().add(new Paragraph(String.valueOf(modBill)).setTextAlignment(TextAlignment.CENTER)));
                        }
                        else table.addCell(new Cell().add(new Paragraph("").setTextAlignment(TextAlignment.CENTER)));
                        continue;
                    }
                    else if(sl == 3){
                        if(i == 3){
                            StringBuilder courseCodes = new StringBuilder();
                            for(GratuityBill gratuityBill : gratuityBillList) {
                                if(gratuityBill.getTaskName().equals("Script Evaluation")){
                                    grossTotal += gratuityBill.getTotalBillAmount();
                                    totalScriptEvaluationBill += gratuityBill.getTotalBillAmount();
                                    if(courseCodes.isEmpty()) courseCodes.append(gratuityBill.getCourseCode());
                                    else  courseCodes.append("\n").append(gratuityBill.getCourseCode());
                                }
                            }
                            table.addCell(new Cell().add(new Paragraph(courseCodes.toString()).setTextAlignment(TextAlignment.CENTER)));

                        }
                        else if(i == 4){
                            StringBuilder scriptCount = new StringBuilder();
                            for(GratuityBill gratuityBill : gratuityBillList) {
                                if(gratuityBill.getTaskName().equals("Script Evaluation")){
                                    if(scriptCount.isEmpty()) scriptCount.append(gratuityBill.getNumberOfScriptsOrStudents() + "x" + gratuityBill.getBillRate());
                                    else scriptCount.append("\n").append(gratuityBill.getNumberOfScriptsOrStudents() + "x" + gratuityBill.getBillRate());
                                }
                            }
                            table.addCell(new Cell().add(new Paragraph(scriptCount.toString()).setTextAlignment(TextAlignment.CENTER)));
                        }
                        else if(i == 8){
                            table.addCell(new Cell().add(new Paragraph(String.valueOf(totalScriptEvaluationBill)).setTextAlignment(TextAlignment.CENTER)));
                        }
                        else table.addCell(new Cell().add(new Paragraph("").setTextAlignment(TextAlignment.CENTER)));
                        continue;
                    }
                    else if(sl == 6){
                        if(i == 4){
                            StringBuilder tabuDetails = new StringBuilder();
                            if(examCommitteeService.isMember(billUser, examCommittee)){
                                for(GratuityBill gratuityBill : gratuityBillList) {
                                    if(gratuityBill.getTaskName().equals("Tabulation") || gratuityBill.getTaskName().equals("Comprehensive Tabulation")){
                                        grossTotal += gratuityBill.getTotalBillAmount();
                                        if(!tabuDetails.isEmpty()) tabuDetails.append("\n");
                                        tabuDetails.append(examCommittee.getStudentCount() +  "x" + gratuityBill.getBillRate());
                                    }
                                }
                            }
                            table.addCell(new Cell().add(new Paragraph(tabuDetails.toString()).setTextAlignment(TextAlignment.CENTER)));
                        }
                        else if(i == 8){
                            StringBuilder tabuDetails = new StringBuilder();
                            if(examCommitteeService.isMember(billUser, examCommittee)){
                                for(GratuityBill gratuityBill : gratuityBillList) {
                                    if(gratuityBill.getTaskName().equals("Tabulation") || gratuityBill.getTaskName().equals("Comprehensive Tabulation")){
                                        if(!tabuDetails.isEmpty()) tabuDetails.append("\n");
                                        tabuDetails.append(gratuityBill.getTotalBillAmount());
                                    }
                                }
                            }
                            table.addCell(new Cell().add(new Paragraph(tabuDetails.toString()).setTextAlignment(TextAlignment.CENTER)));
                        }
                        else table.addCell(new Cell().add(new Paragraph("").setTextAlignment(TextAlignment.CENTER)));
                        continue;
                    }
                    else if(sl == 7){
                       if(i == 8){
                           double chairmanBill = 0;
                           if(examCommittee.getChairman().getUserId().equals(billUser.getUserId())){
                                for(GratuityBill gratuityBill : gratuityBillList) {
                                    if(gratuityBill.getTaskName().equals("Allowance of Chairman")){
                                        grossTotal += gratuityBill.getTotalBillAmount();
                                        chairmanBill = gratuityBill.getTotalBillAmount();
                                    }
                                }
                           }
                           table.addCell(new Cell().add(new Paragraph(String.valueOf(chairmanBill)).setTextAlignment(TextAlignment.CENTER)));
                       }
                       else table.addCell(new Cell().add(new Paragraph("").setTextAlignment(TextAlignment.CENTER)));
                       continue;
                    }
                    else if(sl == 8){
                        if(i == 8){
                            double memberBill = 0;
                            if(examCommitteeService.isMember(billUser, examCommittee)){
                                for(GratuityBill gratuityBill : gratuityBillList) {
                                    if(gratuityBill.getTaskName().equals("Allowance of Member")){
                                        grossTotal += gratuityBill.getTotalBillAmount();
                                        memberBill = gratuityBill.getTotalBillAmount();
                                        System.out.println("allowance of member: " + gratuityBill.getTotalBillAmount());
                                    }
                                }
                            }
                            table.addCell(new Cell().add(new Paragraph(String.valueOf(memberBill)).setTextAlignment(TextAlignment.CENTER)));
                        }
                        else table.addCell(new Cell().add(new Paragraph("").setTextAlignment(TextAlignment.CENTER)));
                        continue;
                    }
                    else if(sl == 13){
                        if(i == 3){
                            StringBuilder courseCodes = new StringBuilder();
                            for(GratuityBill gratuityBill : gratuityBillList) {
                                if(gratuityBill.getTaskName().equals("Class Test")){
                                    grossTotal += gratuityBill.getTotalBillAmount();
                                    if(!courseCodes.isEmpty()) courseCodes.append("\n");
                                    courseCodes.append(gratuityBill.getCourseCode());
                                }
                            }
                            table.addCell(new Cell().add(new Paragraph(String.valueOf(courseCodes)).setTextAlignment(TextAlignment.CENTER)));
                        }
                        else if(i == 4){
                            table.addCell(new Cell().add(new Paragraph(String.valueOf(examCommittee.getStudentCount())).setTextAlignment(TextAlignment.CENTER)));
                        }
                        else if(i == 7){
                            StringBuilder ctCount = new StringBuilder();
                            for(GratuityBill gratuityBill : gratuityBillList) {
                                if(gratuityBill.getTaskName().equals("Class Test")){
                                    grossTotal += gratuityBill.getTotalBillAmount();
                                    if(!ctCount.isEmpty()) ctCount.append("\n");
                                    ctCount.append(gratuityBill.getNumberOfClassTests());
                                }
                            }
                            table.addCell(new Cell().add(new Paragraph(String.valueOf(ctCount)).setTextAlignment(TextAlignment.CENTER)));
                        }
                        else if(i == 8){
                            StringBuilder totalBill = new StringBuilder();
                            for(GratuityBill gratuityBill : gratuityBillList) {
                                if(gratuityBill.getTaskName().equals("Class Test")){
                                    if(!totalBill.isEmpty()) totalBill.append("\n");
                                    totalBill.append(gratuityBill.getTotalBillAmount());
                                }
                            }
                            table.addCell(new Cell().add(new Paragraph(String.valueOf(totalBill)).setTextAlignment(TextAlignment.CENTER)));
                        }
                        else table.addCell(new Cell().add(new Paragraph("").setTextAlignment(TextAlignment.CENTER)));
                        continue;
                    }
                    table.addCell(new Cell().add(new Paragraph("")));
                }
                sl++;
            }

            table.addCell(new Cell().add(new Paragraph(String.valueOf(sl))));
            table.addCell(new Cell().add(new Paragraph("Incidental Charge")));
            table.addCell(new Cell(1, 8).add(new Paragraph("Attached Voucher No: ")));
            table.addCell(new Cell().add(new Paragraph("")));
            table.addCell(new Cell().add(new Paragraph("")));


            table.addCell(new Cell(1, 9)
                    .add(new Paragraph("Total"))
                    .setTextAlignment(TextAlignment.RIGHT)
                    .setPaddingRight(10f)
                    .setBold());
            table.addCell(new Cell(1, 3).add(new Paragraph(String.valueOf(grossTotal))));
            //table.addCell(new Cell().add(new Paragraph("")));

            document.add(table);
            document.add(new Paragraph("\n\n"));

            //signatures table
            float[] sigWidths = {1, 1};
            Table sigTable = new Table(UnitValue.createPercentArray(sigWidths)).setBorder(Border.NO_BORDER);
            sigTable.setWidth(UnitValue.createPercentValue(100));

            Cell leftSig = new Cell().setBorder(Border.NO_BORDER);
            leftSig.add(new Paragraph("Signature\nChairman, Exam Committee").setFontSize(11));
            sigTable.addCell(leftSig);

            Cell rightSig = new Cell().setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.RIGHT);
            rightSig.add(new Paragraph("..................................\nExaminer's Signature").setFontSize(11));
            sigTable.addCell(rightSig);

            document.add(sigTable);

            document.add(new Paragraph("\n"));


            Paragraph payTo = new Paragraph()
                    .add(new Text("Please pay to " + billUser.getName()).setFontSize(11))
                    .add(new Text(" TK: ").setFontSize(12))
                    .add(new Text(" (In words): ").setFontSize(12))
                    .add(new Text(UtilityService.formatBDT(grossTotal)).setFontSize(11));
            document.add(payTo);
            document.add(new Paragraph("\n"));

            Table footerSigs = new Table(UnitValue.createPercentArray(new float[]{1, 1, 1})).setBorder(Border.NO_BORDER);
            footerSigs.setWidth(UnitValue.createPercentValue(100)).setMarginTop(20);

            footerSigs.addCell(new Cell().add(new Paragraph("Receiver's Signature")).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.LEFT).setFontSize(11));
            footerSigs.addCell(new Cell().add(new Paragraph("Section Officer/Assistant Controller of Examination")).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.CENTER).setFontSize(11));
            footerSigs.addCell(new Cell().add(new Paragraph("Controller of Examinations")).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.RIGHT).setFontSize(11));

            document.add(footerSigs);

        }catch (Exception ex){
            ex.printStackTrace();
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
