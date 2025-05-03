import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.io.*;
import java.util.List;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import javax.sound.midi.*;

public class FrmEditorDeMelodias extends JFrame {
    private DefaultTableModel model;
    private JComboBox<String> cbNotas;
    private JComboBox<String> cbFiguras;
    private JComboBox<String> cbOctavas;
    private ListaLigada listaLigada;
    private JTable table;

    public FrmEditorDeMelodias() {
        setTitle("Editor de Melodías");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 400);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        listaLigada = new ListaLigada();

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 4));
        toolbar.setBackground(new Color(240, 240, 240));
        toolbar.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));

        JButton btnAbrir = createIconButton("/img/carpeta.png", "Abrir Melodía");
        JButton btnGuardar = createIconButton("/img/memoria.png", "Guardar Melodía");
        JButton btnAgregarNota = createIconButton("/img/nota.jpg", "Agregar Nota");
        JButton btnEliminarNota = createIconButton("/img/eliminar.png", "Eliminar Nota");
        JButton btnPiano = createIconButton("/img/sonido.jpg", "Reproducir Melodía");

        toolbar.add(btnAbrir);
        toolbar.add(btnGuardar);
        toolbar.add(btnAgregarNota);

        cbNotas = new JComboBox<>();
        cbNotas.setPreferredSize(new Dimension(70, 30));
        toolbar.add(cbNotas);

        cbFiguras = new JComboBox<>();
        cbFiguras.setPreferredSize(new Dimension(90, 30));
        toolbar.add(cbFiguras);

        cbOctavas = new JComboBox<>();
        cbOctavas.setPreferredSize(new Dimension(50, 30));
        toolbar.add(cbOctavas);

        toolbar.add(btnEliminarNota);
        toolbar.add(btnPiano);

        add(toolbar, BorderLayout.NORTH);

        model = new DefaultTableModel();
        table = new JTable(model);
        model.addColumn("Nota");
        model.addColumn("Figura");
        model.addColumn("Octava");

        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.setRowHeight(22);
        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.getTableHeader().setBackground(new Color(240, 240, 240));
        table.getTableHeader().setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(680, 250));
        add(scrollPane, BorderLayout.CENTER);

        inicializarCombos();

        btnAgregarNota.addActionListener(e -> agregarNota());
        btnEliminarNota.addActionListener(e -> eliminarNota());
        btnPiano.addActionListener(e -> reproducirMelodia());
        btnGuardar.addActionListener(e -> guardarMelodia());
        btnAbrir.addActionListener(e -> cargarMelodia());

        setVisible(true);
    }

    private void inicializarCombos() {
        String[] notas = {"DO", "RE", "MI", "FA", "SOL", "LA", "SI"};
        for (String nota : notas)
            cbNotas.addItem(nota);

        String[] figuras = {"REDONDA", "BLANCA", "NEGRA", "CORCHEA"};
        for (String figura : figuras)
            cbFiguras.addItem(figura);

        for (int i = 1; i <= 7; i++)
            cbOctavas.addItem(String.valueOf(i));
    }

    private void agregarNota() {
        String nota = (String) cbNotas.getSelectedItem();
        String figura = (String) cbFiguras.getSelectedItem();
        String octavaStr = (String) cbOctavas.getSelectedItem();
        if (nota == null || figura == null || octavaStr == null)
            return;
        int octava = Integer.parseInt(octavaStr);
        Nota nueva = new Nota(nota, figura, octava);

        listaLigada.agregarNota(nueva);
        model.addRow(new Object[]{nota, figura, octavaStr});
    }

    private void eliminarNota() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            model.removeRow(selectedRow);
            listaLigada.eliminarNota(selectedRow);
        } else {
            JOptionPane.showMessageDialog(this, "Seleccione una fila para eliminar.", "Aviso",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void reproducirMelodia() {
        new Thread(() -> {
            try {
                Synthesizer synth = MidiSystem.getSynthesizer();
                synth.open();
                MidiChannel[] channels = synth.getChannels();
                MidiChannel channel = channels[0];
                Instrument[] instruments = synth.getDefaultSoundbank().getInstruments();
                synth.loadInstrument(instruments[0]); // Piano sound

                List<Nota> notas = listaLigada.obtenerNotas();
                for (Nota nota : notas) {
                    int midiNote = notaToMidi(nota.getNota(), nota.getOctava());
                    int duration = figuraToDuration(nota.getFigura());
                    if (midiNote >= 0) {
                        channel.noteOn(midiNote, 100);
                        Thread.sleep(duration);
                        channel.noteOff(midiNote);
                    } else {
                        Thread.sleep(duration);
                    }
                }
                synth.close();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error al reproducir: " + ex.getMessage());
            }
        }).start();
    }

    private int notaToMidi(String nota, int octava) {
        int baseOctave = 12;
        int octaveOffset = (octava * 12);
        switch (nota.toUpperCase()) {
        case "DO":
            return baseOctave + octaveOffset;
        case "RE":
            return baseOctave + 2 + octaveOffset;
        case "MI":
            return baseOctave + 4 + octaveOffset;
        case "FA":
            return baseOctave + 5 + octaveOffset;
        case "SOL":
            return baseOctave + 7 + octaveOffset;
        case "LA":
            return baseOctave + 9 + octaveOffset;
        case "SI":
            return baseOctave + 11 + octaveOffset;
        default:
            return -1;
        }
    }

    private int figuraToDuration(String figura) {
        switch (figura.toUpperCase()) {
        case "REDONDA":
            return 2000;
        case "BLANCA":
            return 1000;
        case "NEGRA":
            return 500;
        case "CORCHEA":
            return 250;
        default:
            return 500;
        }
    }

    private void guardarMelodia() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Guardar melodía (JSON)");

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            if (!file.getName().toLowerCase().endsWith(".json")) {
                file = new File(file.getParentFile(), file.getName() + ".json");
            }

            try (FileWriter writer = new FileWriter(file)) {
                ObjectMapper mapper = new ObjectMapper();
                mapper.writeValue(writer, listaLigada.obtenerNotas());
                JOptionPane.showMessageDialog(this, "Melodía guardada correctamente.");
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error al guardar: " + e.getMessage());
            }
        }
    }

    private void cargarMelodia() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Cargar melodía (JSON)");

        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();

            try (FileReader reader = new FileReader(file)) {
                ObjectMapper mapper = new ObjectMapper();
                List<Nota> notas = mapper.readValue(reader, new TypeReference<List<Nota>>() {
                });

                listaLigada.limpiarNotas();
                model.setRowCount(0);
                for (Nota nota : notas) {
                    listaLigada.agregarNota(nota);
                    model.addRow(new Object[] { nota.getNota(), nota.getFigura(), nota.getOctava() });
                }
                JOptionPane.showMessageDialog(this, "Melodía cargada correctamente.");
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error al cargar: " + e.getMessage());
            }
        }
    }

    private JButton createIconButton(String imagePath, String tooltip) {
        ImageIcon icon = null;
        try {
            java.net.URL imgURL = getClass().getResource(imagePath);
            if (imgURL != null) {
                ImageIcon originalIcon = new ImageIcon(imgURL);
                // Escalar la imagen al tamaño del botón (36x36)
                Image scaledImage = originalIcon.getImage().getScaledInstance(36, 36, Image.SCALE_SMOOTH);
                icon = new ImageIcon(scaledImage);
            } else {
                System.err.println("No se pudo encontrar el archivo: " + imagePath);
            }
        } catch (Exception e) {
            System.err.println("Error cargando icono: " + e.getMessage());
        }
        JButton button = (icon != null) ? new JButton(icon) : new JButton();
        button.setPreferredSize(new Dimension(36, 36));
        button.setToolTipText(tooltip);
        button.setFocusPainted(false);
        button.setBackground(Color.WHITE);
        button.setBorder(BorderFactory.createLineBorder(new Color(160, 160, 160)));
        return button;
    }
}

        