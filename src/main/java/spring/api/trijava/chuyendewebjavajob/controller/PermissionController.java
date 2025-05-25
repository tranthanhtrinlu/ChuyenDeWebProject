package spring.api.trijava.chuyendewebjavajob.controller;

import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import spring.api.trijava.chuyendewebjavajob.domain.Permission;
import spring.api.trijava.chuyendewebjavajob.domain.response.ResultPaginationDTO;
import spring.api.trijava.chuyendewebjavajob.service.PermissionService;
import spring.api.trijava.chuyendewebjavajob.util.annotation.ApiMessage;
import spring.api.trijava.chuyendewebjavajob.util.error.IdInvalidException;

@RestController
@RequestMapping("/api/v1")
public class PermissionController {

    private final PermissionService permissionService;

    public PermissionController(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @PostMapping("/permissions")
    @ApiMessage("Create a permission")
    public ResponseEntity<Permission> create(@Valid @RequestBody Permission p) throws IdInvalidException {

        if (this.permissionService.isPermissionExist(p)) {
            throw new IdInvalidException("Permission đã tồn tại");
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(this.permissionService.save(p));
    }

    @PutMapping("/permissions")
    @ApiMessage("Update a permission")
    public ResponseEntity<Permission> update(@Valid @RequestBody Permission p) throws IdInvalidException {

        // check exist by id
        if (this.permissionService.fetchById(p.getId()) == null) {
            throw new IdInvalidException("Permission với id = " + p.getId() + " không tồn tại");
        }

        // check exist by module, apiPath, method
        if (this.permissionService.isPermissionExist(p)) {
            throw new IdInvalidException("Permission đã tồn tại");
        }

        return ResponseEntity.ok().body(this.permissionService.update(p));
    }

    @DeleteMapping("/permissions/{id}")
    @ApiMessage("Delete a permission")
    public ResponseEntity<Void> delete(@PathVariable("id") long id) throws IdInvalidException {

        // check exist by id
        if (this.permissionService.fetchById(id) == null) {
            throw new IdInvalidException("Permission với id = " + id + " không tồn tại");
        }

        this.permissionService.delete(id);
        return ResponseEntity.ok().body(null);
    }

    @GetMapping("/permissions")
    @ApiMessage("Fetch permissions")
    public ResponseEntity<ResultPaginationDTO> getPermissions(
            @Filter Specification<Permission> sepc,
            Pageable pageable) {
        return ResponseEntity.ok().body(this.permissionService.getPermissions(sepc, pageable));
    }

}
