package pl.cwikla.bazy.projekt.datamanage;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Optional;

public class DataSourceUSA extends DataSource {
    private Boolean downloaded;

    public DataSourceUSA(DataDownloader dataDownloader, LocalDate lastUpdate) throws IOException, InterruptedException {
        super(dataDownloader, lastUpdate);
        downloaded = false;
    }

    @Override
    protected boolean isCurrentRecordOk(DataParser.Result ignored) {
        if (getDate().isAfter(lastUpdate) && !getProvinceCode().isEmpty() && !getProvinceName().equals("Unknown")
                && getDate().isBefore(LocalDate.parse("2020-04-20"))){
            System.out.println(getDate());
            return true;
        }
        return false;
    }

    @Override
    protected Optional<DataParser.Result> nextChunk(DataDownloader downloader) throws IOException, InterruptedException {
        if(!this.downloaded) {
            this.downloaded = true;

            return Optional.of(DataParser.parseData(downloader.downloadDataForUSA()));
        }
        else{
            return Optional.empty();
        }
    }

    @Override
    public LocalDate getDate() {
        return LocalDate.parse(getValue("date"));
    }

    @Override
    public int getNumberOfCases() {
        return Integer.parseInt(getValue("cases"));
    }

    @Override
    public String getProvinceName() {
        return getValue("county");
    }

    @Override
    public String getProvinceCode() {
        return getValue("fips");
    }

    @Override
    public String getRegionName() {
        return getValue("state");
    }

    @Override
    public String getRegionCode() {
        return getValue("fips").substring(0, 2);
    }

    @Override
    public String getStateName() {
        return "USA";
    }
}
