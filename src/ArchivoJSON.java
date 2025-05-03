import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class ArchivoJSON {
    private ObjectMapper objectMapper;

    public ArchivoJSON() {
        this.objectMapper = new ObjectMapper();
    }

    // Guarda la lista de notas de la ListaLigada a un archivo JSON
    public void guardarMelodia(ListaLigada lista, String nombreArchivo) {
        try {
            // Obtenemos la lista de notas mediante el método público obtenerNotas()
            List<Nota> notas = lista.obtenerNotas();
            // Escribimos la lista de notas en el archivo JSON
            objectMapper.writeValue(new File(nombreArchivo), notas);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Carga una lista de notas desde un archivo JSON y la devuelve como ListaLigada
    public ListaLigada cargarMelodia(String nombreArchivo) {
        ListaLigada lista = new ListaLigada();
        try {
            // Leemos la lista de notas desde el archivo JSON
            List<Nota> notas = objectMapper.readValue(new File(nombreArchivo), new TypeReference<List<Nota>>() {});
            // Agregamos cada nota a la lista ligada
            for (Nota nota : notas) {
                lista.agregarNota(nota);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lista;
    }
}