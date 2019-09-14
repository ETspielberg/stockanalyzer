package org.unidue.ub.libintel.stockanalyzer.alertcontrol;

import org.springframework.batch.item.ItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.unidue.ub.libintel.stockanalyzer.model.settings.Alertcontrol;
import org.unidue.ub.libintel.stockanalyzer.settingsrepositories.AlertcontrolRepository;

import java.util.ArrayList;
import java.util.List;

public class AlertcontrolReader implements ItemReader<Alertcontrol> {

    private List<Alertcontrol> alertcontrols = new ArrayList<>();

    private boolean collected = false;

    @Autowired
    private AlertcontrolRepository alertcontrolRepository;

    @Override
    public Alertcontrol read() {
        if (!collected) {
            alertcontrolRepository.findAll().forEach(entry -> {
                if (entry.isForElisa())
                    alertcontrols.add(entry);
            });
            collected = true;
        }
        if (alertcontrols.size() > 0)
            return alertcontrols.remove(0);
        return null;
    }

}
