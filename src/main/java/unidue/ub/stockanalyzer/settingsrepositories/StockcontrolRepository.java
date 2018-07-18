package unidue.ub.stockanalyzer.settingsrepositories;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import unidue.ub.stockanalyzer.model.settings.Stockcontrol;

import java.util.List;

/**
 * Created by Eike on 22.06.2017.
 */
@RepositoryRestResource(collectionResourceRel = "stockcontrol", path = "stockcontrol")
public interface StockcontrolRepository extends PagingAndSortingRepository<Stockcontrol, String> {

    @Query(value = "select s.* from profile s,profiles_per_user ppu where s.identifier = ppu.identifier and ppu.username = :username and dtype='Stockcontrol'",nativeQuery = true)
    public List<Stockcontrol> findByUsername(@Param("username") String username);
}