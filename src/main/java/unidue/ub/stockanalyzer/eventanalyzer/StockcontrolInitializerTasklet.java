package unidue.ub.stockanalyzer.eventanalyzer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import unidue.ub.stockanalyzer.model.settings.Stockcontrol;
import unidue.ub.stockanalyzer.settingsrepositories.StockcontrolRepository;

import java.util.Optional;

@Component
@StepScope
public class StockcontrolInitializerTasklet implements Tasklet {

    @Autowired
    private StockcontrolRepository stockcontrolRepository;

    @Value("#{jobParameters['stockcontrol.identifier'] ?: 'newProfile'}")
    public String identifier;

    private Logger log = LoggerFactory.getLogger(this.getClass());

    public RepeatStatus execute(StepContribution contribution,
                                ChunkContext chunkContext) {
        Optional<Stockcontrol> result = stockcontrolRepository.findById(identifier);
        Stockcontrol stockcontrol = result.orElseGet(Stockcontrol::new);
        ExecutionContext stepContext = chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext();
        stepContext.put("stockcontrol", stockcontrol);
        log.debug("stored stockcontrol " + identifier + " in job context");
        return RepeatStatus.FINISHED;
    }
}
