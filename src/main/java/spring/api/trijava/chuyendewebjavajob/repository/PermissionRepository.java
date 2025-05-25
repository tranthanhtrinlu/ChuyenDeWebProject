package spring.api.trijava.chuyendewebjavajob.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import spring.api.trijava.chuyendewebjavajob.domain.Permission;

import java.util.List;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long>, JpaSpecificationExecutor<Permission> {
    boolean existsByModuleAndApiPathAndMethod(String module, String apiPath, String method);

    List<Permission> findByIdIn(List<Long> id);
}
