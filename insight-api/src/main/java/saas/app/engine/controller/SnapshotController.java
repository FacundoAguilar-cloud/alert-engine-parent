package saas.app.engine.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import saas.app.core.domain.SiteSnapshot;
import saas.app.core.repository.SiteSnapshotRepository;

import java.util.List;

@RestController
@RequestMapping("/api/snapshots")
@RequiredArgsConstructor
public class SnapshotController {

    private final SiteSnapshotRepository repository;

    @GetMapping("/site/{siteId}")
    public ResponseEntity <List<SiteSnapshot>> getHistoryBySite(@PathVariable Long siteId){
        List<SiteSnapshot> history = repository.findByMonitoredSiteIdOrderBySnapshotTimeDesc(siteId);
            return ResponseEntity.ok(history);
    }
}
