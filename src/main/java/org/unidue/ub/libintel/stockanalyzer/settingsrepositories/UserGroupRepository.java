package org.unidue.ub.libintel.stockanalyzer.settingsrepositories;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.unidue.ub.libintel.stockanalyzer.model.settings.UserGroup;

@RepositoryRestResource(collectionResourceRel = "usergroup", path = "usergroup")
public interface UserGroupRepository extends PagingAndSortingRepository<UserGroup,String> {
}
