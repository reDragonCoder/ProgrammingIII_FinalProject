import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class GestorPuntuaciones {
    private static final String ARCHIVO = "puntuaciones.txt";
    private static final String SEPARADOR = ",";

    public static void guardarPuntuacion(String nombre, int movimientos) {
        if (nombre == null || nombre.trim().isEmpty() || movimientos <= 0) {
            System.err.println("No se guardará puntuación: datos inválidos");
            return;
        }

        try {
            Map<String, Integer> puntuaciones = cargarPuntuaciones();

            if (!puntuaciones.containsKey(nombre) || movimientos < puntuaciones.get(nombre)) {
                puntuaciones.put(nombre, movimientos);
                guardarTodasPuntuaciones(puntuaciones);
            }
        } catch (IOException e) {
            System.err.println("Error al guardar puntuación: " + e.getMessage());
        }
    }

    public static List<String> obtenerPuntuacionesOrdenadas() {
        try {
            Map<String, Integer> puntuaciones = cargarPuntuaciones();

            return puntuaciones.entrySet().stream()
                    .sorted(Map.Entry.comparingByValue())
                    .map(entry -> String.format("%s - %d movimientos", entry.getKey(), entry.getValue()))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            System.err.println("Error al leer puntuaciones: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public static int obtenerPuntuacionUsuario(String nombre) {
        try {
            Map<String, Integer> puntuaciones = cargarPuntuaciones();
            return puntuaciones.getOrDefault(nombre, -1);
        } catch (IOException e) {
            return -1;
        }
    }

    private static Map<String, Integer> cargarPuntuaciones() throws IOException {
        Map<String, Integer> puntuaciones = new HashMap<>();

        if (!new File(ARCHIVO).exists()) {
            return puntuaciones;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(ARCHIVO))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                try {
                    String[] partes = linea.split(SEPARADOR);
                    if (partes.length == 2) {
                        int movimientos = Integer.parseInt(partes[1].trim());
                        if (movimientos > 0) {
                            puntuaciones.put(partes[0].trim(), movimientos);
                        }
                    }
                } catch (NumberFormatException e) {
                    System.err.println("Formato inválido en línea: " + linea);
                }
            }
        }
        return puntuaciones;
    }

    private static void guardarTodasPuntuaciones(Map<String, Integer> puntuaciones) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(ARCHIVO))) {
            for (Map.Entry<String, Integer> entry : puntuaciones.entrySet()) {
                bw.write(entry.getKey() + SEPARADOR + entry.getValue());
                bw.newLine();
            }
        }
    }

    public static void limpiarPuntuacionesInvalidas() {
        try {
            Map<String, Integer> puntuaciones = cargarPuntuaciones();
            guardarTodasPuntuaciones(puntuaciones);
        } catch (IOException e) {
            System.err.println("Error al limpiar puntuaciones: " + e.getMessage());
        }
    }
}