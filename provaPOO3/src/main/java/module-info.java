module org.example.provapoo3 {
    requires javafx.controls;
    requires javafx.fxml;

    opens org.example.provapoo3.controller to javafx.fxml;
    opens org.example.provapoo3.model to javafx.base; // se usa TableView com propriedades
    exports org.example.provapoo3;
}
