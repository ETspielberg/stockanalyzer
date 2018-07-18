package unidue.ub.stockanalyzer.eventanalyzer;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import unidue.ub.media.monographs.Manifestation;
import unidue.ub.stockanalyzer.clients.ManifestationGetterClient;
import unidue.ub.stockanalyzer.clients.NotationGetterClient;
import unidue.ub.stockanalyzer.model.settings.Stockcontrol;

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
            notationGetterClient.getNotationListForNotationgroup(stockcontrol.getIdentifier()).forEach(entry -> notations.add(entry.getNotation()));
        } else {
            if (stockcontrol.getSystemCode().contains(",")) {
                notationGroupStrings = stockcontrol.getSystemCode().split(",");
            } else {
                notationGroupStrings = new String[]{stockcontrol.getSystemCode()};
            }
            for (String notationGroupString : notationGroupStrings) {
                if (notationGroupString.contains("-")) {
                    String startNotation = notationGroupString.substring(0, notationGroupString.indexOf("-"));
                    String endNotation = notationGroupString.substring(notationGroupString.indexOf("-") + 1, notationGroupString.length());
                    notationGetterClient.getNotationList(startNotation,endNotation).forEach(entry -> notations.add(entry.getNotation()));
                } else {
                    notations.add(notationGroupString);
                }
            }
        }
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
