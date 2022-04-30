module net.crejak.bcc {
    requires javafx.controls;
    requires javafx.fxml;


    opens net.crejak.bcc to javafx.fxml;
    exports net.crejak.bcc;
    exports net.crejak.bcc.model;
    opens net.crejak.bcc.model to javafx.fxml;
    exports net.crejak.bcc.controllers;
    opens net.crejak.bcc.controllers to javafx.fxml;
}