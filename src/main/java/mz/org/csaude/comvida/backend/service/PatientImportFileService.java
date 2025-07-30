package mz.org.csaude.comvida.backend.service;

import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import io.micronaut.http.multipart.CompletedFileUpload;
import io.micronaut.scheduling.annotation.Scheduled;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import mz.org.csaude.comvida.backend.dto.PatientImportFileDTO;
import mz.org.csaude.comvida.backend.entity.PatientImportFile;
import mz.org.csaude.comvida.backend.entity.ProgramActivity;
import mz.org.csaude.comvida.backend.entity.SheetImportStatus;
import mz.org.csaude.comvida.backend.entity.SourceSystem;
import mz.org.csaude.comvida.backend.repository.PatientImportFileRepository;
import mz.org.csaude.comvida.backend.repository.ProgramActivityRepository;
import mz.org.csaude.comvida.backend.repository.SheetImportStatusRepository;
import mz.org.csaude.comvida.backend.repository.SourceSystemRepository;
import mz.org.csaude.comvida.backend.util.DateUtils;
import mz.org.fgh.mentoring.util.LifeCycleStatus;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@Singleton
public class PatientImportFileService {

    private final PatientImportFileRepository repository;

    @Inject
    private ProgramActivityRepository programActivityRepository;

    @Inject
    private CohortMemberService cohortMemberService;

    @Inject
    private SettingService settingService;

    @Inject
    private SheetImportStatusRepository sheetImportStatusRepository;

    @Inject
    private SourceSystemRepository sourceSystemRepository;


    public PatientImportFileService(PatientImportFileRepository repository) {
        this.repository = repository;
    }

    public PatientImportFile save(String fileName, byte[] content) {
        PatientImportFile file = new PatientImportFile();
        file.setMessage(fileName);
        file.setFile(content);
        file.setStatus(PatientImportFile.ImportStatus.PENDING);
        file.setProgress(0);
        file.setCreatedAt(new Date());
        return repository.save(file);
    }

    public Optional<PatientImportFile> findById(Long id) {
        return repository.findById(id);
    }

    public Iterable<PatientImportFile> findAll() {
        return repository.findAll();
    }

    public List<SheetImportStatus> findSheetStatusesByFileId(Long fileId) {
        PatientImportFile file = repository.findById(fileId)
                .orElseThrow(() -> new RuntimeException("Ficheiro não encontrado com ID: " + fileId));
        return sheetImportStatusRepository.findByFile(file);
    }

    public PatientImportFile updateStatusAndProgress(Long id, PatientImportFile.ImportStatus status, int progress) {
        return repository.findById(id)
                .map(file -> {
                    file.setStatus(status);
                    file.setProgress(progress);
                    return repository.update(file);
                })
                .orElseThrow(() -> new RuntimeException("Ficheiro não encontrado com ID: " + id));
    }

    public boolean processFileAsync(Long id) {
        return repository.findById(id).isPresent();
    }

    public void processExcelUpload(CompletedFileUpload fileUpload, PatientImportFileDTO patientImportFileDTO, String userUUID, Long sourceSystemId) {
        try {
            ProgramActivity programActivity = programActivityRepository.findById(patientImportFileDTO.getProgramActivity().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Serviço não encontrado com ID: " + patientImportFileDTO.getProgramActivity().getId()));

            SourceSystem sourceSystem = sourceSystemRepository.findById(sourceSystemId).orElseThrow(() -> new IllegalArgumentException("Fonte não encontrado com ID: " +  sourceSystemId)
            );

            PatientImportFile importFile = new PatientImportFile();
            importFile.setName(patientImportFileDTO.getName());
            importFile.setFile(fileUpload.getBytes());
            importFile.setStatus(PatientImportFile.ImportStatus.PENDING);
            importFile.setProgress(0);
            importFile.setProgramActivity(programActivity);
            importFile.setMessage("Upload recebido para processamento. Sistema: " + sourceSystem.getCode());
            importFile.setSourceSystem(sourceSystem);
            importFile.setUuid(UUID.randomUUID().toString());
            importFile.setLifeCycleStatus(LifeCycleStatus.ACTIVE);
            importFile.setCreatedAt(DateUtils.getCurrentDate());
            importFile.setCreatedBy(userUUID);

            repository.save(importFile);

            // Em seguida, pode agendar ou iniciar o processamento assíncrono do Excel...

        } catch (IOException e) {
            throw new RuntimeException("Erro ao ler conteúdo do ficheiro", e);
        }
    }

    public void initializeSheetStatuses(PatientImportFile file, Workbook workbook) {
        sheetImportStatusRepository.deleteByFile(file); // Remove registros antigos (se houver)

        for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
            String sheetName = workbook.getSheetName(i);

            SheetImportStatus sheetStatus = new SheetImportStatus();
            sheetStatus.setSheetName(sheetName);
            sheetStatus.setStatus(SheetImportStatus.SheetStatus.PENDING);
            sheetStatus.setProgress(0);
            sheetStatus.setMessage("Aguardando processamento.");
            sheetStatus.setFile(file);
            sheetStatus.setLifeCycleStatus(file.getLifeCycleStatus());
            sheetStatus.setCreatedAt(new Date());
            sheetStatus.setCreatedBy(file.getCreatedBy());

            sheetImportStatusRepository.save(sheetStatus);
        }
    }

private int calculateGlobalProgress(PatientImportFile file) {
    List<SheetImportStatus> statuses = sheetImportStatusRepository.findByFile(file);
    if (statuses.isEmpty()) return 0;

    int totalProgress = statuses.stream().mapToInt(SheetImportStatus::getProgress).sum();
    return totalProgress / statuses.size();
}


public void processFile(PatientImportFile file) {
    try (InputStream input = new ByteArrayInputStream(file.getFile());
         Workbook workbook = new XSSFWorkbook(input)) {

        initializeSheetStatuses(file, workbook);

        file.setStatus(PatientImportFile.ImportStatus.PROCESSING);
        file.setMessage("Processamento iniciado.");
        file.setProgress(0);
        repository.update(file);

        int totalSheets = workbook.getNumberOfSheets();
        int batchSize = Integer.parseInt(settingService.getSetting("BATCH_SIZE").getValue());

        List<Thread> threads = new ArrayList<>();
        Object lock = new Object();

        for (int i = 0; i < totalSheets; i++) {
            final int sheetIndex = i;

            Thread thread = new Thread(() -> {
                String sheetName = workbook.getSheetName(sheetIndex);
                SheetImportStatus sheetStatus = sheetImportStatusRepository.findByFile(file).stream()
                        .filter(s -> s.getSheetName().equals(sheetName))
                        .findFirst()
                        .orElse(null);

                boolean hasErrors = false; // <-- DECLARAR AQUI, no início da thread

                try {
                    Sheet sheet = workbook.getSheetAt(sheetIndex);
                    int totalRows = sheet.getLastRowNum();
                    int processed = 0;

                    if (sheetStatus != null) {
                        sheetStatus.setStatus(SheetImportStatus.SheetStatus.PROCESSING);
                        sheetStatus.setMessage("Iniciando processamento...");
                        sheetImportStatusRepository.update(sheetStatus);
                    }

                    int processedRows = 0;
                    int successfulRows = 0;
                    List<String> errorMessages = new ArrayList<>();
                    for (int rowIndex = 1; rowIndex <= totalRows; rowIndex += batchSize) {
                        int end = Math.min(rowIndex + batchSize - 1, totalRows);

                        for (int r = rowIndex; r <= end; r++) {
                            Row row = sheet.getRow(r);
                            if (row == null) continue;

                            try {
                                String patientUUID = row.getCell(5).getStringCellValue();
                                cohortMemberService.createFromExcel(patientUUID, sheetName, file.getSourceSystem().getCode());
                                successfulRows++;
                            } catch (Exception e) {
                                hasErrors = true;
                                String errorMessage = "Erro na linha " + r + " da lista " + sheetName + ": " + e.getMessage();
                                errorMessages.add(errorMessage);
                            }
                            processedRows++;
                        }

                        int sheetProgress = (processedRows * 100) / totalRows;

                        if (sheetStatus != null) {
                            sheetStatus.setProgress(sheetProgress);
                            sheetStatus.setMessage("Processando... (" + sheetProgress + "%)");
                            sheetStatus.setUpdatedAt(new Date());
                            sheetImportStatusRepository.update(sheetStatus);
                        }

                        synchronized (lock) {
                            int globalProgress = calculateGlobalProgress(file);
                            file.setProgress(globalProgress);
                            file.setMessage("Processando sheet: " + sheetName + " (" + globalProgress + "%)");
                            repository.update(file);
                        }
                    }

                    if (sheetStatus != null) {
                        sheetStatus.setUpdatedAt(new Date());

                        if (hasErrors) {
                            sheetStatus.setStatus(SheetImportStatus.SheetStatus.FAILED);
                            int failedRows = processedRows - successfulRows;
                            int progress = (int) (((double) successfulRows / processedRows) * 100);
                            sheetStatus.setProgress(progress);


                            // Junta todos os erros com quebra de linha
                            String allErrors = String.join("\n", errorMessages);
                            sheetStatus.setMessage(allErrors);
                        } else {
                            sheetStatus.setStatus(SheetImportStatus.SheetStatus.PROCESSED);
                            sheetStatus.setMessage("Processada com sucesso.");
                            sheetStatus.setProgress(100);
                        }

                        sheetImportStatusRepository.update(sheetStatus);
                    }

                } catch (Exception e) {
                    synchronized (lock) {
                        file.setStatus(PatientImportFile.ImportStatus.FAILED);
                        file.setMessage("Erro em " + sheetName + ": " + e.getMessage());
//                        file.setProgress(0);
                        repository.update(file);
                    }

                    if (sheetStatus != null) {
                        sheetStatus.setStatus(SheetImportStatus.SheetStatus.FAILED);
                        sheetStatus.setMessage("Erro: " + e.getMessage());
                        sheetStatus.setProgress(0);
                        sheetStatus.setUpdatedAt(new Date());
                        sheetImportStatusRepository.update(sheetStatus);
                    }
                }
            });

            threads.add(thread);
            thread.start();
        }

        // Espera todos os threads terminarem
        for (Thread t : threads) {
            try {
                t.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        // Verifica se não falhou
        List<SheetImportStatus> statuses = sheetImportStatusRepository.findByFile(file);

        boolean allProcessed = statuses.stream()
                .allMatch(s -> s.getStatus() == SheetImportStatus.SheetStatus.PROCESSED);

        boolean anyFailed = statuses.stream()
                .anyMatch(s -> s.getStatus() == SheetImportStatus.SheetStatus.FAILED);

        if (allProcessed) {
            file.setMessage("Todas as sheets processadas com sucesso.");
            file.setStatus(PatientImportFile.ImportStatus.PROCESSED);
            file.setProgress(100);
        } else if (anyFailed) {
            file.setStatus(PatientImportFile.ImportStatus.FAILED); //PARTIALLY_FAILED
            file.setMessage("Algumas sheets falharam durante o processamento.");
            file.setProgress(calculateGlobalProgress(file));
        } else {
            file.setStatus(PatientImportFile.ImportStatus.FAILED);
            file.setMessage("Erro geral no processamento.");
            file.setProgress(0);
        }

        repository.update(file);
    } catch (Exception e) {
        file.setStatus(PatientImportFile.ImportStatus.FAILED);
        file.setMessage("Erro global: " + e.getMessage());
        file.setProgress(0);
        repository.update(file);
    }
}




    public void processPendingFiles() {
        List<PatientImportFile> pendingFiles = repository.findByStatus(PatientImportFile.ImportStatus.PENDING);

        for (PatientImportFile file : pendingFiles) {
            try {
                processFile(file);
            } catch (Exception e) {
                file.setStatus(PatientImportFile.ImportStatus.FAILED);
                file.setMessage("Erro: " + e.getMessage());
            }
            repository.update(file);
        }
    }

    public Page<PatientImportFileDTO> findAllPaginated(List<String> statuses, String name, Pageable pageable) {
        Page<PatientImportFile> page;

        boolean hasStatus = statuses != null && !statuses.isEmpty();
        boolean hasName = name != null && !name.isBlank();

        if (hasStatus && hasName) {
            List<PatientImportFile.ImportStatus> enumStatuses = statuses.stream()
                    .map(PatientImportFile.ImportStatus::valueOf)
                    .toList();
            page = repository.findByStatusInAndNameIlike(enumStatuses, "%" + name + "%", pageable);

        } else if (hasStatus) {
            List<PatientImportFile.ImportStatus> enumStatuses = statuses.stream()
                    .map(PatientImportFile.ImportStatus::valueOf)
                    .toList();
            page = repository.findByStatusIn(enumStatuses, pageable);

        } else if (hasName) {
            page = repository.findByNameIlike("%" + name + "%", pageable);

        } else {
            page = repository.findAll(pageable);
        }

        return page.map(PatientImportFileDTO::new);
    }



}
