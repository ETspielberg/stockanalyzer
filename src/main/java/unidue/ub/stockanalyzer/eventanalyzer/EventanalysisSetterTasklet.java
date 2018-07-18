package unidue.ub.stockanalyzer.eventanalyzer;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import unidue.ub.stockanalyzer.datarepositories.EventanalysisRepository;
import unidue.ub.stockanalyzer.model.settings.Status;

@Component
@StepScope
public class EventanalysisSetterTasklet implements Tasklet {

    private Status status;

    @Value("#{jobParameters['stockcontrol.identifier'] ?: 'newProfile'}")
    public String identifier;

    @Autowired
    private EventanalysisRepository eventanalysisRepository;

    public RepeatStatus execute(StepContribution contribution,
                                ChunkContext chunkContext) {
        eventanalysisRepository.setEventanalysisStatusForStockcontrolIds(identifier,status.name());
        return RepeatStatus.FINISHED;
    }

    EventanalysisSetterTasklet setStatus(Status status) {
        this.status = status;
        return this;
    }
}
