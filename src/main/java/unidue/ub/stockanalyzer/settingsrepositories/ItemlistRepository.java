package unidue.ub.stockanalyzer.settingsrepositories;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import unidue.ub.stockanalyzer.model.lists.Itemlist;
import unidue.ub.stockanalyzer.model.lists.SavedItem;

import java.util.List;
import java.util.UUID;

/**
 * Created by Eike on 08.07.2017.
 */
@RepositoryRestResource(collectionResourceRel = "itemlist", path = "itemlist")
public interface ItemlistRepository extends PagingAndSortingRepository<Itemlist,UUID> {

    List<Itemlist> findAllByUsernameOrderByName(String username);

    List<Itemlist> findAllByUsernameOrderByLastChangeDesc(String username);
}
