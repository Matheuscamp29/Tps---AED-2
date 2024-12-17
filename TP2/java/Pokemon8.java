import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Pokemon8 implements Cloneable {
    int id;
    int generation;
    int captureRate;
    String name;
    String description;
    ArrayList<String> types;
    ArrayList<String> abilities;
    double weight;
    double height;
    boolean isLegendary;
    Date captureDate;

    public Pokemon8() {
        this(0, 0, 0, "", "", new ArrayList<>(), new ArrayList<>(), 0.0, 0.0, false, null);
    }

    public Pokemon8(int id, int generation, int captureRate, String name, String description, ArrayList<String> types,
                   ArrayList<String> abilities, double weight, double height, boolean isLegendary, Date captureDate) {
        this.id = id;
        this.generation = generation;
        this.captureRate = captureRate;
        this.name = name;
        this.description = description;
        this.types = types;
        this.abilities = abilities;
        this.weight = weight;
        this.height = height;
        this.isLegendary = isLegendary;
        this.captureDate = captureDate;
    }

    public int getId() { return id; }
    public int getGeneration() { return generation; }
    public int getCaptureRate() { return captureRate; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public ArrayList<String> getTypes() { return types; }
    public ArrayList<String> getAbilities() { return abilities; }
    public double getWeight() { return weight; }
    public double getHeight() { return height; }
    public boolean isLegendary() { return isLegendary; }
    public Date getCaptureDate() { return captureDate; }

    @Override
    public Pokemon8 clone() {
        Pokemon8 cloned = null;
        try {
            cloned = (Pokemon8) super.clone();
            cloned.types = new ArrayList<>(this.types);
            cloned.abilities = new ArrayList<>(this.abilities);
            if (this.captureDate != null) {
                cloned.captureDate = (Date) this.captureDate.clone();
            }
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
        return cloned;
    }

    public void imprimir() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String captureDateStr = (captureDate != null) ? sdf.format(captureDate) : "null";

        String formattedTypes = String.join(", ", types.stream().map(t -> "'" + t + "'").toArray(String[]::new));
        String formattedAbilities = String.join(", ", abilities.stream().map(a -> "'" + a + "'").toArray(String[]::new));

        System.out.println("[#" + id + " -> " + name + ": " + description + " - [" + formattedTypes + "] - ["
                + formattedAbilities + "] - " + weight + "kg - " + height + "m - " + captureRate + "% - "
                + isLegendary + " - " + generation + " gen] - " + captureDateStr);
    }

    public static String[] parseCSVLine(String line) {
        List<String> tokens = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        boolean inQuotes = false;
        int i = 0;
        while (i < line.length()) {
            char c = line.charAt(i);
            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                tokens.add(sb.toString());
                sb = new StringBuilder();
            } else {
                sb.append(c);
            }
            i++;
        }
        tokens.add(sb.toString());
        return tokens.toArray(new String[0]);
    }

    public static boolean isNumeric(String str) {
        try {
            Integer.parseInt(str.trim());
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isDouble(String str) {
        try {
            Double.parseDouble(str.trim());
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isValidDate(String str, SimpleDateFormat dateFormat) {
        try {
            dateFormat.parse(str.trim());
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    public static Pokemon8 buscarPokemon8PorId(int id, List<Pokemon8> Pokemon8s) {
        for (Pokemon8 p : Pokemon8s) {
            if (p.getId() == id) {
                return p;
            }
        }
        return null;
    }

    public static long comparisons = 0;
    public static long movements = 0;

    public static void quickSort(List<Pokemon8> array, int low, int high) {
        if (low < high) {
            int pi = partition(array, low, high);
            if (pi < K) {
                quickSort(array, low, pi - 1);
            }
            if (pi + 1 < K) { 
                quickSort(array, pi + 1, high);
            }
        }
    }

    public static int partition(List<Pokemon8> array, int low, int high) {
        Pokemon8 pivot = array.get(high);
        movements++; 
        int i = low - 1;
        for (int j = low; j < high; j++) {
            comparisons++;
            if (comparePokemon8s(array.get(j), pivot) <= 0) {
                i++;
                swap(array, i, j);
            }
        }
        swap(array, i + 1, high);
        return i + 1;
    }

    public static void swap(List<Pokemon8> array, int i, int j) {
        if (i != j) {
            Pokemon8 temp = array.get(i);
            array.set(i, array.get(j));
            array.set(j, temp);
            movements += 3; 
        }
    }

    public static int comparePokemon8s(Pokemon8 p1, Pokemon8 p2) {
        comparisons++;
        if (p1.getHeight() < p2.getHeight()) {
            return -1;
        } else if (p1.getHeight() > p2.getHeight()) {
            return 1;
        } else {
            return p1.getName().compareTo(p2.getName());
        }
    }

    public static final int K = 10;

    public static void main(String[] args) {
        List<Pokemon8> Pokemon8s = new ArrayList<>();

        try {
            BufferedReader br = new BufferedReader(new FileReader("/tmp/Pokemon8.csv"));
            String line;
            // Pular o cabeçalho
            br.readLine();
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            dateFormat.setLenient(false);

            while ((line = br.readLine()) != null) {
                if (line.equals("FIM")) break;
                if (!line.isEmpty()) {
                    String[] dados = parseCSVLine(line);
                    if (dados.length >= 12) {
                        int id = isNumeric(dados[0]) ? Integer.parseInt(dados[0].trim()) : 0;
                        int generation = isNumeric(dados[1].trim()) ? Integer.parseInt(dados[1].trim()) : 0;
                        String name = dados[2].trim();
                        String description = dados[3].trim();

                        ArrayList<String> types = new ArrayList<>();
                        if (!dados[4].trim().isEmpty()) {
                            types.add(dados[4].trim());
                        }
                        if (!dados[5].trim().isEmpty()) {
                            types.add(dados[5].trim());
                        }

                        String abilitiesStr = dados[6].trim().replaceAll("[\\[\\]'\"]", "");
                        ArrayList<String> abilities = new ArrayList<>(Arrays.asList(abilitiesStr.split(",\\s*")));

                        double weight = isDouble(dados[7].trim()) ? Double.parseDouble(dados[7].trim()) : 0.0;
                        double height = isDouble(dados[8].trim()) ? Double.parseDouble(dados[8].trim()) : 0.0;
                        int captureRate = isNumeric(dados[9].trim()) ? Integer.parseInt(dados[9].trim()) : 0;
                        boolean isLegendary = dados[10].trim().equals("1");
                        Date captureDate = isValidDate(dados[11], dateFormat) ? dateFormat.parse(dados[11].trim()) : null;

                        Pokemon8s.add(new Pokemon8(id, generation, captureRate, name, description,
                                types, abilities, weight, height, isLegendary, captureDate));
                    }
                }
            }
            br.close();
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        List<Pokemon8> selectedPokemon8s = new ArrayList<>();
        Scanner inputScanner = new Scanner(System.in);
        String input = inputScanner.next();

        while (!input.equals("FIM")) {
            int id = Integer.parseInt(input);
            Pokemon8 poke = buscarPokemon8PorId(id, Pokemon8s);
            if (poke != null) {
                selectedPokemon8s.add(poke.clone());
            }
            input = inputScanner.next();
        }
        inputScanner.close();

        long startTime = System.currentTimeMillis();

        // Executa o QuickSort parcial
        quickSort(selectedPokemon8s, 0, selectedPokemon8s.size() - 1);

        // Ordena apenas os primeiros K elementos
        selectedPokemon8s.subList(0, Math.min(K, selectedPokemon8s.size())).sort(new Comparator<Pokemon8>() {
            @Override
            public int compare(Pokemon8 p1, Pokemon8 p2) {
                return comparePokemon8s(p1, p2);
            }
        });

        long endTime = System.currentTimeMillis();
        double executionTime = (endTime - startTime);

        for (int i = 0; i < Math.min(K, selectedPokemon8s.size()); i++) {
            selectedPokemon8s.get(i).imprimir();
        }

        String matricula = "819886";
        String logFileName = matricula + "_quicksortParcial.txt";
        try {
            PrintWriter logWriter = new PrintWriter(new FileWriter(logFileName));
            logWriter.println(matricula + "\t" + comparisons + "\t" + movements + "\t" + String.format("%.2f", executionTime));
            logWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
