import vistas.LoginView;
import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // Abrimos la vista de inicio de sesión
        SwingUtilities.invokeLater(() -> {
            new LoginView().setVisible(true);
        });
    }
}