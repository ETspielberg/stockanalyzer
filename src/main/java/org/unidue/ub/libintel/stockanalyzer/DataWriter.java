package org.unidue.ub.libintel.stockanalyzer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.unidue.ub.libintel.stockanalyzer.datarepositories.CaldRequestRepository;
import org.unidue.ub.libintel.stockanalyzer.datarepositories.EventanalysisRepository;
import org.unidue.ub.libintel.stockanalyzer.datarepositories.NrequestsRepository;
import org.unidue.ub.libintel.stockanalyzer.model.data.CaldRequest;
import org.unidue.ub.libintel.stockanalyzer.model.data.Eventanalysis;
import org.unidue.ub.libintel.stockanalyzer.model.data.Nrequests;

import java.util.List;

@Component
@StepScope
public class DataWriter implements ItemWriter {

    private static final Logger log = LoggerFactory.getLogger(DataWriter.class);

    @Autowired
    private NrequestsRepository nrequestsRepository;

    @Autowired
    private CaldRequestRepository caldRequestRepository;

    @Autowired
    private EventanalysisRepository eventanalysisRepository;

    @Override
    public void write(List list) {
        long successful = 0;
        String type = "";
        for (Object object : list) {
            type = object.getClass().getSimpleName();
                switch (type) {
                    case "Eventanalysis":
                        try {
                            eventanalysisRepository.save((Eventanalysis) object);
                            successful++;
                        } catch (Exception e) {
                            log.warn("could not save eventanalysis " + ((Eventanalysis) object).getIdentifier() + " - " + ((Eventanalysis) object).getShelfmark());
                        }
                        break;
                    case "Nrequests":

                        nrequestsRepository.save((Nrequests) object);
                        successful++;
                        break;
                    case "CaldRequest":
                        caldRequestRepository.save((CaldRequest) object);
                        successful++;
                        break;
                }
            /*  } catch (Exception e) {
                log.warn("could not save object");
            }*/
        }
        log.info("successfully saved " + successful + " of " + list.size() + " " + type);
    }

}
