package saas.app.engine.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import saas.app.core.domain.Product;
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
    public List <Product> getAll(){
        return repository.findAll();
    }

    @PostMapping
    public ResponseEntity <Product> create(@RequestBody Product site){
        site.setActive(true);
        if (site.getCreatedAt() == null){
            site.setCreatedAt(java.time.LocalDateTime.now());
        }

        Product savedSite = repository.save(site);

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
    public ResponseEntity <Product> toggleStatus(@PathVariable Long id){
        return  repository.findById(id).map(site -> {
            site.setActive(!site.isActive());
              return ResponseEntity.ok(repository.save(site));
                }
                ).orElse(ResponseEntity.notFound().build());
    }




}
