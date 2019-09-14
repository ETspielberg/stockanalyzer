package org.unidue.ub.libintel.stockanalyzer.settingsrepositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.unidue.ub.libintel.stockanalyzer.model.lists.Memorylist;

import java.util.List;
import java.util.UUID;

/**
 * Created by Eike on 08.07.2017.
 */
@RepositoryRestResource(collectionResourceRel = "memorylist", path = "memorylist")
public interface MemorylistRepository extends JpaRepository<Memorylist,UUID> {

    List<Memorylist> findAllByUsernameOrderByName(String username);

    List<Memorylist> findAllByUsernameOrderByLastChangeDesc(String username);

}
