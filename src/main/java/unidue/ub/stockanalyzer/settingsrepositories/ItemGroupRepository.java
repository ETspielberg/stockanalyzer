package unidue.ub.stockanalyzer.settingsrepositories;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import unidue.ub.stockanalyzer.model.settings.ItemGroup;


@RepositoryRestResource(collectionResourceRel = "itemGroup", path = "itemGroup")
public interface ItemGroupRepository extends PagingAndSortingRepository<ItemGroup,String> {

}
