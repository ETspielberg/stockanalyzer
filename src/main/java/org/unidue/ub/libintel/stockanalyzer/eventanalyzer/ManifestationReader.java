package org.unidue.ub.libintel.stockanalyzer.eventanalyzer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.unidue.ub.libintel.stockanalyzer.clients.ManifestationGetterClient;
import org.unidue.ub.libintel.stockanalyzer.clients.NotationGetterClient;
import org.unidue.ub.libintel.stockanalyzer.model.settings.Stockcontrol;
import unidue.ub.media.monographs.Manifestation;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Component
public class ManifestationReader implements ItemReader<Manifestation> {

    private int nextManifestationIndex;

    @Autowired
    private NotationGetterClient notationGetterClient;

    @Autowired
    private ManifestationGetterClient manifestationGetterClient;

    private List<Manifestation> manifestationData;

    private Stockcontrol stockcontrol;

    public ManifestationReader() {
        nextManifestationIndex = 0;
    }

    private Logger log = LoggerFactory.getLogger(ManifestationReader.class);

    @Override
    public Manifestation read() {
        if (noManifestationsFound()) {
            collectManifestation();
        }
        Manifestation nextManifestation = null;
        if (nextManifestationIndex < manifestationData.size()) {
            nextManifestation = manifestationData.get(nextManifestationIndex);
            nextManifestationIndex++;
        }
        return nextManifestation;
    }


    public void collectManifestation() {
        List<String> notations = new ArrayList<>();
        String[] notationGroupStrings;
        if (stockcontrol.getSystemCode().isEmpty()) {
            notationGetterClient.getNotationListForGroup(stockcontrol.getSubjectID()).forEach(entry -> notations.add(entry.getNotation()));
            for (String notation : notations) {
                log.debug("found notation " + notation);
            }
        } else {
            if (stockcontrol.getSystemCode().contains(",")) {
                notationGroupStrings = stockcontrol.getSystemCode().split(",");
            } else {
                notationGroupStrings = new String[]{stockcontrol.getSystemCode().trim()};
            }
            for (String notationGroupString : notationGroupStrings) {
                if (notationGroupString.contains("-")) {
                    String startNotation = notationGroupString.substring(0, notationGroupString.indexOf("-")).trim();
                    String endNotation = notationGroupString.substring(notationGroupString.indexOf("-") + 1, notationGroupString.length()).trim();
                    notationGetterClient.getNotationList(startNotation,endNotation).forEach(entry -> notations.add(entry.getNotation()));
                } else {
                    notations.add(notationGroupString.trim());
                }
            }
        }
        for (String notation: notations)
            log.debug("found notation " + notation);
        manifestationData = new ArrayList<>();
        for (String notation : notations) {
            Consumer<Manifestation> buildManifestations = entry -> {
                String titleID = entry.getTitleID();
                if (titleID != null) {
                    manifestationData.add(manifestationGetterClient.buildFullManifestation(titleID));
                }
            };
            manifestationGetterClient.getManifestations(notation,"", "notation").forEach(buildManifestations);
        }
    }


    public void setStockcontrol(Stockcontrol stockcontrol) {
        this.stockcontrol = stockcontrol;
    }

    private boolean noManifestationsFound() {
        return (this.manifestationData == null);
    }

    List<Manifestation> getManifestations() {
        return manifestationData;
    }

    @BeforeStep
    public void retrieveStockcontrol(StepExecution stepExecution) {
        JobExecution jobExecution = stepExecution.getJobExecution();
        ExecutionContext jobContext = jobExecution.getExecutionContext();
        this.stockcontrol = (Stockcontrol) jobContext.get("stockcontrol");
    }
}
