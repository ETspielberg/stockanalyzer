package unidue.ub.stockanalyzer;

import unidue.ub.media.monographs.BibliographicInformation;
import unidue.ub.media.monographs.Event;
import unidue.ub.media.monographs.Item;
import unidue.ub.media.monographs.Manifestation;
import unidue.ub.stockanalyzer.eventanalyzer.ItemFilter;
import unidue.ub.stockanalyzer.model.data.CaldRequest;
import unidue.ub.stockanalyzer.model.data.Nrequests;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MonographTools {

    private final static long daysInMillis = 1000L * 60L * 60L * 24L;

    public static Nrequests getNrequestsFor(Manifestation manifestation, String statusLendable) {
        Nrequests nrequests = new Nrequests();
        for (Item item : manifestation.getItems()) {
            if (item.getDeletionDate().equals("")) {
                nrequests.NItems++;
                if (statusLendable.contains(item.getItemStatus())) {
                    nrequests.NLendable++;
                }
            }
        }
        for (Event event : manifestation.getEvents()) {
            if (event.getEndEvent() != null)
                continue;
            if (event.getType().equals("loan"))
                nrequests.NLoans++;
            if (event.getType().equals("request")) {
                nrequests.NRequests++;
                nrequests.totalDuration += (long) (((double) (System.currentTimeMillis() - event.getTime())) / (double) daysInMillis);
            }
        }
        if (nrequests.NRequests > 0) {
            nrequests.setDate(new Date());
            nrequests.setTitleId(manifestation.getTitleID());
            nrequests.setShelfmark(manifestation.getShelfmark());
            nrequests.setMab(manifestation.getBibliographicInformation().toString());
            nrequests.updateRatio();
            BibliographicInformation bibliographicInformation = manifestation.getBibliographicInformation();
            if (bibliographicInformation.getIsbn() != null)
                nrequests.setIsbn(bibliographicInformation.getIsbn());
            nrequests.setMab(bibliographicInformation.getFullDescription());
            return nrequests;
        } else
            return null;
    }

    public static CaldRequest getCaldRequestsFor(Manifestation manifestation) {
        CaldRequest caldRequest = new CaldRequest();
        for (Event event : manifestation.getEvents()) {
            if (event.getEndEvent() != null)
                continue;
            if (event.getType().equals("Cald")) {
                caldRequest.numberOfRequests++;
                caldRequest.setTarget(event.getItem().getSubLibrary());
                caldRequest.setSource(event.getItem().getCollection());
                caldRequest.setTitleId(manifestation.getTitleID());
                BibliographicInformation bibliographicInformation = manifestation.getBibliographicInformation();
                if (bibliographicInformation.getIsbn() != null)
                    caldRequest.setIsbn(bibliographicInformation.getIsbn());
                caldRequest.setMab(manifestation.getBibliographicInformation().getFullDescription());
                caldRequest.setShelfmark(manifestation.getShelfmark());
            }
        }
        return caldRequest;
    }

    public static List<Event> getFilteredEvents(List<Item> items, ItemFilter itemFilter) {
        List<Event> events = new ArrayList<>();
        for (Item item : items) {
            if (itemFilter.matches(item)) {
                List<Event> itemEvents = item.getEvents();
                for (Event event : itemEvents) {
                    events.add(event);
                    if (event.getEndEvent() != null)
                        events.add(event.getEndEvent());
                }
            }
        }
        return events;
    }
}
