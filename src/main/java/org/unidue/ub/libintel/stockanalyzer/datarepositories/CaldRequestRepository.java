package org.unidue.ub.libintel.stockanalyzer.datarepositories;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.unidue.ub.libintel.stockanalyzer.model.data.CaldRequest;

@RepositoryRestResource(collectionResourceRel = "caldrequest", path = "caldrequest")
public interface CaldRequestRepository extends PagingAndSortingRepository<CaldRequest, String> {

}
