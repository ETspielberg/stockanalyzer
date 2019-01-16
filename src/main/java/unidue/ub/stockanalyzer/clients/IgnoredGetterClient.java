package unidue.ub.stockanalyzer.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.hateoas.Resources;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import unidue.ub.media.blacklist.Ignored;
import unidue.ub.settings.fachref.Notation;

import java.util.List;

@FeignClient("blacklist-backend")
@Component
public interface IgnoredGetterClient {

    @RequestMapping(method= RequestMethod.GET, value="search/findAllByTitleId")
    Resources<Ignored> getIgnoredForTittleId(String titleId);

}
