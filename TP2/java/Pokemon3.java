import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class Pokemon3 implements Cloneable {
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

    public Pokemon3() {
        this(0, 0, 0, "", "", new ArrayList<>(), new ArrayList<>(), 0.0, 0.0, false, null);
    }

    public Pokemon3(int id, int generation, int captureRate, String name, String description, ArrayList<String> types,
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
    public Pokemon3 clone() {
        Pokemon3 cloned = null;
        try {
            cloned = (Pokemon3) super.clone();
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

    public static Pokemon3 buscarPokemon3PorId(int id, List<Pokemon3> Pokemon3s) {
        for (Pokemon3 p : Pokemon3s) {
            if (p.getId() == id) {
                return p;
            }
        }
        return null;
    }

    // Função para comparar dois Pokémons pelo captureDate e, em caso de empate, pelo name
    public static int comparePokemon3s(Pokemon3 p1, Pokemon3 p2) {
        int result;
        if (p1.getCaptureDate() != null && p2.getCaptureDate() != null) {
            result = p1.getCaptureDate().compareTo(p2.getCaptureDate());
        } else if (p1.getCaptureDate() == null && p2.getCaptureDate() != null) {
            result = -1; // Datas nulas vêm antes de datas não nulas
        } else if (p1.getCaptureDate() != null && p2.getCaptureDate() == null) {
            result = 1;
        } else {
            result = 0; // Ambas as datas são nulas
        }

        if (result == 0) {
            // Critério de desempate: nome
            result = p1.getName().compareTo(p2.getName());
        }
        return result;
    }

    public static void main(String[] args) {
        List<Pokemon3> Pokemon3s = new ArrayList<>();

        try {
            File file = new File("/tmp/Pokemon3.csv");
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

                        Pokemon3s.add(new Pokemon3(id, generation, captureRate, name, description,
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
        ArrayList<Pokemon3> selectedPokemon3s = new ArrayList<>();

        Scanner inputScanner = new Scanner(System.in);
        String input = inputScanner.next();

        while (!input.equals("FIM")) {
            int id = Integer.parseInt(input);
            Pokemon3 poke = buscarPokemon3PorId(id, Pokemon3s);

            if (poke != null) {
                selectedPokemon3s.add(poke.clone());
            }

            input = inputScanner.next();
        }

        inputScanner.close();

        // Inicializa os contadores
        int comparisons = 0;
        int movements = 0;

        // Medição do tempo de execução
        long startTime = System.currentTimeMillis();

        //insercao
        for (int i = 1; i < selectedPokemon3s.size(); i++) {
            Pokemon3 key = selectedPokemon3s.get(i);
            int j = i - 1;

            comparisons++; // Primeira comparação
            while (j >= 0 && comparePokemon3s(key, selectedPokemon3s.get(j)) < 0) {
                selectedPokemon3s.set(j + 1, selectedPokemon3s.get(j));
                movements++;
                j--;
                if (j >= 0) {
                    comparisons++; 
                }
            }
            selectedPokemon3s.set(j + 1, key);
            movements++;
        }

        long endTime = System.currentTimeMillis();
        long executionTime = endTime - startTime;

        // Impressão dos Pokémons ordenados
        for (int i = 0; i < selectedPokemon3s.size(); i++) {
            selectedPokemon3s.get(i).imprimir();
        }

        
        String matricula = "819886"; 
        String logFileName = matricula + "_insercao.txt";
        try {
            PrintWriter logWriter = new PrintWriter(new FileWriter(logFileName));
            logWriter.println(matricula + "\t" + comparisons + "\t" + movements + "\t" + executionTime);
            logWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
