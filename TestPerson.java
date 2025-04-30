import java.sql.Connection;

public class TestPerson extends Person {
    public TestPerson() {
        super(); // No connection is needed for the methods you're testing
    }
}

