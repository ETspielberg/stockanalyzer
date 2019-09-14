package org.unidue.ub.libintel.stockanalyzer.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.unidue.ub.libintel.stockanalyzer.datarepositories.EventanalysisRepository;
import org.unidue.ub.libintel.stockanalyzer.model.settings.Notationgroup;
import org.unidue.ub.libintel.stockanalyzer.model.settings.Stockcontrol;
import org.unidue.ub.libintel.stockanalyzer.settingsrepositories.AlertcontrolRepository;
import org.unidue.ub.libintel.stockanalyzer.settingsrepositories.StockcontrolRepository;
import org.unidue.ub.libintel.stockanalyzer.clients.BlacklistClient;
import org.unidue.ub.libintel.stockanalyzer.clients.NotationGetterClient;
import org.unidue.ub.libintel.stockanalyzer.datarepositories.NrequestsRepository;
import org.unidue.ub.libintel.stockanalyzer.model.data.Nrequests;
import org.unidue.ub.libintel.stockanalyzer.model.settings.Alertcontrol;

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

    private final Logger log = LoggerFactory.getLogger(DataController.class);

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
        if (alertcontrolOpt.isEmpty())
            return ResponseEntity.notFound().build();
        Alertcontrol alertcontrol = alertcontrolOpt.get();
        Notationgroup notationgroup = notationGetterClient.getNotationgroup(alertcontrol.getNotationgroup()).getContent();
        log.debug("retrieved notationgroup with start notation " + notationgroup.getNotationsStart() + " till " + notationgroup.getNotationsEnd());
        Timestamp timestamp = new Timestamp(System.currentTimeMillis() - alertcontrol.getTimeperiod()*24*60*60*1000);
        List<Nrequests> nrequestss = this.nrequestsRepository.getNrequestsForAlertcontrolData(notationgroup.getNotationsStart(), notationgroup.getNotationsEnd(), timestamp, (long) alertcontrol.getThresholdDuration(), alertcontrol.getThresholdRequests(), alertcontrol.getThresholdRatio());
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

    @GetMapping("stockcontrol/running/peruser/{username}")
    public ResponseEntity<List<Stockcontrol>> getRunningStockcontrols(@PathVariable String username) {
        List<Stockcontrol> stockcontrols = new ArrayList<>(stockcontrolRepository.findRunningByUsername(username));
        return ResponseEntity.ok(stockcontrols);
    }

    @PostMapping("eventanalysis/setAnalysisStatus")
    public HttpStatus setAnalysisToOld(@RequestBody Map<String, String> json) {
        eventanalysisRepository.setEventanalysisStatusForStockcontrolIds(json.get("identifier"),json.get("status"));
        return HttpStatus.FOUND;
    }
}
