package spring.api.trijava.chuyendewebjavajob.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import spring.api.trijava.chuyendewebjavajob.domain.Subscriber;
import spring.api.trijava.chuyendewebjavajob.service.SubscriberService;
import spring.api.trijava.chuyendewebjavajob.util.annotation.ApiMessage;
import spring.api.trijava.chuyendewebjavajob.util.error.IdInvalidException;

@RestController
@RequestMapping("/api/v1")
public class SubscriberController {

    private final SubscriberService subscriberService;

    public SubscriberController(SubscriberService subscriberService) {
        this.subscriberService = subscriberService;
    }

    @PostMapping("/subscribers")
    @ApiMessage("Create a subscriber")
    public ResponseEntity<Subscriber> create(@Valid @RequestBody Subscriber subscriber) throws IdInvalidException {

        // check email
        boolean isExist = this.subscriberService.isExistsByEmail(subscriber.getEmail());
        if (isExist == true) {
            throw new IdInvalidException("Email " + subscriber.getEmail() + " đã tồn tại");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(this.subscriberService.create(subscriber));
    }

    @PutMapping("/subscribers")
    @ApiMessage("Update a subscriber")
    public ResponseEntity<Subscriber> update(@RequestBody Subscriber subsRequest) throws IdInvalidException {

        // check id
        Subscriber subDB = this.subscriberService.findById(subsRequest.getId());
        if (subDB == null) {
            throw new IdInvalidException("Id " + subsRequest.getId() + " không tồn tại");
        }
        return ResponseEntity.ok().body(this.subscriberService.update(subDB, subsRequest));
    }

    // @PostMapping("/subscribers/skills")
    // @ApiMessage("Get subscriber a skill")
    // public ResponseEntity<Subscriber> getSubscriber() throws IdInvalidException {

    // String email = SecurityUtil.getCurrentUserLogin().isPresent() == true
    // ? SecurityUtil.getCurrentUserLogin().get()
    // : "";
    // return ResponseEntity.ok().body(this.subscriberService.findByEmail(email));
    // }

}
