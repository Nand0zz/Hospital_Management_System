import java.sql.Connection;
public interface DBOperations {
        void addPatientDetails(String name, int age, String gender,
                               double weight, double height, String allergies,
                               boolean familyHistory, long phone, String email);


        void updatePatientDetails(String name, int age, String gender, double weight, double height,
                              String allergies, boolean familyDiabetesHistory, long phone, String email);

        void viewPatientDetails(String email);

        void deletePatientDetails(String email);

        boolean isPatientExists(String email);

        void addDoctorDetails(String name, int age, String gender, String phone, String specialty,
                              double fee, String timeSlot, String email);

        void deleteDoctorDetails(String email);

        void viewDoctorAppointments(String email);

        void updateDoctorDetails(String name, int age, String gender, String phone, String specialty,
                                 double fee, String timeSlot, String email);

        boolean doesDoctorExist(String email);

        void viewDoctorDetails(String email);
}

