package unidue.ub.stockanalyzer.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import unidue.ub.media.monographs.Manifestation;

import java.util.List;

@FeignClient("manifest-getter")
@Component
public interface ManifestationGetterClient {
    @RequestMapping(method= RequestMethod.GET, value="manifestations/manifestations")
    List<Manifestation> getManifestations(@RequestParam("identifier") String identifier, @RequestParam("exact") String exact,
                                          @RequestParam("mode") String mode);

    @RequestMapping(method= RequestMethod.GET, value="manifestations/fullManifestation")
    Manifestation getFullManifestation(@RequestParam("identifier") String identifier,
                                       @RequestParam("exact") String exact);

    @RequestMapping(method= RequestMethod.GET, value="manifestations/buildFullManifestation")
    Manifestation buildFullManifestation(@RequestParam("identifier") String identifier);

    @RequestMapping(method= RequestMethod.GET, value="manifestations/buildActiveManifestation")
    Manifestation buildActiveManifestation(@RequestParam("identifier") String identifier);
}
