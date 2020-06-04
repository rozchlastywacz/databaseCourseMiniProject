package pl.cwikla.bazy.projekt.datamanage;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Optional;

public abstract class DataSource {
    private final DataDownloader dataDownloader;
    protected final LocalDate lastUpdate;
    private DataParser.Result dataIterator;

    public DataSource(DataDownloader dataDownloader, LocalDate lastUpdate) throws IOException, InterruptedException {
        this.dataDownloader = dataDownloader;
        this.lastUpdate = lastUpdate;
    }

    public boolean moveToNextRecord() throws IOException, InterruptedException {
        if (dataIterator == null) {
            Optional<DataParser.Result> dataIterator = nextChunk(dataDownloader);
            if (dataIterator.isPresent()) {
                this.dataIterator = dataIterator.get();
            } else {
                return false;
            }
        }
        boolean isOK = false;
        if (dataIterator.hasNextRecord()) {
            dataIterator.moveToNextRecord();
            isOK = isCurrentRecordOk(dataIterator);
            while (!isOK && dataIterator.hasNextRecord()) {
                dataIterator.moveToNextRecord();
                isOK = isCurrentRecordOk(dataIterator);
            }
        }
        if(!isOK) {
            Optional<DataParser.Result> dataIterator = nextChunk(dataDownloader);
            if (dataIterator.isPresent()) {
                this.dataIterator = dataIterator.get();
                return moveToNextRecord();
            }
        }
        return isOK;
    }
    protected String getValue(String columnName){
        return dataIterator.getValue(columnName);
    }

    protected abstract boolean isCurrentRecordOk(DataParser.Result dataIterator);
    protected abstract Optional<DataParser.Result> nextChunk(DataDownloader downloader) throws IOException, InterruptedException;

    public abstract LocalDate getDate();
    public abstract int getNumberOfCases();
    public abstract String getProvinceName();
    public abstract String getProvinceCode();
    public abstract String getRegionName();
    public abstract String getRegionCode();
    public abstract String getStateName();
}
