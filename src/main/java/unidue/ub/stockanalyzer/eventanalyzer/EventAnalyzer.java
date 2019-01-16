package unidue.ub.stockanalyzer.eventanalyzer;

import org.apache.commons.math3.stat.regression.SimpleRegression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import unidue.ub.media.monographs.Event;
import unidue.ub.stockanalyzer.model.data.Eventanalysis;
import unidue.ub.stockanalyzer.model.settings.ItemGroup;
import unidue.ub.stockanalyzer.model.settings.Stockcontrol;
import unidue.ub.stockanalyzer.model.settings.UserGroup;
import unidue.ub.stockanalyzer.settingsrepositories.ItemGroupRepository;
import unidue.ub.stockanalyzer.settingsrepositories.UserGroupRepository;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Consumer;


/**
 * Calculates <code>Eventanalysis</code> from list of <code>Event</code>-objects
 * or a document docNumber.
 *
 * @author Eike Spielberg
 * @version 1
 */
@Component
public class EventAnalyzer {

    @Autowired
    private UserGroupRepository userGroupRepository;

    @Autowired
    private ItemGroupRepository itemGroupRepository;


    private static final Logger log = LoggerFactory.getLogger(EventAnalyzer.class);
    private String irrelevantUserCategories;
    private String relevantItemCategories;
    private UsageCounters usagecounter = new UsageCounters();
    private Map<String, String> userGroups;
    private Map<String, String> itemGroups;

    EventAnalyzer() {
    }

    /**
     * Calculates the loan and request parameters for a given List of Events
     * with the parameters in the Stockcontrol. The results are stored
     * in an DocumentAnalysis object.
     *
     * @param events a list of Event-objects.
     */

    Eventanalysis analyze(List<Event> events, Stockcontrol stockcontrol) {
        Collections.sort(events);

        //build new analysis and set some fields
        Eventanalysis analysis = new Eventanalysis();
        analysis.setDate(new Date());
        analysis.setCollection(stockcontrol.getCollections());
        analysis.setMaterials(stockcontrol.getMaterials());
        if (stockcontrol.getIdentifier() != null)
            analysis.setStockcontrolId(stockcontrol.getIdentifier());

        usagecounter.reset();
        //start analysis
        if (!events.isEmpty()) {
            prepareUserCategories();
            prepareItemCategories();

            LocalDate TODAY = LocalDate.now();

            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            Hashtable<Integer, Long> allMaxLoansAbs = new Hashtable<>();
            LocalDate startDate = TODAY.minus(stockcontrol.getYearsToAverage(), ChronoUnit.YEARS);
            LocalDate startDateRequests = TODAY.minus(stockcontrol.getYearsOfRequests(), ChronoUnit.YEARS);
            LocalDate miniumumDate = TODAY.minus(stockcontrol.getMinimumYears(), ChronoUnit.YEARS);
            Collections.sort(events);

            Integer yearsBefore = TODAY.getYear() - Integer.parseInt(events.get(0).getDate().substring(0, 4));


            for (int year = 0; year <= yearsBefore; year++) {
                allMaxLoansAbs.put(year, 0L);
            }
            UsageCounters oldUsagecounter = usagecounter.clone();

            for (Event event : events) {
                LocalDate eventDate;
                try {
                    eventDate = LocalDate.parse(event.getDate().substring(0, 10), dtf);
                } catch (Exception e) {
                    continue;
                }
                if (eventDate.isAfter(TODAY))
                    continue;

                updateItemCounter(event);
                int timeIntervall = TODAY.getYear() - eventDate.getYear();
                long loans = usagecounter.getCorrectedLoans();
                for (int i = timeIntervall; i <= yearsBefore; i++) {
                    long maxLoans = Math.max(allMaxLoansAbs.get(i), loans);
                    allMaxLoansAbs.replace(i, maxLoans);
                }
                if (eventDate.isAfter(startDate)) {
                    loans = Math.max(loans, oldUsagecounter.getCorrectedLoans());
                    analysis.setMaxLoansAbs(Math.max(loans, analysis.getMaxLoansAbs()));
                    double relativeLoan = Math.max(usagecounter.getCorrectedRelativLoan(), oldUsagecounter.getCorrectedRelativLoan());
                    analysis.setMaxRelativeLoan(Math.max(relativeLoan, analysis.getMaxRelativeLoan()));
                } else
                    oldUsagecounter = usagecounter.clone();
                long maxItemsNeeded = usagecounter.getAllLoans() + usagecounter.requests;
                if (eventDate.isAfter(startDateRequests)) {
                    analysis.setMaxNumberRequest(Math.max(usagecounter.requests, analysis.getNumberRequests()));
                    analysis.setMaxItemsNeeded(Math.max(maxItemsNeeded, analysis.getMaxItemsNeeded()));
                }


                // try to get the end date. If no end date is given in the event,
                // set the end date to the actual date.
                Event endEvent = event.getEndEvent();
                LocalDate endDate;
                if (endEvent != null)
                    try {
                        endDate = LocalDate.parse(endEvent.getDate().substring(0, 10), dtf);
                    } catch (Exception e) {
                        endDate = eventDate;
                    }
                else
                    endDate = TODAY;
                if (endDate.isBefore(startDate))
                    continue;
                else if (eventDate.isBefore(startDate))
                    eventDate = startDate;
                int days = (int) ChronoUnit.DAYS.between(eventDate, endDate) + 1;

                switch (event.getType()) {
                    // analyze loan events
                    case "loan": {
                        if (event.getBorrowerStatus() == null) {
                            log.info("no Borrower given");
                            usagecounter.daysLoaned += days;
                        } else {
                            if (irrelevantUserCategories.contains(event.getBorrowerStatus()))
                                usagecounter.daysStockLendable -= days; //shouldn't it be daysStock reduced???
                            else
                                usagecounter.daysLoaned += days;
                        }
                        break;
                    }
                    case "inventory": {
                        // analyze stock events
                        if (event.getItem() != null) {
                            if (event.getItem().getItemStatus() != null) {
                                if (relevantItemCategories.contains(event.getItem().getItemStatus()))
                                    usagecounter.daysStockLendable += days;
                            } else
                                usagecounter.daysStockLendable += days;
                        } else
                            usagecounter.daysStockLendable += days;
                        break;
                    }
                    case "request": {
                        // analyze request events
                        if (eventDate.isAfter(startDateRequests)) {
                            analysis.increaseNumberRequests();
                            usagecounter.daysRequested += days;
                        }
                    }
                }
            }
            double slope = calculateSlope(allMaxLoansAbs, yearsBefore);
            analysis.setSlope(slope);
            analysis.setDaysRequested(usagecounter.daysRequested);
            analysis.setMeanRelativeLoan(usagecounter.getMeanRelativeLoan());
            analysis.setLastStock(usagecounter.stock);
            analysis.setLastStockLendable(usagecounter.getStockLendable());


            double staticBuffer = stockcontrol.getStaticBuffer();
            double variableBuffer = stockcontrol.getVariableBuffer();

            int proposedDeletion = 0;
            double ratio = 1;
            if (analysis.getMaxRelativeLoan() != 0)
                ratio = analysis.getMeanRelativeLoan() / analysis.getMaxRelativeLoan();

            if (staticBuffer < 1 && variableBuffer < 1)
                proposedDeletion = ((int) ((usagecounter.stock - analysis.getMaxLoansAbs()) * (1
                        - staticBuffer
                        - variableBuffer * ratio)));
            else if (staticBuffer >= 1 && variableBuffer < 1)
                proposedDeletion = (
                        (int) ((usagecounter.stock - analysis.getMaxLoansAbs() - staticBuffer)
                                * (1 - variableBuffer * ratio)));
            else if (staticBuffer >= 1 && variableBuffer >= 1)
                proposedDeletion = (
                        (int) ((usagecounter.stock - analysis.getMaxLoansAbs() - staticBuffer)
                                - variableBuffer * ratio));
            else if (staticBuffer < 1 && variableBuffer < 1)
                proposedDeletion = (
                        (int) ((usagecounter.stock - analysis.getMaxLoansAbs()) * (1 - staticBuffer)
                                - variableBuffer * ratio));

            if (proposedDeletion < 0)
                analysis.setProposedDeletion(0);
            else
                analysis.setProposedDeletion(proposedDeletion);
            if (analysis.getProposedDeletion() == 0 && ratio > 0.5)
                analysis.setProposedPurchase((int) (-1 * analysis.getLastStock() * 0.001 * ratio));
            if (events.size() > 0) {
                if (LocalDate.parse(events.get(0).getDate().substring(0, 10), dtf).isAfter(miniumumDate))
                    analysis.setProposedDeletion(0);
            }
            if ((double) analysis.getDaysRequested() / (double) analysis.getNumberRequests() >= stockcontrol.getMinimumDaysOfRequest()) {
                analysis.setProposedPurchase(analysis.getMaxItemsNeeded() - analysis.getLastStock());
            }
            analysis.setStatus("proposed");
        } else {
            analysis.setStatus("noEvents");
        }
        return analysis;
    }

    private double calculateSlope(Hashtable<Integer, Long> allMaxLoansAbs, int yearsBefore) {
        SimpleRegression trend = new SimpleRegression();
        for (int year = 1; year <= yearsBefore; year++) {
            trend.addData(year, (double) allMaxLoansAbs.get(year));
        }
        double slope = 0;
        if (trend.getN() > 1)
            slope = trend.getSlope();
        return slope;
    }

    private void updateItemCounter(Event event) {
        switch (event.getType()) {
            case "loan": {
                if (event.getBorrowerStatus() != null) {
                    if (userGroups.get("student").contains(event.getBorrowerStatus()))
                        usagecounter.studentLoans++;
                    else if (userGroups.get("extern").contains(event.getBorrowerStatus()))
                        usagecounter.externLoans++;
                    else if (userGroups.get("intern").contains(event.getBorrowerStatus()))
                        usagecounter.internLoans++;
                    else if (userGroups.get("happ").contains(event.getBorrowerStatus()))
                        usagecounter.happLoans++;
                    else
                        usagecounter.elseLoans++;
                } else
                    usagecounter.elseLoans++;
                break;
            }
            case "return": {
                if (event.getBorrowerStatus() != null) {
                    if (userGroups.get("student").contains(event.getBorrowerStatus()))
                        usagecounter.studentLoans--;
                    else if (userGroups.get("extern").contains(event.getBorrowerStatus()))
                        usagecounter.externLoans--;
                    else if (userGroups.get("intern").contains(event.getBorrowerStatus()))
                        usagecounter.internLoans--;
                    else if (userGroups.get("happ").contains(event.getBorrowerStatus()))
                        usagecounter.happLoans--;
                    else
                        usagecounter.elseLoans--;
                } else
                    usagecounter.elseLoans--;
                break;
            }
            case "inventory": {
                usagecounter.stock++;
                if (event.getItem() != null)
                    if (event.getItem().getItemStatus() != null)
                        if (itemGroups.get("lendable").contains(event.getItem().getItemStatus()))
                            usagecounter.stockLendable++;
                break;
            }
            case "deletion": {
                usagecounter.stock--;
                usagecounter.stockDeleted++;
                if (event.getItem() != null)
                    if (event.getItem().getItemStatus() != null)
                        if (itemGroups.get("lendable").contains(event.getItem().getItemStatus()))
                            usagecounter.stockLendable--;
                break;
            }
            case "request": {
                usagecounter.requests++;
                break;
            }
            case "hold": {
                usagecounter.requests--;
                break;
            }
            case "cald": {
                usagecounter.calds++;
            }
        }
    }

    private void prepareUserCategories() {
        irrelevantUserCategories = "";
        userGroups = new HashMap<>();
        Consumer<UserGroup> sortUserGroup = entry -> {
            userGroups.put(entry.getName(), entry.getUserCategoriesAsString());
            if (!entry.isRelevantForAnalysis())
                irrelevantUserCategories += entry.getUserCategoriesAsString() + " ";
        };
        userGroupRepository.findAll().forEach(sortUserGroup);
    }

    private void prepareItemCategories() {
        relevantItemCategories = "";
        itemGroups = new HashMap<>();
        Consumer<ItemGroup> sortUserGroup = entry -> {
            itemGroups.put(entry.getName(), entry.getItemCategoriesAsString());
            if (!entry.isRelevantForAnalysis())
                relevantItemCategories += entry.getItemCategoriesAsString() + " ";
        };
        itemGroupRepository.findAll().forEach(sortUserGroup);
    }
}
