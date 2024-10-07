package ttt.test.admin;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "admin")
public class Admin {

    @Id
    private String adminId;

    @Column(name = "admin_name", nullable = false, length = 100)
    private String adminName;

    @Column(name = "admin_password", nullable = false, length = 100)
    private String adminPassword;

    // 기본 생성자
    public Admin() {
        this.adminId = UUID.randomUUID().toString(); // 기본 생성자에서 UUID 생성
    }

    // 필드를 포함한 생성자
    public Admin(String adminName, String adminPassword) {
        this.adminId = UUID.randomUUID().toString();
        this.adminName = adminName;
        this.adminPassword = adminPassword;
    }
}
