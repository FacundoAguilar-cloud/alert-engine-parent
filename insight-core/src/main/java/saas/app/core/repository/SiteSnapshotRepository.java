package saas.app.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import saas.app.core.domain.SiteSnapshot;

import java.util.List;
import java.util.Optional;

@Repository
public interface SiteSnapshotRepository extends JpaRepository <SiteSnapshot, Long> {

    List<SiteSnapshot> findByMonitoredSiteIdOrderBySnapshotTimeDesc(Long siteId); //busca snapshots y los ordena del mas reciente al mas viejo
    //va a ocuparse de buscar el snapshot mas reciente
    Optional <SiteSnapshot> findFirstByMonitoredSiteIdOrderBySnapshotTimeDesc(Long siteId);
}
