package unidue.ub.stockanalyzer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import unidue.ub.stockanalyzer.clients.BlacklistClient;
import unidue.ub.stockanalyzer.clients.NotationGetterClient;
import unidue.ub.stockanalyzer.datarepositories.EventanalysisRepository;
import unidue.ub.stockanalyzer.datarepositories.NrequestsRepository;
import unidue.ub.stockanalyzer.model.data.Nrequests;
import unidue.ub.stockanalyzer.model.settings.Alertcontrol;
import unidue.ub.stockanalyzer.model.settings.Notationgroup;
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

    private final NotationGetterClient notationGetterClient;

    private final BlacklistClient blacklistClient;

    @Autowired
    public DataController(StockcontrolRepository stockcontrolRepository,
                          AlertcontrolRepository alertcontrolRepository,
                          EventanalysisRepository eventanalysisRepository,
                          NrequestsRepository nrequestsRepository,
                          NotationGetterClient notationGetterClient,
                          BlacklistClient blacklistClient) {
        this.stockcontrolRepository = stockcontrolRepository;
        this.alertcontrolRepository = alertcontrolRepository;
        this.eventanalysisRepository = eventanalysisRepository;
        this.nrequestsRepository = nrequestsRepository;
        this.notationGetterClient = notationGetterClient;
        this.blacklistClient = blacklistClient;
    }

    @GetMapping("/nrequests/getForTimeperiod")
    public ResponseEntity<?> getForTimeperiod(@Param("startNotation") String startNotation, @Param("endNotation") String endNotation, @Param(value="timeperiod") Long timeperiod) {
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

    @GetMapping("/nrequests/forAlertcontrol/{identifier}")
    public ResponseEntity<?> getForAlertcontrol(@PathVariable("identifier") String identifier, @RequestParam("requestor") String requestor) {
        List<Nrequests> notBlacklistedNrequests = new ArrayList<>();
        Optional<Alertcontrol> alertcontrolOpt = alertcontrolRepository.findById(identifier);
        if (!alertcontrolOpt.isPresent())
            return ResponseEntity.notFound().build();
        Alertcontrol alertcontrol = alertcontrolOpt.get();
        Notationgroup notationgroup = notationGetterClient.getNotationgroup(alertcontrol.getNotationgroup()).getContent();
        Timestamp timestamp = new Timestamp(System.currentTimeMillis() - alertcontrol.getTimeperiod()*24*60*60*1000);
        List<Nrequests> nrequestss = this.nrequestsRepository.getNrequestsForAlertcontrolData(notationgroup.getNotationsStart(), notationgroup.getNotationsEnd(), timestamp, alertcontrol.getThresholdDuration(), alertcontrol.getThresholdRequests(), alertcontrol.getThresholdRatio());
        for (Nrequests nrequests : nrequestss) {
            if (!this.blacklistClient.isBlocked(nrequests.getIdentifier(), "nrequests")) {
                if (!("".equals(requestor))) {
                    if (nrequests.getStatus() == null || nrequests.getStatus().equals("") || nrequests.getStatus().equals("NEW")) {
                        nrequests.setStatus(requestor);
                        notBlacklistedNrequests.add(nrequests);
                    } else if (nrequests.getStatus().contains(requestor)) {
                        continue;
                    } else {
                        nrequests.setStatus(nrequests.getStatus() + " " + requestor);
                        notBlacklistedNrequests.add(nrequests);
                    }
                    nrequestsRepository.save(nrequests);
                }
            }
        }
        return ResponseEntity.ok(notBlacklistedNrequests);
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
