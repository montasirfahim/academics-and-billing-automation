package com.example.service;
import com.example.entity.ExamCommittee;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class UtilityService {

    public String calendarLink(LocalDateTime meetingDateTime) {
        final ZoneId DHAKA_ZONE = ZoneId.of("Asia/Dhaka");
        final DateTimeFormatter GOOGLE_CAL_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss");

        final String title = "Question Moderation Meeting";
        final String details = "Please review documents and make the necessary preparation prior to the meeting.";
        final String location = "Dept.of ICT, MBSTU";

        ZonedDateTime startDhaka = meetingDateTime.atZone(DHAKA_ZONE);
        ZonedDateTime endDhaka = startDhaka.plusHours(2); // Meeting duration is 2 hours

        // Format Dates for Google Calendar URL
        String startFormatted = startDhaka.format(GOOGLE_CAL_FORMAT);
        String endFormatted = endDhaka.format(GOOGLE_CAL_FORMAT);
        String dateRange = startFormatted + "/" + endFormatted;

        // URL Encode Event Details
        try {
            String encodedTitle = URLEncoder.encode(title, StandardCharsets.UTF_8.toString());
            String encodedDetails = URLEncoder.encode(details, StandardCharsets.UTF_8.toString());
            String encodedLocation = URLEncoder.encode(location, StandardCharsets.UTF_8.toString());

            // Final Calendar Link ---
            return "https://calendar.google.com/calendar/render?action=TEMPLATE" +
                    "&text=" + encodedTitle +
                    "&dates=" + dateRange +
                    "&details=" + encodedDetails +
                    "&location=" + encodedLocation +
                    "&ctz=" + DHAKA_ZONE.getId(); //(Client Time Zone) ensures correct placement

        } catch (Exception e) {
            e.printStackTrace();
            return "#error";
        }
    }

    public String generateCustomSemesterCode(String semesterParity, Integer semesterScheduledYear) {
        String customCode = "";
        if(semesterParity.contains("B.Sc")){
            customCode = "B.Sc_";
            if(semesterParity.contains("1st")){
                customCode += "1st_";
            }
            else customCode += "2nd_";
        }
        else if(semesterParity.contains("M.Sc")){
            customCode = "M.Sc_";
            if(semesterParity.contains("1st")){
                customCode += "1st_";
            }
            else if(semesterParity.contains("2nd")){
                customCode += "2nd_";
            }
            else customCode += "3rd_";
        }
        else if(semesterParity.contains("M.Engineering")){
            customCode = "M.Engg_";
            if(semesterParity.contains("1st")){
                customCode += "1st_";
            }
            else if(semesterParity.contains("2nd")){
                customCode += "2nd_";
            }
            else customCode += "3rd_";
        }

        customCode += String.valueOf(semesterScheduledYear);

        return customCode;
    }

    private static final String[] ones = {
            "", "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine", "Ten",
            "Eleven", "Twelve", "Thirteen", "Fourteen", "Fifteen", "Sixteen", "Seventeen", "Eighteen", "Nineteen"
    };

    private static final String[] tens = {
            "", "", "Twenty", "Thirty", "Forty", "Fifty", "Sixty", "Seventy", "Eighty", "Ninety"
    };

    public static String convertToWords(long n) {
        if (n == 0) return "";

        if (n >= 10000000) { //1 Crore(Koti)
            return convertToWords(n / 10000000) + " Crore " + convertToWords(n % 10000000);
        }
        if (n >= 100000) {   //1 Lakh
            return convertToWords(n / 100000) + " Lakh " + convertToWords(n % 100000);
        }
        if (n >= 1000) {     //1 Thousand(Hazar)
            return convertToWords(n / 1000) + " Thousand " + convertToWords(n % 1000);
        }
        if (n >= 100) {      //1 Hundred
            return convertToWords(n / 100) + " Hundred " + convertToWords(n % 100);
        }
        if (n >= 20) {
            return tens[(int)(n / 10)] + (n % 10 != 0 ? " " + ones[(int)(n % 10)] : "");
        }
        return ones[(int)n];
    }

    public static String formatBDT(double amount) {
        if (amount == 0) return "Zero Taka Only";

        long taka = (long) amount;
        long paisa = Math.round((amount - taka) * 100);

        String takaPart = convertToWords(taka).trim();
        String paisaPart = convertToWords(paisa).trim();

        StringBuilder finalResult = new StringBuilder();

        if (taka > 0) {
            finalResult.append(takaPart).append(" Taka");
        }

        if (paisa > 0) {
            //"and" if there was a Taka part
            if(taka > 0) {
                finalResult.append(" and ");
            }
            finalResult.append(paisaPart).append(" Paisa");
        }

        return finalResult.append(" Only").toString();
    }

    public static boolean validateStudentId(String studentId) {
        return studentId.length() == 7 && studentId.matches("IT\\d{5}");
    }

    public static String getBatchNameFromExamCommittee(ExamCommittee examCommittee) {
        return examCommittee.getSemesterYearName() + " " + examCommittee.getSemester().getSemesterParity() + " Semester, Session: " + examCommittee.getSession();
    }
}