package unidue.ub.stockanalyzer.eventanalyzer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import unidue.ub.media.monographs.Event;
import unidue.ub.media.monographs.Expression;
import unidue.ub.stockanalyzer.clients.BlacklistClient;
import unidue.ub.stockanalyzer.model.data.Eventanalysis;
import unidue.ub.stockanalyzer.model.settings.Stockcontrol;
import unidue.ub.stockanalyzer.settingsrepositories.ItemGroupRepository;
import unidue.ub.stockanalyzer.settingsrepositories.UserGroupRepository;

import java.util.List;

import static unidue.ub.stockanalyzer.MonographUtils.getActiveCollectionOverview;
import static unidue.ub.stockanalyzer.MonographUtils.getFilteredEvents;

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
        List<Event> events = getFilteredEvents(expression.getItems(),itemFilter);
        Eventanalysis analysis = new EventAnalyzer(userGroupRepository, itemGroupRepository).analyze(events, stockcontrol);
        analysis.setTitleId(expression.getShelfmarkBase());
        analysis.setShelfmark(expression.getShelfmarkBase());
        analysis.setMab(expression.getBibliographicInformation().toString());
        analysis.setComment(getActiveCollectionOverview(expression.getItems()));
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
