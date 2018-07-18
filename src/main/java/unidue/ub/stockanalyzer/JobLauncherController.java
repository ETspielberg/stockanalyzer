package unidue.ub.stockanalyzer;

import org.springframework.batch.core.*;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import unidue.ub.stockanalyzer.model.settings.Alertcontrol;
import unidue.ub.stockanalyzer.model.settings.Stockcontrol;
import unidue.ub.stockanalyzer.settingsrepositories.AlertcontrolRepository;
import unidue.ub.stockanalyzer.settingsrepositories.StockcontrolRepository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
@EnableScheduling
public class JobLauncherController {

    private final static long dayInMillis = 24L*60L*60L*1000L;

    private final static long HalfAYear = 182L * dayInMillis;

    @Autowired
    JobLauncher jobLauncher;

    @Autowired
    Job eventanalyzerJob;

    @Autowired
    Job nrequestsJob;

    @Autowired
    private StockcontrolRepository stockcontrolRepository;

    @Autowired
    private AlertcontrolRepository alertcontrolRepository;

    @RequestMapping("/eventanalyzer")
    public ResponseEntity<?> runEventanalzer(String identifier) throws Exception {
        JobParametersBuilder jobParametersBuilder = new JobParametersBuilder();
        jobParametersBuilder.addString("stockcontrol.identifier", identifier).addLong("time",System.currentTimeMillis()).toJobParameters();
        JobParameters jobParameters = jobParametersBuilder.toJobParameters();
        jobLauncher.run(eventanalyzerJob, jobParameters);
        return ResponseEntity.status(HttpStatus.FOUND).build();
    }

    /*
    avoid annoyance with HATEOAS spring implementation
     */
    @GetMapping("/stockcontrol/all")
    public ResponseEntity<List<Stockcontrol>> getAllStockcontrol() {
        List<Stockcontrol> stockcontrols = new ArrayList<>();
        stockcontrolRepository.findAll().forEach(stockcontrols::add);
        return ResponseEntity.ok(stockcontrols);
    }

    @GetMapping("/alertcontrol/all")
    public ResponseEntity<List<Alertcontrol>> getAllAlertcontrol() {
        List<Alertcontrol> alertcontrols = new ArrayList<>();
        alertcontrolRepository.findAll().forEach(alertcontrols::add);
        return ResponseEntity.ok(alertcontrols);
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
