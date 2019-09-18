package org.unidue.ub.libintel.stockanalyzer.nrequests;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.unidue.ub.libintel.stockanalyzer.model.media.Manifestation;
import org.unidue.ub.libintel.stockanalyzer.model.data.Nrequests;
import org.unidue.ub.libintel.stockanalyzer.model.settings.ItemGroup;
import org.unidue.ub.libintel.stockanalyzer.settingsrepositories.ItemGroupRepository;

import static org.unidue.ub.libintel.stockanalyzer.MonographUtils.getNrequestsFor;


public class NrequestsProcessor implements ItemProcessor<Manifestation, Nrequests> {


    @Autowired
    private ItemGroupRepository itemGroupRepository;

    public NrequestsProcessor() {
    }

    @Override
    public Nrequests process(final Manifestation manifestation) {
        String lendable = itemGroupRepository.findById("lendable").orElseGet(ItemGroup::new).getItemCategoriesAsString();
        return getNrequestsFor(manifestation, lendable);
    }
}
