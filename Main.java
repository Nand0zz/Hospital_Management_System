import java.sql.Connection;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        try {
            Connection conn = DBHandler.connect();
            Scanner sc = new Scanner(System.in);

            while (true) {
                System.out.println("\n--- Welcome to Online Hospital Management System ---");
                System.out.println("1. Patient");
                System.out.println("2. Doctor");
                System.out.println("3. Exit");
                System.out.print("Choose your role: ");
                int role = sc.nextInt();
                sc.nextLine();

                switch (role) {
                    case 1 -> handlePatient(conn, sc);
                    case 2 -> handleDoctor(conn, sc);
                    case 3 -> {
                        System.out.println("Thank you for using the system!");
                        System.exit(0);
                    }
                    default -> System.out.println("Invalid choice!");
                }
            }

        } catch (Exception e) {
            System.out.println("Fatal Error: " + e.getMessage());
        }
    }

    private static void handlePatient(Connection conn, Scanner sc) {
        Person person = new Patient(conn);

        while (true) {
            System.out.println("\n--- Patient Access ---");
            System.out.println("1. Sign Up");
            System.out.println("2. Login");
            System.out.println("3. Forgot Password");
            System.out.println("4. Back");
            System.out.print("Choose an option: ");
            int choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1 -> person.signIn();
                case 2 -> {
                    if (person.login()) {
                        String email = person.getEmail();
                        Patient patient = new Patient(conn, email);
                        while (true) {
                            System.out.println("\n--- Patient Dashboard ---");
                            System.out.println("1. Personal Details Menu");
                            System.out.println("2. Appointment Menu");
                            System.out.println("3. Logout");
                            System.out.print("Choose an option: ");
                            int dashChoice = sc.nextInt();
                            sc.nextLine();
                            switch (dashChoice) {
                                case 1 -> patient.personalDetailsMenu(email);
                                case 2 -> patient.appointmentMenu(email);
                                case 3 -> {
                                    person.logout();
                                    return;
                                }
                                default -> System.out.println("Invalid option!");
                            }
                        }
                    }
                }
                case 3 -> person.forgotPassword();
                case 4 -> {
                    return;
                }
                default -> System.out.println("Invalid choice!");
            }
        }
    }

    private static void handleDoctor(Connection conn, Scanner sc) {
        Person person = new Doctor(conn); // <- Remove the email from constructor

        while (true) {
            System.out.println("\n--- Doctor Access ---");
            System.out.println("1. Sign Up");
            System.out.println("2. Login");
            System.out.println("3. Forgot Password");
            System.out.println("4. Back");
            System.out.print("Choose an option: ");
            int choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1 -> person.signIn();
                case 2 -> {
                    if (person.login()) {
                        String email = person.getEmail();
                        Doctor doctor = new Doctor(conn, email);
                        while (true) {
                            System.out.println("\n--- Doctor Dashboard ---");
                            System.out.println("1. View/Edit Personal Details");
                            System.out.println("2. Logout");
                            System.out.print("Choose an option: ");
                            int dashChoice = sc.nextInt();
                            sc.nextLine();
                            switch (dashChoice) {
                                case 1 -> doctor.doctorDetailsMenu(email);
                                case 2 -> {
                                    person.logout();
                                    return;
                                }
                                default -> System.out.println("Invalid option!");
                            }
                        }
                    }
                }
                case 3 -> person.forgotPassword();
                case 4 -> {
                    return;
                }
                default -> System.out.println("Invalid choice!");
            }
        }
    }
}