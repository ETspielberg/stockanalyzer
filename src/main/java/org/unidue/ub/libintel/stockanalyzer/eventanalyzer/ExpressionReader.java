package org.unidue.ub.libintel.stockanalyzer.eventanalyzer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemReader;
import org.unidue.ub.libintel.stockanalyzer.model.media.Expression;
import org.unidue.ub.libintel.stockanalyzer.model.media.Manifestation;
import org.unidue.ub.libintel.stockanalyzer.model.settings.Stockcontrol;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

public class ExpressionReader implements ItemReader<Expression> {

    private boolean noExpressionsFound;

    private Enumeration<Expression> expressionEnumeration;

    private ManifestationReader manifestationReader;

    private Stockcontrol stockcontrol;

    private Logger log = LoggerFactory.getLogger(ExpressionReader.class);

    ExpressionReader() {
        noExpressionsFound = true;
    }

    public ExpressionReader(ManifestationReader manifestationReader) {
        noExpressionsFound = true;
        this.manifestationReader = manifestationReader;
    }

    @Override
    public Expression read() {
        if (noExpressionsFound) {
            collectManifestation();
        }
        try {
            if (expressionEnumeration.hasMoreElements())
                return expressionEnumeration.nextElement();
            else return null;
        } catch (Exception e) {
            return null;
        }
    }

    private void collectManifestation() {
        Hashtable<String, Expression> expressionData = new Hashtable<>();
        manifestationReader.setStockcontrol(stockcontrol);
        manifestationReader.collectManifestation();
        List<Manifestation> manifestations = manifestationReader.getManifestations();
        if (manifestations.size() != 0) {
            for (Manifestation manifestation : manifestations) {
                if (expressionData.containsKey(manifestation.getShelfmarkBase())) {
                    expressionData.get(manifestation.getShelfmarkBase()).addManifestation(manifestation);
                } else {
                    Expression expression = new Expression(manifestation.getShelfmarkBase());
                    expression.addManifestation(manifestation);
                    expressionData.put(expression.getShelfmarkBase(), expression);
                }
            }
            expressionEnumeration = expressionData.elements();
            noExpressionsFound = false;
        }
    }

    @BeforeStep
    public void retrieveStockcontrol(StepExecution stepExecution) {
        JobExecution jobExecution = stepExecution.getJobExecution();
        ExecutionContext jobContext = jobExecution.getExecutionContext();
        this.stockcontrol = (Stockcontrol) jobContext.get("stockcontrol");
    }
}
