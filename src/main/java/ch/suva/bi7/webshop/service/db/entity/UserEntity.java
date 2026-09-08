package ch.suva.bi7.webshop.service.db.entity;

public class UserEntity {
    private final String username;
    private final String email;
    private final String password;
    private final boolean isAdmin;

    public UserEntity(String username,
                      String email,
                      String password,
                      boolean isAdmin) {


        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username darf nicht null/leer sein");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Passwort darf nicht null/leer sein");
        }

        this.username = username;
        this.email = email.trim();
        this.password = password;
        this.isAdmin = isAdmin;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public boolean isAdmin() {
        return isAdmin;
    }
}
