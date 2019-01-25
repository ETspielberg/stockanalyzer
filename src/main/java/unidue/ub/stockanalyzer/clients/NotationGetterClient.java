package unidue.ub.stockanalyzer.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.hateoas.Resources;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import unidue.ub.stockanalyzer.model.settings.Notation;

@FeignClient("settings-backend")
@Component
public interface NotationGetterClient {

    @RequestMapping(method= RequestMethod.GET, value="/notation/ForGroup/{identifier")
    Resources<Notation> getNotationListForNotationgroup(@PathVariable String identifier);

    @RequestMapping(method= RequestMethod.GET, value="/notation/search/getNotationList")
    Resources<Notation> getNotationList(@RequestParam("startNotation") String startNotation, @RequestParam("endNotation") String endNotation);
}
