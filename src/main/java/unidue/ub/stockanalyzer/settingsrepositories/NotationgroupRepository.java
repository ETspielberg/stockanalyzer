package unidue.ub.stockanalyzer.settingsrepositories;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import unidue.ub.settings.fachref.Notationgroup;

/**
 * Created by Eike on 07.07.2017.
 */
@RepositoryRestResource(collectionResourceRel = "notationgroup", path = "notationgroup")
public interface NotationgroupRepository extends PagingAndSortingRepository<Notationgroup,String> {
}
