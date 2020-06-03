package pl.cwikla.bazy.projekt.view;

import pl.cwikla.bazy.projekt.datamanage.DBFiller;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

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
        new DBFiller().updateDB();
        new Visualizer();
    }

}
