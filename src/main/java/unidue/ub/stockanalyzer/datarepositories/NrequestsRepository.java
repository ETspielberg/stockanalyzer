package unidue.ub.stockanalyzer.datarepositories;


import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Temporal;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import unidue.ub.stockanalyzer.model.data.Nrequests;

import javax.persistence.TemporalType;
import java.util.Date;
import java.util.List;

@RepositoryRestResource(collectionResourceRel = "nrequests", path = "nrequests")
public interface NrequestsRepository  extends PagingAndSortingRepository<Nrequests, String> {

    @Query(value = "SELECT p.* FROM Nrequests p WHERE SUBSTRING(p.shelfmark,1,3) BETWEEN :startNotation AND :endNotation", nativeQuery = true)
    List<Nrequests> getNrequestsForNotationgroup(@Param("startNotation") String startNotation, @Param("endNotation") String endNotation);

    @Query(value = "SELECT p.* FROM Nrequests p WHERE SUBSTRING(p.shelfmark,1,3) BETWEEN :startNotation AND :endNotation AND date >= :startDate", nativeQuery = true)
    List<Nrequests> getNrequestsForNotationgroupSinceDate(@Param("startNotation") String startNotation, @Param("endNotation") String endNotation, @Param("startDate") @Temporal(TemporalType.TIMESTAMP) Date startDate);

    @Query(value = "SELECT p.* FROM Nrequests p WHERE SUBSTRING(p.shelfmark,1,3) BETWEEN :startNotation AND :endNotation AND p.date >= :startDate  AND (p.total_duration > :thresholdDuration OR p.nrequests > thresholdNrequests OR p.ratio > :thresholdRatio )", nativeQuery = true)
    List<Nrequests> getNrequestsForAlertcontrolData(String startNotation, String endNotation, @Temporal(TemporalType.TIMESTAMP) Date startDate, int thresholdDuration, int thresholdNrequests, double thresholdRatio);

}
