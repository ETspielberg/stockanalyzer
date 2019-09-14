package org.unidue.ub.libintel.stockanalyzer.stockstatistics;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.unidue.ub.libintel.stockanalyzer.clients.NotationGetterClient;
import org.unidue.ub.libintel.stockanalyzer.model.settings.Notation;

import java.util.*;

@Component
public class NotationReader  implements ItemReader<Notation> {

    private NotationGetterClient notationGetterClient;

    private Collection<Notation> notations;

    private final Logger log = LoggerFactory.getLogger(NotationReader.class);

    @Autowired
    public NotationReader(NotationGetterClient notationGetterClient) {
        this.notationGetterClient = notationGetterClient;
    }

    @Override
    public Notation read() {
        log.debug("reading all notations from notation repository");
        if (notations == null) {
            notations = notationGetterClient.getAllNotations().getContent();
        }
        Iterator<Notation> notationIterator = notations.iterator();
        if (notationIterator.hasNext()) {
            log.debug("starting analysis of next notation");
            return notationIterator.next();
        }
        else {
            log.info("finished list of notations");
            return null;
        }
    }
}
