package system;
public abstract class Person {

    private String name;
    private String email;
    private String phone;

    public Person(String name, String email, String phone) {
        this.name = name;
        this.email = email;
        this.phone = phone;
    }

    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }

    public void setName(String name) {
        if (name == null || name.isEmpty())
            throw new IllegalArgumentException("Invalid name");
        this.name = name;
    }

    public void setEmail(String email) {
        if (email == null || !email.contains("@"))
            throw new IllegalArgumentException("Invalid email");
        this.email = email;
    }

    public void setPhone(String phone) {
        if (phone == null || phone.isEmpty())
            throw new IllegalArgumentException("Invalid phone");
        this.phone = phone;
    }
}
