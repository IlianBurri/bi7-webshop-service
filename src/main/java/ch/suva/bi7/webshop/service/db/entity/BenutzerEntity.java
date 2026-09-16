package ch.suva.bi7.webshop.service.db.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "user")
public class BenutzerEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "username", nullable = false, length = 100)
    private String username;

    @Column(name = "email", nullable = false, unique = true, length = 150)
    private String email;

    @Column(name = "password", nullable = false, length = 255)
    private String password;

    @Column(name = "isAdmin", nullable = false)
    private boolean isAdmin;

    protected BenutzerEntity() {
        // Required by JPA
    }

    public BenutzerEntity(String username, String email, String password, boolean isAdmin) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username darf nicht null/leer sein");
        }
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("E-Mail darf nicht null/leer sein");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Passwort darf nicht null/leer sein");
        }

        this.id = null;
        this.username = username.trim();
        this.email = email.trim();
        this.password = password;
        this.isAdmin = isAdmin;
    }

    public Integer getId() {
        return id;
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
