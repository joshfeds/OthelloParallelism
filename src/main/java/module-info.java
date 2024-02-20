module org.example.othelloparallelism2real2cool {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;

    opens org.example.othelloparallelism2real2cool to javafx.fxml;
    exports org.example.othelloparallelism2real2cool;
}