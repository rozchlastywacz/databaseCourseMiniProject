package pl.cwikla.bazy.projekt.view;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.DefaultXYDataset;
import org.jfree.data.xy.XYDataset;
import pl.cwikla.bazy.projekt.datamanage.DBGetter;
import pl.cwikla.bazy.projekt.model.DataRecord;
import pl.cwikla.bazy.projekt.model.Province;
import pl.cwikla.bazy.projekt.model.Region;
import pl.cwikla.bazy.projekt.model.State;

import java.awt.*;
import java.time.LocalDate;

public class PlotPanel extends ChartPanel {
    private final DBGetter DBGetter;

    public PlotPanel() {
        super(ChartFactory.createTimeSeriesChart(
                "NONE",
                "dates",
                "number of cases",
                new DefaultXYDataset(),
                false,
                false,
                false));
        DBGetter = new DBGetter();
        this.setBackground(Color.CYAN);
        this.setPreferredSize(new Dimension(1200, 800));
    }

    public void plot(State state, Region region, Province province) {
        String title = state.getName() + " - " + region.getName() + " - " + province.getName();
        var series = new TimeSeries("Date");
        DBGetter.getDataFrom(state, region, province)
                .forEach(dataRecord -> series.add(getDayFromDataRecord(dataRecord), dataRecord.getNumberOfCases()));

        XYDataset dataset = new TimeSeriesCollection(series);
        setCurrentPlot(title, dataset);
    }


    public void plot(State state, Region region) {
        String title = state.getName() + " - " + region.getName();
        var series = new TimeSeries("Date");
        DBGetter.getDataFrom(state, region)
                .forEach(dataRecord -> addOrUpdate(series, dataRecord));

        XYDataset dataset = new TimeSeriesCollection(series);
        setCurrentPlot(title, dataset);
    }


    public void plot(State state) {
        String title = state.getName();
        var series = new TimeSeries("Date");
        DBGetter.getDataFrom(state)
                .forEach(dataRecord -> addOrUpdate(series, dataRecord));

        XYDataset dataset = new TimeSeriesCollection(series);
        setCurrentPlot(title, dataset);

    }

    private void addOrUpdate(TimeSeries series, DataRecord dataRecord) {
        Day day = getDayFromDataRecord(dataRecord);
        Number value = series.getValue(day);
        if (value == null) {
            series.add(day, dataRecord.getNumberOfCases());
        } else {
            series.update(day, dataRecord.getNumberOfCases() + value.intValue());
        }
    }

    private Day getDayFromDataRecord(DataRecord dataRecord) {
        LocalDate date = dataRecord.getDate();
        return new Day(date.getDayOfMonth(), date.getMonthValue(), date.getYear());
    }

    private void setCurrentPlot(String title, XYDataset dataset) {
        JFreeChart chart = ChartFactory.createTimeSeriesChart(
                title,
                "dates",
                "number of cases",
                dataset,
                false,
                false,
                false);
        XYPlot plot = chart.getXYPlot();
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        renderer.setDefaultLinesVisible(false);
        renderer.setDefaultShapesVisible(true);
        plot.setRenderer(renderer);
        this.setChart(chart);
    }
}
