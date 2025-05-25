package spring.api.trijava.chuyendewebjavajob.service;

import com.turkraft.springfilter.builder.FilterBuilder;
import com.turkraft.springfilter.converter.FilterSpecification;
import com.turkraft.springfilter.converter.FilterSpecificationConverter;
import com.turkraft.springfilter.parser.FilterParser;
import com.turkraft.springfilter.parser.node.FilterNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import spring.api.trijava.chuyendewebjavajob.domain.Job;
import spring.api.trijava.chuyendewebjavajob.domain.Resume;
import spring.api.trijava.chuyendewebjavajob.domain.User;
import spring.api.trijava.chuyendewebjavajob.domain.response.ResultPaginationDTO;
import spring.api.trijava.chuyendewebjavajob.domain.response.resume.ResCreateResumeDTO;
import spring.api.trijava.chuyendewebjavajob.domain.response.resume.ResFetchResumeDTO;
import spring.api.trijava.chuyendewebjavajob.domain.response.resume.ResUpdateResumeDTO;
import spring.api.trijava.chuyendewebjavajob.repository.JobRepository;
import spring.api.trijava.chuyendewebjavajob.repository.ResumeRepository;
import spring.api.trijava.chuyendewebjavajob.repository.UserRepository;
import spring.api.trijava.chuyendewebjavajob.util.SecurityUtil;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ResumeService {

    @Autowired
    FilterBuilder fb;

    @Autowired
    private FilterParser filterParser;

    @Autowired
    private FilterSpecificationConverter filterSpecificationConverter;

    private final ResumeRepository resumeRepository;
    private final UserRepository userRepository;
    private final JobRepository jobRepository;

    public ResumeService(ResumeRepository resumeRepository,
            UserRepository userRepository,
            JobRepository jobRepository) {
        this.resumeRepository = resumeRepository;
        this.userRepository = userRepository;
        this.jobRepository = jobRepository;
    }

    public ResCreateResumeDTO create(Resume r) {
        r = this.resumeRepository.save(r);

        ResCreateResumeDTO res = new ResCreateResumeDTO();
        res.setId(r.getId());
        res.setCreatedAt(r.getCreatedAt());
        res.setCreatedBy(r.getCreatedBy());

        return res;
    }

    public ResUpdateResumeDTO update(Resume r) {
        r = this.resumeRepository.save(r);

        ResUpdateResumeDTO res = new ResUpdateResumeDTO();
        res.setUpdatedAt(r.getUpdatedAt());
        res.setUpdatedBy(r.getUpdatedBy());

        return res;
    }

    public void delete(Resume r) {
        this.resumeRepository.delete(r);
    }

    public Resume findResumeById(long id) {
        Optional<Resume> r = this.resumeRepository.findById(id);
        if (r.isPresent()) {
            return r.get();
        }

        return null;
    }

    public boolean checkResumeExistByUserAndJob(Resume resume) {
        // check user by id
        if (resume.getUser() == null)
            return false;

        Optional<User> u = this.userRepository.findById(resume.getUser().getId());
        if (u.isEmpty())
            return false;

        // check job by id
        if (resume.getJob() == null)
            return false;

        Optional<Job> j = this.jobRepository.findById(resume.getJob().getId());
        if (j.isEmpty())
            return false;

        return true;
    }

    public ResFetchResumeDTO convertGetResumeDTO(Resume r) {
        ResFetchResumeDTO res = new ResFetchResumeDTO();

        res.setId(r.getId());
        res.setEmail(r.getEmail());
        res.setStatus(r.getStatus());
        res.setUrl(r.getUrl());
        res.setCreatedAt(r.getCreatedAt());
        res.setUpdatedAt(r.getUpdatedAt());
        res.setCreatedBy(r.getCreatedBy());
        res.setUpdatedBy(r.getUpdatedBy());

        if (r.getJob() != null) {
            res.setCompanyName(r.getJob().getCompany().getName());
        }

        res.setUserResume(new ResFetchResumeDTO.UserResume(r.getUser().getId(), r.getUser().getName()));
        res.setJobResume(new ResFetchResumeDTO.JobResume(r.getJob().getId(), r.getJob().getName()));
        return res;
    }

    public ResultPaginationDTO fetchAll(Specification<Resume> spec, Pageable pageable) {
        Page<Resume> pageUser = this.resumeRepository.findAll(spec, pageable);

        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();

        mt.setCurrentPage(pageable.getPageNumber() + 1);
        mt.setPageSize(pageable.getPageSize());

        mt.setTotalPages(pageUser.getTotalPages());
        mt.setTotalElements(pageUser.getTotalElements());

        rs.setMeta(mt);

        // mảng chứa id
        List<ResFetchResumeDTO> listResume = pageUser.getContent()
                .stream().map(item -> this.convertGetResumeDTO(item))
                .collect(Collectors.toList());

        rs.setResult(listResume);
        return rs;
    }

    public ResultPaginationDTO fetchResumeByUser(Pageable pageable) {
        // query builder
        String email = SecurityUtil.getCurrentUserLogin().isPresent() == true
                ? SecurityUtil.getCurrentUserLogin().get()
                : "";
        FilterNode node = filterParser.parse("email='" + email + "'");
        FilterSpecification<Resume> spec = filterSpecificationConverter.convert(node);
        Page<Resume> pageResume = this.resumeRepository.findAll(spec, pageable);

        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();

        mt.setCurrentPage(pageable.getPageNumber() + 1);
        mt.setPageSize(pageable.getPageSize());

        mt.setTotalPages(pageResume.getTotalPages());
        mt.setTotalElements(pageResume.getTotalElements());

        rs.setMeta(mt);

        // remove sensitive data
        List<ResFetchResumeDTO> listResume = pageResume.getContent()
                .stream().map(item -> this.convertGetResumeDTO(item))
                .collect(Collectors.toList());

        rs.setResult(listResume);

        return rs;
    }
}
