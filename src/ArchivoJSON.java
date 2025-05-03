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

    public void guardarMelodia(ListaLigada lista, String nombreArchivo) {
        try {
            List<Nota> notas = lista.obtenerNotas();
            objectMapper.writeValue(new File(nombreArchivo), notas);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ListaLigada cargarMelodia(String nombreArchivo) {
        ListaLigada lista = new ListaLigada();
        try {
            List<Nota> notas = objectMapper.readValue(new File(nombreArchivo), new TypeReference<List<Nota>>() {});
            for (Nota nota : notas) {
                lista.agregarNota(nota);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lista;
    }
}