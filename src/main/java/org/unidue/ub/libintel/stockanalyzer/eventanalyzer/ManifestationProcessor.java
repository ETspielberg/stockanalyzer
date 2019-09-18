package org.unidue.ub.libintel.stockanalyzer.eventanalyzer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.unidue.ub.libintel.stockanalyzer.MonographUtils;
import org.unidue.ub.libintel.stockanalyzer.clients.BlacklistClient;
import org.unidue.ub.libintel.stockanalyzer.model.data.Eventanalysis;
import org.unidue.ub.libintel.stockanalyzer.model.media.Event;
import org.unidue.ub.libintel.stockanalyzer.model.media.Manifestation;
import org.unidue.ub.libintel.stockanalyzer.model.settings.Stockcontrol;
import org.unidue.ub.libintel.stockanalyzer.settingsrepositories.ItemGroupRepository;
import org.unidue.ub.libintel.stockanalyzer.settingsrepositories.UserGroupRepository;

import java.util.List;

public class ManifestationProcessor implements ItemProcessor<Manifestation, Eventanalysis> {

    private static final Logger log = LoggerFactory.getLogger(ManifestationProcessor.class);

    private Stockcontrol stockcontrol;

    @Autowired
    private BlacklistClient blacklistClient;

    @Autowired
    private ItemGroupRepository itemGroupRepository;

    @Autowired
    UserGroupRepository userGroupRepository;

    public ManifestationProcessor() {
    }

    @Override
    public Eventanalysis process(final Manifestation manifestation) {
        if (blacklistClient.isBlocked(manifestation.getTitleID(), "eventanalysis")) {
            log.debug(manifestation.getTitleID() + " is blacklisted, skipping analysis");
            return null;
        } else
            log.debug("analyzing manifestation " + manifestation.getTitleID() + " and shelfmark " + manifestation.getShelfmark());
        return calculateAnalysis(manifestation, stockcontrol);
    }

    @BeforeStep
    public void retrieveStockcontrol(StepExecution stepExecution) {
        JobExecution jobExecution = stepExecution.getJobExecution();
        ExecutionContext jobContext = jobExecution.getExecutionContext();
        this.stockcontrol = (Stockcontrol) jobContext.get("stockcontrol");
        log.info("retrieved stockcontrol " + stockcontrol.toString() + " from execution context by manifestation reader");
    }

    private Eventanalysis calculateAnalysis(Manifestation manifestation, Stockcontrol stockcontrol) {
        ItemFilter itemFilter = new ItemFilter(stockcontrol.getCollections(), stockcontrol.getMaterials());
        List<Event> events = MonographUtils.getFilteredEvents(manifestation.getItems(),itemFilter);
        Eventanalysis analysis = new EventAnalyzer(userGroupRepository, itemGroupRepository).analyze(events, stockcontrol);
        analysis.setTitleId(manifestation.getTitleID());
        analysis.setShelfmark(manifestation.getShelfmark());
        analysis.setMab(manifestation.getBibliographicInformation().getFullDescription());
        analysis.setComment(MonographUtils.getActiveCollectionOverview(manifestation.getItems()));
        return analysis;
    }
    }
