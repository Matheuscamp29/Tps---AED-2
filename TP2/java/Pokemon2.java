//selecao por nome
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class Pokemon2 implements Cloneable {
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

    public Pokemon2() {
        this(0, 0, 0, "", "", new ArrayList<>(), new ArrayList<>(), 0.0, 0.0, false, null);
    }

    public Pokemon2(int id, int generation, int captureRate, String name, String description, ArrayList<String> types,
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

    // Getters
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
    public Pokemon2 clone() {
        Pokemon2 cloned = null;
        try {
            cloned = (Pokemon2) super.clone();
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

    // Método para imprimir os atributos
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

    public static Pokemon2 buscarPokemon2PorId(int id, List<Pokemon2> Pokemon2s) {
        for (Pokemon2 p : Pokemon2s) {
            if (p.getId() == id) {
                return p;
            }
        }
        return null;
    }

    public static void main(String[] args) {
        List<Pokemon2> Pokemon2s = new ArrayList<>();

        try {
            File file = new File("/tmp/Pokemon2.csv");
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

                        // Handle multiple types in separate fields
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

                        Pokemon2s.add(new Pokemon2(id, generation, captureRate, name, description,
                                new ArrayList<>(types), new ArrayList<>(abilities), weight, height,
                                isLegendary, captureDate));
                    }
                }
            }

            sc.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Lista para armazenar os Pokémons selecionados
        ArrayList<Pokemon2> selectedPokemon2s = new ArrayList<>();

        Scanner inputScanner = new Scanner(System.in);
        String input = inputScanner.next();

        while (!input.equals("FIM")) {
            int id = Integer.parseInt(input);
            Pokemon2 poke = buscarPokemon2PorId(id, Pokemon2s);

            if (poke != null) {
                selectedPokemon2s.add(poke.clone());
            } else {
            }

            input = inputScanner.next();
        }

        inputScanner.close();

        // Inicializa os contadores
        int comparisons = 0;
        int movements = 0;

        // Medição do tempo de execução
        long startTime = System.currentTimeMillis();

        // Implementação do Selection Sort
        for (int i = 0; i < selectedPokemon2s.size() - 1; i++) {
            int minIndex = i;
            for (int j = i + 1; j < selectedPokemon2s.size(); j++) {
                comparisons++;
                if (selectedPokemon2s.get(j).getName().compareTo(selectedPokemon2s.get(minIndex).getName()) < 0) {
                    minIndex = j;
                }
            }
            if (minIndex != i) {
                // Swap
                Pokemon2 temp = selectedPokemon2s.get(i);
                selectedPokemon2s.set(i, selectedPokemon2s.get(minIndex));
                selectedPokemon2s.set(minIndex, temp);
                movements += 3; 
            }
        }

        long endTime = System.currentTimeMillis();
        long executionTime = endTime - startTime;

        // Impressão dos Pokémons ordenados
        for (int i = 0; i < selectedPokemon2s.size(); i++) {
            selectedPokemon2s.get(i).imprimir();
        }

        
        String matricula = "819886"; 
        String logFileName = matricula + "_selecao.txt";
        try {
            PrintWriter logWriter = new PrintWriter(new FileWriter(logFileName));
            logWriter.println(matricula + "\t" + comparisons + "\t" + movements + "\t" + executionTime);
            logWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
