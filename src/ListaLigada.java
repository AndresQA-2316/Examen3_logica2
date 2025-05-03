import java.util.ArrayList;
import java.util.List;

public class ListaLigada {
    private Nodo cabeza; 

    private class Nodo {
        Nota dato;
        Nodo siguiente;

        Nodo(Nota dato) {
            this.dato = dato;
            this.siguiente = null;
        }
    }

    public void agregarNota(Nota nota) {
        Nodo nuevo = new Nodo(nota);
        if (cabeza == null) {
            cabeza = nuevo;
        } else {
            Nodo actual = cabeza;
            while (actual.siguiente != null) {
                actual = actual.siguiente;
            }
            actual.siguiente = nuevo;
        }
    }

    public List<Nota> obtenerNotas() {
        List<Nota> notas = new ArrayList<>();
        Nodo actual = cabeza;  // Esto es válido solo dentro de ListaLigada
        while (actual != null) {
            notas.add(actual.dato);
            actual = actual.siguiente;
        }
        return notas;
    }

    public void eliminarNota(int indice) {
    }

    public void limpiar() {
        cabeza = null;
    }

    public void limpiarNotas() {
    }
}