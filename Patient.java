import customexception.*;
import customexception.InvalidInputException;

import java.sql.*;
import java.util.Scanner;

public class Patient extends Person {
    private final Scanner sc = new Scanner(System.in);
    private final DBOperations db = new DBHandler();
    private Connection conn;

    public Patient(Connection conn, String email) {
        this.conn = conn;
        this.email = email;
    }

    public Patient(Connection conn) {
    }

    // Getter for email
    public String getEmail() {
        return email;
    }

    public void personalDetailsMenu(String email) {
        while (true) {
            System.out.println("\n--- Personal Details Menu ---");
            System.out.println("1. Add Personal Details");
            System.out.println("2. View Personal Details");
            System.out.println("3. Update Personal Details");
            System.out.println("4. Delete Personal Details");
            System.out.println("5. Back to Main Menu");
            System.out.print("Choose an option: ");
            int choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1 -> addPersonalDetails(email);
                case 2 -> {
                    System.out.print("Enter Email to View Details: ");
                    String viewEmail = sc.nextLine();
                    db.viewPatientDetails(viewEmail);
                }
                case 3 -> {
                    System.out.print("Enter Email to Update Details: ");
                    String updateEmail = sc.nextLine();
                    updatePersonalDetails(updateEmail);
                }
                case 4 -> {
                    System.out.print("Enter Email to Delete Details: ");
                    String deleteEmail = sc.nextLine();
                    db.deletePatientDetails(deleteEmail);
                }
                case 5 -> {
                    return;
                }
                default -> System.out.println("Invalid option!");
            }
        }
    }

    private void addPersonalDetails(String email) {
        try {
            System.out.print("Enter Name: ");
            String name = sc.nextLine();
            if (!ValidationUtils.isValidName(name)) throw new InvalidNameException("Name must only contain letters and spaces.");

            System.out.print("Enter Age: ");
            int age = Integer.parseInt(sc.nextLine());
            if (age <= 0 || age > 120) throw new InvalidAgeException("Age must be between 1 and 120.");

            System.out.print("Enter Gender:[Female/Male/other] ");
            String gender = sc.nextLine();

            System.out.print("Enter Weight (in kg): ");
            double weight = Double.parseDouble(sc.nextLine());

            System.out.print("Enter Height (in cm): ");
            double height = Double.parseDouble(sc.nextLine());

            System.out.print("Enter Allergies (if any): ");
            String allergies = sc.nextLine();

            System.out.print("Family Diabetes History (Yes/No): ");
            String diabetesInput = sc.nextLine().trim().toLowerCase();
            boolean familyHistory = diabetesInput.equals("yes") || diabetesInput.equals("y");

            System.out.print("Enter Phone Number: ");
            String phoneInput = sc.nextLine();
            if (!ValidationUtils.isValidPhone(phoneInput)) {
                throw new InvalidPhoneNumberException("Phone number must be exactly 10 digits.");
            }
            long phone = Integer.parseInt(phoneInput);

            db.addPatientDetails(name, age, gender, weight, height, allergies, familyHistory, phone, email);

        } catch (InvalidNameException | InvalidAgeException | InvalidPhoneNumberException e) {
            System.out.println("Input Error: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Number Format Error: Please enter valid numeric values.");
        } catch (Exception e) {
            System.out.println("Unexpected Error: " + e.getMessage());
        }
    }

    private void updatePersonalDetails(String email) {
        System.out.println("Enter new details:");
        addPersonalDetails(email);
    }

    public void appointmentMenu(String email) {
        if (!db.isPatientExists(email)) {
            System.out.println("Patient personal details not found. Please enter them first.");
            return;
        }

        Appointment appointment = new Appointment(DBHandler.conn);

        while (true) {
            System.out.println("\n--- Appointment Menu ---");
            System.out.println("1. Book Appointment");
            System.out.println("2. View Appointment");
            System.out.println("3. Update Appointment");
            System.out.println("4. Delete Appointment");
            System.out.println("5. Back to Main Menu");
            System.out.print("Choose an option: ");
            int choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1 -> appointment.bookAppointment();
                case 2 -> appointment.viewAppointment();
                case 3 -> appointment.updateAppointment();
                case 4 -> appointment.deleteAppointment();

                case 5 -> {
                    return;
                }
                default -> System.out.println("Invalid option!");
            }
        }
    }
}

