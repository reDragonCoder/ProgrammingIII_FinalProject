import javax.swing.*;               // para la interfaz gráfica (JFrame, JButton, JOptionPane, etc.).
import java.awt.*;                  // para layouts y colores
import java.awt.event.*;            // para manejar los eventos
import java.util.PriorityQueue;     // para el metodo de branch and bound
import java.util.Set;               // para registrar los tableros ya visitados
import java.util.HashSet;           // ""
import java.util.List;              // para las listas de vecinos
import java.util.ArrayList;         // ""
import java.util.Arrays;            // para comparar los tableros


public class ModoManual extends JFrame { //ventana donde se mostrará el modo manual
    private JButton[][] botones = new JButton[3][3]; //arreglo 3x3 para las fichas
    private int[][] tablero = {
            {1, 2, 3},
            {4, 5, 6},
            {7, 8, 0} // 0 representa el espacio vacío
    };

    // Variables para las sugerencias
    private JButton sugerenciaActual = null; //para el botón que se está sugiriendo
    private Timer parpadeoTimer;    //parpadeo del boton sugerido
    private boolean resaltado = false;  //controla si esta encendido o apagado el parpadeo


    // Configuración de la ventana
    public ModoManual() {
        setTitle("8-Puzzle - Modo Manual");
        setSize(300, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Configuración del botón de sugerir movimiento
        JButton sugerenciaBtn = new JButton("Sugerir Movimiento"); // boton para las sugerencias
        sugerenciaBtn.addActionListener(e -> sugerirMovimientoBranchAndBound()); // evento que llama al metodo de sugerir
        add(sugerenciaBtn, BorderLayout.SOUTH); // se añade el boton en la parte de abajo


        // Configuración visual del tablero
        JPanel panel = new JPanel(new GridLayout(3, 3)); // Crea el panel para poner los botones
        Font fuente = new Font("Arial", Font.BOLD, 28); // añade caracteristicas


        // Crea los botones dentro del panel
        for (int i = 0; i < 3; i++) {  //guarda la posicion i y la j
            for (int j = 0; j < 3; j++) {
                botones[i][j] = new JButton();
                botones[i][j].setFont(fuente);
                int fila = i, col = j; // Necesario para el listener

                botones[i][j].addActionListener(e -> moverFicha(fila, col));
                panel.add(botones[i][j]);  //Cada botón, cuando se presiona, intenta mover su ficha mediante moverFicha(fila, col)
            }
        }

        mezclarTablero(100); // 100 movimientos aleatorios  validos
        actualizarTablero(); //actualiza visualmete el tablero

        add(panel); // añade el panel a la ventana
        setVisible(true); //lo hace visible
    }



    private void moverFicha(int i, int j) { // recibe como parámetros la posición (i, j) de la ficha que se quiere mover
        if (esMovible(i, j)) { // Llama al metodo esMovible(i, j) que verifica si la ficha está junto al espacio vacío (0)
            int i0 = obtenerFilaVacia();       // Se localiza la posición actual del espacio vacío dentro del tablero
            int j0 = obtenerColumnaVacia();    // i0 y j0 son las coordenadas de la celda que contiene el 0.

            // Intercambiar
            // la ficha seleccionada (i, j) se mueve al espacio vacío (i0, j0), y ese espacio ahora contiene 0.
            tablero[i0][j0] = tablero[i][j];
            tablero[i][j] = 0;

            // Si había una sugerencia activa (una ficha parpadeando) se elimina
            if (sugerenciaActual != null) {
                sugerenciaActual.setBackground(null);
                sugerenciaActual = null;
                if (parpadeoTimer != null) parpadeoTimer.stop();
            }

            // refresca la interfaz para que se vea el tablero después del movimiento.
            actualizarTablero();

            if (estaResuelto()) {
                JOptionPane.showMessageDialog(this, "¡Felicidades! Has resuelto el puzzle");
            }
        }
    }

    // Determina si una ficha se puede mover ( si está junto al espacio vacío).
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

    private void actualizarTablero() {
        for (int i = 0; i < 3; i++) {    //recorre todo el tablero y guarda el valor actual (valor) que puede ser un numero del 1 al 8 o el 0
            for (int j = 0; j < 3; j++) {
                int valor = tablero[i][j];
                botones[i][j].setText(valor == 0 ? "" : String.valueOf(valor)); //Si es 0, se muestra como una celda vacía (texto vacío "").
                botones[i][j].setBackground(valor == 0 ? Color.LIGHT_GRAY : null); // si es un numero muestr ese numero
            }
        }
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

    /*
       Ramificación: Explorar todos los movimientos posibles desde un estado actual.
       Poda (Bound): Eliminar caminos que no pueden mejorar la mejor solución encontrada hasta el momento (basado en heurísticas).
       La profundidad (número de movimientos hechos desde el inicio).
       El costo heurístico (una estimación de lo lejos que está de la solución).

       Distancia de Manhattan: Es una heurística que suma cuántos pasos horizontales y verticales le falta a cada ficha para llegar a su lugar
                                EJ. Si el 5 está en la posición (2,2) pero debe estar en (1,1), la distancia es |2-1| + |2-1| = 2.

       Fórmula utilizada: f(n) = g(n) + h(n)
            - g(n) = costo acumulado (número de movimientos hechos)
            - h(n) = costo estimado hasta la meta (por ejemplo, la distancia de Manhattan)

       Flujo del algoritmo:
            - Inicializa el nodo raíz con el tablero inicial.
            - Calcula f(n) para ese nodo.
            - Inserta el nodo en una cola de prioridad.
            - Mientras la cola no esté vacía:
                - Extrae el nodo con menor f(n).
                - Si el tablero está resuelto, listo
                - si no:
                    - Genera sus hijos (todos los movimientos válidos).
                    - Calcula f(n) para cada uno.
                    - Inserta los hijos en la cola.
            - Podar: Si un nodo tiene un f(n) mayor que la mejor solución conocida, no se explora.
    */
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
            sugerenciaActual.setBackground(null);
            sugerenciaActual = null;
        }

        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++)
                if (tablero[i][j] != sugerido[i][j] && tablero[i][j] != 0) {
                    sugerenciaActual = botones[i][j];
                    iniciarParpadeo(sugerenciaActual);
                    return;
                }
    }

    private void iniciarParpadeo(JButton boton) {
        Color original = boton.getBackground();
        Color resaltadoColor = Color.YELLOW;
        final int[] conteo = {0};
        final int maxParpadeos = 6; // 3 veces ON/OFF

        if (parpadeoTimer != null && parpadeoTimer.isRunning()) {
            parpadeoTimer.stop();
        }

        parpadeoTimer = new Timer(200, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (conteo[0] >= maxParpadeos) {
                    parpadeoTimer.stop();
                    boton.setBackground(resaltadoColor);
                } else {
                    boton.setBackground(resaltado ? original : resaltadoColor);
                    resaltado = !resaltado;
                    conteo[0]++;
                }
            }
        });
        parpadeoTimer.start();
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(ModoManual::new);
    }
}
