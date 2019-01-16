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
import unidue.ub.media.blacklist.Ignored;
import unidue.ub.media.monographs.Event;
import unidue.ub.media.monographs.Expression;
import unidue.ub.stockanalyzer.clients.IgnoredGetterClient;
import unidue.ub.stockanalyzer.model.data.Eventanalysis;
import unidue.ub.stockanalyzer.model.settings.Stockcontrol;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static unidue.ub.stockanalyzer.MonographUtils.getActiveCollectionOverview;
import static unidue.ub.stockanalyzer.MonographUtils.getFilteredEvents;

@StepScope
public class ExpressionProcessor implements ItemProcessor<Expression, Eventanalysis> {

    private static final Logger log = LoggerFactory.getLogger(ExpressionProcessor.class);

    @Autowired
    private IgnoredGetterClient ignoredGetterClient;

    private Stockcontrol stockcontrol;

    public ExpressionProcessor() {
    }

    @Override
    public Eventanalysis process(final Expression expression) {
        log.info("analyzing expression  " + expression.getShelfmarkBase() + " and shelfmark " + expression.getShelfmarkBase());
        List<Ignored> ignoreds = new ArrayList<>();
        ignoredGetterClient.getIgnoredForTittleId(expression.getShelfmarkBase()).forEach(ignoreds::add);
        if (ignoreds.size() != 0) {
            for (Ignored ignored : ignoreds) {
                if (ignored.getExpire().after(new Date()) && ignored.getType().equals("eventanalysis"))
                    return null;
            }
        }
        return calculateAnalysis(expression, stockcontrol);
    }

    private Eventanalysis calculateAnalysis(Expression expression, Stockcontrol stockcontrol) {
        ItemFilter itemFilter = new ItemFilter(stockcontrol.getCollections(), stockcontrol.getMaterials());
        List<Event> events = getFilteredEvents(expression.getItems(),itemFilter);
        Eventanalysis analysis = new EventAnalyzer().analyze(events, stockcontrol);
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
        log.info("retrieved stockcontrol " + stockcontrol.toString() + " from execution context by expression processor");
    }
}
