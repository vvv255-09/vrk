public class User {
    private int id;
    private String fullName;
    private String login;
    private String password;
    private String role;

    public User(int id, String fullName, String login, String password, String role) {
        this.id = id;
        this.fullName = fullName;
        this.login = login;
        this.password = password;
        this.role = role;
    }

    public User(String fullName, String login, String password, String role) {
        this.fullName = fullName;
        this.login = login;
        this.password = password;
        this.role = role;
    }

    public int getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public String getRole() {
        return role;
    }

    @Override
    public String toString() {
        return "ID: " + id +
                ", ФИО: " + fullName +
                ", Логин: " + login +
                ", Роль: " + role;
    }
}