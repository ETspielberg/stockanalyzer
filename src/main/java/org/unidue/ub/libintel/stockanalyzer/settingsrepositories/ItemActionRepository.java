package org.unidue.ub.libintel.stockanalyzer.settingsrepositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.unidue.ub.libintel.stockanalyzer.model.lists.ItemAction;

import java.util.UUID;

@RepositoryRestResource(collectionResourceRel = "itemAction", path = "itemAction")
public interface ItemActionRepository extends JpaRepository<ItemAction, UUID> {
}
