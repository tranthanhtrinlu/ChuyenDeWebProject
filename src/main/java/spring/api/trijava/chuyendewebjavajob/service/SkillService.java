package spring.api.trijava.chuyendewebjavajob.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import spring.api.trijava.chuyendewebjavajob.domain.Skill;
import spring.api.trijava.chuyendewebjavajob.domain.response.ResultPaginationDTO;
import spring.api.trijava.chuyendewebjavajob.repository.SkillRepository;

import java.util.Optional;

@Service
public class SkillService {

    private final SkillRepository skillRepository;

    public SkillService(SkillRepository skillRepository) {
        this.skillRepository = skillRepository;
    }

    public boolean isNameExist(String name) {
        return this.skillRepository.existsByName(name);
    }

    public Skill fetchSkillById(long id) {
        Optional<Skill> s = this.skillRepository.findById(id);
        if (s.isPresent()) {
            return s.get();
        }
        return null;
    }

    public Skill createSkill(Skill s) {
        return this.skillRepository.save(s);
    }

    public Skill updateSkill(Skill s) {
        return this.skillRepository.save(s);
    }

    public void deleteSkill(long id) {
        // delete job (inside job_skill table)
        Optional<Skill> s = this.skillRepository.findById(id);
        Skill currentSkill = s.get();

        currentSkill.getJobs().forEach(job -> job.getSkills().remove(currentSkill));

        // delete subscriber (inside subscriber_skill table)
        currentSkill.getSubscribers().forEach(sub -> sub.getSkills().remove(currentSkill));

        // delete skill
        this.skillRepository.delete(currentSkill);
    }

    public ResultPaginationDTO fetchAllSkill(Specification<Skill> spec, Pageable pageable) {
        Page<Skill> p = this.skillRepository.findAll(spec, pageable);

        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();

        // Lấy từ Fe
        mt.setCurrentPage(pageable.getPageNumber() + 1);
        mt.setPageSize(pageable.getPageSize());

        // lấy từ database
        mt.setTotalPages(p.getTotalPages());
        mt.setTotalElements(p.getTotalElements());

        rs.setMeta(mt);
        rs.setResult(p.getContent());

        return rs;
    }
}
