/*Interfaz gráfica principal del juego, donde el usuario elige entre:
    - Jugar en modo manual
    - Jugar en modo inteligente
    - Ver puntuaciones (aún no implementado)
    - Salir del programa
*/

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Menu_Principal extends JFrame {
    public Menu_Principal() {
        // Configuración de la ventana principal gráfica
        setTitle("8-Puzzle - Menú Principal");           // Título
        setSize(400, 300);                   // Tamaño en pixeles
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);   // Cerrar al presionar la x
        setLocationRelativeTo(null);                      // Centrar ventana

        // Crear panel principal: 5 filas, 4 columnas
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(5, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50)); // Margen

        // Crear botones
        JButton btnModoManual = new JButton("Modo Manual");
        JButton btnModoInteligente = new JButton("Modo Inteligente");
        JButton btnPuntuaciones = new JButton("Ver Puntuaciones");
        JButton btnSalir = new JButton("Salir");

        // Agregar eventos a los botones
        btnModoManual.addActionListener(e -> {
            new ModoManual();// abrir el modo manual
            setVisible(false); //esconde el menu
        });

        btnModoInteligente.addActionListener(e -> {
            new ModoInteligente();
            setVisible(false); //esconde el menu
        });

        btnPuntuaciones.addActionListener(e -> {
            // puntuaciones desde archivo
            JOptionPane.showMessageDialog(this, "Puntuaciones aun no implementadas.");
        });

        btnSalir.addActionListener(e -> System.exit(0)); //termina el programa

        // Agregar botones al panel
        panel.add(new JLabel("Selecciona un modo de juego:", SwingConstants.CENTER));
        panel.add(btnModoManual);
        panel.add(btnModoInteligente);
        panel.add(btnPuntuaciones);
        panel.add(btnSalir);

        add(panel);//Coloca el panel en la ventana
        setVisible(true); //Muestra el menu
    }
    // Inicio del programa
    public static void main(String[] args) {
        SwingUtilities.invokeLater(Menu_Principal::new); //invokeLater se usa para asegurarse de que la GUI se cree en el hilo de eventos de Swing
    }
}
