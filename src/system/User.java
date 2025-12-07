package system;

public class User {

    private int id;
    private String name;
    private String email;
    private String phone;
    private String town;
    private UserType type;
    private String passwordHash;   

    public User(int id, String name, String email, String phone,
                String town, UserType type, String passwordHash) {

        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.town = town;
        this.type = type;
        this.passwordHash = passwordHash;
    }

    public int getId() { 
        return id; 
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getTown() {
        return town;
    }

    public UserType getType() { 
        return type; 
    }

    public String getPasswordHash() { 
        return passwordHash; 
    }

    @Override
    public String toString() {
        return id + "," + name + "," + email + "," + phone + ","
                + town + "," + type + "," + passwordHash;
    }
}
