package spring.api.trijava.chuyendewebjavajob.controller;

import com.turkraft.springfilter.boot.Filter;
import com.turkraft.springfilter.builder.FilterBuilder;
import com.turkraft.springfilter.converter.FilterSpecificationConverter;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import spring.api.trijava.chuyendewebjavajob.domain.Company;
import spring.api.trijava.chuyendewebjavajob.domain.Job;
import spring.api.trijava.chuyendewebjavajob.domain.Resume;
import spring.api.trijava.chuyendewebjavajob.domain.User;
import spring.api.trijava.chuyendewebjavajob.domain.response.ResultPaginationDTO;
import spring.api.trijava.chuyendewebjavajob.domain.response.resume.ResCreateResumeDTO;
import spring.api.trijava.chuyendewebjavajob.domain.response.resume.ResFetchResumeDTO;
import spring.api.trijava.chuyendewebjavajob.domain.response.resume.ResUpdateResumeDTO;
import spring.api.trijava.chuyendewebjavajob.service.ResumeService;
import spring.api.trijava.chuyendewebjavajob.service.UserService;
import spring.api.trijava.chuyendewebjavajob.util.SecurityUtil;
import spring.api.trijava.chuyendewebjavajob.util.annotation.ApiMessage;
import spring.api.trijava.chuyendewebjavajob.util.error.IdInvalidException;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1")
public class ResumeController {
    private final ResumeService resumeService;
    private final UserService userService;

    private final FilterBuilder filterBuilder;
    private final FilterSpecificationConverter filterSpecificationConverter;

    public ResumeController(ResumeService resumeService,
            UserService userService,
            FilterBuilder filterBuilder,
            FilterSpecificationConverter filterSpecificationConverter) {
        this.resumeService = resumeService;
        this.userService = userService;
        this.filterBuilder = filterBuilder;
        this.filterSpecificationConverter = filterSpecificationConverter;
    }

    @PostMapping("/resumes")
    @ApiMessage("Create a resume")
    public ResponseEntity<ResCreateResumeDTO> create(@Valid @RequestBody Resume r) throws IdInvalidException {

        boolean checkExist = this.resumeService.checkResumeExistByUserAndJob(r);

        if (!checkExist) {
            throw new IdInvalidException("User/Job không tồn tại");
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(this.resumeService.create(r));
    }

    @PutMapping("/resumes")
    @ApiMessage("Update a resume")
    public ResponseEntity<ResUpdateResumeDTO> update(@RequestBody Resume r) throws IdInvalidException {

        Resume resume = this.resumeService.findResumeById(r.getId());

        if (resume == null) {
            throw new IdInvalidException("Resume với id = " + r.getId() + " không tồn tại");
        }

        resume.setStatus(r.getStatus());

        return ResponseEntity.ok().body(this.resumeService.update(resume));
    }

    @DeleteMapping("/resumes/{id}")
    @ApiMessage("Delete a resume")
    public ResponseEntity<Void> deleteResume(@PathVariable("id") long id) throws IdInvalidException {

        Resume resume = this.resumeService.findResumeById(id);
        if (resume == null) {
            throw new IdInvalidException("Resume id= " + id + " không tồn tại");
        }

        this.resumeService.delete(resume);
        return ResponseEntity.ok().body(null);
    }

    @GetMapping("/resumes/{id}")
    @ApiMessage("Get a resume by id")
    public ResponseEntity<ResFetchResumeDTO> getResume(@PathVariable("id") long id) throws IdInvalidException {

        Resume resume = this.resumeService.findResumeById(id);
        if (resume == null) {
            throw new IdInvalidException("Resume id= " + id + " không tồn tại");
        }

        return ResponseEntity.ok().body(this.resumeService.convertGetResumeDTO(resume));
    }

    @GetMapping("/resumes")
    @ApiMessage("Fetch all resume with paginate")
    public ResponseEntity<ResultPaginationDTO> fetchAll(
            @Filter Specification<Resume> spec,
            Pageable pageable) {
        List<Long> arrJobs = null;
        //
        String email = SecurityUtil.getCurrentUserLogin().isPresent() == true
                ? SecurityUtil.getCurrentUserLogin().get()
                : "";

        // Lấy user theo email
        User currentUser = this.userService.handleGetUserByUsername(email);
        if (currentUser != null) {
            // Lấy company theo user
            Company userCompany = currentUser.getCompany();
            if (userCompany != null) {
                // Lấy job theo company
                List<Job> companyJobs = userCompany.getJobs();
                if (companyJobs != null && companyJobs.size() > 0) {
                    arrJobs = companyJobs.stream().map(x -> x.getId())
                            .collect(Collectors.toList());
                }
            }
        }

        Specification<Resume> jobInspec = filterSpecificationConverter.convert(filterBuilder.field("job")
                .in(filterBuilder.input(arrJobs)).get());

        Specification<Resume> finalSpec = jobInspec.and(spec);

        return ResponseEntity.ok().body(this.resumeService.fetchAll(finalSpec, pageable));
    }

    @PostMapping("/resumes/by-user")
    @ApiMessage("Get list resumes by user")
    public ResponseEntity<ResultPaginationDTO> fetchResumeByUser(Pageable pageable) {

        return ResponseEntity.ok().body(this.resumeService.fetchResumeByUser(pageable));
    }

}
