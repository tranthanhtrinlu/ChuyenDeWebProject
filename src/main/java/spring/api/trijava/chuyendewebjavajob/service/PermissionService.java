package spring.api.trijava.chuyendewebjavajob.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import spring.api.trijava.chuyendewebjavajob.domain.Permission;
import spring.api.trijava.chuyendewebjavajob.domain.response.ResultPaginationDTO;
import spring.api.trijava.chuyendewebjavajob.repository.PermissionRepository;

import java.util.Optional;

@Service
public class PermissionService {

    private final PermissionRepository permissionRepository;

    public PermissionService(PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }

    public boolean isPermissionExist(Permission p) {
        return this.permissionRepository.existsByModuleAndApiPathAndMethod(
                p.getModule(), p.getApiPath(), p.getMethod());
    }

    public Permission fetchById(long id) {
        Optional<Permission> p = this.permissionRepository.findById(id);
        if (p.isPresent()) {
            return p.get();
        }

        return null;
    }

    public Permission save(Permission p) {
        return this.permissionRepository.save(p);
    }

    public Permission update(Permission p) {
        Permission permission = this.fetchById(p.getId());
        if (permission != null) {
            permission.setName(p.getName());
            permission.setApiPath(p.getApiPath());
            permission.setMethod(p.getMethod());
            permission.setModule(p.getModule());

            permission = this.save(permission);
            return permission;
        }

        return null;
    }

    public void delete(long id) {

        // delete permission_role
        Optional<Permission> p = this.permissionRepository.findById(id);
        p.get().getRoles().forEach(role -> role.getPermissions().remove(p));

        this.permissionRepository.delete(p.get());
    }

    public ResultPaginationDTO getPermissions(Specification<Permission> spec, Pageable pageable) {
        Page<Permission> pagePermission = this.permissionRepository.findAll(spec, pageable);

        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();

        mt.setCurrentPage(pageable.getPageNumber() + 1);
        mt.setPageSize(pageable.getPageSize());

        mt.setTotalPages(pagePermission.getTotalPages());
        mt.setTotalElements(pagePermission.getTotalElements());

        rs.setMeta(mt);
        rs.setResult(pagePermission.getContent());

        return rs;
    }
}
