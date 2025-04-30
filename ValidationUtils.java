// Utils.java
public class ValidationUtils {

    public static boolean isValidName(String name) {
        return name != null && name.matches("[a-zA-Z\\s]+");
    }

    public static boolean isValidPhone(String phone) {
        return phone != null && phone.matches("\\d{10}");
    }
    public static boolean isValidEmail(String email) {
        // Basic email regex pattern
        String regex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        return email != null && email.matches(regex);
    }

}
