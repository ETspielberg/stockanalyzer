package org.unidue.ub.libintel.stockanalyzer.nrequests;

import org.springframework.batch.item.ItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import unidue.ub.media.monographs.Manifestation;
import org.unidue.ub.libintel.stockanalyzer.clients.ManifestationGetterClient;

import java.util.ArrayList;
import java.util.List;

public class NrequestsReader implements ItemReader<Manifestation> {

    private List<Manifestation> manifestations = new ArrayList<>();

    private boolean collected = false;

    @Autowired
    private ManifestationGetterClient manifestationGetterClient;

    @Override
    public Manifestation read() {
        if (!collected)
            collectManifestationsByOpenRequests();
        if (manifestations.size() > 0)
            return manifestations.remove(0);
        return null;
    }

    private void collectManifestationsByOpenRequests() {
        List<Manifestation> manifestationsFound = new ArrayList<>(manifestationGetterClient.getManifestations("", "", "openRequests"));
        for (Manifestation manifestation : manifestationsFound)
            manifestations.add(manifestationGetterClient.buildActiveManifestation(manifestation.getTitleID()));
        collected = true;
    }
}
