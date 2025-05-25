package spring.api.trijava.chuyendewebjavajob.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import spring.api.trijava.chuyendewebjavajob.domain.Resume;

@Repository
public interface ResumeRepository extends JpaRepository<Resume, Long>, JpaSpecificationExecutor<Resume> {

}
