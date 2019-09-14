package org.unidue.ub.libintel.stockanalyzer.stockstatistics;

import org.unidue.ub.libintel.stockanalyzer.eventanalyzer.*;

/*
@Configuration
@EnableBatchProcessing
public class StockstatisticsConfiguration {

    private NotationGetterClient notationGetterClient;


    private final JobBuilderFactory jobBuilderFactory;

    private final StepBuilderFactory stepBuilderFactory;

    private final ManifestationGetterClient manifestationGetterClient;

    @Autowired
    public StockstatisticsConfiguration(JobBuilderFactory jobBuilderFactory,
                                        StepBuilderFactory stepBuilderFactory,
                                        NotationGetterClient notationGetterClient,
                                        ManifestationGetterClient manifestationGetterClient) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
        this.notationGetterClient = notationGetterClient;
        this.manifestationGetterClient = manifestationGetterClient;
    }

    @Bean
    @StepScope
    public ManifestationReader manifestationReader() {
        return new ManifestationReader();
    }


    @Bean
    @StepScope
    public PrintUsageWriter mongoWriter() {
        return new PrintUsageWriter();
    }

    @Bean
    public Job usageJob(JobExecutionListener listener) {
        FlowBuilder<Flow> flowBuilder = new FlowBuilder<>("manifestationUsageFlow");
        Flow flow = flowBuilder
                .start(stepTransform())
                .end();
        return jobBuilderFactory.get("usageJob")
                .incrementer(new RunIdIncrementer())
                .listener(listener)
                .start(flow)
                .end()
                .build();
    }

    @Bean
    public Step stepTransform() {
        return stepBuilderFactory.get("stepTransform")
                .<Notation, List<ManifestationUsage>>chunk(10)
                .reader(new NotationReader(notationGetterClient))
                .processor(new NotationProcessor(manifestationGetterClient))
                .writer(mongoWriter())
                .build();
    }

}
*/
