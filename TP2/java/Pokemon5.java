import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class Pokemon5 implements Cloneable {
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

    public Pokemon5() {
        this(0, 0, 0, "", "", new ArrayList<>(), new ArrayList<>(), 0.0, 0.0, false, null);
    }

    public Pokemon5(int id, int generation, int captureRate, String name, String description, ArrayList<String> types,
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
    public Pokemon5 clone() {
        Pokemon5 cloned = null;
        try {
            cloned = (Pokemon5) super.clone();
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

    public static Pokemon5 buscarPokemon5PorId(int id, List<Pokemon5> Pokemon5s) {
        for (Pokemon5 p : Pokemon5s) {
            if (p.getId() == id) {
                return p;
            }
        }
        return null;
    }

    public static int comparisons = 0;
    public static int movements = 0;

    public static void countingSort(ArrayList<Pokemon5> array) {
        int max = Integer.MIN_VALUE;
        int min = Integer.MAX_VALUE;
        for (Pokemon5 p : array) {
            if (p.getCaptureRate() > max) {
                max = p.getCaptureRate();
            }
            if (p.getCaptureRate() < min) {
                min = p.getCaptureRate();
            }
        }
        int range = max - min + 1;
        int[] count = new int[range];
        ArrayList<Pokemon5> output = new ArrayList<>(Collections.nCopies(array.size(), null));

        for (Pokemon5 p : array) {
            count[p.getCaptureRate() - min]++;
            movements++;
        }

        for (int i = 1; i < count.length; i++) {
            count[i] += count[i - 1];
            movements++;
        }

        for (int i = array.size() - 1; i >= 0; i--) {
            Pokemon5 p = array.get(i);
            int idx = count[p.getCaptureRate() - min] - 1;
            output.set(idx, p);
            count[p.getCaptureRate() - min]--;
            movements++;
        }

        for (int i = 0; i < output.size(); i++) {
            array.set(i, output.get(i));
            movements++;
        }

        int start = 0;
        while (start < array.size()) {
            int end = start;
            while (end + 1 < array.size() && array.get(end).getCaptureRate() == array.get(end + 1).getCaptureRate()) {
                end++;
            }
            if (end > start) {
                // Sort by name within this range
                Collections.sort(array.subList(start, end + 1), new Comparator<Pokemon5>() {
                    public int compare(Pokemon5 p1, Pokemon5 p2) {
                        comparisons++;
                        return p1.getName().compareTo(p2.getName());
                    }
                });
            }
            start = end + 1;
        }
    }

    public static void main(String[] args) {
        List<Pokemon5> Pokemon5s = new ArrayList<>();

        try {
            File file = new File("/tmp/Pokemon5.csv");
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

                        Pokemon5s.add(new Pokemon5(id, generation, captureRate, name, description,
                                new ArrayList<>(types), new ArrayList<>(abilities), weight, height,
                                isLegendary, captureDate));
                    }
                }
            }

            sc.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        ArrayList<Pokemon5> selectedPokemon5s = new ArrayList<>();

        Scanner inputScanner = new Scanner(System.in);
        String input = inputScanner.next();

        while (!input.equals("FIM")) {
            int id = Integer.parseInt(input);
            Pokemon5 poke = buscarPokemon5PorId(id, Pokemon5s);

            if (poke != null) {
                selectedPokemon5s.add(poke.clone());
            }

            input = inputScanner.next();
        }

        inputScanner.close();

        comparisons = 0;
        movements = 0;

        long startTime = System.currentTimeMillis();

        countingSort(selectedPokemon5s);

        long endTime = System.currentTimeMillis();
        long executionTime = endTime - startTime;

        for (int i = 0; i < selectedPokemon5s.size(); i++) {
            selectedPokemon5s.get(i).imprimir();
        }

        String matricula = "819886"; 
        String logFileName = matricula + "_countingsort.txt";
        try {
            PrintWriter logWriter = new PrintWriter(new FileWriter(logFileName));
            logWriter.println(matricula + "\t" + comparisons + "\t" + movements + "\t" + executionTime);
            logWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
