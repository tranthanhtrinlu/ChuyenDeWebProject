package spring.api.trijava.chuyendewebjavajob.controller;

import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import spring.api.trijava.chuyendewebjavajob.domain.Role;
import spring.api.trijava.chuyendewebjavajob.domain.response.ResultPaginationDTO;
import spring.api.trijava.chuyendewebjavajob.service.RoleService;
import spring.api.trijava.chuyendewebjavajob.util.annotation.ApiMessage;
import spring.api.trijava.chuyendewebjavajob.util.error.IdInvalidException;

@RestController
@RequestMapping("/api/v1")
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @PostMapping("/roles")
    @ApiMessage("Create a role")
    public ResponseEntity<Role> create(@Valid @RequestBody Role r) throws IdInvalidException {

        if (this.roleService.eixstByName(r.getName())) {
            throw new IdInvalidException("Role với id = " + r.getName() + " đã tồn tại");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(this.roleService.create(r));
    }

    @PutMapping("/roles")
    @ApiMessage("Update a role")
    public ResponseEntity<Role> update(@Valid @RequestBody Role r) throws IdInvalidException {

        // check id
        if (this.roleService.fetchById(r.getId()) == null) {
            throw new IdInvalidException("Role với id = " + r.getId() + " không tồn tại");
        }

        // check name
        // if (this.roleService.eixstByName(r.getName())) {
        // throw new IdInvalidException("Role với name = " + r.getName() + " đã tồn
        // tại");
        // }

        return ResponseEntity.ok().body(this.roleService.update(r));
    }

    @DeleteMapping("/roles/{id}")
    @ApiMessage("Delete a role")
    public ResponseEntity<Void> delete(@PathVariable("id") long id) throws IdInvalidException {

        // check id
        if (this.roleService.fetchById(id) == null) {
            throw new IdInvalidException("Role với id = " + id + " không tồn tại");
        }

        this.roleService.delete(id);
        return ResponseEntity.ok().body(null);
    }

    @GetMapping("/roles")
    @ApiMessage("Fetch roles")
    public ResponseEntity<ResultPaginationDTO> getRoles(
            @Filter Specification<Role> spec, Pageable pageable) {

        return ResponseEntity.ok().body(this.roleService.getRoles(spec, pageable));
    }

    @GetMapping("/roles/{id}")
    @ApiMessage("Fetch role by id")
    public ResponseEntity<Role> getById(@PathVariable("id") long id) throws IdInvalidException {

        Role r = this.roleService.fetchById(id);
        if (r == null) {
            throw new IdInvalidException("Không tồn tại Role với id = " + id);
        }

        return ResponseEntity.ok().body(r);
    }

}
