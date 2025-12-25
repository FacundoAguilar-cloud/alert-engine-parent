package saas.app.core.repository;

import jakarta.annotation.Resource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import saas.app.core.domain.MonitoredSite;
import saas.app.core.domain.SiteSnapshot;

@Repository
public interface SiteSnapshotRepository extends JpaRepository <SiteSnapshot, Long> {
}
