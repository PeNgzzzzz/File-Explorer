module wwan.FileExplorer {
    requires javafx.controls;
    requires javafx.fxml;
                requires kotlin.stdlib;
    
                            
    opens wwan.FileExplorer to javafx.fxml;
    exports wwan.FileExplorer;
}