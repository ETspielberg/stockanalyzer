package org.unidue.ub.libintel.stockanalyzer.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.unidue.ub.libintel.stockanalyzer.model.elisa.ElisaRequest;

@FeignClient(name="elisa", configuration = FeignConfiguration.class)
@Component
public interface ElisaClient {

    @PostMapping("/sendToElisa")
    ResponseEntity<?> sendToElisa(@RequestBody ElisaRequest protokollToElisaRequest);


}
