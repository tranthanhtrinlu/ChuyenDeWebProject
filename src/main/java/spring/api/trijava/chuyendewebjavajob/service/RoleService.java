package spring.api.trijava.chuyendewebjavajob.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import spring.api.trijava.chuyendewebjavajob.domain.Permission;
import spring.api.trijava.chuyendewebjavajob.domain.Role;
import spring.api.trijava.chuyendewebjavajob.domain.response.ResultPaginationDTO;
import spring.api.trijava.chuyendewebjavajob.repository.PermissionRepository;
import spring.api.trijava.chuyendewebjavajob.repository.RoleRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RoleService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    public RoleService(RoleRepository roleRepository, PermissionRepository permissionRepository) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
    }

    public boolean eixstByName(String name) {
        return this.roleRepository.existsByName(name);
    }

    public Role fetchById(long id) {
        Optional<Role> r = this.roleRepository.findById(id);
        if (r.isPresent()) {
            return r.get();
        }

        return null;
    }

    public Role create(Role r) {
        // check permissions
        if (r.getPermissions() != null) {
            List<Long> id = r.getPermissions()
                    .stream().map(x -> x.getId())
                    .collect(Collectors.toList());

            List<Permission> p = this.permissionRepository.findByIdIn(id);
            r.setPermissions(p);
        }
        return this.roleRepository.save(r);
    }

    public Role update(Role r) {
        Role rBD = this.fetchById(r.getId());

        // check permissions
        if (r.getPermissions() != null) {
            List<Long> id = r.getPermissions()
                    .stream().map(x -> x.getId())
                    .collect(Collectors.toList());

            List<Permission> p = this.permissionRepository.findByIdIn(id);
            r.setPermissions(p);
        }

        rBD.setName(r.getName());
        rBD.setDescription(r.getDescription());
        rBD.setActive(r.isActive());
        rBD.setPermissions(r.getPermissions());

        rBD = this.roleRepository.save(rBD);

        return rBD;
    }

    public void delete(long id) {
        this.roleRepository.deleteById(id);
    }

    public ResultPaginationDTO getRoles(Specification<Role> spec, Pageable pageable) {
        Page<Role> rolePermission = this.roleRepository.findAll(spec, pageable);

        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();

        mt.setCurrentPage(pageable.getPageNumber() + 1);
        mt.setPageSize(pageable.getPageSize());

        mt.setTotalPages(rolePermission.getTotalPages());
        mt.setTotalElements(rolePermission.getTotalElements());

        rs.setMeta(mt);
        rs.setResult(rolePermission.getContent());

        return rs;
    }
}
