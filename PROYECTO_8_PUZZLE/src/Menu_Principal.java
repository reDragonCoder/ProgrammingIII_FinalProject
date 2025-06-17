import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import javax.swing.border.*;

public class Menu_Principal extends JFrame {
    private String nombreUsuario;

    public Menu_Principal(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;

        // Configuración básica de la ventana
        setTitle("8-Puzzle - Menú Principal");
        setSize(500, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Centrar ventana

        // Panel principal
        JPanel panelPrincipal = new JPanel(new BorderLayout());
        panelPrincipal.setBorder(new EmptyBorder(20, 20, 20, 20));
        panelPrincipal.setBackground(new Color(240, 240, 240));

        // Panel de contenido
        JPanel panelContenido = new JPanel();
        panelContenido.setLayout(new BoxLayout(panelContenido, BoxLayout.Y_AXIS));
        panelContenido.setBackground(Color.WHITE);

        // crear los bordes por separado
        Border bordeExterno = new LineBorder(new Color(200, 200, 200));
        Border margenInterno = new EmptyBorder(20, 40, 20, 40);
        panelContenido.setBorder(BorderFactory.createCompoundBorder(bordeExterno, margenInterno));

        // Título
        JLabel titulo = new JLabel("8-Puzzle");
        titulo.setFont(new Font("Arial", Font.BOLD, 28));
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        titulo.setBorder(new EmptyBorder(10, 0, 20, 0));

        // Subtítulo con nombre de usuario
        JLabel subtitulo = new JLabel("Jugador: " + nombreUsuario);
        subtitulo.setFont(new Font("Segoe UI", Font.BOLD, 16));
        subtitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        subtitulo.setBorder(new EmptyBorder(0, 0, 30, 0));

        // Panel de botones
        JPanel panelBotones = new JPanel();
        panelBotones.setLayout(new GridLayout(4, 1, 10, 15));
        panelBotones.setMaximumSize(new Dimension(300, 250));
        panelBotones.setBackground(Color.WHITE);

        // Crear botones
        JButton btnModoManual = crearBoton("Modo Manual", new Color(70, 130, 180));
        JButton btnModoInteligente = crearBoton("Modo Inteligente", new Color(70, 130, 180));
        JButton btnPuntuaciones = crearBoton("Ver Puntuaciones", new Color(100, 150, 200));
        JButton btnSalir = crearBoton("Salir", new Color(200, 80, 80));

        // Configurar acciones de los botones
        btnModoManual.addActionListener(e -> {
            new ModoManual(nombreUsuario, this);
            this.setVisible(false);
        });

        btnModoInteligente.addActionListener(e -> {
            new ModoInteligente(Menu_Principal.this);
            setVisible(false);
        });

        btnPuntuaciones.addActionListener(e -> {
            new Pantalla_Puntuaciones(this);
            this.setVisible(false);
        });


        btnSalir.addActionListener(e -> System.exit(0));

        // Agregar componentes
        panelBotones.add(btnModoManual);
        panelBotones.add(btnModoInteligente);
        panelBotones.add(btnPuntuaciones);
        panelBotones.add(btnSalir);

        panelContenido.add(titulo);
        panelContenido.add(subtitulo);
        panelContenido.add(panelBotones);

        panelPrincipal.add(panelContenido, BorderLayout.CENTER);
        add(panelPrincipal);

        // Hacer visible la ventana al final del constructor
        setVisible(true);
    }

    private JButton crearBoton(String texto, Color color) {
        JButton boton = new JButton(texto);
        boton.setFont(new Font("Roboto", Font.PLAIN, 16));
        boton.setBackground(color);
        boton.setForeground(Color.WHITE);
        boton.setFocusPainted(false);
        boton.setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 25));
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Efecto hover
        boton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                boton.setBackground(color.darker());
            }
            public void mouseExited(MouseEvent e) {
                boton.setBackground(color);
            }
        });

        return boton;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Registro_Usuario registro = new Registro_Usuario();

        });
    }
}



