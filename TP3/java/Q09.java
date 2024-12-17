import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Q09 {
    public static void main(String[] args) {
        GerenciadorPokemons gp = new GerenciadorPokemons();
        gp.lerPokemonsDeArquivo();
        Pokemon[] pokemons = gp.getPokemons();

        // Pilha com alocação flexível
        Pilha pilha = new Pilha();

        Scanner sc = new Scanner(System.in);
        String LinhaID = "";
        // Leitura dos ids para a pilha
        while (!LinhaID.trim().equals("FIM")) {
            LinhaID = sc.nextLine();
            if (LinhaID.trim().equals("FIM")) {
                break;
            }
            int id = Integer.parseInt(LinhaID.trim());
            Pokemon poke = buscarPokemonPorId(id, pokemons);
            if (poke != null) {
                try {
                    pilha.push(poke.clone());
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        }

        String linhaComando = sc.nextLine();
        int numComando = Integer.parseInt(linhaComando.trim());

        for (int i = 0; i < numComando; i++) {
            String comandoLinha = sc.nextLine();
            comandoPilha(comandoLinha, pilha, pokemons);
        }

        // Mostrar a pilha
        pilha.mostrar();
    }

    // Procurar Pokémon pelo id
    public static Pokemon buscarPokemonPorId(int id, Pokemon[] pokemons) {
        for (Pokemon p : pokemons) {
            if (p != null && p.getId() == id) {
                return p;
            }
        }
        return null;
    }

    // Comandos da pilha
    public static void comandoPilha(String comandoLinha, Pilha pilha, Pokemon[] pokemons) {
        String[] tokens = comandoLinha.trim().split(" ");
        String command = tokens[0].toUpperCase();

        try {
            switch (command) {
                case "I":
                    if (tokens.length < 2) {
                        throw new Exception("Comando 'I' necessita de um ID");
                    }
                    int id = Integer.parseInt(tokens[1]);
                    Pokemon poke = buscarPokemonPorId(id, pokemons);
                    if (poke != null) {
                        pilha.push(poke.clone());
                    }
                    break;
                case "R":
                    Pokemon removed = pilha.pop();
                    if (removed != null) {
                        System.out.println("(R) " + removed.getName());
                    }
                    break;
                default:
                    throw new Exception("Comando inválido: " + command);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}

class Pilha {
    private ArrayList<Pokemon> array;

    public Pilha() {
        array = new ArrayList<>();
    }

    // Inserir no topo da pilha
    public void push(Pokemon pokemon) throws Exception {
        array.add(pokemon);
    }

    // Remover do topo da pilha
    public Pokemon pop() throws Exception {
        if (array.isEmpty()) {
            throw new Exception("Erro ao remover: pilha vazia");
        }
        return array.remove(array.size() - 1);
    }

    // Mostrar a pilha da base para o topo
    public void mostrar() {
        for (int i = 0; i < array.size(); i++) { // Mostrar da base para o topo
            System.out.print("[" + i + "] ");
            array.get(i).imprimir();
        }
    }
}

class Pokemon implements Cloneable {
    private int id;
    private int generation;
    private int captureRate;
    private String name;
    private String description;
    private ArrayList<String> types;
    private ArrayList<String> abilities;
    private double weight;
    private double height;
    private boolean isLegendary;
    private Date captureDate;

    public Pokemon() {
        this.id = 0;
        this.generation = 0;
        this.captureRate = 0;
        this.name = "";
        this.description = "";
        this.types = new ArrayList<>();
        this.abilities = new ArrayList<>();
        this.weight = 0.0;
        this.height = 0.0;
        this.isLegendary = false;
        this.captureDate = null;
    }

    public Pokemon(int id, int generation, int captureRate, String name, String description, ArrayList<String> types,
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
    public Pokemon clone() {
        Pokemon cloned = null;
        try {
            cloned = (Pokemon) super.clone();
            cloned.types = new ArrayList<>(this.types);
            cloned.abilities = new ArrayList<>(this.abilities);
            if (this.captureDate != null) {
                cloned.captureDate = (Date) this.captureDate.clone();
            }
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return cloned;
    }

    public void imprimir() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String captureDateStr = (captureDate != null) ? sdf.format(captureDate) : "null";

        String formattedTypes = String.join("', '", types);
        if (!formattedTypes.isEmpty()) {
            formattedTypes = "'" + formattedTypes + "'";
        }
        String formattedAbilities = String.join("', '", abilities);
        if (!formattedAbilities.isEmpty()) {
            formattedAbilities = "'" + formattedAbilities + "'";
        }

        System.out.println("[#" + id + " -> " + name + ": " + description + " - [" + formattedTypes + "] - ["
                + formattedAbilities + "] - " + weight + "kg - " + height + "m - " + captureRate + "% - "
                + isLegendary + " - " + generation + " gen] - " + captureDateStr);
    }
}

class GerenciadorPokemons {
    private Pokemon[] pokemons = new Pokemon[1000];
    private int numPokemons = 0;

    public void lerPokemonsDeArquivo() {
        try {
            BufferedReader br = new BufferedReader(new FileReader("/tmp/pokemon.csv"));
            String line;
            br.readLine(); // Pular o cabeçalho
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

                        Pokemon pokemon = new Pokemon(id, generation, captureRate, name, description,
                                types, abilities, weight, height, isLegendary, captureDate);

                        pokemons[numPokemons++] = pokemon;
                    }
                }
            }
            br.close();
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }

    public Pokemon[] getPokemons() {
        return Arrays.copyOf(pokemons, numPokemons);
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
}
