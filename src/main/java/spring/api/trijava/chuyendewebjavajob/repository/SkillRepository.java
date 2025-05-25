package spring.api.trijava.chuyendewebjavajob.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import spring.api.trijava.chuyendewebjavajob.domain.Skill;

import java.util.List;

@Repository
public interface SkillRepository extends JpaRepository<Skill, Long>, JpaSpecificationExecutor<Skill> {

    boolean existsByName(String name);

    List<Skill> findByIdIn(List<Long> id);
}
