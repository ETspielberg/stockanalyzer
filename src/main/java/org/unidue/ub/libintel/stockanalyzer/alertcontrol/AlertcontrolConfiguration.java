package org.unidue.ub.libintel.stockanalyzer.alertcontrol;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.unidue.ub.libintel.stockanalyzer.clients.ElisaClient;
import org.unidue.ub.libintel.stockanalyzer.model.elisa.ElisaRequest;
import org.unidue.ub.libintel.stockanalyzer.model.settings.Alertcontrol;
import org.unidue.ub.libintel.stockanalyzer.service.NrequestsService;

@Configuration
public class AlertcontrolConfiguration {

    private final JobBuilderFactory jobBuilderFactory;

    private final StepBuilderFactory stepBuilderFactory;

    private final NrequestsService nrequestsService;

    private final ElisaClient elisaClient;

    @Autowired
    public AlertcontrolConfiguration(JobBuilderFactory jobBuilderFactory,
                                     StepBuilderFactory stepBuilderFactory,
                                     NrequestsService nrequestsService,
                                     ElisaClient elisaClient) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
        this.nrequestsService = nrequestsService;
        this.elisaClient = elisaClient;
    }

    @Bean
    @StepScope
    public AlertcontrolReader alertcontrolReader() {
        return new AlertcontrolReader();
    }

    @Bean
    @StepScope
    public AlertcontrolProcessor alertcontrolProcessor() {
        return new AlertcontrolProcessor(nrequestsService);
    }

    @Bean
    @StepScope
    public ElisaWriter writer() {
        return new ElisaWriter(elisaClient);
    }

    @Bean
    public Step stepNrequests() {
        return stepBuilderFactory.get("stepNrequests")
                .<Alertcontrol, ElisaRequest>chunk(1000)
                .reader(alertcontrolReader())
                .processor(alertcontrolProcessor())
                .writer(writer())
                .build();
    }

    @Bean
    public Job alertcontrolJob() {
        return jobBuilderFactory.get("alertcontrolJob")
                .incrementer(new RunIdIncrementer())
                .start(stepNrequests())
                .build();

    }
}
