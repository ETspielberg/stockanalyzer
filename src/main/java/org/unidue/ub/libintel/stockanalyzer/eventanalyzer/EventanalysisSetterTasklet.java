package org.unidue.ub.libintel.stockanalyzer.eventanalyzer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.stereotype.Component;
import org.unidue.ub.libintel.stockanalyzer.datarepositories.EventanalysisRepository;
import org.unidue.ub.libintel.stockanalyzer.model.settings.Status;

@Component
@StepScope
public class EventanalysisSetterTasklet implements Tasklet {

    private Status status;

    @Value("#{jobParameters['stockcontrol.identifier'] ?: 'newProfile'}")
    public String identifier;

    Logger log = LoggerFactory.getLogger(EventanalysisSetterTasklet.class);

    @Autowired
    private EventanalysisRepository eventanalysisRepository;

    public RepeatStatus execute(StepContribution contribution,
                                ChunkContext chunkContext) {
        try {
            eventanalysisRepository.setEventanalysisStatusForStockcontrolIds(identifier,status.name());
        } catch (JpaSystemException jse) {
            log.debug("no old eventanalyses to change status to obsolete.", jse);
        }

        return RepeatStatus.FINISHED;
    }

    EventanalysisSetterTasklet setStatus(Status status) {
        this.status = status;
        return this;
    }
}
