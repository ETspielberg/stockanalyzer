package unidue.ub.stockanalyzer.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;


@FeignClient(name="blacklist-backend", configuration = FeignConfiguration.class)
@Component
public interface BlacklistClient {

    @RequestMapping(method= RequestMethod.GET, value="/isBlocked/{identifier}")
    Boolean isBlocked(@PathVariable String identifier, @RequestParam String type);

}
