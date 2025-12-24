package saas.app.engine.controller;

import org.springframework.web.bind.annotation.*;
import saas.app.core.domain.MonitoredSite;
import saas.app.core.repository.MonitoredSiteRepository;

import java.util.List;

@RestController
@RequestMapping("/api/sites")


public class SiteController {

    private final MonitoredSiteRepository repository;

    public SiteController(MonitoredSiteRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List <MonitoredSite> getAll(){
        return repository.findAll();
    }

    @PostMapping
    public  MonitoredSite create(@RequestBody MonitoredSite site){
        site.setActive(true);
        return repository.save(site);
    }




}
