package org.unidue.ub.libintel.stockanalyzer.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.unidue.ub.libintel.stockanalyzer.model.media.Manifestation;

import java.util.List;

@FeignClient(name="getter", configuration = FeignConfiguration.class)
@Component
public interface ManifestationGetterClient {
    @RequestMapping(method= RequestMethod.GET, value="/manifestations")
    List<Manifestation> getManifestations(@RequestParam("identifier") String identifier, @RequestParam("exact") String exact,
                                          @RequestParam("mode") String mode);

    @RequestMapping(method= RequestMethod.GET, value="/fullManifestation")
    Manifestation getFullManifestation(@RequestParam("identifier") String identifier,
                                       @RequestParam("exact") String exact);

    @RequestMapping(method= RequestMethod.GET, value="/buildFullManifestation")
    Manifestation buildFullManifestation(@RequestParam("identifier") String identifier);

    @RequestMapping(method= RequestMethod.GET, value="/buildActiveManifestation")
    Manifestation buildActiveManifestation(@RequestParam("identifier") String identifier);
}
