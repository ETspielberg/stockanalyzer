package org.unidue.ub.libintel.stockanalyzer.settingsrepositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.unidue.ub.libintel.stockanalyzer.model.lists.TitleAction;

import java.util.UUID;

@RepositoryRestResource(collectionResourceRel = "titleAction", path = "titleAction")
public interface TitleActionRepository extends JpaRepository<TitleAction, UUID> {
}
