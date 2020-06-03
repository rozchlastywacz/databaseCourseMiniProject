package pl.cwikla.bazy.projekt.view;

import pl.cwikla.bazy.projekt.datamanage.DBGetter;
import pl.cwikla.bazy.projekt.model.Province;
import pl.cwikla.bazy.projekt.model.Region;
import pl.cwikla.bazy.projekt.model.State;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class OptionsPanel extends JPanel {
    private final JList<Object> states;
    private final JList<Object> regions;
    private final JList<Object> provinces;
    private final PlotPanel plotPanel;
    private final DBGetter DBGetter;

    public OptionsPanel(PlotPanel plotPanel) {
        this.plotPanel = plotPanel;
        DBGetter = new DBGetter();
        states = new JList<>(DBGetter.getAllStates().toArray());
        regions = new JList<>();
        provinces = new JList<>();
        states.addListSelectionListener(e -> regions.setModel(new MyListModel(DBGetter.getAllRegionsIn(states.getSelectedValue()))));
        regions.addListSelectionListener(e -> provinces.setModel(new MyListModel(DBGetter.getAllProvincesIn(regions.getSelectedValue()))));
        JButton searchButton = new JButton("plot data");
        searchButton.addMouseListener(new MyMouseAdapter());
        states.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        states.setLayoutOrientation(JList.HORIZONTAL_WRAP);
        states.setVisibleRowCount(-1);
        JScrollPane statesScroll = new JScrollPane(states);
        statesScroll.setPreferredSize(new Dimension(80, 80));
        JPanel statesPanel = new JPanel();
        statesPanel.setLayout(new BorderLayout());
        JLabel statesLabel = new JLabel("States");
        statesPanel.add(statesLabel, BorderLayout.NORTH);
        statesPanel.add(statesScroll, BorderLayout.SOUTH);
        this.add(statesPanel);

        regions.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        regions.setLayoutOrientation(JList.HORIZONTAL_WRAP);
        regions.setVisibleRowCount(-1);
        JScrollPane regionsScroll = new JScrollPane(regions);
        regionsScroll.setPreferredSize(new Dimension(160, 80));
        JPanel regionsPanel = new JPanel();
        regionsPanel.setLayout(new BorderLayout());
        JLabel regionsLabel = new JLabel("Regions");
        regionsPanel.add(regionsLabel, BorderLayout.NORTH);
        regionsPanel.add(regionsScroll, BorderLayout.SOUTH);
        this.add(regionsPanel);

        provinces.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        provinces.setLayoutOrientation(JList.HORIZONTAL_WRAP);
        provinces.setVisibleRowCount(-1);
        JScrollPane provincesScroll = new JScrollPane(provinces);
        provincesScroll.setPreferredSize(new Dimension(160, 80));
        JPanel provincesPanel = new JPanel();
        provincesPanel.setLayout(new BorderLayout());
        JLabel provincesLabel = new JLabel("Provinces");
        provincesPanel.add(provincesLabel, BorderLayout.NORTH);
        provincesPanel.add(provincesScroll, BorderLayout.SOUTH);
        this.add(provincesPanel);

        this.add(searchButton);
    }

    private void checkValuesAndUpdatePlot() {
        Object state = states.getSelectedValue();
        Object region = regions.getSelectedValue();
        Object province = provinces.getSelectedValue();
        if (state instanceof State && region instanceof Region && province instanceof Province)
            plotPanel.plot((State) state, (Region) region, (Province) province);
        else if (state instanceof State && region instanceof Region && province instanceof String)
            plotPanel.plot((State) state, (Region) region);
        else if (state instanceof State && region instanceof String)
            plotPanel.plot((State) state);
    }

    private static class MyListModel extends AbstractListModel<Object> {

        private final List<?> list;
        private static final String ALL = "ALL";
        ;

        public MyListModel(List<?> list) {
            this.list = list;
        }

        @Override
        public int getSize() {
            return list.size() + 1;
        }

        @Override
        public Object getElementAt(int index) {
            return index == 0 ? ALL : list.get(index - 1);
        }
    }

    private class MyMouseAdapter extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            checkValuesAndUpdatePlot();
        }
    }
}
