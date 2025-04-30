import java.sql.*;

public class DBHandler implements DBOperations {

    private static final String URL = "jdbc:mysql://localhost:3306/hospital_management";
    private static final String USER = "root";
    private static final String PASSWORD = "helloworld";

    static Connection conn;

    // Method to get the DB connection
    public static Connection connect() throws SQLException {
        if (conn == null || conn.isClosed()) {
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
        }
        return conn;
    }

    public void addPatientDetails(String name, int age, String gender,
                                  double weight, double height, String allergies,
                                  boolean familyHistory, long phone, String email) {
        try {
            connect();
            PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO Patients (p_name, age, gender, weight, height, allergies, family_diabetes_history, phone_number, email) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?) " +
                            "ON DUPLICATE KEY UPDATE p_name=?, age=?, gender=?, weight=?, height=?, allergies=?, family_diabetes_history=?, phone_number=?"
            );

            // Set values for INSERT
            ps.setString(1, name);
            ps.setInt(2, age);
            ps.setString(3, gender);
            ps.setDouble(4, weight);
            ps.setDouble(5, height);
            ps.setString(6, allergies);
            ps.setBoolean(7, familyHistory);
            ps.setInt(8, (int) phone);
            ps.setString(9, email);

            // Set values for UPDATE (same as INSERT except email, which is primary key)
            ps.setString(10, name);
            ps.setInt(11, age);
            ps.setString(12, gender);
            ps.setDouble(13, weight);
            ps.setDouble(14, height);
            ps.setString(15, allergies);
            ps.setBoolean(16, familyHistory);
            ps.setInt(17, (int) phone);

            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Patient details saved successfully.");
            } else {
                System.out.println("No changes made to the patient record.");
            }

        } catch (SQLException e) {
            System.out.println("Error while adding patient details: " + e.getMessage());
        }
    }



    @Override
    public void updatePatientDetails(String name, int age, String gender, double weight, double height,
                                     String allergies, boolean familyDiabetesHistory, long phone, String email) {
        String query = "UPDATE patients SET p_name = ?, age = ?, gender = ?, weight = ?, height = ?, allergies = ?, family_diabetes_history = ?, phone_number = ? WHERE email = ?";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, name);
            ps.setInt(2, age);
            ps.setString(3, gender);
            ps.setDouble(4, weight);
            ps.setDouble(5, height);
            ps.setString(6, allergies);
            ps.setBoolean(7, familyDiabetesHistory);
            ps.setInt(8, (int) phone);
            ps.setString(9, email);
            int rowsUpdated = ps.executeUpdate();
            System.out.println(rowsUpdated > 0 ? "Patient details updated successfully." : "No patient found with given email.");
        } catch (SQLException e) {
            System.out.println("Database error while updating: " + e.getMessage());
        }
    }


    @Override
    public void viewPatientDetails(String email) {
        String query = "SELECT * FROM patients WHERE email = ?";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                System.out.println("\n--- Patient Details ---");
                System.out.println("Name: " + rs.getString("p_name"));
                System.out.println("Age: " + rs.getInt("age"));
                System.out.println("Gender: " + rs.getString("gender"));
                System.out.println("Weight: " + rs.getDouble("weight"));
                System.out.println("Height: " + rs.getDouble("height"));
                System.out.println("Allergies: " + rs.getString("allergies"));
                System.out.println("Family Diabetes History: " + rs.getBoolean("family_diabetes_history"));
                System.out.println("Phone: " + rs.getString("phone_number"));
            } else {
                System.out.println("No patient found with the given email.");
            }
        } catch (SQLException e) {
            System.out.println("Error fetching patient details: " + e.getMessage());
        }
    }

    @Override
    public void deletePatientDetails(String email) {
        String checkQuery = "SELECT * FROM patients WHERE email = ?";
        String deleteQuery = "DELETE FROM patients WHERE email = ?";
        try (PreparedStatement checkPs = conn.prepareStatement(checkQuery)) {
            checkPs.setString(1, email);
            ResultSet rs = checkPs.executeQuery();
            if (rs.next()) {
                try (PreparedStatement deletePs = conn.prepareStatement(deleteQuery)) {
                    deletePs.setString(1, email);
                    deletePs.executeUpdate();
                    System.out.println("Patient details deleted successfully.");
                }
            } else {
                System.out.println("No patient found with the given email.");
            }
        } catch (SQLException e) {
            System.out.println("Database error while deleting: " + e.getMessage());
        }
    }




    @Override
    public boolean isPatientExists(String email) {
        try {
            connect();
            PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM Patients WHERE email = ?");
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        } catch (SQLException e) {
            System.out.println("Error checking patient: " + e.getMessage());
        }
        return false;
    }
    @Override
    public void addDoctorDetails(String name, int age, String gender, String phone, String specialty,
                                 double fee, String timeSlot, String email) {
        try {
            connect();
            PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO Doctors (d_name, age, gender, phone_number, specialty, consultation_fee, time_slot_online, email) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?, ?) " +
                            "ON DUPLICATE KEY UPDATE d_name=?, age=?, gender=?, phone_number=?, specialty=?, consultation_fee=?, time_slot_online=?"
            );

            ps.setString(1, name);
            ps.setInt(2, age);
            ps.setString(3, gender);
            ps.setString(4, phone);
            ps.setString(5, specialty);
            ps.setDouble(6, fee);
            ps.setString(7, timeSlot);
            ps.setString(8, email);

            ps.setString(9, name);
            ps.setInt(10, age);
            ps.setString(11, gender);
            ps.setString(12, phone);
            ps.setString(13, specialty);
            ps.setDouble(14, fee);
            ps.setString(15, timeSlot);

            int rows = ps.executeUpdate();
            System.out.println(rows > 0 ? "Doctor details saved successfully." : "No changes made.");
        } catch (SQLException e) {
            System.out.println("Error while saving doctor details: " + e.getMessage());
        }
    }
    @Override
    public void viewDoctorDetails(String email) {
        try {
            connect();
            String query = "SELECT * FROM Doctors WHERE email = ?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                System.out.println("\n--- Doctor Profile ---");
                System.out.println("Name: " + rs.getString("d_name"));
                System.out.println("Age: " + rs.getInt("age"));
                System.out.println("Gender: " + rs.getString("gender"));
                System.out.println("Phone Number: " + rs.getString("phone_number"));
                System.out.println("Specialty: " + rs.getString("specialty"));
                System.out.println("Consultation Fee: ₹" + rs.getDouble("consultation_fee"));
                System.out.println("Online Slot Timing: " + rs.getString("time_slot_online"));
            } else {
                System.out.println("Doctor not found with the provided email.");
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving doctor details: " + e.getMessage());
        }
    }
    @Override
    public void deleteDoctorDetails(String email) {
        try {
            connect();
            String query = "DELETE * from doctors WHERE email = ?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, email);
            int rows = ps.executeUpdate();
            System.out.println(rows > 0 ? "Doctor profile deleted successfully." : "No doctor found with the given email.");
        } catch (SQLException e) {
            System.out.println("Error while deleting doctor: " + e.getMessage());
        }
    }
    @Override
    public void updateDoctorDetails(String name, int age, String gender, String phone, String specialty,
                                    double fee, String timeSlot, String email) {
        try {
            connect();
            String query = "UPDATE Doctors SET d_name=?, age=?, gender=?, phone_number=?, specialty=?, consultation_fee=?, time_slot_online=? WHERE email = ?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, name);
            ps.setInt(2, age);
            ps.setString(3, gender);
            ps.setString(4, phone);
            ps.setString(5, specialty);
            ps.setDouble(6, fee);
            ps.setString(7, timeSlot);
            ps.setString(8, email);

            int rows = ps.executeUpdate();
            System.out.println(rows > 0 ? "Doctor profile updated successfully." : "No doctor found with the provided email.");
        } catch (SQLException e) {
            System.out.println("Error updating doctor profile: " + e.getMessage());
        }
    }
    @Override
    public void viewDoctorAppointments(String doctorEmail) {
        try {
            connect();
            String query = """
                SELECT a.appointment_id, a.p_name, a.symptom_name, a.appointment_date, a.time_slot, a.appointment_type
                FROM Appointments a
                JOIN Doctors d ON a.doctor_id = d.doctor_id
                WHERE d.email = ?
            """;
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, doctorEmail);
            ResultSet rs = ps.executeQuery();

            System.out.println("\n--- Appointments for Doctor ---");
            boolean found = false;
            while (rs.next()) {
                found = true;
                System.out.println("Appointment ID: " + rs.getInt("appointment_id"));
                System.out.println("Patient Name: " + rs.getString("p_name"));
                System.out.println("Symptom: " + rs.getString("symptom_name"));
                System.out.println("Date: " + rs.getDate("appointment_date"));
                System.out.println("Time: " + rs.getTime("time_slot"));
                System.out.println("Type: " + rs.getString("appointment_type"));
                System.out.println("-------------------------------------");
            }
            if (!found) {
                System.out.println("No appointments found for this doctor.");
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving appointments: " + e.getMessage());
        }
    }
    @Override
    public boolean doesDoctorExist(String email) {
        try {
            connect();
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM d_registration WHERE email = ?");
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            System.out.println("Error checking doctor existence: " + e.getMessage());
            return false;
        }
    }
}

