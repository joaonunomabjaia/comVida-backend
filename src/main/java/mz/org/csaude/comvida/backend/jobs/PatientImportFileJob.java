package mz.org.csaude.comvida.backend.jobs;

import io.micronaut.context.event.ApplicationEventListener;
import io.micronaut.runtime.event.ApplicationStartupEvent;
import jakarta.annotation.PreDestroy;
import jakarta.inject.Singleton;
import mz.org.csaude.comvida.backend.service.PatientImportFileService;
import mz.org.csaude.comvida.backend.service.SettingService;

import java.util.concurrent.*;

@Singleton
public class PatientImportFileJob implements ApplicationEventListener<ApplicationStartupEvent> {

    private final PatientImportFileService importFileService;
    private final SettingService settingService;

    private ScheduledExecutorService scheduler;

    public PatientImportFileJob(PatientImportFileService importFileService, SettingService settingService) {
        this.importFileService = importFileService;
        this.settingService = settingService;
    }

    @Override
    public void onApplicationEvent(ApplicationStartupEvent event) {
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduleNextRun();
    }

    private void scheduleNextRun() {
        int intervalMinutes = 5;
        try {
            intervalMinutes = Integer.parseInt(settingService.getSetting("IMPORT_JOB_INTERVAL_MIN").getValue());
        } catch (Exception e) {
            System.err.println("Erro ao obter setting IMPORT_JOB_INTERVAL_MIN. Usando 5 minutos como padrão. " + e.getMessage());
        }

        scheduler.schedule(() -> {
            try {
                importFileService.filesToBeProcessed();
            } catch (Exception e) {
                System.err.println("Erro ao processar ficheiros pendentes: " + e.getMessage());
            } finally {
                scheduleNextRun();
            }
        }, intervalMinutes, TimeUnit.MINUTES);
    }

    @PreDestroy
    public void shutdown() {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
        }
    }
}
