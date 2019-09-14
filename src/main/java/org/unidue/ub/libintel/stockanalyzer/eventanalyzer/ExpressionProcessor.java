package org.unidue.ub.libintel.stockanalyzer.eventanalyzer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.unidue.ub.libintel.stockanalyzer.MonographUtils;
import org.unidue.ub.libintel.stockanalyzer.clients.BlacklistClient;
import org.unidue.ub.libintel.stockanalyzer.model.data.Eventanalysis;
import org.unidue.ub.libintel.stockanalyzer.model.settings.Stockcontrol;
import org.unidue.ub.libintel.stockanalyzer.settingsrepositories.ItemGroupRepository;
import org.unidue.ub.libintel.stockanalyzer.settingsrepositories.UserGroupRepository;
import unidue.ub.media.monographs.Event;
import unidue.ub.media.monographs.Expression;

import java.util.List;

@StepScope
public class ExpressionProcessor implements ItemProcessor<Expression, Eventanalysis> {

    private static final Logger log = LoggerFactory.getLogger(ExpressionProcessor.class);

    @Autowired
    private BlacklistClient blacklistClient;

    @Autowired
    private ItemGroupRepository itemGroupRepository;

    @Autowired
    UserGroupRepository userGroupRepository;

    private Stockcontrol stockcontrol;

    public ExpressionProcessor() {
    }

    @Override
    public Eventanalysis process(final Expression expression) {
        expression.calculateId();
        try {
            if (blacklistClient.isBlocked(expression.getShelfmarkBase(), "eventanalysis")) {
                log.debug(expression.getId() + " is blacklisted, skipping analysis");
                return null;
            } else {
                log.info("analyzing title ids '" + expression.getId() + "' with shelfmarks '" + expression.getShelfmarkBase() + "'");
                return calculateAnalysis(expression, stockcontrol);
            }
        } catch ( Exception e) {
            log.debug("could not connect to blacklist", e);
            return calculateAnalysis(expression, stockcontrol);
        }

    }

    private Eventanalysis calculateAnalysis(Expression expression, Stockcontrol stockcontrol) {
        ItemFilter itemFilter = new ItemFilter(stockcontrol.getCollections(), stockcontrol.getMaterials());
        List<Event> events = MonographUtils.getFilteredEvents(expression.getItems(),itemFilter);
        Eventanalysis analysis = new EventAnalyzer(userGroupRepository, itemGroupRepository).analyze(events, stockcontrol);
        analysis.setTitleId(expression.getShelfmarkBase());
        analysis.setShelfmark(expression.getShelfmarkBase());
        analysis.setMab(expression.getBibliographicInformation().toString());
        analysis.setComment(MonographUtils.getActiveCollectionOverview(expression.getItems()));
        return analysis;
    }

    @BeforeStep
    public void retrieveStockcontrol(StepExecution stepExecution) {
        JobExecution jobExecution = stepExecution.getJobExecution();
        ExecutionContext jobContext = jobExecution.getExecutionContext();
        this.stockcontrol = (Stockcontrol) jobContext.get("stockcontrol");
        log.debug("retrieved stockcontrol " + stockcontrol.toString() + " from execution context by expression processor");
    }
}
