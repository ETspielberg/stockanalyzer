package unidue.ub.stockanalyzer.controller;

import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import unidue.ub.stockanalyzer.datarepositories.EventanalysisRepository;
import unidue.ub.stockanalyzer.datarepositories.NrequestsRepository;
import unidue.ub.stockanalyzer.model.data.Nrequests;
import unidue.ub.stockanalyzer.model.settings.Alertcontrol;
import unidue.ub.stockanalyzer.model.settings.Stockcontrol;
import unidue.ub.stockanalyzer.settingsrepositories.AlertcontrolRepository;
import unidue.ub.stockanalyzer.settingsrepositories.StockcontrolRepository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

@Controller
@EnableScheduling
public class JobLauncherController {

    private final static long dayInMillis = 24L*60L*60L*1000L;

    private final static long HalfAYear = 182L * dayInMillis;

    private final
    JobLauncher jobLauncher;

    private final
    Job eventanalyzerJob;

    private final
    Job nrequestsJob;

    private final StockcontrolRepository stockcontrolRepository;

    @Autowired
    public JobLauncherController(JobLauncher jobLauncher, Job eventanalyzerJob, Job nrequestsJob, StockcontrolRepository stockcontrolRepository) {
        this.jobLauncher = jobLauncher;
        this.eventanalyzerJob = eventanalyzerJob;
        this.nrequestsJob = nrequestsJob;
        this.stockcontrolRepository = stockcontrolRepository;
    }

    @RequestMapping("/eventanalyzer/{identifier}")
    public ResponseEntity<?> runEventanalzer(@PathVariable String identifier) throws Exception {
        JobParametersBuilder jobParametersBuilder = new JobParametersBuilder();
        jobParametersBuilder.addString("stockcontrol.identifier", identifier).addLong("time",System.currentTimeMillis()).toJobParameters();
        JobParameters jobParameters = jobParametersBuilder.toJobParameters();
        jobLauncher.run(eventanalyzerJob, jobParameters);
        return ResponseEntity.status(HttpStatus.FOUND).build();
    }



    @Scheduled(cron="0 0 2 * * *")
    @RequestMapping("/nrequests")
    public void runNrequestsCollector() throws Exception {
        JobParametersBuilder jobParametersBuilder = new JobParametersBuilder();
        jobParametersBuilder.addDate("date", new Date()).addLong("time",System.currentTimeMillis()).toJobParameters();
        JobParameters jobParameters = jobParametersBuilder.toJobParameters();
        jobLauncher.run(nrequestsJob, jobParameters);
    }

    @Scheduled(cron="0 0 23 * * SAT")
    @PostMapping("/runalleventanalyzer")
    public void runAllEventanalyzer() throws JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {
        List<Stockcontrol> stockcontrols = new ArrayList<>();
        stockcontrolRepository.findAll().forEach(
                entry -> {
                    if (entry.getLastrun().getTime() < System.currentTimeMillis()- HalfAYear)
                        stockcontrols.add(entry);
                });
        for (Stockcontrol stockcontrol : stockcontrols) {
            JobParametersBuilder jobParametersBuilder = new JobParametersBuilder();
            jobParametersBuilder.addString("stockcontrol.identifier", stockcontrol.getIdentifier()).addLong("time",System.currentTimeMillis()).toJobParameters();
            JobParameters jobParameters = jobParametersBuilder.toJobParameters();
            jobLauncher.run(eventanalyzerJob, jobParameters);
        }
    }
}
