package pl.cwikla.bazy.projekt.view;

import pl.cwikla.bazy.projekt.datamanage.*;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.NoSuchElementException;

public class Visualizer {
    private final JFrame mainWindow;
    private final OptionsPanel optionsPanel;
    private final PlotPanel plotPanel;

    public Visualizer() {
        mainWindow = new JFrame("Italy Covid-19 Data Visualizer");
        plotPanel = new PlotPanel();
        optionsPanel = new OptionsPanel(plotPanel);

        mainWindow.setLayout(new BorderLayout());
        mainWindow.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        mainWindow.setMinimumSize(new Dimension(1200, 800));

        mainWindow.add(plotPanel, BorderLayout.NORTH);
        mainWindow.add(optionsPanel, BorderLayout.SOUTH);
        mainWindow.setVisible(true);
        mainWindow.pack();
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        DBFiller filler = new DBFiller();
        DBGetter getter = new DBGetter();
        DataDownloader downloader = new DataDownloader();
        if(filler.updateDB(new DataSourceItaly(downloader, getter.getLastUpdate("ITA")))){
            System.out.println("Italy is up to date");
        }
        if(filler.updateDB(new DataSourceUSA(downloader, getter.getLastUpdate("USA")))){
            System.out.println("USA is up to date");
        }
        new Visualizer();

    }

}
