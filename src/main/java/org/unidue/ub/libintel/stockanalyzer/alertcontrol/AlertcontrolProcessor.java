package org.unidue.ub.libintel.stockanalyzer.alertcontrol;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.unidue.ub.libintel.stockanalyzer.model.data.Nrequests;
import org.unidue.ub.libintel.stockanalyzer.model.elisa.ElisaRequest;
import org.unidue.ub.libintel.stockanalyzer.model.elisa.Title;
import org.unidue.ub.libintel.stockanalyzer.model.elisa.TitleData;
import org.unidue.ub.libintel.stockanalyzer.model.settings.Alertcontrol;
import org.unidue.ub.libintel.stockanalyzer.service.NotFoundException;
import org.unidue.ub.libintel.stockanalyzer.service.NrequestsService;

import java.util.ArrayList;
import java.util.List;

public class AlertcontrolProcessor implements ItemProcessor<Alertcontrol, ElisaRequest> {

    private final NrequestsService nrequestsService;

    private final Logger log = LoggerFactory.getLogger(AlertcontrolProcessor.class);

    public AlertcontrolProcessor(NrequestsService nrequestsService) {
        this.nrequestsService = nrequestsService;
    }

    @Override
    public ElisaRequest process(Alertcontrol alertcontrol) {
        try {
            ElisaRequest elisaRequest = new ElisaRequest();
            elisaRequest.setNotepadName("Vormerkungen-Alert " + alertcontrol.getDescription());
            elisaRequest.setUserID(alertcontrol.getElisaAccount());
            List<Nrequests> nrequestss = nrequestsService.forAlertcontrol(alertcontrol.getIdentifier());
            List<Title> titles = new ArrayList<>();
            for (Nrequests nrequests : nrequestss) {
                if (nrequests.getIsbn() != null) {
                    TitleData titleData = new TitleData(nrequests.getIsbn());
                    titleData.setNotiz("D/E??:" + nrequests.getNRequests() + ", NB: " + nrequests.getShelfmark());
                    titleData.setNotizIntern("ausleihbare Exemplare: " + nrequests.NLendable + ", Vormerkungen: " + nrequests.getNRequests());
                    titles.add(new Title(titleData));
                }
            }
            elisaRequest.setTitles(titles.toArray(new Title[0]));
            return elisaRequest;
        } catch (NotFoundException nfe) {
            log.warn("no nrequests found for alertcontrol: " + alertcontrol.getIdentifier());
            return null;
        }

    }
}
