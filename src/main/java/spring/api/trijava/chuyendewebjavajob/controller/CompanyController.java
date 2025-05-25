package spring.api.trijava.chuyendewebjavajob.controller;

import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import spring.api.trijava.chuyendewebjavajob.domain.Company;
import spring.api.trijava.chuyendewebjavajob.domain.response.ResultPaginationDTO;
import spring.api.trijava.chuyendewebjavajob.service.CompanyService;
import spring.api.trijava.chuyendewebjavajob.util.annotation.ApiMessage;

@RestController
@RequestMapping("/api/v1")
public class CompanyController {
    private CompanyService companyService;

    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    @PostMapping("/companies")
    @ApiMessage("create a company")
    public ResponseEntity<Company> create(@Valid @RequestBody Company company) {

        return ResponseEntity.status(HttpStatus.CREATED).body(this.companyService.handleSaveCompany(company));
    }

    @DeleteMapping("/companies/{id}")
    @ApiMessage("delete a company")
    public ResponseEntity<Void> delete(@PathVariable("id") long id) {
        this.companyService.handleDeleteCompany(id);
        return ResponseEntity.ok().body(null);
    }

    @GetMapping("/companies/{id}")
    @ApiMessage("get a company by id")
    public ResponseEntity<Company> getCompanyById(@PathVariable("id") long id) {
        return ResponseEntity.ok().body(this.companyService.getCompanyById(id));
    }

    // Pageable sẽ tự động lấy pageNumber, pageSize, sort từ Postman
    // chỉ cần đặt đúng param là page, size, sort
    // param filter của Specification
    @GetMapping("/companies")
    @ApiMessage("get all company")
    public ResponseEntity<ResultPaginationDTO> getAllCompany(
            @Filter Specification<Company> spec,
            Pageable pageable
    // @RequestParam("currentPage") Optional<String> currentPage,
    // @RequestParam("pageSize") Optional<String> pageSize
    ) {
        // String sCurrentPage = currentPage.isPresent() ? currentPage.get() : "";
        // String sPageSize = pageSize.isPresent() ? pageSize.get() : "";

        // int current = Integer.parseInt(sCurrentPage);
        // int size = Integer.parseInt(sPageSize);
        // // Pageable không có hàm tạo => PageRequest kế thừa Pageable
        // Pageable pageable = PageRequest.of(current - 1, size);
        return ResponseEntity.ok().body(this.companyService.getAllCompany(spec, pageable));
    }

    @PutMapping("/companies")
    @ApiMessage("update a company")
    public ResponseEntity<Company> updateCompany(@Valid @RequestBody Company company) {
        return ResponseEntity.ok().body(this.companyService.updateCompany(company));
    }

}
