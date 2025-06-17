import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.border.*;

public class Registro_Usuario {
    private String nombreUsuario;

    public Registro_Usuario() {
        // Configuración de la ventana 
        JDialog dialog = new JDialog();
        dialog.setTitle("Registro de Jugador");
        dialog.setSize(400, 350);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setLocationRelativeTo(null);
        dialog.setModal(true);
        dialog.setResizable(false);

        // Forzar decoración estándar
        dialog.setUndecorated(false);

        // Panel principal con fondo degradado
        JPanel panelPrincipal = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                Color colorInicio = new Color(50, 120, 180);
                Color colorFin = new Color(80, 160, 220);
                g2d.setPaint(new GradientPaint(0, 0, colorInicio, getWidth(), getHeight(), colorFin));
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        panelPrincipal.setBorder(new EmptyBorder(40, 30, 30, 30));

        // Panel de contenido
        JPanel panelContenido = new JPanel();
        panelContenido.setLayout(new BoxLayout(panelContenido, BoxLayout.Y_AXIS));
        panelContenido.setOpaque(false);
        panelContenido.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(255, 255, 255, 100), 2, true),
                new EmptyBorder(20, 20, 20, 20)
        ));

        // Icono de bienvenida
        JLabel icono = new JLabel("🧩");
        icono.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 36));
        icono.setAlignmentX(Component.CENTER_ALIGNMENT);
        icono.setBorder(new EmptyBorder(10, 0, 15, 0));

        // Etiqueta de instrucción
        JLabel lblInstruccion = new JLabel("Ingresa tu nombre:");
        lblInstruccion.setFont(new Font("Arial", Font.BOLD, 16));
        lblInstruccion.setForeground(Color.WHITE);
        lblInstruccion.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblInstruccion.setBorder(new EmptyBorder(0, 0, 10, 0));

        // Campo de texto
        JTextField txtNombre = new JTextField();
        txtNombre.setFont(new Font("Consolas", Font.PLAIN, 14));
        txtNombre.setMaximumSize(new Dimension(250, 30));
        txtNombre.setAlignmentX(Component.CENTER_ALIGNMENT);
        txtNombre.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(200, 200, 200)),
                new EmptyBorder(5, 10, 5, 10)
        ));

        // Botón de aceptar
        JButton btnAceptar = new JButton("Comenzar Juego");
        btnAceptar.setFont(new Font("Roboto", Font.BOLD, 14));
        btnAceptar.setBackground(new Color(255, 195, 0));
        btnAceptar.setForeground(Color.BLACK);
        btnAceptar.setFocusPainted(false);
        btnAceptar.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(230, 175, 0)),
                new EmptyBorder(10, 25, 10, 25)
        ));
        btnAceptar.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnAceptar.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Efecto hover
        btnAceptar.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                btnAceptar.setBackground(new Color(255, 215, 0));
            }
            public void mouseExited(MouseEvent e) {
                btnAceptar.setBackground(new Color(255, 195, 0));
            }
        });

        // Acción del botón
        btnAceptar.addActionListener(e -> {
            nombreUsuario = txtNombre.getText().trim();
            if (nombreUsuario.isEmpty()) {
                JOptionPane.showMessageDialog(dialog,
                        "Por favor ingresa un nombre válido",
                        "Nombre requerido",
                        JOptionPane.WARNING_MESSAGE);
            } else {
                dialog.dispose();
                SwingUtilities.invokeLater(() -> new Menu_Principal(nombreUsuario));
            }
        });

        // Configurar Enter para aceptar
        txtNombre.addActionListener(e -> btnAceptar.doClick());

        // Agregar componentes
        panelContenido.add(icono);
        panelContenido.add(lblInstruccion);
        panelContenido.add(Box.createRigidArea(new Dimension(0, 8)));
        panelContenido.add(txtNombre);
        panelContenido.add(Box.createRigidArea(new Dimension(0, 15)));
        panelContenido.add(btnAceptar);

        panelPrincipal.add(panelContenido, BorderLayout.CENTER);
        dialog.add(panelPrincipal);
        dialog.setVisible(true);
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }
}