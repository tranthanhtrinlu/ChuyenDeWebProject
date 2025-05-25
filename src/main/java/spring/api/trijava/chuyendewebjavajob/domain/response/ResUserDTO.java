package spring.api.trijava.chuyendewebjavajob.domain.response;

import java.time.Instant;

import spring.api.trijava.chuyendewebjavajob.util.constant.GenderEnum;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResUserDTO {
    private long id;
    private String email;
    private String name;
    private GenderEnum gender;
    private String address;
    private int age;
    private Instant updatedAt;
    private Instant createdAt;
    private CompanyUser companyUser;

    private RoleUser roleUser;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CompanyUser {
        private long id;
        private String name;
    }

    @Getter
    @Setter
    public static class RoleUser {
        private long id;
        private String name;
    }
}
