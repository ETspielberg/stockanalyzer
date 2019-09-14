package org.unidue.ub.libintel.stockanalyzer.settingsrepositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.unidue.ub.libintel.stockanalyzer.model.lists.MemorylistItem;

import java.util.UUID;

/**
 * Created by Eike on 08.07.2017.
 */
@RepositoryRestResource(collectionResourceRel = "memorylistItem", path = "memorylistItem")
public interface MemorylistItemRepository extends JpaRepository<MemorylistItem,UUID> {
}
