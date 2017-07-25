package sesame.querying.configDB;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Main {

    public static void main(String[] args) {
        QueryInterface queryInterface = new QueryInterface();

        JFrame window = new JFrame("K500 configuration data");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setSize(new Dimension(700, 500));


        JTextArea dataField = new JTextArea();
        dataField.setSize(new Dimension(500, 400));
        JScrollPane scrollPane = new JScrollPane(dataField);

        JTextField deviceName = new JTextField();
        deviceName.setPreferredSize(new Dimension(100,50));
        deviceName.setEditable(true);



        JButton devTypesButton = new JButton("List all device types");
        devTypesButton.setSize(300,50);
        devTypesButton.addActionListener(e -> dataField.setText(queryInterface.getAllDevTypes()));

        JButton devicesButton = new JButton("List all devices");
        devicesButton.setSize(300,50);
        devicesButton.addActionListener(e -> dataField.setText(queryInterface.getAllDevices()));

        JButton clearButton = new JButton("Clear");
        clearButton.setSize(300,50);
        clearButton.addActionListener(e -> dataField.setText(""));

        JButton channelsButton = new JButton("List all channels for device: ");
        channelsButton.setSize(300,50);
        channelsButton.addActionListener(e -> dataField.setText(queryInterface.getAllChannelsForDevice(deviceName.getText())));


        JPanel butContainer = new JPanel();
        butContainer.setLayout(new FlowLayout());
        butContainer.add(clearButton);
        butContainer.add(devTypesButton);
        butContainer.add(devicesButton);
        butContainer.add(channelsButton);
        butContainer.add(deviceName);


        window.setLayout(new GridLayout(1,2));
        window.add(butContainer);
        window.add(dataField);

        window.setVisible(true);


        queryInterface.disconnect();
    }
}
