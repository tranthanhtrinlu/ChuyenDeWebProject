package spring.api.trijava.chuyendewebjavajob.domain.response.file;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ResUpdateFileDTO {
    private String fileName;
    private Instant uploadedAt;
}
