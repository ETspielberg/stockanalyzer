package org.unidue.ub.libintel.stockanalyzer.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.unidue.ub.libintel.stockanalyzer.clients.BlacklistClient;
import org.unidue.ub.libintel.stockanalyzer.clients.NotationGetterClient;
import org.unidue.ub.libintel.stockanalyzer.datarepositories.NrequestsRepository;
import org.unidue.ub.libintel.stockanalyzer.model.data.Nrequests;
import org.unidue.ub.libintel.stockanalyzer.model.settings.Alertcontrol;
import org.unidue.ub.libintel.stockanalyzer.model.settings.Notationgroup;
import org.unidue.ub.libintel.stockanalyzer.settingsrepositories.AlertcontrolRepository;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
public class NrequestsService {


    private final AlertcontrolRepository alertcontrolRepository;

    private final NrequestsRepository nrequestsRepository;

    private final NotationGetterClient notationGetterClient;

    private final BlacklistClient blacklistClient;

    private final Logger log = LoggerFactory.getLogger(NrequestsService.class);

    public NrequestsService(
            AlertcontrolRepository alertcontrolRepository,
            NrequestsRepository nrequestsRepository,
            NotationGetterClient notationGetterClient,
            BlacklistClient blacklistClient
    ) {
        this.alertcontrolRepository = alertcontrolRepository;
        this.nrequestsRepository = nrequestsRepository;
        this.notationGetterClient = notationGetterClient;
        this.blacklistClient = blacklistClient;
    }

    public List<Nrequests> forAlertcontrol(String identifier) throws NotFoundException {
        List<Nrequests> notBlacklistedNrequests = new ArrayList<>();
        Optional<Alertcontrol> alertcontrolOpt = alertcontrolRepository.findById(identifier);
        if (alertcontrolOpt.isEmpty())
            throw new NotFoundException("identifier not found: " + identifier);
        Alertcontrol alertcontrol = alertcontrolOpt.get();
        Notationgroup notationgroup = notationGetterClient.getNotationgroup(alertcontrol.getNotationgroup()).getContent();
        log.debug("retrieved notationgroup with start notation " + notationgroup.getNotationsStart() + " till " + notationgroup.getNotationsEnd());
        Timestamp timestamp = new Timestamp(System.currentTimeMillis() - alertcontrol.getTimeperiod() * 24 * 60 * 60 * 1000);
        List<Nrequests> nrequestss = this.nrequestsRepository.getNrequestsForAlertcontrolData(notationgroup.getNotationsStart(), notationgroup.getNotationsEnd(), timestamp, (long) alertcontrol.getThresholdDuration(), alertcontrol.getThresholdRequests(), alertcontrol.getThresholdRatio());
        for (Nrequests nrequests : nrequestss) {
            if (!this.blacklistClient.isBlocked(nrequests.getIdentifier(), "nrequests")) {
                notBlacklistedNrequests.add(nrequests);
            }
        }
        return notBlacklistedNrequests;
    }
}
