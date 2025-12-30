package saas.app.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import saas.app.core.domain.MonitoredSite;

import java.util.List;

@Repository
public interface MonitoredSiteRepository extends JpaRepository <MonitoredSite, Long> {
    List <MonitoredSite> findByActiveTrue();

    List<MonitoredSite> id(Long id);
}
