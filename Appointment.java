import customexception.*;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.Date;

public class Appointment {
    private final Connection conn;
    private final Scanner sc = new Scanner(System.in);

    public Appointment(Connection conn) {
        this.conn = conn;
    }

    // Book Appointment using only email
    public void bookAppointment() {
        try {
            System.out.print("Enter your email: ");
            String email = sc.nextLine();

            // Step 1: Validate patient email exists and fetch patient info
            PreparedStatement validate = conn.prepareStatement("SELECT * FROM Patients WHERE email = ?");
            validate.setString(1, email);
            ResultSet patientRs = validate.executeQuery();
            if (!patientRs.next()) {
                throw new InvalidInputException("Email not found in patient records.");
            }
            int patientId = patientRs.getInt("patient_id");
            String patientName = patientRs.getString("p_name");

            // Step 2: Fetch symptoms
            Statement stmt = conn.createStatement();
            ResultSet symptomRs = stmt.executeQuery("SELECT symptom_id, symptom_name FROM Symptoms");
            Map<Integer, Integer> symptomIdMap = new HashMap<>();
            int i = 1;
            System.out.println("--- Available Symptoms ---");
            while (symptomRs.next()) {
                symptomIdMap.put(i, symptomRs.getInt("symptom_id"));
                System.out.println(i + ". " + symptomRs.getString("symptom_name"));
                i++;
            }
            System.out.print("Select your symptom (by number): ");
            int symptomChoice = sc.nextInt();
            sc.nextLine();
            int symptomId = symptomIdMap.get(symptomChoice);

            // Step 3: Fetch doctors for selected symptom using specialty
            PreparedStatement docStmt = conn.prepareStatement(
                    "SELECT d.doctor_id, d.d_name, d.time_slot_online " +
                            "FROM Doctors d " +
                            "JOIN Symptoms s ON d.specialty = s.specialty " +
                            "WHERE s.symptom_id = ?"
            );
            docStmt.setInt(1, symptomId);
            ResultSet docRs = docStmt.executeQuery();

            List<Integer> doctorIds = new ArrayList<>();
            List<String> doctorNames = new ArrayList<>();
            List<String> onlineSlots = new ArrayList<>();
            i = 1;
            System.out.println("--- Available Doctors ---");
            while (docRs.next()) {
                doctorIds.add(docRs.getInt("doctor_id"));
                doctorNames.add(docRs.getString("d_name"));
                onlineSlots.add(docRs.getString("time_slot_online"));
                System.out.println(i + ". " + docRs.getString("d_name"));
                i++;
            }

// If no doctors available
            if (doctorIds.isEmpty()) {
                System.out.println("No doctors available for the selected symptom.");
                return;
            }

            System.out.print("Select doctor (by number): ");
            int doctorChoice = sc.nextInt();
            sc.nextLine();
            int doctorId = doctorIds.get(doctorChoice - 1);
            String doctorName = doctorNames.get(doctorChoice - 1);
            String doctorOnlineSlot = onlineSlots.get(doctorChoice - 1);

            System.out.println("Doctor's Online Slot: " + doctorOnlineSlot);

            System.out.print("Enter appointment type (online/offline): ");
            String appointmentType = sc.nextLine().toLowerCase();
            if (!appointmentType.equals("online") && !appointmentType.equals("offline")) {
                throw new InvalidInputException("Invalid appointment type. Must be online or offline.");
            }

            System.out.print("Enter appointment date (YYYY-MM-DD): ");
            String dateStr = sc.nextLine();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date date;
            try {
                date = sdf.parse(dateStr);
                if (date.before(new Date())) {
                    throw new InvalidDateException("Appointment date cannot be in the past.");
                }
            } catch (ParseException e) {
                throw new InvalidDateException("Invalid date format.");
            }

// handles time
            // handles time
            String time;
            if (appointmentType.equals("online")) {
                // Parse the time_slot_online like "3:00 PM - 6:00 PM"
                String[] parts = doctorOnlineSlot.split(" - ");
                if (parts.length != 2) {
                    throw new TimeException("Doctor's online slot format is invalid.");
                }

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("h:mm a", Locale.ENGLISH);

                LocalTime startTime = LocalTime.parse(parts[0].trim(), formatter);
                LocalTime endTime = LocalTime.parse(parts[1].trim(), formatter);

                // Generate slots at 30-minute intervals
                List<String> availableSlots = new ArrayList<>();
                while (!startTime.isAfter(endTime.minusMinutes(30))) {
                    availableSlots.add(startTime.toString() + ":00");
                    startTime = startTime.plusMinutes(30);
                }

                // Display available time slots
                System.out.println("Available Time Slots:");
                for (int j = 0; j < availableSlots.size(); j++) {
                    System.out.println((j + 1) + ". " + availableSlots.get(j));
                }

                // Let patient pick a slot
                System.out.print("Select your time slot (by number): ");
                int timeChoice = sc.nextInt();
                sc.nextLine();
                if (timeChoice < 1 || timeChoice > availableSlots.size()) {
                    throw new TimeException("Invalid time slot selected.");
                }

                time = availableSlots.get(timeChoice - 1);
            }

            else {
                System.out.print("Enter time slot (HH:MM:SS): ");
                time = sc.nextLine();
                if (!time.matches("\\d{2}:\\d{2}:\\d{2}")) {
                    throw new TimeException("Time must be in HH:MM:SS format.");
                }
            }
            // Prevent duplicate booking
            PreparedStatement checkDuplicate = conn.prepareStatement(
                    "SELECT * FROM Appointments WHERE patient_id = ? AND doctor_id = ? AND appointment_date = ? AND time_slot = ?"
            );
            checkDuplicate.setInt(1, patientId);
            checkDuplicate.setInt(2, doctorId);
            checkDuplicate.setString(3, dateStr);
            checkDuplicate.setString(4, time);
            ResultSet dupRs = checkDuplicate.executeQuery();
            if (dupRs.next()) {
                throw new DuplicateAppointmentException("You already have an appointment booked with this doctor at the selected time.");
            }


            PreparedStatement insert = conn.prepareStatement(
                    "INSERT INTO Appointments (email, patient_id, doctor_id, symptom_id, p_name, d_name, symptom_name, appointment_type, appointment_date, time_slot) " +
                            "SELECT ?, ?, ?, ?, ?, d.d_name, s.symptom_name, ?, ?, ? FROM Doctors d, Symptoms s WHERE d.doctor_id = ? AND s.symptom_id = ?"
            );
            insert.setString(1, email);
            insert.setInt(2, patientId);
            insert.setInt(3, doctorId);
            insert.setInt(4, symptomId);
            insert.setString(5, patientName);
            insert.setString(6, appointmentType);
            insert.setString(7, dateStr);
            insert.setString(8, time);
            insert.setInt(9, doctorId);
            insert.setInt(10, symptomId);
            insert.executeUpdate();

            System.out.println("Appointment booked successfully!");

        } catch (SQLException | InvalidInputException | InvalidDateException | TimeException |
                 DuplicateAppointmentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public void viewAppointment() {
        try {
            System.out.print("Enter your email: ");
            String email = sc.nextLine();

            PreparedStatement ps = conn.prepareStatement("SELECT p_name, symptom_name, d_name, appointment_type, appointment_date, time_slot FROM Appointments WHERE email = ?");
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();

            boolean found = false;
            System.out.println("\n--- Your Appointments ---");
            while (rs.next()) {
                found = true;
                System.out.println("Patient: " + rs.getString("p_name"));
                System.out.println("Symptom: " + rs.getString("symptom_name"));
                System.out.println("Doctor: " + rs.getString("d_name"));
                System.out.println("Type: " + rs.getString("appointment_type"));
                System.out.println("Date: " + rs.getDate("appointment_date"));
                System.out.println("Time: " + rs.getTime("time_slot"));
                System.out.println("---------------------------");
            }

            if (!found) {
                System.out.println("No appointments found.");
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public void updateAppointment() {
        try {
            System.out.print("Enter your email: ");
            String email = sc.nextLine();

            PreparedStatement delete = conn.prepareStatement("DELETE FROM Appointments WHERE email = ?");
            delete.setString(1, email);
            delete.executeUpdate();

            System.out.println("Please fill new appointment details:");
            bookAppointment();

        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public void deleteAppointment() {
        try {
            System.out.print("Enter your email: ");
            String email = sc.nextLine();

            PreparedStatement delete = conn.prepareStatement("DELETE FROM Appointments WHERE email = ?");
            delete.setString(1, email);
            int rows = delete.executeUpdate();

            if (rows > 0) {
                System.out.println("Appointment(s) deleted successfully.");
            } else {
                System.out.println("No appointments found to delete.");
            }

        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
