package unidue.ub.stockanalyzer.datarepositories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import unidue.ub.stockanalyzer.model.data.Nrequests;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Controller
public class NrequestsController {

    @Autowired
    private NrequestsRepository nrequestsRepository;

    @GetMapping("/nrequests/getForTimeperiod")
    public ResponseEntity<?> getForTimeperiod(@Param("startNotation") String startNotation, @Param("endNotation") String endNotation, @Param(value="timeperiod") Integer timeperiod) {

        LocalDateTime startDate = LocalDateTime.now().minusDays(timeperiod);
        ZonedDateTime zonedStartDate = startDate.atZone(ZoneId.systemDefault());
        Date date = Date.from(zonedStartDate.toInstant());
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.MILLISECOND, 0);

        List<Nrequests> nrequests = nrequestsRepository.getNrequestsForNotationgroupSinceDate(startNotation,endNotation, new Timestamp(cal.getTimeInMillis()));
        if (nrequests == null)
            nrequests = new ArrayList<>();
        return ResponseEntity.ok(nrequests);

    }

}
