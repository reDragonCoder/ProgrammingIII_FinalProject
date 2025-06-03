import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import javax.swing.Timer;

class EstadoPuzzle implements Comparable<EstadoPuzzle> {
    public int[][] tablero;
    public int filaCero, colCero;
    public EstadoPuzzle padre;
    public int costoG;
    public int costoH;

    public EstadoPuzzle(int[][] tablero) {
        this.tablero = new int[3][3];
        for (int i = 0; i < 3; i++)
            this.tablero[i] = tablero[i].clone();
        encontrarCero();
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
        List<EstadoPuzzle> hijos = new ArrayList<>();
        int[][] direcciones = {{-1,0}, {1,0}, {0,-1}, {0,1}};
        for (int[] dir : direcciones) {
            int nuevaFila = filaCero + dir[0];
            int nuevaCol = colCero + dir[1];
            if (nuevaFila >= 0 && nuevaFila < 3 && nuevaCol >= 0 && nuevaCol < 3) {
                int[][] nuevoTablero = copiarTablero();
                nuevoTablero[filaCero][colCero] = nuevoTablero[nuevaFila][nuevaCol];
                nuevoTablero[nuevaFila][nuevaCol] = 0;
                EstadoPuzzle hijo = new EstadoPuzzle(nuevoTablero);
                hijo.padre = this;
                hijos.add(hijo);
            }
        }
        return hijos;
    }

    public int[][] copiarTablero() {
        int[][] copia = new int[3][3];
        for (int i = 0; i < 3; i++)
            copia[i] = tablero[i].clone();
        return copia;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof EstadoPuzzle)) return false;
        EstadoPuzzle otro = (EstadoPuzzle) o;
        return Arrays.deepEquals(this.tablero, otro.tablero);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(tablero);
    }

    public void calcularHeuristica(EstadoPuzzle meta) {
        costoH = 0;
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++) {
                int val = tablero[i][j];
                if (val != 0) {
                    for (int m = 0; m < 3; m++)
                        for (int n = 0; n < 3; n++)
                            if (meta.tablero[m][n] == val)
                                costoH += Math.abs(i - m) + Math.abs(j - n);
                }
            }
    }

    @Override
    public int compareTo(EstadoPuzzle otro) {
        return Integer.compare(this.costoG + this.costoH, otro.costoG + otro.costoH);
    }
}

class ResolverPuzzle {
    public static List<EstadoPuzzle> resolverAEstrella(EstadoPuzzle inicio, EstadoPuzzle meta) {
        PriorityQueue<EstadoPuzzle> abierta = new PriorityQueue<>();
        Set<EstadoPuzzle> cerrada = new HashSet<>();

        inicio.costoG = 0;
        inicio.calcularHeuristica(meta);
        abierta.add(inicio);

        while (!abierta.isEmpty()) {
            EstadoPuzzle actual = abierta.poll();
            if (actual.equals(meta))
                return reconstruirCamino(actual);

            cerrada.add(actual);

            for (EstadoPuzzle hijo : actual.generarHijos()) {
                if (cerrada.contains(hijo)) continue;

                hijo.costoG = actual.costoG + 1;
                hijo.calcularHeuristica(meta);
                hijo.padre = actual;

                if (!abierta.contains(hijo)) {
                    abierta.add(hijo);
                }
            }
        }
        return null;
    }

    private static List<EstadoPuzzle> reconstruirCamino(EstadoPuzzle objetivo) {
        List<EstadoPuzzle> camino = new ArrayList<>();
        EstadoPuzzle actual = objetivo;
        while (actual != null) {
            camino.add(actual);
            actual = actual.padre;
        }
        Collections.reverse(camino);
        return camino;
    }
}

public class ModoInteligente extends JFrame {
    private JPanel tableroPanel;
    private List<EstadoPuzzle> solucion;
    private int pasoActual = 0;
    private Timer animador;

    public ModoInteligente(EstadoPuzzle estadoInicial, EstadoPuzzle estadoMeta) {
        setTitle("8 Puzzle - Modo Inteligente");
        setSize(400, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        if (!esSolucionable(estadoInicial.tablero) || !esSolucionable(estadoMeta.tablero) ||
                esSolucionable(estadoInicial.tablero) != esSolucionable(estadoMeta.tablero)) {
            JOptionPane.showMessageDialog(this, "Estado inicial y meta no son compatibles. No hay solución.");
            System.exit(0);
        }

        solucion = ResolverPuzzle.resolverAEstrella(estadoInicial, estadoMeta);
        if (solucion == null) {
            JOptionPane.showMessageDialog(this, "No se pudo encontrar una solución.");
            System.exit(0);
        }

        tableroPanel = new JPanel(new GridLayout(3, 3));
        add(tableroPanel, BorderLayout.CENTER);

        animador = new Timer(1000, e -> mostrarPaso());
        animador.setInitialDelay(0);
        animador.start();

        setVisible(true);
    }

    private void mostrarPaso() {
        if (pasoActual >= solucion.size()) {
            animador.stop();
            JOptionPane.showMessageDialog(this, "¡Puzzle resuelto!");
            return;
        }

        EstadoPuzzle estado = solucion.get(pasoActual++);
        tableroPanel.removeAll();
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++) {
                String val = estado.tablero[i][j] == 0 ? "" : String.valueOf(estado.tablero[i][j]);
                JLabel label = new JLabel(val, SwingConstants.CENTER);
                label.setFont(new Font("Arial", Font.BOLD, 32));
                label.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                tableroPanel.add(label);
            }

        tableroPanel.revalidate();
        tableroPanel.repaint();
    }

    private static EstadoPuzzle pedirEstado(String titulo) {
        JTextField[] campos = new JTextField[9];
        JPanel panel = new JPanel(new GridLayout(3, 3));
        for (int i = 0; i < 9; i++) {
            campos[i] = new JTextField(1);
            panel.add(campos[i]);
        }

        int result = JOptionPane.showConfirmDialog(null, panel, titulo, JOptionPane.OK_CANCEL_OPTION);
        if (result != JOptionPane.OK_OPTION) return null;

        int[][] matriz = new int[3][3];
        Set<Integer> numeros = new HashSet<>();
        for (int i = 0; i < 9; i++) {
            try {
                int num = Integer.parseInt(campos[i].getText());
                if (num < 0 || num > 8 || !numeros.add(num)) {
                    JOptionPane.showMessageDialog(null, "Ingresa números únicos del 0 al 8.");
                    return null;
                }
                matriz[i / 3][i % 3] = num;
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Entrada inválida. Solo números.");
                return null;
            }
        }

        return new EstadoPuzzle(matriz);
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

    public ModoInteligente() {
        this(pedirEstado("Estado Inicial"), pedirEstado("Estado Meta"));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            EstadoPuzzle estadoInicial = null, estadoMeta = null;
            while (estadoInicial == null) estadoInicial = pedirEstado("Estado Inicial");
            while (estadoMeta == null) estadoMeta = pedirEstado("Estado Meta");
            new ModoInteligente(estadoInicial, estadoMeta);
        });
    }
}
