import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class Pokemon1 implements Cloneable {
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

    public Pokemon1() {
        this(0, 0, 0, "", "", new ArrayList<>(), new ArrayList<>(), 0.0, 0.0, false, null);
    }

    public Pokemon1(int id, int generation, int captureRate, String name, String description, ArrayList<String> types,
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
    public Pokemon1 clone() {
        Pokemon1 cloned = null;
        try {
            cloned = (Pokemon1) super.clone();
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

    // Função para imprimir os Pokémons de acordo com o ID 
    public void imprimir() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String captureDateStr = (captureDate != null) ? sdf.format(captureDate) : "null";

        // Formatando types e abilities
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
                inQuotes = !inQuotes; // Alterna o estado
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
        boolean result = false;
        try {
            Integer.parseInt(str.trim());
            result = true;
        } catch (NumberFormatException e) {
            // resultado permanece false
        }
        return result;
    }

    public static boolean isDouble(String str) {
        boolean result = false;
        try {
            Double.parseDouble(str.trim());
            result = true;
        } catch (NumberFormatException e) {
            // resultado permanece false
        }
        return result;
    }

    public static boolean isValidDate(String str, SimpleDateFormat dateFormat) {
        boolean result = false;
        try {
            dateFormat.parse(str.trim());
            result = true;
        } catch (Exception e) {
            // resultado permanece false
        }
        return result;
    }

    public static Pokemon1 buscarPokemonPorId(int id, List<Pokemon1> pokemons) {
        Pokemon1 foundPokemon = null;
        int i = 0;
        while (foundPokemon == null && i < pokemons.size()) {
            Pokemon1 p = pokemons.get(i);
            if (p.getId() == id) {
                foundPokemon = p;
            }
            i++;
        }
        return foundPokemon;
    }

    // Função para buscar Pokémon por nome 
    public static boolean buscarPokemonPorNome(String nome, List<Pokemon1> pokemons, Comparacao comparacao) {
        for (Pokemon1 p : pokemons) {
            comparacao.increment();
            if (p.getName().equalsIgnoreCase(nome)) {
                return true;
            }
        }
        return false;
    }

    // Classe para contar comparações
    static class Comparacao {
        private int contador;

        public Comparacao() {
            this.contador = 0;
        }

        public void increment() {
            this.contador++;
        }

        public int getContador() {
            return contador;
        }
    }

    public static void main(String[] args) {
        List<Pokemon1> pokemons = new ArrayList<>();

        // Leitura de todos os Pokémons do arquivo /tmp/pokemon.csv
        try {
            File file = new File("/tmp/pokemon.csv");


            Scanner sc = new Scanner(file);

            // Ignorar a primeira linha (cabeçalho)
            if (sc.hasNextLine()) {
                sc.nextLine();
            }

            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            dateFormat.setLenient(false);

            while (sc.hasNextLine()) {
                String linha = sc.nextLine();
                if (!linha.isEmpty()) {
                    String[] dados = parseCSVLine(linha);
                    if (dados.length >= 12) {
                        int id = isNumeric(dados[0]) ? Integer.parseInt(dados[0].trim()) : 0;
                        int generation = isNumeric(dados[1].trim()) ? Integer.parseInt(dados[1].trim()) : 0;
                        String name = dados[2].trim();
                        String description = dados[3].trim();

                        String typesStr = dados[4].trim();
                        typesStr = typesStr.replaceAll("[\\[\\]'\"]", "");
                        List<String> types = Arrays.asList(typesStr.split(",\\s*"));

                        boolean isLegendary = Boolean.parseBoolean(dados[5].trim());

                        String abilitiesStr = dados[6].trim();
                        abilitiesStr = abilitiesStr.replaceAll("[\\[\\]'\"]", "");
                        List<String> abilities = Arrays.asList(abilitiesStr.split(",\\s*"));

                        double weight = isDouble(dados[7].trim()) ? Double.parseDouble(dados[7].trim()) : 0.0;
                        double height = isDouble(dados[8].trim()) ? Double.parseDouble(dados[8].trim()) : 0.0;
                        int captureRate = isNumeric(dados[9].trim()) ? Integer.parseInt(dados[9].trim()) : 0;

                        String dateStr = dados[10].trim();
                        Date captureDate = isValidDate(dateStr, dateFormat) ? dateFormat.parse(dateStr) : null;

                        // Adicionar o Pokémon à lista
                        pokemons.add(new Pokemon1(id, generation, captureRate, name, description,
                                new ArrayList<>(types), new ArrayList<>(abilities), weight, height,
                                isLegendary, captureDate));
                    }
                }
            }

            sc.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Agora, ler os IDs da entrada padrão e construir a lista de Pokémons selecionados
        List<Pokemon1> selectedPokemons = new ArrayList<>();

        Scanner inputScanner = new Scanner(System.in);
        String inputLine = inputScanner.nextLine().trim();

        while (!inputLine.equals("FIM")) {
            if (!inputLine.isEmpty()) {
                try {
                    int id = Integer.parseInt(inputLine);
                    Pokemon1 p = buscarPokemonPorId(id, pokemons);
                    if (p != null) {
                        selectedPokemons.add(p.clone()); // Adicionar uma cópia para evitar problemas de aliasing
                    }
                } catch (NumberFormatException e) {
                    // Ignorar entradas que não sejam números
                }
            }

            if (inputScanner.hasNextLine()) {
                inputLine = inputScanner.nextLine().trim();
            } else {
                break;
            }
        }

        // Leitura dos nomes a serem pesquisados
        List<String> nomesPesquisados = new ArrayList<>();

        if (inputScanner.hasNextLine()) {
            inputLine = inputScanner.nextLine().trim();
            while (!inputLine.equals("FIM")) {
                if (!inputLine.isEmpty()) {
                    nomesPesquisados.add(inputLine);
                }

                if (inputScanner.hasNextLine()) {
                    inputLine = inputScanner.nextLine().trim();
                } else {
                    break;
                }
            }
        }

        inputScanner.close();

        // Início da medição de tempo
        long inicio = System.currentTimeMillis();
        Comparacao comparacao = new Comparacao();

        // Realiza as pesquisas sequenciais
        for (String nomePesquisado : nomesPesquisados) {
            boolean encontrado = buscarPokemonPorNome(nomePesquisado, selectedPokemons, comparacao);
            if (encontrado) {
                System.out.println("SIM");
            } else {
                System.out.println("NAO");
            }
        }

        // Fim da medição de tempo
        long fimTempo = System.currentTimeMillis();
        long tempoExecucao = fimTempo - inicio;

        String matricula = "819886"; 
        String nomeArquivo = matricula + "_sequencial.txt";

        try (FileWriter fw = new FileWriter(nomeArquivo)) {
            fw.write(matricula + "\t" + tempoExecucao + "\t" + comparacao.getContador() + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
