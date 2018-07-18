package unidue.ub.stockanalyzer.eventanalyzer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import unidue.ub.stockanalyzer.model.settings.Status;
import unidue.ub.stockanalyzer.model.settings.Stockcontrol;
import unidue.ub.stockanalyzer.settingsrepositories.StockcontrolRepository;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Component
public class StockcontrolSettingTasklet implements Tasklet {

    @Autowired
    private StockcontrolRepository stockcontrolRepository;

    private final static Logger log = LoggerFactory.getLogger(StockcontrolSettingTasklet.class);

    private Status status;

    @Override
    public RepeatStatus execute(StepContribution contribution,
                                ChunkContext chunkContext) {
        Stockcontrol stockcontrol = (Stockcontrol) chunkContext.getStepContext().getJobExecutionContext().get("stockcontrol");
        stockcontrol.setStatus(status);
        stockcontrol.setLastrun(Timestamp.valueOf(LocalDateTime.now()));
        stockcontrolRepository.save(stockcontrol);
        return RepeatStatus.FINISHED;
    }

    StockcontrolSettingTasklet setStatus(Status status) {
        this.status = status;
        return this;
    }
}
