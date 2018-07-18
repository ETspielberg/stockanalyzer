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
import unidue.ub.media.monographs.Item;
import unidue.ub.media.monographs.Manifestation;
import unidue.ub.stockanalyzer.clients.IgnoredGetterClient;
import unidue.ub.stockanalyzer.model.data.Eventanalysis;
import unidue.ub.stockanalyzer.model.settings.Stockcontrol;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static unidue.ub.stockanalyzer.MonographTools.getFilteredEvents;

public class ManifestationProcessor implements ItemProcessor<Manifestation, Eventanalysis> {

    private static final Logger log = LoggerFactory.getLogger(ManifestationProcessor.class);

    private Stockcontrol stockcontrol;

    @Autowired
    private IgnoredGetterClient ignoredGetterClient;

    public ManifestationProcessor() {
    }

    @Override
    public Eventanalysis process(final Manifestation manifestation) {
        log.info("analyzing manifestation " + manifestation.getTitleID() + " and shelfmark " + manifestation.getShelfmark());
        List<Ignored> ignoreds = new ArrayList<>(ignoredGetterClient.getIgnoredForTittleId(manifestation.getTitleID()));
        if (ignoreds.size() != 0) {
            for (Ignored ignored : ignoreds) {
                if (ignored.getExpire().after(new Date()) && ignored.getType().equals("eventanalysis"))
                    return null;
            }
        }
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
        Eventanalysis analysis = new EventAnalyzer().analyze(events, stockcontrol);
        analysis.setTitleId(manifestation.getTitleID());
        analysis.setShelfmark(manifestation.getShelfmark());
        analysis.setMab(manifestation.getBibliographicInformation().getFullDescription());
        return analysis;
    }
    }
