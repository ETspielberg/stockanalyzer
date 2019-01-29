package unidue.ub.stockanalyzer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import unidue.ub.stockanalyzer.datarepositories.CaldRequestRepository;
import unidue.ub.stockanalyzer.datarepositories.EventanalysisRepository;
import unidue.ub.stockanalyzer.datarepositories.NrequestsRepository;
import unidue.ub.stockanalyzer.model.data.CaldRequest;
import unidue.ub.stockanalyzer.model.data.Eventanalysis;
import unidue.ub.stockanalyzer.model.data.Nrequests;

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
                        eventanalysisRepository.save((Eventanalysis) object);
                        successful++;
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
