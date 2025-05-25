package spring.api.trijava.chuyendewebjavajob.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import spring.api.trijava.chuyendewebjavajob.domain.Company;
import spring.api.trijava.chuyendewebjavajob.domain.User;
import spring.api.trijava.chuyendewebjavajob.domain.response.ResultPaginationDTO;
import spring.api.trijava.chuyendewebjavajob.repository.CompanyRepository;
import spring.api.trijava.chuyendewebjavajob.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
public class CompanyService {
    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;

    public CompanyService(CompanyRepository companyRepository,
            UserRepository userRepository) {
        this.companyRepository = companyRepository;
        this.userRepository = userRepository;
    }

    public Company handleSaveCompany(Company company) {
        return this.companyRepository.save(company);
    }

    public void handleDeleteCompany(long id) {
        // xóa user => company
        Optional<Company> c = this.companyRepository.findById(id);
        if (c.isPresent()) {
            List<User> users = this.userRepository.findByCompany(c.get());
            this.userRepository.deleteAll(users);
        }

        this.companyRepository.deleteById(id);
    }

    public ResultPaginationDTO getAllCompany(Specification<Company> spec, Pageable pageable) {
        Page<Company> p = this.companyRepository.findAll(spec, pageable);

        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();

        // Lấy từ Fe
        mt.setCurrentPage(pageable.getPageNumber() + 1);
        mt.setPageSize(pageable.getPageSize());

        // lấy từ database
        mt.setTotalPages(p.getTotalPages());
        mt.setTotalElements(p.getTotalElements());

        rs.setMeta(mt);
        rs.setResult(p.getContent());

        return rs;
    }

    public Company getCompanyById(long id) {
        Optional<Company> c = this.companyRepository.findById(id);
        if (c.isPresent()) {
            return c.get();
        }
        return null;
    }

    public Company updateCompany(Company company) {
        Company c = this.getCompanyById(company.getId());
        if (c != null) {
            c.setName(company.getName());
            c.setAddress(company.getAddress());
            c.setDescription(company.getDescription());
            c.setLogo(company.getLogo());
            return this.handleSaveCompany(c);
        }

        return null;
    }
}
