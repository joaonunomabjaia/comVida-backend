package mz.org.csaude.comvida.backend.jobs;

import io.micronaut.context.annotation.Context;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.inject.Singleton;
import jakarta.inject.Inject;
import mz.org.csaude.comvida.backend.service.PatientImportFileService;
import mz.org.csaude.comvida.backend.service.SettingService;
import mz.org.csaude.comvida.backend.entity.Setting;

import java.util.concurrent.*;

@Context
@Singleton
public class PatientImportFileJob {

    @Inject
    PatientImportFileService importFileService;

    @Inject
    SettingService settingService;

    private ScheduledExecutorService scheduler;

    @PostConstruct
    public void start() {
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
                importFileService.processPendingFiles();
            } catch (Exception e) {
                System.err.println("Erro ao processar ficheiros pendentes: " + e.getMessage());
            } finally {
                scheduleNextRun(); // Agendar novamente após execução
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
