package unidue.ub.stockanalyzer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
public class DataController {

    private final StockcontrolRepository stockcontrolRepository;

    private final AlertcontrolRepository alertcontrolRepository;

    private final EventanalysisRepository eventanalysisRepository;

    private final NrequestsRepository nrequestsRepository;

    @Autowired
    public DataController(StockcontrolRepository stockcontrolRepository, AlertcontrolRepository alertcontrolRepository, EventanalysisRepository eventanalysisRepository, NrequestsRepository nrequestsRepository) {
        this.stockcontrolRepository = stockcontrolRepository;
        this.alertcontrolRepository = alertcontrolRepository;
        this.eventanalysisRepository = eventanalysisRepository;
        this.nrequestsRepository = nrequestsRepository;
    }

    @GetMapping("/nrequests/getForTimeperiod")
    public ResponseEntity<?> getForTimeperiod(@Param("startNotation") String startNotation, @Param("endNotation") String endNotation, @Param(value="timeperiod") Integer timeperiod) {

        LocalDateTime startDate = LocalDateTime.now().minusDays(timeperiod);
        ZonedDateTime zonedStartDate = startDate.atZone(ZoneId.systemDefault());
        Date date = Date.from(zonedStartDate.toInstant());
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.MILLISECOND, 0);

        List<Nrequests> nrequests = nrequestsRepository.getNrequestsForNotationgroupSinceDate(startNotation,endNotation, new Timestamp(cal.getTimeInMillis()));
        if (nrequests == null)
            nrequests = new ArrayList<>();
        return ResponseEntity.ok(nrequests);

    }

    @GetMapping("/stockcontrol/all")
    public ResponseEntity<List<Stockcontrol>> getAllStockcontrol() {
        List<Stockcontrol> stockcontrols = new ArrayList<>();
        stockcontrolRepository.findAll().forEach(stockcontrols::add);
        return ResponseEntity.ok(stockcontrols);
    }

    @GetMapping("/stockcontrol/peruser/{username}")
    public ResponseEntity<List<Stockcontrol>> getAllStockcontrol(@PathVariable String username) {
        List<Stockcontrol> stockcontrols = new ArrayList<>(stockcontrolRepository.findByUsername(username));
        return ResponseEntity.ok(stockcontrols);
    }

    @GetMapping("/alertcontrol/peruser/{username}")
    public ResponseEntity<List<Alertcontrol>> getAllAlertcontrol(@PathVariable String username) {
        List<Alertcontrol> alertcontrols = new ArrayList<>(alertcontrolRepository.findByUsername(username));
        return ResponseEntity.ok(alertcontrols);
    }

    @PostMapping("eventanalysis/setAnalysisStatus")
    public HttpStatus setAnalysisToOld(@RequestBody Map<String, String> json) {
        eventanalysisRepository.setEventanalysisStatusForStockcontrolIds(json.get("identifier"),json.get("status"));
        return HttpStatus.FOUND;
    }
}
