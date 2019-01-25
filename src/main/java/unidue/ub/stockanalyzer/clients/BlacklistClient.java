package unidue.ub.stockanalyzer.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.hateoas.Resources;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import unidue.ub.media.blacklist.Ignored;


@FeignClient("blacklist-backend")
@Component
public interface BlacklistClient {

    @RequestMapping(method= RequestMethod.GET, value="/getIgnoredFor/{identifier}")
    Resources<Ignored> getIgnoredForTittleId(@PathVariable String identifier);

    @RequestMapping(method= RequestMethod.GET, value="/isBlocked/{identifier}")
    Boolean isBlocked(@PathVariable String identifier, String type);

}
