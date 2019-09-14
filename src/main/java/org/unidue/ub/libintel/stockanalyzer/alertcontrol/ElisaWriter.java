package org.unidue.ub.libintel.stockanalyzer.alertcontrol;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemWriter;
import org.springframework.http.ResponseEntity;
import org.unidue.ub.libintel.stockanalyzer.clients.ElisaClient;
import org.unidue.ub.libintel.stockanalyzer.model.elisa.ElisaRequest;

import java.util.List;

@StepScope
public class ElisaWriter implements ItemWriter<ElisaRequest> {

    private final ElisaClient elisaClient;

    private final Logger log = LoggerFactory.getLogger(ElisaWriter.class);

    ElisaWriter(ElisaClient elisaClient) {
        this.elisaClient = elisaClient;
    }

    @Override
    public void write(List list) {
        long successful = 0;
        for (Object object : list) {
            ElisaRequest elisaRequest = (ElisaRequest) object;
            ResponseEntity<?> response = elisaClient.sendToElisa(elisaRequest);
            if (response.getStatusCode().is2xxSuccessful())
                successful++;
            else
                log.warn("could not send Data to Elisa");
        }
        log.info("to send: " +  list.size() + ", successful: " + successful);
    }
}
