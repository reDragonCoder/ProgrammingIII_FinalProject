import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import javax.swing.border.*;

public class ModoManual extends JFrame {
    private JButton[][] botones = new JButton[3][3];
    private int[][] tablero = {
            {1, 2, 3},
            {4, 5, 6},
            {7, 8, 0}
    };
    private int movimientosJugador = 0;
    private String nombreUsuario;
    private JLabel lblMovimientos;

    // Variables para las sugerencias
    private JButton sugerenciaActual = null;
    private Timer parpadeoTimer;
    private boolean resaltado = false;

    public ModoManual(String nombreUsuario, JFrame parent) {
        this.nombreUsuario = nombreUsuario;

        setTitle("8-Puzzle - Modo Manual");
        setSize(400, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(parent);

        // Panel principal con fondo degradado
        JPanel panelPrincipal = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                Color colorInicio = new Color(230, 240, 250);
                Color colorFin = new Color(200, 220, 240);
                g2d.setPaint(new GradientPaint(0, 0, colorInicio, getWidth(), getHeight(), colorFin));
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        panelPrincipal.setBorder(new EmptyBorder(15, 15, 15, 15));

        // Panel del tablero
        JPanel panelTablero = new JPanel(new GridLayout(3, 3, 5, 5));
        panelTablero.setOpaque(false);
        panelTablero.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Fuente para los números
        Font fuenteNumeros = new Font("Arial", Font.BOLD, 36);

        // Crear botones del tablero con estilo mejorado
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                botones[i][j] = new JButton();
                botones[i][j].setFont(fuenteNumeros);
                botones[i][j].setFocusPainted(false);
                botones[i][j].setBorder(BorderFactory.createRaisedBevelBorder());

                // Estilo para botones normales
                botones[i][j].setBackground(new Color(255, 255, 255));
                botones[i][j].setForeground(new Color(70, 130, 180));

                int fila = i, col = j;
                botones[i][j].addActionListener(e -> moverFicha(fila, col));
                panelTablero.add(botones[i][j]);
            }
        }

        // Panel de control inferior
        JPanel panelControl = new JPanel(new BorderLayout());
        panelControl.setOpaque(false);
        panelControl.setBorder(new EmptyBorder(10, 20, 10, 20));

        // Crear un nuevo panes para los botones de abajo
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 5));
        panelBotones.setOpaque(false);

        // Contador de movimientos
        lblMovimientos = new JLabel("Movimientos: 0");
        lblMovimientos.setFont(new Font("Arial", Font.BOLD, 14));
        lblMovimientos.setForeground(new Color(70, 70, 70));
        lblMovimientos.setHorizontalAlignment(SwingConstants.CENTER);
        panelControl.add(lblMovimientos, BorderLayout.NORTH);

        // Botón de sugerencia
        JButton btnSugerencia = new JButton("Sugerir Movimiento");
        btnSugerencia.setFont(new Font("Arial", Font.BOLD, 12));
        btnSugerencia.setBackground(new Color(255, 215, 0));
        btnSugerencia.setForeground(Color.BLACK);
        btnSugerencia.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(230, 175, 0)),
                new EmptyBorder(8, 15, 8, 15)
        ));
        btnSugerencia.addActionListener(e -> sugerirMovimientoBranchAndBound());

        // Efecto hover para el botón
        btnSugerencia.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                btnSugerencia.setBackground(new Color(255, 230, 100));
            }
            public void mouseExited(MouseEvent e) {
                btnSugerencia.setBackground(new Color(255, 215, 0));
            }
        });

        // Botón para volver al menu
        JButton btnVolver = new JButton("Volver al Menú");
        btnVolver.setFont(new Font("Arial", Font.BOLD, 12));
        btnVolver.setBackground(new Color(255, 215, 0));
        btnVolver.setForeground(Color.BLACK);
        btnVolver.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(230, 175, 0)),
                new EmptyBorder(8, 15, 8, 15)
        ));
        btnVolver.addActionListener(e -> volverAlMenuPrincipal());

        // Efecto hover para el botón
        btnVolver.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                btnVolver.setBackground(new Color(255, 230, 100));
            }
            public void mouseExited(MouseEvent e) {
                btnVolver.setBackground(new Color(255, 215, 0));
            }
        });


        // Añadir los botones al panel de botones
        panelBotones.add(btnSugerencia);
        panelBotones.add(btnVolver);

        // Añadir el panel de botones
        panelControl.add(panelBotones, BorderLayout.CENTER);

        // Ensamblar la interfaz
        panelPrincipal.add(panelTablero, BorderLayout.CENTER);
        panelPrincipal.add(panelControl, BorderLayout.SOUTH);
        add(panelPrincipal);

        // Mezclar y mostrar el tablero
        mezclarTablero(100);
        actualizarTablero();
        setVisible(true);
    }

    private void actualizarTablero() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                int valor = tablero[i][j];
                botones[i][j].setText(valor == 0 ? "" : String.valueOf(valor));

                // Estilo para la celda vacía
                if (valor == 0) {
                    botones[i][j].setBackground(new Color(240, 240, 240));
                    botones[i][j].setBorder(BorderFactory.createLoweredBevelBorder());
                } else {
                    botones[i][j].setBackground(new Color(255, 255, 255));
                    botones[i][j].setBorder(BorderFactory.createRaisedBevelBorder());
                }
            }
        }
        lblMovimientos.setText("Movimientos: " + movimientosJugador);
    }

    private void volverAlMenuPrincipal() {
        this.dispose();
        new Menu_Principal(nombreUsuario);
    }

    private void moverFicha(int i, int j) {
        if (esMovible(i, j)) {
            int i0 = obtenerFilaVacia();
            int j0 = obtenerColumnaVacia();

            tablero[i0][j0] = tablero[i][j];
            tablero[i][j] = 0;

            if (sugerenciaActual != null) {
                sugerenciaActual.setBackground(null);
                sugerenciaActual = null;
                if (parpadeoTimer != null) parpadeoTimer.stop();
            }

            movimientosJugador++;
            actualizarTablero();

            if (estaResuelto()) {
                // panel personalizado para el mensaje
                JPanel panelMensaje = new JPanel(new BorderLayout());
                panelMensaje.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
                panelMensaje.setBackground(new Color(240, 248, 255)); // Fondo azul claro

                // Título
                JLabel titulo = new JLabel("¡Felicidades " + nombreUsuario + "!", JLabel.CENTER);
                titulo.setFont(new Font("Arial", Font.BOLD, 20));
                titulo.setForeground(new Color(0, 100, 0)); // Verde oscuro
                titulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

                // Mensaje principal con HTML para formato
                JLabel mensaje = new JLabel(
                        "<html><div style='text-align: center;'>" +
                                "¡Has resuelto el puzzle en <b>" + movimientosJugador + "</b> movimientos!<br><br>" +
                                "Tu puntuación ha sido guardada." +
                                "</div></html>",
                        JLabel.CENTER
                );
                mensaje.setFont(new Font("Arial", Font.BOLD, 14));

                // Panel para el botón
                JPanel panelBoton = new JPanel();
                panelBoton.setOpaque(false);
                panelBoton.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

                JButton btnAceptar = new JButton("Aceptar");
                btnAceptar.setFont(new Font("Arial", Font.BOLD, 12));
                btnAceptar.setBackground(new Color(70, 130, 180));
                btnAceptar.setForeground(Color.WHITE);
                btnAceptar.setFocusPainted(false);
                btnAceptar.setBorder(BorderFactory.createCompoundBorder(
                        new LineBorder(new Color(50, 100, 150), 1),
                        new EmptyBorder(5, 15, 5, 15)
                ));
                btnAceptar.addActionListener(e -> {
                    GestorPuntuaciones.guardarPuntuacion(nombreUsuario, movimientosJugador);
                    volverAlMenuPrincipal();
                });

                // Efecto hover para el botón
                btnAceptar.addMouseListener(new MouseAdapter() {
                    public void mouseEntered(MouseEvent e) {
                        btnAceptar.setBackground(new Color(100, 150, 200));
                    }
                    public void mouseExited(MouseEvent e) {
                        btnAceptar.setBackground(new Color(70, 130, 180));
                    }
                });

                // Ensamblar el mensaje
                panelMensaje.add(titulo, BorderLayout.NORTH);
                panelMensaje.add(mensaje, BorderLayout.CENTER);
                panelBoton.add(btnAceptar);
                panelMensaje.add(panelBoton, BorderLayout.SOUTH);

                // Mostrar el diálogo personalizado
                JOptionPane optionPane = new JOptionPane(
                        panelMensaje,
                        JOptionPane.PLAIN_MESSAGE,
                        JOptionPane.DEFAULT_OPTION,
                        null,
                        new Object[]{},
                        null
                );

                JDialog dialog = optionPane.createDialog(this, "¡Nivel completado!");
                dialog.setVisible(true);
            }
        }
    }

    // Determina si una ficha se puede mover (si está junto al espacio vacío).
    private boolean esMovible(int i, int j) {
        int i0 = obtenerFilaVacia();
        int j0 = obtenerColumnaVacia();
        // Usa la distancia de Manhattan para saber si la ficha está justo al lado del espacio vacío
        return (Math.abs(i - i0) + Math.abs(j - j0)) == 1;
        //(i - i0) y (j - j0) calculan la diferencia de posición.
        //Lo convierte en valor absoluto
        //Si la suma da 1, están en celdas vecinas y se puede mover.
    }

    // Encuentra la fila donde está el número 0 (espacio vacío).
    private int obtenerFilaVacia() {
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++)
                if (tablero[i][j] == 0) return i;
        return -1;
        /*Recorre cada celda del tablero, cuando encuentra el cero devuelve su posicion i*/
    }

    // igual que el metodo anterior pero con las columnas
    private int obtenerColumnaVacia() {
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++)
                if (tablero[i][j] == 0) return j;
        return -1;
    }


    // mezcla el tablero realizando una cantidad dada de movimientos aleatorios válidos, simulando desplazamientos reales del juego.
    private void mezclarTablero(int movimientos) {
        for (int k = 0; k < movimientos; k++) {
            // localiza el cero
            int i0 = obtenerFilaVacia();
            int j0 = obtenerColumnaVacia();

            
            // Posibles movimientos válidos desde la posición vacía
            int[][] posibles = {
                    {i0 - 1, j0}, // arriba
                    {i0 + 1, j0}, // abajo
                    {i0, j0 - 1}, // izquierda
                    {i0, j0 + 1}  // derecha
            };

            // Seleccionar uno aleatorio que esté dentro del tablero
            java.util.List<int[]> validos = new java.util.ArrayList<>(); //Crea una lista de movimientos válidos dentro del rango 3x3.
            for (int[] pos : posibles) {
                int i = pos[0], j = pos[1];
                if (i >= 0 && i < 3 && j >= 0 && j < 3) // Evita errores por índices fuera de límites.
                    validos.add(pos);
            }

            if (!validos.isEmpty()) {
                int[] seleccion = validos.get((int) (Math.random() * validos.size())); // Selecciona una dirección aleatoria de las válidas.
                int i = seleccion[0], j = seleccion[1];
                // Realiza el intercambio entre el espacio vacío y la ficha elegida, simulando un movimiento y cambia el estado del tablero
                tablero[i0][j0] = tablero[i][j];
                tablero[i][j] = 0;
            }
        }
    }

    private boolean estaResuelto() {
        int[][] solucion = { // Define el estado final correcto del juego.
                {1, 2, 3},
                {4, 5, 6},
                {7, 8, 0}
        };

        for (int i = 0; i < 3; i++) // Recorre el tablero comparando celda por celda con la solución.
            for (int j = 0; j < 3; j++)
                if (tablero[i][j] != solucion[i][j])
                    return false;
        return true;
    }


    private void sugerirMovimientoBranchAndBound() {
        PriorityQueue<NodoPuzzle> cola = new PriorityQueue<>(); // una cola de prioridad que siempre extrae el nodo con menor costo f(n) = g(n) + h(n).
        Set<String> visitados = new HashSet<>(); // guarda los tableros ya explorados para evitar ciclos.

        NodoPuzzle nodoInicial = new NodoPuzzle(tablero, 0, null);  // Crea el nodo inicial con nivel (profundidad) 0 y sin padre.
        cola.add(nodoInicial);                                                  // se agrega a la cola y se marca como visitado
        visitados.add(Arrays.deepToString(tablero));

        while (!cola.isEmpty()) { // Mientras haya nodos por explorar, se extrae el mejor (con menor costo estimado total f(n)).
            NodoPuzzle actual = cola.poll();

            if (esMeta(actual.tablero)) {  // Si el tablero está en el estado final (resuelto), retrocede por los padres para hallar el primer movimiento de la ruta.
                // Se localiza el tablero inmediatamente siguiente al actual, y se llama a sugerirDiferencia() para marcar visualmente el botón.
                NodoPuzzle primero = actual;
                while (primero.padre != null && primero.padre.padre != null) {
                    primero = primero.padre;
                }
                sugerirDiferencia(primero.tablero);
                return;
            }




            // Generación de nodos hijos (movimientos posibles):
            for (int[][] vecino : generarVecinos(actual.tablero)) {        // Por cada tablero vecino (moviendo la casilla vacía), se evita repetir tableros ya explorados
                String clave = Arrays.deepToString(vecino);                // calcula el nuevo nodo hijo y lo añade a la cola.
                if (!visitados.contains(clave)) {
                    visitados.add(clave);
                    cola.add(new NodoPuzzle(vecino, actual.nivel + 1, actual));
                }
            }
        }
        // Si no se encuentra solucion
        JOptionPane.showMessageDialog(this, "No se encontró una sugerencia.");
    }

    private boolean esMeta(int[][] tablero) { // Verifica si el tablero es igual al de la solución.
        int[][] solucion = {{1,2,3}, {4,5,6}, {7,8,0}};
        return Arrays.deepEquals(tablero, solucion);
    }

    private List<int[][]> generarVecinos(int[][] estado) { // Devuelve una lista de tableros resultantes de mover la ficha vacía en las 4 direcciones válidas (arriba, abajo, izquierda, derecha).
        List<int[][]> vecinos = new ArrayList<>();
        int i0 = -1, j0 = -1;

        // Buscar espacio vacío
        outer:
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++)
                if (estado[i][j] == 0) {
                    i0 = i; j0 = j;
                    break outer;
                }

        int[][] direcciones = {{-1,0},{1,0},{0,-1},{0,1}};
        for (int[] dir : direcciones) {
            int i = i0 + dir[0];
            int j = j0 + dir[1];
            if (i >= 0 && i < 3 && j >= 0 && j < 3) {
                int[][] nuevo = copiar(estado);
                nuevo[i0][j0] = nuevo[i][j];
                nuevo[i][j] = 0;
                vecinos.add(nuevo);
            }
        }
        return vecinos;
    }


    class NodoPuzzle implements Comparable<NodoPuzzle> { //Representa un nodo del árbol de búsqueda.
        int[][] tablero; //estado actual del tablero
        int nivel; // profundidad (g)
        int costo; // f(n) = g(n) + h(n)
        NodoPuzzle padre; // referencia al nodo anterior.

        public NodoPuzzle(int[][] tablero, int nivel, NodoPuzzle padre) {
            this.tablero = copiar(tablero);
            this.nivel = nivel;
            this.padre = padre;
            this.costo = nivel + calcularHeuristica();
        }

        // Distancia de Manhattan total de cada ficha a su posición objetivo
        private int calcularHeuristica() {
            int dist = 0;
            for (int i = 0; i < 3; i++)
                for (int j = 0; j < 3; j++) {
                    int val = tablero[i][j];
                    if (val != 0) {
                        int ti = (val - 1) / 3;
                        int tj = (val - 1) % 3;
                        dist += Math.abs(i - ti) + Math.abs(j - tj);
                    }
                }
            return dist;
        }

        // crea una copia profunda de un tablero 3x3, evitando modificar el original:
        private int[][] copiar(int[][] original) {
            int[][] copia = new int[3][3];
            for (int i = 0; i < 3; i++)
                copia[i] = Arrays.copyOf(original[i], 3);
            return copia;
        }

        // Permite que la PriorityQueue ordene los nodos por su costo total estimado f(n), favoreciendo los más prometedores.
        @Override
        public int compareTo(NodoPuzzle otro) {
            return Integer.compare(this.costo, otro.costo);
        }

        // Indica que dos nodos son iguales si su contenido (el tablero) es igual.
        @Override
        public boolean equals(Object o) {
            if (!(o instanceof NodoPuzzle)) return false;
            return Arrays.deepEquals(this.tablero, ((NodoPuzzle) o).tablero);
        }

        // Permite que objetos NodoPuzzle funcionen correctamente como claves en conjuntos (HashSet) o mapas (HashMap).
        @Override
        public int hashCode() {
            return Arrays.deepHashCode(tablero);
        }
    }


    private int[][] copiar(int[][] original) { //para el arrayList
        int[][] copia = new int[3][3];
        for (int i = 0; i < 3; i++)
            copia[i] = Arrays.copyOf(original[i], 3);
        return copia;
    }

    private void sugerirDiferencia(int[][] sugerido) {
        if (sugerenciaActual != null) {
            sugerenciaActual.setBackground(Color.WHITE);
            sugerenciaActual = null;
            if (parpadeoTimer != null) parpadeoTimer.stop();
        }

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (tablero[i][j] != sugerido[i][j] && tablero[i][j] != 0) {
                    sugerenciaActual = botones[i][j];
                    iniciarParpadeo(sugerenciaActual);
                    return;
                }
            }
        }
    }

    // Metodo iniciarParpadeo con efecto mejorado
    private void iniciarParpadeo(JButton boton) {
        Color original = boton.getBackground();
        Color resaltadoColor = new Color(255, 255, 150); // Amarillo claro

        if (parpadeoTimer != null && parpadeoTimer.isRunning()) {
            parpadeoTimer.stop();
        }

        parpadeoTimer = new Timer(300, new ActionListener() {
            int conteo = 0;
            final int maxParpadeos = 8; // 4 veces ON/OFF

            @Override
            public void actionPerformed(ActionEvent e) {
                if (conteo >= maxParpadeos) {
                    parpadeoTimer.stop();
                    boton.setBackground(new Color(255, 255, 180));
                } else {
                    boton.setBackground(resaltado ? original : resaltadoColor);
                    resaltado = !resaltado;
                    conteo++;
                }
            }
        });
        parpadeoTimer.start();
    }

}
