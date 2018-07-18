package unidue.ub.stockanalyzer.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import unidue.ub.settings.fachref.Notation;

import java.util.List;

@FeignClient("settings-backend")
@Component
public interface NotationGetterClient {

    @RequestMapping(method= RequestMethod.GET, value="search/getNotationListForNotationgroup")
    List<Notation> getNotationListForNotationgroup(@RequestParam("identifier") String identifier);

    @RequestMapping(method= RequestMethod.GET, value="search/getNotationList")
    List<Notation> getNotationList(@RequestParam("startNotation") String startNotation, @RequestParam("endNotation") String endNotation);
}
