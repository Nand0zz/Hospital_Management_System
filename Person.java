import customexception.InvalidEmailException;
import customexception.InvalidNameException;
import customexception.InvalidPasswordException;

import java.sql.*;
import java.util.Scanner;
import java.util.regex.Pattern;

public abstract class Person {
    protected String name;
    protected String email;
    protected String password;
    protected Scanner sc = new Scanner(System.in);
    protected Connection conn;

    public Person() {
        // Removed this.conn = conn; as it's redundant.
    }

    // Ask user for role before sign-up/login
    public String askRole() {
        String role = "";
        while (true) {
            System.out.print("Enter role (patient/doctor): ");
            role = sc.nextLine().trim().toLowerCase();
            if (role.equals("patient") || role.equals("doctor")) break;
            System.out.println("Invalid role. Please enter either 'patient' or 'doctor'.");
        }
        return role;
    }

    public void signIn() {
        try {
            System.out.println("--- Sign Up ---");
            String role = askRole();

            System.out.print("Enter name: ");
            name = sc.nextLine();
            validateName(name);

            System.out.print("Enter email: ");
            email = sc.nextLine();
            validateEmail(email);

            System.out.print("Enter password (alphanumeric only): ");
            password = sc.nextLine();
            validatePassword(password);

            // Initialize DBHandler and establish connection
            DBHandler dbHandler = new DBHandler();
            conn = dbHandler.connect(); // Use the instance variable 'conn' here
            String table = role.equals("patient") ? "p_registration" : "d_registration";

            PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO " + table + " (name, email, password) VALUES (?, ?, ?)"
            );
            ps.setString(1, name);
            ps.setString(2, email);
            ps.setString(3, password);
            ps.executeUpdate();
            System.out.println("Signed up successfully as " + role + "!\n");

        } catch (InvalidNameException | InvalidEmailException | InvalidPasswordException e) {
            System.out.println("Validation Error: " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
        }
    }

    public String getEmail() {
        return this.email;
    }

    public boolean login() {
        System.out.println("--- Login ---");
        String role = askRole();  // Assuming askRole() prompts for the role

        System.out.print("Enter email: ");
        String enteredEmail = sc.nextLine();
        System.out.print("Enter password: ");
        String enteredPassword = sc.nextLine();

        // Simple email format validation using regex
        String emailPattern = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        boolean isValidEmail = Pattern.matches(emailPattern, enteredEmail);

        // Simple password validation
        boolean isValidPassword = enteredPassword.length() >= 6;  // Example: password must be at least 6 characters long

        if (isValidEmail && isValidPassword) {
            try {
                // Initialize DBHandler and establish connection
                DBHandler dbHandler = new DBHandler();
                conn = dbHandler.connect();
                String table = role.equals("patient") ? "p_registration" : "d_registration";

                PreparedStatement ps = conn.prepareStatement(
                        "SELECT * FROM " + table + " WHERE email = ? AND password = ?"
                );
                ps.setString(1, enteredEmail);
                ps.setString(2, enteredPassword);
                ResultSet rs = ps.executeQuery();

                if (rs.next()) {
                    System.out.println("Login successful as " + role + "!\n");
                    this.name = rs.getString("name");
                    this.email = enteredEmail;
                    this.password = enteredPassword;
                    return true;
                } else {
                    System.out.println("Invalid credentials.\n");
                    return false;
                }
            } catch (SQLException e) {
                System.out.println("SQL Error: " + e.getMessage());
                return false;
            }
        } else {
            System.out.println("Invalid email or password format.\n");
            return false;
        }
    }

    public void forgotPassword() {
        try {
            System.out.println("--- Forgot Password ---");
            String role = askRole();

            System.out.print("Enter registered email: ");
            String enteredEmail = sc.nextLine();
            validateEmail(enteredEmail);

            System.out.print("Enter new password (alphanumeric): ");
            String newPassword = sc.nextLine();
            validatePassword(newPassword);

            // Initialize DBHandler and establish connection
            DBHandler dbHandler = new DBHandler();
            conn = dbHandler.connect();
            String table = role.equals("patient") ? "p_registration" : "d_registration";

            PreparedStatement ps = conn.prepareStatement(
                    "UPDATE " + table + " SET password = ? WHERE email = ?"
            );
            ps.setString(1, newPassword);
            ps.setString(2, enteredEmail);
            int rows = ps.executeUpdate();

            if (rows > 0) {
                System.out.println("Password updated successfully!\n");
            } else {
                System.out.println("No such email found.\n");
            }

        } catch (InvalidEmailException | InvalidPasswordException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
        }
    }

    public void signOut() {
        try {
            System.out.println("--- Delete Account ---");
            String role = askRole();

            // Initialize DBHandler and establish connection
            DBHandler dbHandler = new DBHandler();
            conn = dbHandler.connect();
            String table = role.equals("patient") ? "p_registration" : "d_registration";

            PreparedStatement ps = conn.prepareStatement(
                    "DELETE FROM " + table + " WHERE email = ?"
            );
            ps.setString(1, email);
            int rows = ps.executeUpdate();

            if (rows > 0) {
                System.out.println("Account deleted successfully.\n");
            } else {
                System.out.println("No such account found.\n");
            }

            System.exit(0);
        } catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
        }
    }

    public void logout() {
        System.out.println("You have been logged out. Goodbye!");
        System.exit(0);
    }

    // ---------- Validation Methods ----------
    public static void validateName(String name) throws InvalidNameException {
        if (!name.matches("[A-Za-z ]+")) {
            throw new InvalidNameException("Name must contain only alphabets and spaces.");
        }
    }

    public static void validateEmail(String email) throws InvalidEmailException {
        if (!email.contains("@") || !email.contains(".")) {
            throw new InvalidEmailException("Email must contain '@' and '.' characters.");
        }
    }

    public static void validatePassword(String password) throws InvalidPasswordException {
        if (password.length() < 6) {
            throw new InvalidPasswordException("Password must be at least 6 characters long.");
        }
        if (!password.matches("[A-Za-z0-9]+")) {
            throw new InvalidPasswordException("Password must be alphanumeric (no special characters).");
        }
        if (!password.matches(".*[A-Za-z].*") || !password.matches(".*[0-9].*")) {
            throw new InvalidPasswordException("Password must contain both letters and numbers.");
        }
    }
}
