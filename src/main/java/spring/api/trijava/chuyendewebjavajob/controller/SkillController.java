package spring.api.trijava.chuyendewebjavajob.controller;

import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import spring.api.trijava.chuyendewebjavajob.domain.Skill;
import spring.api.trijava.chuyendewebjavajob.domain.response.ResultPaginationDTO;
import spring.api.trijava.chuyendewebjavajob.service.SkillService;
import spring.api.trijava.chuyendewebjavajob.util.annotation.ApiMessage;
import spring.api.trijava.chuyendewebjavajob.util.error.IdInvalidException;

@RestController
@RequestMapping("/api/v1")
public class SkillController {
    private final SkillService skillService;

    public SkillController(SkillService skillService) {
        this.skillService = skillService;
    }

    @PostMapping("/skills")
    @ApiMessage("Create a skill")
    public ResponseEntity<Skill> create(@Valid @RequestBody Skill s) throws IdInvalidException {

        // check name
        if (s.getName() != null && this.skillService.isNameExist(s.getName())) {
            throw new IdInvalidException("Skill name = " + s.getName() + " đã tồn tại");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(this.skillService.createSkill(s));
    }

    @PutMapping("/skills")
    @ApiMessage("Update a skill")
    public ResponseEntity<Skill> update(@Valid @RequestBody Skill s) throws IdInvalidException {

        // check id
        Skill currentSkill = this.skillService.fetchSkillById(s.getId());
        if (currentSkill == null) {
            throw new IdInvalidException("Skill id = " + s.getId() + "không đã tồn tại");
        }

        // check name
        if (s.getName() != null && this.skillService.isNameExist(s.getName())) {
            throw new IdInvalidException("Skill name = " + s.getName() + " đã tồn tại");
        }

        currentSkill.setName(s.getName());
        return ResponseEntity.ok().body(this.skillService.updateSkill(currentSkill));
    }

    @DeleteMapping("/skills/{id}")
    @ApiMessage("Delete a skill")
    public ResponseEntity<Void> delete(@PathVariable("id") long id) throws IdInvalidException {

        // check id
        Skill currentSkill = this.skillService.fetchSkillById(id);
        if (currentSkill == null) {
            throw new IdInvalidException("Skill id = " + id + "không đã tồn tại");
        }
        this.skillService.deleteSkill(id);
        return ResponseEntity.ok().body(null);
    }

    @GetMapping("/skills")
    @ApiMessage("fetch all skill")
    public ResponseEntity<ResultPaginationDTO> gettAll(
            @Filter Specification<Skill> sepc,
            Pageable pageable) {
        return ResponseEntity.status(HttpStatus.OK).body(this.skillService.fetchAllSkill(sepc, pageable));
    }

}
