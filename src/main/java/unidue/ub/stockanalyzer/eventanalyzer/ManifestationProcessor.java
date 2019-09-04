package unidue.ub.stockanalyzer.eventanalyzer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import unidue.ub.media.blacklist.Ignored;
import unidue.ub.media.monographs.Event;
import unidue.ub.media.monographs.Manifestation;
import unidue.ub.stockanalyzer.clients.BlacklistClient;
import unidue.ub.stockanalyzer.model.data.Eventanalysis;
import unidue.ub.stockanalyzer.model.settings.Stockcontrol;
import unidue.ub.stockanalyzer.settingsrepositories.ItemGroupRepository;
import unidue.ub.stockanalyzer.settingsrepositories.UserGroupRepository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static unidue.ub.stockanalyzer.MonographUtils.getFilteredEvents;
import static unidue.ub.stockanalyzer.MonographUtils.getActiveCollectionOverview;

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
        List<Event> events = getFilteredEvents(manifestation.getItems(),itemFilter);
        Eventanalysis analysis = new EventAnalyzer(userGroupRepository, itemGroupRepository).analyze(events, stockcontrol);
        analysis.setTitleId(manifestation.getTitleID());
        analysis.setShelfmark(manifestation.getShelfmark());
        analysis.setMab(manifestation.getBibliographicInformation().getFullDescription());
        analysis.setComment(getActiveCollectionOverview(manifestation.getItems()));
        return analysis;
    }
    }
