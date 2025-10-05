package com.example.service;
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
        // --- 1. Define Constants and Event Details ---
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
}