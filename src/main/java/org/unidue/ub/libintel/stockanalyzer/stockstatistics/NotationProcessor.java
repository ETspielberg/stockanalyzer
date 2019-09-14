package org.unidue.ub.libintel.stockanalyzer.stockstatistics;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import unidue.ub.media.monographs.Event;
import unidue.ub.media.monographs.Manifestation;
import org.unidue.ub.libintel.stockanalyzer.clients.ManifestationGetterClient;
import org.unidue.ub.libintel.stockanalyzer.model.settings.Notation;
import org.unidue.ub.libintel.stockanalyzer.model.usage.CollectionUsage;
import org.unidue.ub.libintel.stockanalyzer.model.usage.ManifestationUsage;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Consumer;

@Component
public class NotationProcessor implements ItemProcessor<Notation, List<ManifestationUsage>> {

    private ManifestationGetterClient manifestationGetterClient;

    private static Logger log = LoggerFactory.getLogger(NotationProcessor.class);

    @Autowired
    public NotationProcessor(ManifestationGetterClient manifestationGetterClient) {
        this.manifestationGetterClient = manifestationGetterClient;
    }

    @Override
    public List<ManifestationUsage> process(final Notation notation) {
        log.info("analyzing notation " + notation.getNotation() + " - " + notation.getDescription());
        List<Manifestation> manifestations = new ArrayList<>();
        Consumer<Manifestation> buildManifestations = entry -> {
            String titleID = entry.getTitleID();
            if (titleID != null) {
                manifestations.add(manifestationGetterClient.buildFullManifestation(titleID));
            }
        };
        manifestationGetterClient.getManifestations(notation.getNotation(),"", "notation").forEach(buildManifestations);
        List<ManifestationUsage> manifestationUsages = new ArrayList<>();
        for (Manifestation manifestation: manifestations) {
            log.debug("analyzing manifestation " + manifestation.getTitleID());
            List<Event> events = manifestation.getEvents();
            Collections.sort(events);
            log.debug("manifestation contains " + events.size() + " events.");
            ManifestationUsage manifestationUsage = new ManifestationUsage();

            manifestationUsage.setTitleId(manifestation.getTitleID());

            log.debug("adding shelfmarks and barcodes to manifestation usage");
            manifestationUsage.setShelfmarks(Arrays.asList(manifestation.getShelfmarks()));
            manifestationUsage.setBarcodes(manifestation.getBarcodes());

            if (!events.isEmpty()) {
                for (Event event : events) {
                    Date eventDate;
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                    try {
                        eventDate = formatter.parse(event.getDate().substring(0, 10));
                    } catch (Exception e) {
                        log.warn("could not read time stamp. Skipping event", e);
                        continue;
                    }

                    String collection = event.getItem().getCollection();
                    log.debug("building collection usage for collection " + collection);

                    CollectionUsage collectionUsage = manifestationUsage.getUsageForCollection(collection);

                    if (event.getType() != null) {
                        if ("loan".equals(event.getType()) || "return".equals(event.getType())) {
                            log.debug("adding loan event for group " + event.getBorrowerStatus());
                            collectionUsage.addDeltaForGroup(eventDate, event.getDelta(), event.getBorrowerStatus());
                        }
                        else if ("request".equals(event.getType()) || "hold".equals(event.getType())) {
                            log.debug("adding request event");
                            collectionUsage.addRequestsDelta(eventDate, event.getDelta());
                        }
                        else if ("inventory".equals(event.getType()) || "deletion".equals(event.getType())) {
                            log.debug("adding stock event");
                            collectionUsage.addStockDelta(eventDate, event.getDelta());
                        }
                        else if ("cald".equals(event.getType()) || "caldDelivery".equals(event.getType())) {
                            log.debug("adding cald event");
                            collectionUsage.addCaldDelta(eventDate, event.getDelta());
                        }
                    }
                }
            }
            manifestationUsages.add(manifestationUsage);
        }
        return manifestationUsages;

    }
}
