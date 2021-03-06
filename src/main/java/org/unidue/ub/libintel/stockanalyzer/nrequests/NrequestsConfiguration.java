package org.unidue.ub.libintel.stockanalyzer.nrequests;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.unidue.ub.libintel.stockanalyzer.DataWriter;
import org.unidue.ub.libintel.stockanalyzer.model.data.Nrequests;
import org.unidue.ub.libintel.stockanalyzer.model.media.Manifestation;

@Configuration
public class NrequestsConfiguration {

    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;

    @Bean
    @StepScope
    public NrequestsReader nrequestsReader() {
        return new NrequestsReader();
    }

    @Bean
    @StepScope
    public NrequestsProcessor nrequestsProcessor() {
        return new NrequestsProcessor();
    }

    @Bean
    @StepScope
    public DataWriter writer() {
        return new DataWriter();
    }

    @Bean
    public Step stepNrequests() {
        return stepBuilderFactory.get("stepNrequests")
                .<Manifestation, Nrequests>chunk(1000)
                .reader(nrequestsReader())
                .processor(nrequestsProcessor())
                .writer(writer())
                .build();
    }

    @Bean
    public Job nrequestsJob() {
        return jobBuilderFactory.get("nrequestsJob")
                .incrementer(new RunIdIncrementer())
                .start(stepNrequests())
                .build();

    }
}
