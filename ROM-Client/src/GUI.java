import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.sql.SQLOutput;
import java.util.HashMap;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;

import org.json.simple.*;

public class GUI implements ActionListener {
    private JFrame frame;
    private JPanel header, content, right_content, timestamp, menuList, row, orderList;
    private JLabel brand, date, price_total;
    private Order order;
    private JSONArray obj;
    private int order_id = 1;
    private static HashMap boughtlist;

    public GUI(String name) {
        init(name);
    }

    public void init(String name) {
        // frame
        frame = new JFrame("" + name);
        // icon for windows
        frame.setIconImage(new ImageIcon("img/icon.png").getImage());
        frame.setVisible(true);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // order
        order = new Order();
        // header
        header = new JPanel();
        header.setLayout(new GridLayout(1, 3));
        header.setBackground(Color.darkGray);
        // brand
        brand = new JLabel(" Food by Us");
        brand.setLayout(new FlowLayout());
        brand.setIcon(new ImageIcon("img/icon.png"));
        brand.setFont(new Font("Courier New", Font.BOLD, 50));
        brand.setForeground(Color.WHITE);
        header.setBackground(new Color(47, 79, 79));
//        brand.setIcon(new ImageIcon("img/logo.png"));
//        brand.setVerticalAlignment(JLabel.CENTER);
//        brand.setHorizontalAlignment(JLabel.LEFT);
        header.add(brand);
        // timestamp
        timestamp = new JPanel();
        timestamp.setLayout(new GridLayout(2, 1));
        timestamp.setBackground(new Color(47, 79, 79));
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDateTime now = LocalDateTime.now();
        date = new JLabel(dtf.format(now) + " ");
        Clock time = new Clock();
        Thread thread = new Thread(time);
        thread.start();
        date.setForeground(Color.WHITE);
        time.setForeground(Color.WHITE);
        date.setFont(new Font("Courier New", Font.BOLD, 14));
        time.setFont(new Font("Courier New", Font.BOLD, 14));
        date.setHorizontalAlignment(JLabel.RIGHT);
        time.setHorizontalAlignment(JLabel.RIGHT);
        date.setIcon(new ImageIcon("img/calendar.png"));
        time.setIcon(new ImageIcon("img/time.png"));
        timestamp.add(date);
        timestamp.add(time);
        header.add(timestamp);
        // content
        content = new JPanel();
        content.setLayout(new FlowLayout());
        content.setBackground(Color.darkGray);
        // menu
        Json data = new Json();
        obj = data.openJson("data/menu.json");
        menuList = new JPanel();
        menuList.setLayout(new GridLayout(4, 1));
        menuList.setBackground(Color.gray);
        row = new JPanel();
        row.setBackground(new Color(0, 0, 0));
        // add menu
        for (int i = 0; i < 12; i++) {
            if (i < obj.size()) {
                JSONObject obj1 = (JSONObject) obj.get(i);
                JPanel menu = new JPanel();
                // price, button
                JPanel small = new JPanel();
                // menu image
                JLabel image = new JLabel();
                image.setIcon(new ImageIcon("img/" + obj1.get("img")));
                image.setHorizontalAlignment(JLabel.CENTER);
                // menu price
                JLabel price = new JLabel(obj1.get("name") + " : " + obj1.get("price") + " ฿");
                price.setHorizontalAlignment(JLabel.CENTER);
                // add item, remove item to order
                JButton button = new JButton("+ " + obj1.get("id"));
                JButton button1 = new JButton("- " + obj1.get("id"));
                button.addActionListener(this);
                button1.addActionListener(this);
                button.setBackground(new Color(180, 79, 79));
                button1.setBackground(new Color(79, 180, 79));
                button.setForeground(Color.WHITE);
                button1.setForeground(Color.WHITE);
                // add to small
                small.add(price);
                JPanel buttonField = new JPanel();
                buttonField.add(button);
                buttonField.add(button1);
                buttonField.setLayout(new GridLayout(1, 2));
                small.add(buttonField);
                small.setLayout(new GridLayout(2, 1));
                // add to menu
                menu.add(image, BorderLayout.NORTH);
                menu.add(small, BorderLayout.CENTER);
                menu.setPreferredSize(new Dimension(200, 122));
                menu.setLayout(new GridLayout(2, 1));
                row.add(menu);
            }
            else {
                JPanel menu = new JPanel();
                menu.setPreferredSize(new Dimension(200, 122));
                row.add(menu);
            }
            if (i % 3 == 2 && i != 0) {
                menuList.add(row);
                row = new JPanel();
                row.setBackground(new Color(0, 0, 0));
            }

        }
        // right content
        right_content = new JPanel();
        right_content.setLayout(new BoxLayout(right_content, BoxLayout.Y_AXIS));
        // price
        JPanel priceBox = new JPanel();
        priceBox.setLayout(new GridLayout(1, 2));
        priceBox.setBackground(Color.darkGray);
        priceBox.setPreferredSize(new Dimension(480, 30));
        JLabel price_text = new JLabel(" Total: ");
        price_total = new JLabel("00.00 ฿ ");
        price_text.setHorizontalAlignment(JLabel.LEFT);
        price_total.setHorizontalAlignment(JLabel.RIGHT);
        price_text.setFont(new Font("Courier New", Font.BOLD, 40));
        price_total.setFont(new Font("Courier New", Font.BOLD, 34));
        price_text.setForeground(Color.WHITE);
        price_total.setForeground(Color.ORANGE);
        priceBox.add(price_text);
        priceBox.add(price_total);
        // table
        orderList = new JPanel();
        orderList.setBackground(new Color(0, 0, 0));
        right_content.add(orderList);
        right_content.add(priceBox);
        addTable(orderList);
        // add menuList, orderList to content
        content.add(menuList);
        content.add(right_content);
        // footer
        JPanel optionPanel = new JPanel();
        optionPanel.setLayout(new GridLayout(1,2));
        optionPanel.setBackground(Color.darkGray);
        // order, reset button
        JButton summit = new JButton("Order");
        JButton reset = new JButton("Reset");
        summit.setFont(new Font("Courier New", Font.BOLD, 15));
        reset.setFont(new Font("Courier New", Font.BOLD, 15));
        summit.addActionListener(this);
        reset.addActionListener(this);
        summit.setIcon(new ImageIcon("img/cart.png"));
        reset.setIcon(new ImageIcon("img/trash.png"));
        summit.setPreferredSize(new Dimension(150, 50));
        reset.setPreferredSize(new Dimension(150, 50));
        // add button to footer
        optionPanel.add(summit);
        optionPanel.add(reset);
        right_content.add(optionPanel);
        // add header, content to frame
        frame.getContentPane().add(header, BorderLayout.NORTH);
        frame.getContentPane().add(content, BorderLayout.CENTER);
//        frame.getContentPane().add(footer, BorderLayout.SOUTH);
        // frame
        frame.pack();
        centerWindow(frame);
    }

    private void addTable(JPanel orderList) {
        orderList.removeAll();
        orderList.revalidate();
        orderList.repaint();
        HashMap<String, Integer> data = order.getOrder();
        Object[][] tableData = new Object[data.keySet().size()][3];
        String column[] = {"Order", "Total", "Price"};
        int index = 0;
        for (String key : data.keySet()) {
            tableData[index][0] = " " + key;
            tableData[index][1] = data.get(key);
            for (int i = 0; i < obj.size(); i++) {
                JSONObject obj1 = (JSONObject) obj.get(i);
                if (obj1.get("name").equals(key)) {
                    double price = Double.parseDouble("" + obj1.get("price"));
                    int total = data.get(key);
                    tableData[index][2] = String.format("%.02f ", (price * total));
                    Total.addPrice(key, (price * total));
                }
            }
            index++;
        }
        price_total.setText(String.format("%.02f ฿ ", Total.getTotal()));
        TableModel model = new DefaultTableModel(tableData, column) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable table = new JTable(model);
        table.getTableHeader().setFont(new Font("Courier New", Font.BOLD, 16));
        table.setFont(new Font("Courier New", Font.BOLD, 18));
        table.setRowHeight(table.getRowHeight() + 10);
        // center header
        TableCellRenderer rendererFromHeader = table.getTableHeader().getDefaultRenderer();
        JLabel headerLabel = (JLabel) rendererFromHeader;
        headerLabel.setHorizontalAlignment(JLabel.CENTER);
        // center cell
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        table.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
        // right cell
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(JLabel.RIGHT);
        table.getColumnModel().getColumn(2).setCellRenderer(rightRenderer);

        JScrollPane scrollPane = new JScrollPane(table);

        orderList.add(scrollPane);
    }

    public void actionPerformed(ActionEvent ae) {
//        System.out.println(ae.getActionCommand());
        if (ae.getActionCommand().equals("Reset")) {
            if (!order.getOrder().isEmpty()) {
                ImageIcon icon = new ImageIcon("img/alert.png");
                int n = JOptionPane.showConfirmDialog(null,
                        "Reset Anyway!",
                        "Do you want to reset this?",
                        JOptionPane.YES_NO_OPTION, 3, icon);
                if (n == 0) {
                    order.resetOrder();
                    Total.reset();
                    addTable(orderList);
                }
            }
        } else if (ae.getActionCommand().equals("Order")) {
            if (!order.getOrder().isEmpty()) {
                ImageIcon icon = new ImageIcon("img/alert.png");
                int n = JOptionPane.showConfirmDialog(null,
                        "Press \"Yes\" to confirm your order!",
                        "Confirm your order",
                        JOptionPane.YES_NO_OPTION, 3, icon);
                if (n == 0) {
                    Json orderJSON = new Json();
                    order.addOrderID();
                    Network network = new Network(orderJSON.toJson(order.getOrder(), order, LocalDateTime.now()));
                    Thread thread = new Thread(network);
                    thread.start();
                    boughtlist = order.getOrder();

                    order.resetOrder();
                    Total.reset();
                    addTable(orderList);

                }
            }
        } else {
            String orderName = "";
            String name = ae.getActionCommand();
            String[] check = name.split(" ");
            for (int i = 0; i < 12; i++) {
                if (i < obj.size()) {
                    JSONObject obj1 = (JSONObject) obj.get(i);
                    if (obj1.get("id").equals(check[1])) {
                        orderName = (String) obj1.get("name");
                        break;
                    }
                }
            }
            if (check[0].equals("+")) {
                order.addOrder(orderName);
            } else if (check[0].equals("-")) {
                order.removeOrder(orderName);
            }
            addTable(orderList);
        }
    }

    public void centerWindow(JFrame frame) {
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (int) ((dimension.getWidth() - frame.getWidth()) / 2);
        int y = (int) ((dimension.getHeight() - frame.getHeight()) / 2);
        frame.setLocation(x, y);
    }

    public static HashMap getBoughtList() {
        return boughtlist;
    }

}
