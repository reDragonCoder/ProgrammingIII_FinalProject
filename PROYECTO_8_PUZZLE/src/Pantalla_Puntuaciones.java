import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.List;

public class Pantalla_Puntuaciones extends JFrame {
    private JFrame ventanaPrincipal;

    public Pantalla_Puntuaciones(JFrame ventanaPrincipal) {
        this.ventanaPrincipal = ventanaPrincipal;
        configurarVentana();
        initUI();
        setVisible(true);
    }

    private void configurarVentana() {
        setTitle("Ranking de Puntuaciones");
        setSize(550, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Fondo degradado para toda la ventana
        setContentPane(new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                Color colorInicio = new Color(230, 240, 250);
                Color colorFin = new Color(200, 220, 240);
                g2d.setPaint(new GradientPaint(0, 0, colorInicio, getWidth(), getHeight(), colorFin));
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        });
    }

    private void initUI() {
        // Panel principal con BoxLayout vertical
        JPanel panelPrincipal = new JPanel();
        panelPrincipal.setLayout(new BoxLayout(panelPrincipal, BoxLayout.Y_AXIS));
        panelPrincipal.setBorder(new EmptyBorder(15, 15, 15, 15));
        panelPrincipal.setOpaque(false);

        // Título
        JLabel titulo = new JLabel("MEJORES PUNTUACIONES", SwingConstants.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 24));
        titulo.setForeground(new Color(70, 70, 150));
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        titulo.setBorder(new EmptyBorder(10, 0, 20, 0));

        // Tabla de puntuaciones con tamaño fijo
        String[] columnas = {"Posición", "Jugador", "Movimientos"};
        Object[][] datos = obtenerDatosPuntuaciones();

        JTable tabla = new JTable(datos, columnas);
        tabla.setFont(new Font("Arial", Font.PLAIN, 14));
        tabla.setRowHeight(25);
        tabla.setEnabled(false);

        // Personalizar header
        JTableHeader header = tabla.getTableHeader();
        header.setFont(new Font("Arial", Font.BOLD, 15));
        header.setBackground(new Color(70, 130, 180));
        header.setForeground(Color.WHITE);

        // Centrar contenido
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < tabla.getColumnCount(); i++) {
            tabla.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        // Panel para la tabla con tamaño fijo y scroll
        JScrollPane scrollPane = new JScrollPane(tabla);
        scrollPane.setPreferredSize(new Dimension(500, 300)); // Tamaño fijo
        scrollPane.setMaximumSize(new Dimension(500, 300)); // Evita que crezca
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        // Botón Volver
        JButton btnCerrar = new JButton("Volver al Menú");
        btnCerrar.setFont(new Font("Arial", Font.BOLD, 14));
        btnCerrar.setBackground(new Color(70, 130, 180));
        btnCerrar.setForeground(Color.WHITE);
        btnCerrar.setFocusPainted(false);
        btnCerrar.setBorder(new CompoundBorder(
                new LineBorder(new Color(50, 100, 150), 1),
                new EmptyBorder(8, 25, 8, 25)
        ));
        btnCerrar.addActionListener(e -> {
            if (ventanaPrincipal != null) {
                ventanaPrincipal.setVisible(true);
            }
            dispose();
        });

        // Panel para el botón con espacio arriba
        JPanel panelBoton = new JPanel();
        panelBoton.setLayout(new BoxLayout(panelBoton, BoxLayout.X_AXIS));
        panelBoton.setOpaque(false);
        panelBoton.setBorder(new EmptyBorder(20, 0, 0, 0));
        panelBoton.add(Box.createHorizontalGlue());
        panelBoton.add(btnCerrar);
        panelBoton.add(Box.createHorizontalGlue());

        // Organización final
        panelPrincipal.add(titulo);
        panelPrincipal.add(Box.createRigidArea(new Dimension(0, 10)));
        panelPrincipal.add(scrollPane);
        panelPrincipal.add(Box.createVerticalGlue());
        panelPrincipal.add(panelBoton);

        // Agregar scroll al panel principal
        JScrollPane mainScroll = new JScrollPane(panelPrincipal);
        mainScroll.setBorder(BorderFactory.createEmptyBorder());
        mainScroll.getViewport().setOpaque(false);

        add(mainScroll);
    }

    private void mostrarPuntuaciones(JTextArea area) {
        List<String> puntuaciones = GestorPuntuaciones.obtenerPuntuacionesOrdenadas();

        if (puntuaciones.isEmpty()) {
            area.setText("No hay puntuaciones registradas aún.\n\n¡Completa el puzzle para aparecer aquí!");
            return;
        }

        StringBuilder sb = new StringBuilder();
        int posicion = 1;

        for (String puntuacion : puntuaciones) {
            String[] partes = puntuacion.split(" - ");
            if (partes.length == 2) {
                sb.append(String.format("%2d. %-20s %s\n",
                        posicion++,
                        partes[0],
                        partes[1]));
            }
        }

        area.setText(sb.toString());
    }

    private Object[][] obtenerDatosPuntuaciones() {
        List<String> puntuaciones = GestorPuntuaciones.obtenerPuntuacionesOrdenadas();

        if (puntuaciones.isEmpty()) {
            return new Object[][]{{"1", "No hay datos", ""}};
        }

        Object[][] datos = new Object[puntuaciones.size()][3];
        int posicion = 1;

        for (int i = 0; i < puntuaciones.size(); i++) {
            String[] partes = puntuaciones.get(i).split(" - ");
            if (partes.length == 2) {
                datos[i][0] = posicion++;
                datos[i][1] = partes[0];
                datos[i][2] = partes[1] + " ";
            }
        }

        return datos;
    }
}