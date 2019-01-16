package unidue.ub.stockanalyzer.settingsrepositories;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import unidue.ub.stockanalyzer.model.settings.UserGroup;

@RepositoryRestResource(collectionResourceRel = "usergroup", path = "usergroup")
public interface UserGroupRepository extends PagingAndSortingRepository<UserGroup,String> {
}
