package spring.api.trijava.chuyendewebjavajob.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import spring.api.trijava.chuyendewebjavajob.util.SecurityUtil;
import spring.api.trijava.chuyendewebjavajob.util.constant.GenderEnum;

import java.time.Instant;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name;

    @NotBlank(message = "email không được để trống")
    private String email;

    @NotBlank(message = "password không được để trống")
    private String password;

    private int age;

    @Enumerated(EnumType.STRING)
    private GenderEnum gender;

    private String address;

    @Column(columnDefinition = "MEDIUMTEXT")
    private String refreshToken;

    private Instant createdAt;
    private Instant updatedAt;
    private String createdBy;
    private String updatedBy;

    @ManyToOne
    @JoinColumn(name = "company_id")
    private Company company;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    @JsonIgnore
    List<Resume> resumes;

    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;

    // Lưu thông tin người dùng đăng nhập, thời gian khi thao tác create, update
    @PrePersist
    public void handleBeforeCreate() {
        try {
            this.createdBy = SecurityUtil.getCurrentUserLogin().isPresent() == true
                    ? SecurityUtil.getCurrentUserLogin().get()
                    : "system"; // Đặt mặc định là "system" cho register
            this.createdAt = Instant.now();
        } catch (Exception e) {
            // Nếu không có authentication context (như khi register)
            this.createdBy = "system";
            this.createdAt = Instant.now();
        }
    }

    @PreUpdate
    public void handleBeforeUpdate() {
        try {
            this.updatedBy = SecurityUtil.getCurrentUserLogin().isPresent() == true
                    ? SecurityUtil.getCurrentUserLogin().get()
                    : "system";
            this.updatedAt = Instant.now();
        } catch (Exception e) {
            this.updatedBy = "system";
            this.updatedAt = Instant.now();
        }
    }
}
