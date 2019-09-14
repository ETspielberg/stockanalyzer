package org.unidue.ub.libintel.stockanalyzer;

import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.MapJobRepositoryFactoryBean;
import org.springframework.batch.support.transaction.ResourcelessTransactionManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;
import org.unidue.ub.libintel.stockanalyzer.model.lists.Memorylist;
import org.unidue.ub.libintel.stockanalyzer.model.lists.MemorylistItem;
import org.unidue.ub.libintel.stockanalyzer.model.settings.Alertcontrol;
import org.unidue.ub.libintel.stockanalyzer.model.settings.ItemGroup;
import org.unidue.ub.libintel.stockanalyzer.model.settings.Stockcontrol;
import org.unidue.ub.libintel.stockanalyzer.model.settings.UserGroup;
import org.unidue.ub.libintel.stockanalyzer.model.lists.ItemAction;
import org.unidue.ub.libintel.stockanalyzer.model.lists.TitleAction;

@Configuration
public class BatchConfiguration implements RepositoryRestConfigurer {

    @Override
    public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config) {
        config.exposeIdsFor(
                Alertcontrol.class,
                Stockcontrol.class,
                ItemGroup.class,
                UserGroup.class,
                Memorylist.class,
                MemorylistItem.class,
                ItemAction.class,
                TitleAction.class
        );
    }

    @Bean
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(100);
        return executor;
    }

    @Bean(name = "transactionManager")
    public PlatformTransactionManager transactionManager() {
        return new ResourcelessTransactionManager();
    }

    @Bean
    public JobRepository jobRepository() throws Exception {
        MapJobRepositoryFactoryBean mapJobRepositoryFactoryBean = new MapJobRepositoryFactoryBean(transactionManager());
        mapJobRepositoryFactoryBean.setTransactionManager(transactionManager());
        return mapJobRepositoryFactoryBean.getObject();
    }

    @Bean
    public SimpleJobLauncher jobLauncher(JobRepository jobRepository) {
        SimpleJobLauncher simpleJobLauncher = new SimpleJobLauncher();
        simpleJobLauncher.setJobRepository(jobRepository);
        return simpleJobLauncher;
    }

}
