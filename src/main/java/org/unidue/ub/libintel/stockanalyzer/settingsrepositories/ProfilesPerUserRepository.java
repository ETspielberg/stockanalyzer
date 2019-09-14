package org.unidue.ub.libintel.stockanalyzer.settingsrepositories;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.unidue.ub.libintel.stockanalyzer.model.settings.ProfilesPerUser;

import java.util.List;

/**
 * Created by Eike on 05.07.2017.
 */
@RepositoryRestResource(collectionResourceRel = "profilesperuser", path = "profilesperuser")
public interface ProfilesPerUserRepository extends PagingAndSortingRepository<ProfilesPerUser,Long> {

    List<ProfilesPerUser> findByUsername(@Param("username") String username);

    List<ProfilesPerUser> findByIdentifier(@Param("identifier") String identifier);
}
