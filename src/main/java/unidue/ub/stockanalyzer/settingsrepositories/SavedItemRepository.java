package unidue.ub.stockanalyzer.settingsrepositories;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import unidue.ub.stockanalyzer.model.lists.SavedItem;

/**
 * Created by Eike on 08.07.2017.
 */
@RepositoryRestResource(collectionResourceRel = "savedItem", path = "savedItem")
public interface SavedItemRepository extends PagingAndSortingRepository<SavedItem,String> {
}
