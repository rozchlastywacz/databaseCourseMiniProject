package pl.cwikla.bazy.projekt.datamanage;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

public class DataSourceItaly extends DataSource {
    private static final String LINE_TO_SKIP = "In fase di definizione/aggiornamento";
    private LocalDate lastDownloaded;

    public DataSourceItaly(DataDownloader dataDownloader, LocalDate lastUpdate) throws IOException, InterruptedException {
        super(dataDownloader, lastUpdate);
    }

    @Override
    protected boolean isCurrentRecordOk(DataParser.Result dataIterator) {
        return !dataIterator.getValue("denominazione_provincia").equals(LINE_TO_SKIP);
    }

    @Override
    protected Optional<DataParser.Result> nextChunk(DataDownloader downloader) throws IOException, InterruptedException {
        if(lastDownloaded == null){
            this.lastDownloaded = lastUpdate;
        }
        lastDownloaded = lastDownloaded.plusDays(1);
        if (lastDownloaded.isBefore(LocalDate.now())) {
            return Optional.of(DataParser.parseData(downloader.downloadDataForItaly(lastDownloaded)));
        } else {
            return Optional.empty();
        }
    }

    @Override
    public LocalDate getDate() {
        return LocalDateTime.parse(getValue("data")).toLocalDate();
    }

    @Override
    public int getNumberOfCases() {
        return Integer.parseInt(getValue("totale_casi"));
    }

    @Override
    public String getProvinceName() {
        return getValue("denominazione_provincia");
    }

    @Override
    public String getProvinceCode() {
        return getValue("codice_provincia");
    }

    @Override
    public String getRegionName() {
        return getValue("denominazione_regione");
    }

    @Override
    public String getRegionCode() {
        return getValue("codice_regione");
    }

    @Override
    public String getStateName() {
        return getValue("stato");
    }
}
