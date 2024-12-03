import vistas.LoginView;
import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // Abrimos la vista de inicio de sesión
        SwingUtilities.invokeLater(() -> {
            try {
                new LoginView().setVisible(true);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }
}