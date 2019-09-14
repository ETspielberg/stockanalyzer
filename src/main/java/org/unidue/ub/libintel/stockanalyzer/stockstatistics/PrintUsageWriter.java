package org.unidue.ub.libintel.stockanalyzer.stockstatistics;

/*
@Component
@StepScope
public class PrintUsageWriter implements ItemWriter<List<ManifestationUsage>> {

    private static final Logger log = LoggerFactory.getLogger(PrintUsageWriter.class);

    @Autowired
    private ManifestationUsageRepository manifestationUsageRepository;

    public PrintUsageWriter() { }

    @Override
    public void write(List list) {
        long successful = 0;
        for (Object object : list) {
            try {
                manifestationUsageRepository.save((ManifestationUsage) object);
                successful++;
            } catch (Exception e) {
                log.warn("could not save manifestation usage " + ((ManifestationUsage) object).getTitleId());
            }
        }
        log.info("successfully saved " + successful + " of " + list.size());
    }
}
*/
