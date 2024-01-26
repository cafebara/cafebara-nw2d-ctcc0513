package cict.cafebara;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Modality;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.Stack;

public class OrderController {
    // Data structures
    private final LinkedList<OrderItem> linkedList = new LinkedList<>();
    private final ObservableList<String> listViewItems = FXCollections.observableArrayList();
    private final Stack<String> orderHistory = new Stack<>();
    // Menu Array
    private final String[] itemName = {
            "Cafebara's Black Coffee",
            "Cocoa Bliss Delight",
            "Latte Harmony",
            "Bold Brew Espresso",
            "Gelato Euphoria Espresso",
            "Zen Green Matcha",
            "CappuCloud"
    };
    private final double[] itemPrice = {125, 185, 150, 220, 300, 150, 125};
    //UI elements
    @FXML private ListView<String> orderList;
    @FXML private Label total;
    @FXML private Button item1, item2, item3, item4, item5, item6, item7, confirm, delete, sortAZ, sortZA, sortLowHigh, sortHighLow, previous, next;
    @FXML private TextArea historyPane;
    private int orderCounter = 0;
    private int currentOrderIndex = -1;

    public void initialize() {
        // Set initial data to the orderList ListView
        orderList.setItems(getListViewItems());

        // Customizes appearance of ListView
        orderList.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    setPrefHeight(50); // Reset the style if the cell is empty
                } else {
                    // Parse the item string and format for display
                    String[] parts = item.split("₱");
                    String itemName = parts[0];
                    String price = parts[1];

                    // Adjust the widths as needed
                    String formattedItem = String.format("%-30s", itemName);
                    String formattedPrice = String.format("%7s", price);

                    // Set the formatted text and style for the cell
                    setText(formattedItem + "₱" + formattedPrice);
                    setStyle("-fx-font-family: 'Monospaced'; -fx-font-size: 30;-fx-font-weight: bold;");
                    setPrefHeight(50);
                }
            }
        });

        item1.setOnAction(e -> addItem(0));
        item2.setOnAction(e -> addItem(1));
        item3.setOnAction(e -> addItem(2));
        item4.setOnAction(e -> addItem(3));
        item5.setOnAction(e -> addItem(4));
        item6.setOnAction(e -> addItem(5));
        item7.setOnAction(e -> addItem(6));
        delete.setOnAction((e -> removeSelectedItem()));
        sortAZ.setOnAction(e -> sortAlphabeticallyAZ());
        sortZA.setOnAction(e -> sortAlphabeticallyZA());
        sortLowHigh.setOnAction(e -> sortPriceLowToHigh());
        sortHighLow.setOnAction(e -> sortPriceHighToLow());
        confirm.setOnAction(e -> confirmOrder());
        previous.setOnAction(e -> previousHistory());
        next.setOnAction(e -> nextHistory());

        // Sets style for the total label
        total.setStyle("-fx-font-family: 'Segoe UI Semibold'; -fx-font-size: 30;");
        historyPane.setStyle("-fx-font-family: 'Monospaced'; -fx-font-weight: bold; -fx-font-size: 25;");
    }

    // Calculates the total cost of the order
    private double calculateTotal() {
        double totalCost = 0;
        for (OrderItem item : linkedList) {
            totalCost += item.itemPrice();
        }
        total.setText("Total: ₱ " + totalCost);
        return totalCost;
    }

    // Getter for the observable list of ListView items
    private ObservableList<String> getListViewItems() {
        return listViewItems;
    }

    // Update the ListView with the current order items
    private void updateListView() {
        listViewItems.clear();
        for (OrderItem item : linkedList) {
            listViewItems.add(item.toString());
        }
        orderList.setItems(getListViewItems());
    }

    // Ordering Functions
    private void addItem(int itemNumber) {
        String name = itemName[itemNumber];
        String price = String.valueOf(itemPrice[itemNumber]);
        if (!name.isEmpty() && !price.isEmpty()) {
            OrderItem orderItem = new OrderItem(name, Double.parseDouble(price));
            linkedList.addFirst(orderItem);
            listViewItems.addFirst(orderItem.toString());
            orderList.setItems(getListViewItems());
        }
        calculateTotal();
    }

    private void removeSelectedItem() {
        int selectedIndex = orderList.getSelectionModel().getSelectedIndex();
        if (selectedIndex < 0) {
            return;
        }

        OrderItem deletedItem = linkedList.remove(selectedIndex);
        listViewItems.remove(deletedItem.toString());
        orderList.setItems(getListViewItems());
        calculateTotal();
    }

    public void confirmOrder() {
        if (!linkedList.isEmpty()) {
            double total = calculateTotal();
            StringBuilder orderDetails = new StringBuilder();

            // Find the maximum length of the item name to determine padding for prices
            int itemNameLength = 0;
            for (OrderItem item : linkedList) {
                itemNameLength = Math.max(itemNameLength, item.itemName.length());
            }

            // Format the items with padding for a single column
            for (OrderItem item : linkedList) {
                String itemNamePadding = " ".repeat(itemNameLength - item.itemName.length() + 5);

                orderDetails.append(item.itemName).append(itemNamePadding);
                orderDetails.append(" - P").append(item.itemPrice).append("\n");
            }

            orderDetails.append("\nTotal: P").append(total);

            // Show confirmation popup without cancel button
            Alert confirmationAlert = new Alert(Alert.AlertType.INFORMATION);
            confirmationAlert.initModality(Modality.NONE);
            confirmationAlert.setTitle("Order Confirmation");
            confirmationAlert.setHeaderText("Order Confirmed!");
            confirmationAlert.setContentText(orderDetails.toString());

            // Set the font to monospaced
            confirmationAlert.getDialogPane().setStyle("-fx-font-family: 'Monospaced'; -fx-font-weight: bold");

            confirmationAlert.showAndWait();

            orderHistory.push("Order: " + ++orderCounter + "\n" + orderDetails);
            linkedList.clear();
            listViewItems.clear();
            currentOrderIndex = orderHistory.size() - 1;

            // Update the historyPane
            updateHistoryPane();
        }
        calculateTotal();
    }

    // Order List Sorting Functions
    private void sortAlphabeticallyAZ() {
        linkedList.sort(Comparator.comparing(OrderItem::itemName));
        updateListView();
    }

    private void sortAlphabeticallyZA() {
        linkedList.sort(Comparator.comparing(OrderItem::itemName).reversed());
        updateListView();
    }

    private void sortPriceLowToHigh() {
        linkedList.sort(Comparator.comparingDouble(OrderItem::itemPrice));
        updateListView();
    }

    private void sortPriceHighToLow() {
        linkedList.sort(Comparator.comparingDouble(OrderItem::itemPrice).reversed());
        updateListView();
    }

    // History View Functions
    private void previousHistory() {
        if (currentOrderIndex > 0) {
            currentOrderIndex--;
            updateHistoryPane();
        }
    }

    private void nextHistory() {
        if (currentOrderIndex < orderHistory.size() - 1) {
            currentOrderIndex++;
            updateHistoryPane();
        }
    }

    // Update the historyPane with the current order details
    private void updateHistoryPane() {
        if (!orderHistory.isEmpty() && currentOrderIndex >= 0) {
            historyPane.setText(orderHistory.get(currentOrderIndex));
        }
    }

    // Formats item name and price string
    private record OrderItem(String itemName, double itemPrice) {
        @Override
        public String toString() {
            int totalWidth = 40; // Adjust the total width as needed
            String format = "%-" + (totalWidth - 7) + "s₱%7.2f";
            return String.format(format, itemName, itemPrice);
        }
    }
}