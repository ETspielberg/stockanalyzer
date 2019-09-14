package org.unidue.ub.libintel.stockanalyzer.controller;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.unidue.ub.libintel.stockanalyzer.model.settings.Stockcontrol;
import org.unidue.ub.libintel.stockanalyzer.settingsrepositories.AlertcontrolRepository;
import org.unidue.ub.libintel.stockanalyzer.settingsrepositories.StockcontrolRepository;
import org.unidue.ub.libintel.stockanalyzer.model.settings.Alertcontrol;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
@EnableScheduling
public class JobLauncherController {

    private final static long dayInMillis = 24L * 60L * 60L * 1000L;

    private final static long HalfAYear = 182L * dayInMillis;

    private final JobLauncher jobLauncher;

    private final Job eventanalyzerJob;

    private final Job nrequestsJob;

    private final Job alertcontrolJob;

    private final StockcontrolRepository stockcontrolRepository;

    private final AlertcontrolRepository alertcontrolRepository;

    @Autowired
    public JobLauncherController(JobLauncher jobLauncher,
                                 Job eventanalyzerJob,
                                 Job nrequestsJob,
                                 Job alertcontrolJob,
                                 StockcontrolRepository stockcontrolRepository,
                                 AlertcontrolRepository alertcontrolRepository) {
        this.jobLauncher = jobLauncher;
        this.eventanalyzerJob = eventanalyzerJob;
        this.nrequestsJob = nrequestsJob;
        this.stockcontrolRepository = stockcontrolRepository;
        this.alertcontrolRepository = alertcontrolRepository;
        this.alertcontrolJob = alertcontrolJob;
    }

    @RequestMapping("/eventanalyzer/{identifier}")
    public ResponseEntity<?> runEventanalzer(@PathVariable String identifier) throws Exception {
        JobParametersBuilder jobParametersBuilder = new JobParametersBuilder();
        jobParametersBuilder.addString("stockcontrol.identifier", identifier).addLong("time", System.currentTimeMillis()).toJobParameters();
        JobParameters jobParameters = jobParametersBuilder.toJobParameters();
        jobLauncher.run(eventanalyzerJob, jobParameters);
        return ResponseEntity.status(HttpStatus.FOUND).build();
    }


    @Scheduled(cron = "0 0 2 * * *")
    @RequestMapping("/nrequests")
    public void runNrequestsCollector() throws Exception {
        JobParametersBuilder jobParametersBuilder = new JobParametersBuilder();
        jobParametersBuilder.addDate("date", new Date()).addLong("time", System.currentTimeMillis()).toJobParameters();
        JobParameters jobParameters = jobParametersBuilder.toJobParameters();
        jobLauncher.run(nrequestsJob, jobParameters);
    }

    @Scheduled(cron = "0 0 23 * * SAT")
    @PostMapping("/runalleventanalyzer")
    public void runAllEventanalyzer() throws JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {
        List<Stockcontrol> stockcontrols = new ArrayList<>();
        stockcontrolRepository.findAll().forEach(
                entry -> {
                    if (entry.getLastrun().getTime() < System.currentTimeMillis() - HalfAYear)
                        stockcontrols.add(entry);
                });
        for (Stockcontrol stockcontrol : stockcontrols) {
            JobParametersBuilder jobParametersBuilder = new JobParametersBuilder();
            jobParametersBuilder.addString("stockcontrol.identifier", stockcontrol.getIdentifier()).addLong("time", System.currentTimeMillis()).toJobParameters();
            JobParameters jobParameters = jobParametersBuilder.toJobParameters();
            jobLauncher.run(eventanalyzerJob, jobParameters);
        }
    }

    @Scheduled(cron = "0 0 10 * * *")
    @PostMapping("/sendNrequestsToElisa")
    public void sendNrequestsToElisa() throws JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {
        List<Alertcontrol> alertcontrols = new ArrayList<>();
        alertcontrolRepository.findAll().forEach(entry -> alertcontrols.add(entry));
        for (Alertcontrol alertcontrol : alertcontrols) {
            if (alertcontrol.isForElisa()) {
                JobParametersBuilder jobParametersBuilder = new JobParametersBuilder();
                jobParametersBuilder.addString("alertcontrols.identifier", alertcontrol.getIdentifier()).addLong("time", System.currentTimeMillis()).toJobParameters();
                JobParameters jobParameters = jobParametersBuilder.toJobParameters();
                jobLauncher.run(alertcontrolJob, jobParameters);
            }
        }
    }
    /*

    @PostMapping("/runFullManifestationUsage")
    public void runFullManifestationUsage() throws Exception {
        JobParametersBuilder jobParametersBuilder = new JobParametersBuilder();
        jobParametersBuilder.addDate("date", new Date()).addLong("time", System.currentTimeMillis()).toJobParameters();
        JobParameters jobParameters = jobParametersBuilder.toJobParameters();
        jobLauncher.run(usageJob, jobParameters);
    }
    */

}
