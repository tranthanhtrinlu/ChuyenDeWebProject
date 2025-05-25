package spring.api.trijava.chuyendewebjavajob.controller;

import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import spring.api.trijava.chuyendewebjavajob.domain.Job;
import spring.api.trijava.chuyendewebjavajob.domain.response.ResultPaginationDTO;
import spring.api.trijava.chuyendewebjavajob.domain.response.job.ResCreateJobDTO;
import spring.api.trijava.chuyendewebjavajob.domain.response.job.ResUpdateJobDTO;
import spring.api.trijava.chuyendewebjavajob.service.JobService;
import spring.api.trijava.chuyendewebjavajob.util.annotation.ApiMessage;
import spring.api.trijava.chuyendewebjavajob.util.error.IdInvalidException;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1")
public class JobController {

    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    @PostMapping("/jobs")
    @ApiMessage("Create a job")
    public ResponseEntity<ResCreateJobDTO> create(@Valid @RequestBody Job j) {

        return ResponseEntity.status(HttpStatus.CREATED).body(this.jobService.create(j));
    }

    @PutMapping("/jobs")
    @ApiMessage("Update a job")
    public ResponseEntity<ResUpdateJobDTO> update(@Valid @RequestBody Job j) throws IdInvalidException {
        Optional<Job> currentJob = this.jobService.fetchJobById(j.getId());
        if (!currentJob.isPresent()) {
            throw new IdInvalidException("Job not found");
        }

        return ResponseEntity.ok().body(this.jobService.update(j, currentJob.get()));
    }

    @DeleteMapping("/jobs/{id}")
    @ApiMessage("Delete a job")
    public ResponseEntity<Void> delete(@PathVariable("id") long id) throws IdInvalidException {
        Optional<Job> currentJob = this.jobService.fetchJobById(id);
        if (!currentJob.isPresent()) {
            throw new IdInvalidException("Job not found");
        }
        this.jobService.delete(id);
        return ResponseEntity.ok().body(null);
    }

    @GetMapping("/jobs/{id}")
    @ApiMessage("Get a job by id")
    public ResponseEntity<Job> getJob(@PathVariable("id") long id) throws IdInvalidException {
        Optional<Job> currentJob = this.jobService.fetchJobById(id);
        if (!currentJob.isPresent()) {
            throw new IdInvalidException("Job not found");
        }

        return ResponseEntity.ok().body(currentJob.get());
    }

    @GetMapping("/jobs")
    @ApiMessage("Get all job")
    public ResponseEntity<ResultPaginationDTO> getAllJOb(
            @Filter Specification<Job> spec,
            Pageable pageable) {
        return ResponseEntity.ok().body(this.jobService.fetchAll(spec, pageable));
    }

}
