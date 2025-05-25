package spring.api.trijava.chuyendewebjavajob.domain.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReqLoginDTO {

    @NotBlank(message = "username không được trống")
    private String username;

    @NotBlank(message = "password không được trống")
    private String password;
}
