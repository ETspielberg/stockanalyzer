package unidue.ub.stockanalyzer.settingsrepositories;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import unidue.ub.stockanalyzer.model.settings.Alertcontrol;


import java.util.List;

/**
 * Created by Eike on 08.07.2017.
 */
@RepositoryRestResource(collectionResourceRel = "alertcontrol", path = "alertcontrol")
public interface AlertcontrolRepository extends PagingAndSortingRepository<Alertcontrol,String> {

    @Query(value = "select s.* from profile s,profiles_per_user ppu where s.identifier = ppu.identifier and ppu.username = :username and dtype='Alertcontrol'",nativeQuery = true)
    public List<Alertcontrol> findByUsername(@Param("username") String username);
}
