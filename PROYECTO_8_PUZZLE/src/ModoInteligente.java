import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import javax.swing.Timer;

public class ModoInteligente extends JFrame {
    // Colores mejorados con paleta más armónica
    private static final Color COLOR_PRIMARIO = new Color(52, 152, 219);
    private static final Color COLOR_SECUNDARIO = new Color(41, 128, 185);
    private static final Color COLOR_TERCIARIO = new Color(231, 76, 60);
    private static final Color COLOR_FONDO = new Color(236, 240, 241);
    private static final Color COLOR_CELDA = new Color(255, 255, 255);
    private static final Color COLOR_VACIO = new Color(189, 195, 199);
    private static final Color COLOR_TEXTO = new Color(44, 62, 80);

    // Componentes de la interfaz
    private JPanel tableroPanel;
    private List<EstadoPuzzle> solucion;
    private int pasoActual = 0;
    private Timer animador;
    private JButton btnPausa, btnReiniciar, btnMenuPrincipal, btnNuevoPuzzle;
    private JSlider velocidadSlider;
    private JLabel lblPasos, lblTitulo;
    private JFrame menuPrincipal;

    public ModoInteligente(JFrame menuPrincipal) {
        this.menuPrincipal = menuPrincipal;
        configurarVentana();
        pedirDatosYPrepararSolucion();
    }

    private void configurarVentana() {
        setTitle("8 Puzzle - Modo Inteligente");
        setSize(650, 650); // Tamaño
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Fondo
        setContentPane(new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setColor(COLOR_FONDO);
                g2d.fillRect(0, 0, getWidth(), getHeight());

                // Textura
                g2d.setColor(new Color(0, 0, 0, 5));
                for (int i = 0; i < getWidth(); i += 4) {
                    for (int j = 0; j < getHeight(); j += 4) {
                        g2d.fillRect(i, j, 1, 1);
                    }
                }
            }
        });
    }

    private void pedirDatosYPrepararSolucion() {
        EstadoPuzzle inicial = mostrarDialogoEstado("Estado Inicial del Puzzle");
        if (inicial == null) {
            volverAlMenu();
            return;
        }

        EstadoPuzzle meta = mostrarDialogoEstado("Estado Meta del Puzzle");
        if (meta == null) {
            volverAlMenu();
            return;
        }

        if (!validarEstados(inicial, meta)) {
            pedirDatosYPrepararSolucion();
            return;
        }

        solucion = ResolverPuzzle.resolverAEstrella(inicial, meta);

        if (solucion == null) {
            mostrarMensajeError("No se encontró solución", "No se pudo encontrar una solución con los estados proporcionados.");
            pedirDatosYPrepararSolucion();
            return;
        }

        crearInterfaz();
        setVisible(true);
    }

    private void crearInterfaz() {
        getContentPane().removeAll();

        // Panel principal
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setOpaque(false);

        // Panel del título
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        titlePanel.setOpaque(false);

        lblTitulo = new JLabel("SOLUCIÓN DEL 8-PUZZLE");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitulo.setForeground(COLOR_TEXTO);
        titlePanel.add(lblTitulo);

        // Panel del tablero
        tableroPanel = new JPanel(new GridLayout(3, 3, 8, 8));
        tableroPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        tableroPanel.setBackground(COLOR_FONDO);
        tableroPanel.setOpaque(false);

        // Inicializar el tablero con celdas vacías
        for (int i = 0; i < 9; i++) {
            tableroPanel.add(crearCelda(0));
        }

        // Panel de controles inferiores (contiene todo lo que va abajo)
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
        bottomPanel.setOpaque(false);

        // Panel para controles de velocidad y pasos
        JPanel controlsPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        controlsPanel.setOpaque(false);
        controlsPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        // Panel de velocidad
        JPanel speedPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        speedPanel.setOpaque(false);

        velocidadSlider = new JSlider(100, 2000, 1000);
        configurarSlider(velocidadSlider);

        speedPanel.add(new JLabel("Velocidad:"));
        speedPanel.add(velocidadSlider);

        // Panel de pasos
        JPanel stepsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        stepsPanel.setOpaque(false);

        lblPasos = new JLabel(String.format("Paso: %d/%d", pasoActual, solucion.size()-1));
        lblPasos.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblPasos.setForeground(COLOR_TEXTO);

        stepsPanel.add(lblPasos);

        controlsPanel.add(speedPanel);
        controlsPanel.add(stepsPanel);

        // Panel de botones principal
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));

        // Crear todos los botones con el mismo tamaño y alineación
        btnPausa = crearBoton("Pausar", COLOR_PRIMARIO);
        btnReiniciar = crearBoton("Reiniciar", COLOR_SECUNDARIO);
        btnNuevoPuzzle = crearBoton("Nuevo Puzzle", COLOR_SECUNDARIO);
        btnMenuPrincipal = crearBoton("Menú Principal", COLOR_TERCIARIO);

        // Añadir espacio rígido entre botones para centrarlos
        buttonPanel.add(Box.createHorizontalGlue());
        buttonPanel.add(btnPausa);
        buttonPanel.add(Box.createRigidArea(new Dimension(15, 0)));
        buttonPanel.add(btnReiniciar);
        buttonPanel.add(Box.createRigidArea(new Dimension(15, 0)));
        buttonPanel.add(btnNuevoPuzzle);
        buttonPanel.add(Box.createRigidArea(new Dimension(15, 0)));
        buttonPanel.add(btnMenuPrincipal);
        buttonPanel.add(Box.createHorizontalGlue());

        // Agregar componentes al panel inferior
        bottomPanel.add(controlsPanel);
        bottomPanel.add(buttonPanel);

        // Ensamblar la interfaz principal
        mainPanel.add(titlePanel, BorderLayout.NORTH);
        mainPanel.add(tableroPanel, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);

        // Configurar eventos
        configurarEventos();

        // Iniciar animación
        animador = new Timer(velocidadSlider.getValue(), e -> mostrarPaso());
        animador.setInitialDelay(500);
        animador.start();
    }

    private JButton crearBoton(String texto, Color color) {
        JButton boton = new JButton(texto);
        boton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        boton.setBackground(color);
        boton.setForeground(Color.WHITE);
        boton.setFocusPainted(false);

        // Establecer alineación vertical y horizontal centrada
        boton.setAlignmentX(Component.CENTER_ALIGNMENT);
        boton.setAlignmentY(Component.CENTER_ALIGNMENT);

        // Tamaño preferido consistente
        boton.setPreferredSize(new Dimension(140, 40));
        boton.setMaximumSize(new Dimension(140, 40)); // Importante para BoxLayout

        boton.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(color.darker(), 1),
                new EmptyBorder(10, 10, 10, 10)
        ));

        boton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                boton.setBackground(color.brighter());
                boton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            }
            public void mouseExited(MouseEvent e) {
                boton.setBackground(color);
                boton.setCursor(Cursor.getDefaultCursor());
            }
        });

        return boton;
    }

    private void configurarSlider(JSlider slider) {
        slider.setInverted(true);
        slider.setPreferredSize(new Dimension(180, 40));
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        slider.setMajorTickSpacing(950);
        slider.setFont(new Font("Segoe UI", Font.PLAIN, 10));
    }

    private void configurarEventos() {
        btnPausa.addActionListener(e -> toggleAnimacion());
        btnReiniciar.addActionListener(e -> reiniciarAnimacion());
        btnNuevoPuzzle.addActionListener(e -> {
            animador.stop();
            pedirDatosYPrepararSolucion();
        });
        btnMenuPrincipal.addActionListener(e -> volverAlMenu());
        velocidadSlider.addChangeListener(e -> animador.setDelay(velocidadSlider.getValue()));
    }

    private void toggleAnimacion() {
        if (animador.isRunning()) {
            animador.stop();
            btnPausa.setText("Continuar");
            // Mantener el mismo tamaño aunque cambie el texto
            btnPausa.setPreferredSize(new Dimension(140, 40));
            btnPausa.setMaximumSize(new Dimension(140, 40));
        } else {
            animador.start();
            btnPausa.setText("Pausar");
        }
        // Forzar actualización del layout
        btnPausa.getParent().revalidate();
    }

    private void mostrarPaso() {
        if (pasoActual >= solucion.size()) {
            animador.stop();
            btnPausa.setEnabled(false);
            mostrarMensajeExito("¡Puzzle resuelto!", "Se completó en " + (solucion.size()-1) + " pasos.");
            return;
        }

        EstadoPuzzle estado = solucion.get(pasoActual);
        lblPasos.setText(String.format("Paso: %d/%d", pasoActual, solucion.size()-1));

        // Actualizar las celdas existentes sin recrearlas
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                JLabel celda = (JLabel) tableroPanel.getComponent(i * 3 + j);
                actualizarCelda(celda, estado.tablero[i][j]);
            }
        }

        pasoActual++;
    }

    // Metodo para actualizar una celda existente
    private void actualizarCelda(JLabel celda, int valor) {
        celda.setText(valor == 0 ? "" : String.valueOf(valor));
        celda.setBackground(valor == 0 ? COLOR_VACIO : COLOR_CELDA);

        // Actualizar el borde según si es vacío o no
        if (valor != 0) {
            celda.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createRaisedBevelBorder(),
                    BorderFactory.createCompoundBorder(
                            new MatteBorder(2, 2, 4, 2, COLOR_PRIMARIO),
                            new EmptyBorder(5, 5, 5, 5)
                    )
            ));
        } else {
            celda.setBorder(BorderFactory.createCompoundBorder(
                    new MatteBorder(2, 2, 4, 2, COLOR_PRIMARIO),
                    new EmptyBorder(5, 5, 5, 5)
            ));
        }
    }

    private JLabel crearCelda(int valor) {
        JLabel celda = new JLabel(valor == 0 ? "" : String.valueOf(valor), SwingConstants.CENTER);
        celda.setFont(new Font("Segoe UI", Font.BOLD, 48));
        celda.setBorder(BorderFactory.createCompoundBorder(
                new MatteBorder(2, 2, 4, 2, COLOR_PRIMARIO),
                new EmptyBorder(5, 5, 5, 5)
        ));
        celda.setOpaque(true);
        celda.setBackground(valor == 0 ? COLOR_VACIO : COLOR_CELDA);
        celda.setForeground(COLOR_TEXTO);

        // Efecto de elevación para celdas no vacías
        if (valor != 0) {
            celda.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createRaisedBevelBorder(),
                    celda.getBorder()
            ));
        }

        return celda;
    }

    private void reiniciarAnimacion() {
        animador.stop();
        pasoActual = 0;
        mostrarPaso();
        animador.start();
        btnPausa.setText("Pausar");
        btnPausa.setEnabled(true);
    }

    private void volverAlMenu() {
        if (animador != null) animador.stop();
        dispose();
        if (menuPrincipal != null) menuPrincipal.setVisible(true);
    }

    private EstadoPuzzle mostrarDialogoEstado(String titulo) {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        JPanel gridPanel = new JPanel(new GridLayout(3, 3, 5, 5));

        JTextField[] campos = new JTextField[9];
        for (int i = 0; i < 9; i++) {
            campos[i] = new JTextField(2);
            campos[i].setHorizontalAlignment(JTextField.CENTER);
            campos[i].setFont(new Font("Segoe UI", Font.BOLD, 24));
            campos[i].setBorder(BorderFactory.createLineBorder(COLOR_PRIMARIO, 1));
            gridPanel.add(campos[i]);
        }

        panel.add(new JLabel("<html><div style='text-align:center;'>Ingrese números del 0 al 8<br>(0 = espacio vacío)</div></html>"),
                BorderLayout.NORTH);
        panel.add(gridPanel, BorderLayout.CENTER);

        while (true) {
            int result = JOptionPane.showConfirmDialog(this, panel, titulo,
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (result != JOptionPane.OK_OPTION) return null;

            int[][] matriz = new int[3][3];
            Set<Integer> numeros = new HashSet<>();
            boolean error = false;

            for (int i = 0; i < 9; i++) {
                try {
                    String text = campos[i].getText().trim();
                    if (text.isEmpty()) {
                        mostrarMensajeError("Campo vacío", "No deje campos vacíos. Use 0 para el espacio vacío.");
                        error = true;
                        break;
                    }

                    int num = Integer.parseInt(text);
                    if (num < 0 || num > 8 || !numeros.add(num)) {
                        mostrarMensajeError("Valor inválido", "Ingrese números únicos del 0 al 8.");
                        error = true;
                        break;
                    }
                    matriz[i / 3][i % 3] = num;
                } catch (NumberFormatException e) {
                    mostrarMensajeError("Entrada inválida", "Solo se permiten números enteros.");
                    error = true;
                    break;
                }
            }

            if (!error) return new EstadoPuzzle(matriz);
        }
    }

    private boolean validarEstados(EstadoPuzzle inicial, EstadoPuzzle meta) {
        boolean solucionableInicial = esSolucionable(inicial.tablero);
        boolean solucionableMeta = esSolucionable(meta.tablero);

        if (!solucionableInicial || !solucionableMeta) {
            String mensaje = String.format(
                    "<html><div style='width:250px;'>" +
                            "<b>Estados no compatibles:</b><br><br>" +
                            "Estado inicial: %s<br>" +
                            "Estado meta: %s<br><br>" +
                            "Ambos deben ser solucionables o no solucionables." +
                            "",
                    solucionableInicial ? "VÁLIDO" : "NO VÁLIDO",
                    solucionableMeta ? "VÁLIDO" : "NO VÁLIDO"
            );
            mostrarMensajeError("Error de validación", mensaje);
            return false;
        }

        return true;
    }

    private void mostrarMensajeError(String titulo, String mensaje) {
        JOptionPane.showMessageDialog(this,
                "<html><div style='width:250px;'>" + mensaje + "</div></html>",
                titulo,
                JOptionPane.ERROR_MESSAGE);
    }

    private void mostrarMensajeExito(String titulo, String mensaje) {
        JOptionPane.showMessageDialog(this,
                "<html><div style='width:250px;'>" + mensaje + "</div></html>",
                titulo,
                JOptionPane.INFORMATION_MESSAGE);
    }

    public static EstadoPuzzle pedirEstado(String titulo) {
        JPanel panel = new JPanel(new BorderLayout());
        JPanel gridPanel = new JPanel(new GridLayout(3, 3, 5, 5));

        JTextField[] campos = new JTextField[9];
        for (int i = 0; i < 9; i++) {
            campos[i] = new JTextField(2);
            campos[i].setHorizontalAlignment(JTextField.CENTER);
            campos[i].setFont(new Font("Arial", Font.BOLD, 24));
            gridPanel.add(campos[i]);
        }

        panel.add(gridPanel, BorderLayout.CENTER);
        panel.add(new JLabel("Ingrese números del 0 al 8 (0 = espacio vacío):"), BorderLayout.NORTH);

        while (true) {
            int result = JOptionPane.showConfirmDialog(null, panel, titulo,
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (result != JOptionPane.OK_OPTION) return null;

            int[][] matriz = new int[3][3];
            Set<Integer> numeros = new HashSet<>();
            boolean error = false;

            for (int i = 0; i < 9; i++) {
                try {
                    String text = campos[i].getText().trim();
                    if (text.isEmpty()) {
                        JOptionPane.showMessageDialog(null, "No deje campos vacíos. Use 0 para el espacio vacío.");
                        error = true;
                        break;
                    }

                    int num = Integer.parseInt(text);
                    if (num < 0 || num > 8 || !numeros.add(num)) {
                        JOptionPane.showMessageDialog(null, "Ingresa números únicos del 0 al 8.");
                        error = true;
                        break;
                    }
                    matriz[i / 3][i % 3] = num;
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(null, "Entrada inválida. Solo números enteros.");
                    error = true;
                    break;
                }
            }

            if (!error) return new EstadoPuzzle(matriz);
        }
    }

    private static boolean esSolucionable(int[][] tablero) {
        int[] plano = new int[9];
        int idx = 0;
        for (int[] fila : tablero)
            for (int val : fila)
                plano[idx++] = val;

        int inversiones = 0;
        for (int i = 0; i < 9; i++) {
            if (plano[i] == 0) continue;
            for (int j = i + 1; j < 9; j++) {
                if (plano[j] != 0 && plano[i] > plano[j]) inversiones++;
            }
        }
        return inversiones % 2 == 0;
    }

    static class EstadoPuzzle implements Comparable<EstadoPuzzle> {
        public int[][] tablero;
        public int filaCero, colCero;
        public EstadoPuzzle padre;
        public int costoG;
        public int costoH;
        private int hash;

        public EstadoPuzzle(int[][] tablero) {
            this.tablero = new int[3][3];
            for (int i = 0; i < 3; i++)
                this.tablero[i] = tablero[i].clone();
            encontrarCero();
            calcularHash();
        }

        private void encontrarCero() {
            for (int i = 0; i < 3; i++)
                for (int j = 0; j < 3; j++)
                    if (tablero[i][j] == 0) {
                        filaCero = i;
                        colCero = j;
                        return;
                    }
        }

        public List<EstadoPuzzle> generarHijos() {
            List<EstadoPuzzle> hijos = new ArrayList<>(4);
            int[][] direcciones = {{-1,0}, {1,0}, {0,-1}, {0,1}};

            for (int[] dir : direcciones) {
                int nuevaFila = filaCero + dir[0];
                int nuevaCol = colCero + dir[1];

                if (nuevaFila >= 0 && nuevaFila < 3 && nuevaCol >= 0 && nuevaCol < 3) {
                    EstadoPuzzle hijo = new EstadoPuzzle(this.tablero);
                    hijo.tablero[filaCero][colCero] = hijo.tablero[nuevaFila][nuevaCol];
                    hijo.tablero[nuevaFila][nuevaCol] = 0;
                    hijo.filaCero = nuevaFila;
                    hijo.colCero = nuevaCol;
                    hijo.padre = this;
                    hijo.costoG = this.costoG + 1;
                    hijos.add(hijo);
                }
            }
            return hijos;
        }

        private void calcularHash() {
            this.hash = Arrays.deepHashCode(tablero);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof EstadoPuzzle)) return false;
            EstadoPuzzle otro = (EstadoPuzzle) o;
            return Arrays.deepEquals(this.tablero, otro.tablero);
        }

        @Override
        public int hashCode() {
            return hash;
        }

        public void calcularHeuristica(EstadoPuzzle meta) {
            costoH = 0;
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    int val = tablero[i][j];
                    if (val != 0) {
                        outerloop:
                        for (int m = 0; m < 3; m++) {
                            for (int n = 0; n < 3; n++) {
                                if (meta.tablero[m][n] == val) {
                                    costoH += Math.abs(i - m) + Math.abs(j - n);
                                    break outerloop;
                                }
                            }
                        }
                    }
                }
            }
        }

        @Override
        public int compareTo(EstadoPuzzle otro) {
            return Integer.compare(this.costoG + this.costoH, otro.costoG + otro.costoH);
        }
    }

    static class ResolverPuzzle {
        public static List<EstadoPuzzle> resolverAEstrella(EstadoPuzzle inicio, EstadoPuzzle meta) {
            PriorityQueue<EstadoPuzzle> abierta = new PriorityQueue<>();
            Set<EstadoPuzzle> cerrada = new HashSet<>();
            Set<EstadoPuzzle> enAbierta = new HashSet<>();

            inicio.costoG = 0;
            inicio.calcularHeuristica(meta);
            abierta.add(inicio);
            enAbierta.add(inicio);

            while (!abierta.isEmpty()) {
                EstadoPuzzle actual = abierta.poll();
                enAbierta.remove(actual);

                if (actual.equals(meta))
                    return reconstruirCamino(actual);

                cerrada.add(actual);

                for (EstadoPuzzle hijo : actual.generarHijos()) {
                    if (cerrada.contains(hijo)) continue;

                    hijo.calcularHeuristica(meta);

                    EstadoPuzzle existente = encontrarEnAbierta(hijo, enAbierta);
                    if (existente == null || hijo.costoG < existente.costoG) {
                        hijo.padre = actual;
                        if (existente != null) {
                            abierta.remove(existente);
                            enAbierta.remove(existente);
                        }
                        abierta.add(hijo);
                        enAbierta.add(hijo);
                    }
                }
            }
            return null;
        }

        private static EstadoPuzzle encontrarEnAbierta(EstadoPuzzle estado, Set<EstadoPuzzle> enAbierta) {
            for (EstadoPuzzle e : enAbierta) {
                if (e.equals(estado)) {
                    return e;
                }
            }
            return null;
        }

        private static List<EstadoPuzzle> reconstruirCamino(EstadoPuzzle objetivo) {
            LinkedList<EstadoPuzzle> camino = new LinkedList<>();
            EstadoPuzzle actual = objetivo;
            while (actual != null) {
                camino.addFirst(actual);
                actual = actual.padre;
            }
            return camino;
        }
    }
}