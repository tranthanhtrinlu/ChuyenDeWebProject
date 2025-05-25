package spring.api.trijava.chuyendewebjavajob.service;

import org.springframework.stereotype.Service;
import spring.api.trijava.chuyendewebjavajob.domain.Skill;
import spring.api.trijava.chuyendewebjavajob.domain.Subscriber;
import spring.api.trijava.chuyendewebjavajob.repository.JobRepository;
import spring.api.trijava.chuyendewebjavajob.repository.SkillRepository;
import spring.api.trijava.chuyendewebjavajob.repository.SubscriberRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SubscriberService {
    private final SubscriberRepository subscriberRepository;
    private final SkillRepository skillRepository;
    private final JobRepository jobRepository;

    public SubscriberService(SubscriberRepository subscriberRepository,
            SkillRepository skillRepository,
            JobRepository jobRepository) {
        this.subscriberRepository = subscriberRepository;
        this.skillRepository = skillRepository;
        this.jobRepository = jobRepository;
    }

    public boolean isExistsByEmail(String email) {
        return this.subscriberRepository.existsByEmail(email);
    }

    public Subscriber create(Subscriber subscriber) {
        // check skill list
        if (subscriber.getSkills() != null) {
            List<Long> reqSkills = subscriber.getSkills()
                    .stream().map(x -> x.getId())
                    .collect(Collectors.toList());

            List<Skill> dbSkill = this.skillRepository.findByIdIn(reqSkills);
            subscriber.setSkills(dbSkill);
        }

        return this.subscriberRepository.save(subscriber);
    }

    public Subscriber findById(long id) {
        Optional<Subscriber> s = this.subscriberRepository.findById(id);
        if (s.isPresent()) {
            return s.get();
        }

        return null;
    }

    public Subscriber update(Subscriber subsDB, Subscriber subsRequest) {
        // get List id skill
        if (subsRequest.getSkills() != null) {
            List<Long> reqSkills = subsRequest.getSkills()
                    .stream().map(x -> x.getId())
                    .collect(Collectors.toList());

            List<Skill> dbSkill = this.skillRepository.findByIdIn(reqSkills);
            subsDB.setSkills(dbSkill);
        }

        return this.subscriberRepository.save(subsDB);
    }

    // @Scheduled(cron = 1000)
    // public void testCron() {
    // System.out.println(">>>Cron");
    // }

    // public ResEmailJob convertJobToSendEmail(Job job) {
    // ResEmailJob res = new ResEmailJob();
    // res.setName(job.getName());
    // res.setSalary(job.getSalary());
    // res.setCompany(new ResEmailJob.CompanyEmail(job.getCompany().getName()));
    // List<Skill> skills = job.getSkills();
    // List<ResEmailJob.SkillEmail> s = skills.stream().map(skill -> new
    // ResEmailJob.SkillEmail(skill.getName()))
    // .collect(Collectors.toList());
    // res.setSkills(s);
    // return res;
    // }

    // public void sendSubscribersEmailJobs() {
    // List<Subscriber> listSubs = this.subscriberRepository.findAll();
    // if (listSubs != null && listSubs.size() > 0) {
    // for (Subscriber sub : listSubs) {
    // List<Skill> listSkills = sub.getSkills();
    // if (listSkills != null && listSkills.size() > 0) {
    // List<Job> listJobs = this.jobRepository.findBySkillsIn(listSkills);
    // if (listJobs != null && listJobs.size() > 0) {

    // List<ResEmailJob> arr = listJobs.stream().map(
    // job -> this.convertJobToSendEmail(job)).collect(Collectors.toList());

    // this.emailService.sendEmailFromTemplateSync(
    // sub.getEmail(),
    // "Cơ hội việc làm hot đang chờ đón bạn, khám phá ngay",
    // "job",
    // sub.getName(),
    // arr);
    // }
    // }
    // }
    // }
    // }

    // public Subscriber findByEmail(String email) {
    // return this.subscriberRepository.findByEmail(email);
    // }

}
