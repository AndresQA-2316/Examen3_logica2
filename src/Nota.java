
public class Nota {
    public String nota;   // Asegúrate de que el nombre coincida con el JSON
    public String figura;  // Asegúrate de que el nombre coincida con el JSON
    public int octava; 

    public Nota() {}  // Constructor sin argumentos necesario para Jackson

    public Nota(String nota, String figura, int octava) {
        this.nota = nota;
        this.figura = figura;
        this.octava = octava;
    }

    public String getNota() {
        return nota;
    }

    public void setNota(String nota) {
        this.nota = nota;
    }

    public String getFigura() {
        return figura;
    }

    public void setFigura(String figura) {
        this.figura = figura;
    }

    public int getOctava() {
        return octava;
    }

    public void setOctava(int octava) {
        this.octava = octava;
    }
}