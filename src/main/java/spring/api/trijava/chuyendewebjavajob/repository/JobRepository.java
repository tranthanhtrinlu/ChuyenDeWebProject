package spring.api.trijava.chuyendewebjavajob.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import spring.api.trijava.chuyendewebjavajob.domain.Job;

public interface JobRepository extends JpaRepository<Job, Long>, JpaSpecificationExecutor<Job> {

}
