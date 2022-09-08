package account.controllers;

import account.entities.Event;
import account.services.EventService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class AuditorController {

    private final EventService eventService;

    public AuditorController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping("/api/security/events")
    public List<Event> getSecurityEvents() {
        return eventService.findAll();
    }

}
