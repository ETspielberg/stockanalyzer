package org.unidue.ub.libintel.stockanalyzer.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.unidue.ub.libintel.stockanalyzer.model.settings.Notation;
import org.unidue.ub.libintel.stockanalyzer.model.settings.Notationgroup;

import java.util.List;

@FeignClient(name="settings-backend", configuration = FeignConfiguration.class)
@Component
public interface NotationGetterClient {

    @RequestMapping(method= RequestMethod.GET, value="/notation/forGroup/{identifier}")
    List<Notation> getNotationListForGroup(@PathVariable("identifier") String identifier);

    @RequestMapping(method= RequestMethod.GET, value="/notation/search/getNotationList")
    Resources<Notation> getNotationList(@RequestParam("startNotation") String startNotation, @RequestParam("endNotation") String endNotation);

    @RequestMapping(method= RequestMethod.GET, value="/notationgroup/{identifier}")
    Resource<Notationgroup> getNotationgroup(@PathVariable("identifier") String identifier);

    @RequestMapping(method= RequestMethod.GET, value="/notation/all")
    Resources<Notation> getAllNotations();
}
