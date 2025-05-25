package spring.api.trijava.chuyendewebjavajob.controller;

import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import spring.api.trijava.chuyendewebjavajob.domain.Role;
import spring.api.trijava.chuyendewebjavajob.domain.User;
import spring.api.trijava.chuyendewebjavajob.domain.response.ResCreateUserDTO;
import spring.api.trijava.chuyendewebjavajob.domain.response.ResUpdateUserDTO;
import spring.api.trijava.chuyendewebjavajob.domain.response.ResUserDTO;
import spring.api.trijava.chuyendewebjavajob.domain.response.ResultPaginationDTO;
import spring.api.trijava.chuyendewebjavajob.service.RoleService;
import spring.api.trijava.chuyendewebjavajob.service.UserService;
import spring.api.trijava.chuyendewebjavajob.util.annotation.ApiMessage;
import spring.api.trijava.chuyendewebjavajob.util.error.IdInvalidException;

@RestController
@RequestMapping("/api/v1")

public class UserController {

    private final UserService userService;
    private final RoleService roleService;
    private PasswordEncoder passwordEncoder;

    public UserController(UserService userService,
            PasswordEncoder passwordEncoder,
            RoleService roleService) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.roleService = roleService;
    }

    @PostMapping("/users/create")
    @ApiMessage("create a user")
    public ResponseEntity<ResCreateUserDTO> createNewUser(@Valid @RequestBody User u) throws IdInvalidException {

        boolean isEmailExist = this.userService.isEmailExist(u.getEmail());
        if (isEmailExist) {
            throw new IdInvalidException("Email " + u.getEmail() + " đã tồn tại");
        }

        Role r = this.roleService.fetchById(u.getRole().getId());
        if (r == null) {
            throw new IdInvalidException("Không tồn tại role với id = " + u.getRole().getId());
        }

        String hashPass = passwordEncoder.encode(u.getPassword());
        u.setPassword(hashPass);
        User user = this.userService.handleCreateUser(u);
        return ResponseEntity.status(HttpStatus.CREATED).body(this.userService.convertToResCreateUserDTO(user));
    }

    @DeleteMapping("/users/{id}")
    @ApiMessage("delete a user by id")
    public ResponseEntity<Void> deleteUser(@PathVariable("id") long id) throws IdInvalidException {

        User currentU = this.userService.findUserById(id);
        if (currentU == null) {
            throw new IdInvalidException("Không tồn tại User với id: " + id);
        }
        this.userService.deleteUser(id);
        return ResponseEntity.ok(null);
    }

    @GetMapping("/users/{id}")
    @ApiMessage("get user by id")
    public ResponseEntity<ResUserDTO> getUserById(@PathVariable("id") long id) throws IdInvalidException {
        User currentU = this.userService.findUserById(id);

        if (currentU == null) {
            throw new IdInvalidException("Không tồn tại User với id: " + id);
        }

        return ResponseEntity.status(HttpStatus.OK).body(this.userService.convertToResUserDTO(currentU));
    }

    // Pageable sẽ tự động lấy pageNumber, pageSize, sort từ Postman
    // chỉ cần đặt đúng param là page, size, sort
    // param filter của Specification
    @GetMapping("/users")
    @ApiMessage("fetch all users")
    public ResponseEntity<ResultPaginationDTO> getAllUser(
            @Filter Specification<User> spec,
            Pageable pageable
    // @RequestParam("currentPage") Optional<String> currentPage,
    // @RequestParam("pageSize") Optional<String> pageSize)
    ) {
        // String sCurrentPage = currentPage.isPresent() ? currentPage.get() : "";
        // String sPageSize = pageSize.isPresent() ? pageSize.get() : "";

        // int current = Integer.parseInt(sCurrentPage);
        // int size = Integer.parseInt(sPageSize);
        // // Pageable không có hàm tạo => PageRequest kế thừa Pageable
        // Pageable pageable = PageRequest.of(current - 1, size);
        return ResponseEntity.ok().body(this.userService.getAllUser(spec, pageable));
    }

    @PutMapping("/users")
    @ApiMessage("update a user")
    public ResponseEntity<ResUpdateUserDTO> updateUser(@RequestBody User u) throws IdInvalidException {

        User currentU = this.userService.updateUser(u);

        if (currentU == null) {
            throw new IdInvalidException("Không tồn tại User với id: " + u.getId());
        }

        return ResponseEntity.ok(this.userService.convertToResUpdateUserDTO(currentU));
    }

}
