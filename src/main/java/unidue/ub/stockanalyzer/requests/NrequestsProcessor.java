package unidue.ub.stockanalyzer.requests;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import unidue.ub.media.monographs.Manifestation;
import unidue.ub.stockanalyzer.model.data.Nrequests;
import unidue.ub.stockanalyzer.model.settings.ItemGroup;
import unidue.ub.stockanalyzer.settingsrepositories.ItemGroupRepository;

import static unidue.ub.stockanalyzer.MonographTools.getNrequestsFor;


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
