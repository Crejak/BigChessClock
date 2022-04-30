module net.crejak.bcc {
    requires javafx.controls;
    requires javafx.fxml;


    opens net.crejak.bcc to javafx.fxml;
    exports net.crejak.bcc;
}