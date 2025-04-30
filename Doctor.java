import customexception.*;
import java.sql.*;
import java.util.Scanner;

public class Doctor extends Person {
    private final Scanner sc = new Scanner(System.in);
    private final DBOperations db = new DBHandler();
    private Connection conn;

    public Doctor(Connection conn) {
        this.conn = conn;
        this.email = email;
    }

    public Doctor(Connection conn, String email) {
        this.conn = conn;
        this.email = email;

    }

    public void doctorDetailsMenu(String email) {
        while (true) {
            System.out.println("\n--- Doctor Details Menu ---");
            System.out.println("1. Add Personal Details");
            System.out.println("2. View Personal Details");
            System.out.println("3. Update Personal Details");
            System.out.println("4. Delete Personal Details");
            System.out.println("5. View Appointments");
            System.out.println("6. Back to Main Menu");
            System.out.print("Choose an option: ");
            int choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1 -> addDoctorDetails(email);
                case 2 -> db.viewDoctorDetails(email);
                case 3 -> updateDoctorDetails();
                case 4 -> db.deleteDoctorDetails(email);
                case 5 -> db.viewDoctorAppointments(email);
                case 6 -> { return; }
                default -> System.out.println("Invalid option!");
            }
        }
    }

    private void addDoctorDetails(String email) {
        try {
            System.out.print("Enter Name: ");
            String name = sc.nextLine();
            if (!ValidationUtils.isValidName(name))
                throw new InvalidNameException("Name must only contain letters and spaces.");

            System.out.print("Enter Age: ");
            int age = Integer.parseInt(sc.nextLine());
            if (age <= 0 || age > 120)
                throw new InvalidAgeException("Age must be between 1 and 120.");

            System.out.print("Enter Gender: ");
            String gender = sc.nextLine();

            System.out.print("Enter Phone Number: ");
            String phone = sc.nextLine();
            if (!ValidationUtils.isValidPhone(phone))
                throw new InvalidPhoneNumberException("Invalid phone number format.");

            System.out.print("Enter Speciality: ");
            String specialty = sc.nextLine();

            System.out.print("Enter Consultation Fee: ");
            double fee = Double.parseDouble(sc.nextLine());

            System.out.print("Enter Online Time Slot (e.g. 10:00:00-13:00:00): ");
            String slot = sc.nextLine();

            if (!ValidationUtils.isValidEmail(email))
                throw new InvalidEmailException("Invalid email format.");

            db.addDoctorDetails(name, age, gender, phone, specialty, fee, slot, email);

        } catch (InvalidNameException | InvalidAgeException | InvalidPhoneNumberException | InvalidEmailException e) {
            System.out.println("Input Error: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Number Format Error: Please enter valid numeric values.");
        } catch (Exception e) {
            System.out.println("Unexpected Error: " + e.getMessage());
        }
    }

    private void updateDoctorDetails() {
        try {
            System.out.println("\n--- Update Doctor Personal Details ---");

            System.out.print("Enter Your Email: ");
            String email = sc.nextLine();
            if (!ValidationUtils.isValidEmail(email))
                throw new InvalidEmailException("Invalid email format.");

            if (!db.doesDoctorExist(email)) {
                System.out.println("Doctor with this email does not exist.");
                return;
            }

            System.out.print("Enter New Name: ");
            String name = sc.nextLine();
            if (!ValidationUtils.isValidName(name))
                throw new InvalidNameException("Name must only contain letters and spaces.");

            System.out.print("Enter New Age: ");
            int age = Integer.parseInt(sc.nextLine());
            if (age <= 0 || age > 120)
                throw new InvalidAgeException("Age must be between 1 and 120.");

            System.out.print("Enter New Gender: ");
            String gender = sc.nextLine();

            System.out.print("Enter New Phone Number: ");
            String phone = sc.nextLine();
            if (!ValidationUtils.isValidPhone(phone))
                throw new InvalidPhoneNumberException("Invalid phone number format.");

            System.out.print("Enter New Speciality: ");
            String specialty = sc.nextLine();

            System.out.print("Enter New Consultation Fee: ");
            double fee = Double.parseDouble(sc.nextLine());

            System.out.print("Enter New Online Time Slot (e.g. 10:00:00-13:00:00): ");
            String slot = sc.nextLine();

            db.updateDoctorDetails(name, age, gender, phone, specialty, fee, slot, email);

        } catch (InvalidNameException | InvalidAgeException | InvalidPhoneNumberException | InvalidEmailException e) {
            System.out.println("Input Error: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Number Format Error: Please enter valid numeric values.");
        } catch (Exception e) {
            System.out.println("Unexpected Error: " + e.getMessage());
        }
    }
}