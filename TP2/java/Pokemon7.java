import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class Pokemon7 implements Cloneable {
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

    public Pokemon7() {
        this(0, 0, 0, "", "", new ArrayList<>(), new ArrayList<>(), 0.0, 0.0, false, null);
    }

    public Pokemon7(int id, int generation, int captureRate, String name, String description, ArrayList<String> types,
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
    public Pokemon7 clone() {
        Pokemon7 cloned = null;
        try {
            cloned = (Pokemon7) super.clone();
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

        String formattedTypes = types.stream()
                .map(type -> "'" + type + "'")
                .collect(Collectors.joining(", "));
        String formattedAbilities = abilities.stream()
                .map(ability -> "'" + ability + "'")
                .collect(Collectors.joining(", "));

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
        } catch (Exception e) {
            return false;
        }
    }

    public static Pokemon7 buscarPokemon7PorId(int id, List<Pokemon7> Pokemon7s) {
        for (Pokemon7 p : Pokemon7s) {
            if (p.getId() == id) {
                return p;
            }
        }
        return null;
    }

    public static int comparisons = 0;
    public static int movements = 0;

    public static void partialSelectionSort(ArrayList<Pokemon7> array, int k) {
        for (int i = 0; i < k && i < array.size() - 1; i++) {
            int minIndex = i;
            for (int j = i + 1; j < array.size(); j++) {
                comparisons++;
                if (array.get(j).getName().compareTo(array.get(minIndex).getName()) < 0) {
                    minIndex = j;
                }
            }
            if (minIndex != i) {
                Pokemon7 temp = array.get(i);
                array.set(i, array.get(minIndex));
                array.set(minIndex, temp);
                movements += 3;
            }
        }
    }

    public static void main(String[] args) {
        List<Pokemon7> Pokemon7s = new ArrayList<>();

        try {
            File file = new File("/tmp/Pokemon7.csv");
            Scanner sc = new Scanner(file);

            if (sc.hasNextLine()) {
                sc.nextLine();
            }

            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            dateFormat.setLenient(false);

            while (sc.hasNextLine()) {
                String linha = sc.nextLine();
                if (linha.equals("FIM")) break;

                if (!linha.isEmpty()) {
                    String[] dados = parseCSVLine(linha);
                    if (dados.length >= 12) {

                        int id = isNumeric(dados[0]) ? Integer.parseInt(dados[0].trim()) : 0;
                        int generation = isNumeric(dados[1].trim()) ? Integer.parseInt(dados[1].trim()) : 0;
                        String name = dados[2].trim();
                        String description = dados[3].trim();

                        List<String> types = new ArrayList<>();
                        if (!dados[4].trim().isEmpty()) types.add(dados[4].trim());
                        if (!dados[5].trim().isEmpty()) types.add(dados[5].trim());

                        String abilitiesStr = dados[6].trim();
                        abilitiesStr = abilitiesStr.replaceAll("[\\[\\]'\"]", "");
                        List<String> abilities = Arrays.asList(abilitiesStr.split(",\\s*"));

                        double weight = isDouble(dados[7].trim()) ? Double.parseDouble(dados[7].trim()) : 0.0;
                        double height = isDouble(dados[8].trim()) ? Double.parseDouble(dados[8].trim()) : 0.0;
                        int captureRate = isNumeric(dados[9].trim()) ? Integer.parseInt(dados[9].trim()) : 0;

                        boolean isLegendary = dados[10].trim().equals("1");

                        Date captureDate = isValidDate(dados[11], dateFormat) ? dateFormat.parse(dados[11].trim()) : null;

                        Pokemon7s.add(new Pokemon7(id, generation, captureRate, name, description,
                                new ArrayList<>(types), new ArrayList<>(abilities), weight, height,
                                isLegendary, captureDate));
                    }
                }
            }

            sc.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        ArrayList<Pokemon7> selectedPokemon7s = new ArrayList<>();

        Scanner inputScanner = new Scanner(System.in);
        String input = inputScanner.next();

        while (!input.equals("FIM")) {
            int id = Integer.parseInt(input);
            Pokemon7 poke = buscarPokemon7PorId(id, Pokemon7s);

            if (poke != null) {
                selectedPokemon7s.add(poke.clone());
            }

            input = inputScanner.next();
        }

        inputScanner.close();

        comparisons = 0;
        movements = 0;

        long startTime = System.currentTimeMillis();

        int k = 10;
        partialSelectionSort(selectedPokemon7s, k);

        long endTime = System.currentTimeMillis();
        long executionTime = endTime - startTime;

        for (int i = 0; i < k; i++) {
            selectedPokemon7s.get(i).imprimir();
        }

        String matricula = "819886"; 
        String logFileName = matricula + "_selecaoParcial.txt";
        try {
            PrintWriter logWriter = new PrintWriter(new FileWriter(logFileName));
            logWriter.println(matricula + "\t" + comparisons + "\t" + movements + "\t" + executionTime);
            logWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
