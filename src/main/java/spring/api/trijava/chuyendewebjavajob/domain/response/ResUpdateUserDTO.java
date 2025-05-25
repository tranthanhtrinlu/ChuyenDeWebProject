package spring.api.trijava.chuyendewebjavajob.domain.response;

import java.time.Instant;

import spring.api.trijava.chuyendewebjavajob.util.constant.GenderEnum;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
public class ResUpdateUserDTO {
    private long id;
    private String name;
    private GenderEnum gender;
    private String address;
    private int age;
    private Instant updatedAt;

    private CompanyUser companyUser;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CompanyUser {
        private long id;
        private String name;
    }
}
