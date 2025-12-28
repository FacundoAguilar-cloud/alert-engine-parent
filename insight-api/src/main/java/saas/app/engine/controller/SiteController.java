package saas.app.engine.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity <MonitoredSite> create(@RequestBody MonitoredSite site){
        site.setActive(true);
        if (site.getCreatedAt() == null){
            site.setCreatedAt(java.time.LocalDateTime.now());
        }

        MonitoredSite savedSite = repository.save(site);

        return ResponseEntity.status(HttpStatus.CREATED).body(savedSite);

    }

    @DeleteMapping("/{id}")
    public ResponseEntity <Void> delete(@PathVariable Long id){
        if (!repository.existsById(id)){
            return ResponseEntity.notFound().build();
        }
        repository.deleteById(id);

        return  ResponseEntity.noContent().build();


    }

    @PatchMapping("/{id}/toggle")
    public ResponseEntity <MonitoredSite> toggleStatus()




}
