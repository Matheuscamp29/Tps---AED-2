import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Q301 {
    public static void main(String[] args) {
        GerenciadorPokemons gp = new GerenciadorPokemons();
        gp.lerPokemonsDeArquivo();
        Pokemon[] pokemons = gp.getPokemons();

        // lista
        Lista lista = new Lista();

        Scanner sc = new Scanner(System.in);
        String LinhaID="";
        // leitura dos ids para a lsita
        while (!LinhaID.trim().equals("FIM")) {
            LinhaID = sc.nextLine();
            if (LinhaID.trim().equals("FIM")) {
                break;
            }
            int id = Integer.parseInt(LinhaID.trim());
            Pokemon poke = buscarPokemonPorId(id, pokemons);
            if (poke != null) {
                try {
                    lista.inserirFim(poke.clone());
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        }

        String linhaComando = sc.nextLine();
        int numComando = Integer.parseInt(linhaComando.trim());

        for (int i = 0; i < numComando; i++) {
            String comandoLinha = sc.nextLine();
            comandoLista(comandoLinha, lista, pokemons);
        }

        // mostrar a lista 
        lista.mostrar();
    }

    // procurar pok pelo id
    public static Pokemon buscarPokemonPorId(int id, Pokemon[] pokemons) {
        for (Pokemon p : pokemons) {
            if (p != null && p.getId() == id) {
                return p;
            }
        }
        return null;
    }

    // comandos da lista
    public static void comandoLista(String comandoLinha, Lista lista, Pokemon[] pokemons) {
        String[] tokens = comandoLinha.trim().split(" ");
        String command = tokens[0];

        try {
            switch (command) {
                case "II": {
                    int id = Integer.parseInt(tokens[1]);
                    Pokemon poke = buscarPokemonPorId(id, pokemons);
                    if (poke != null) {
                        lista.inserirInicio(poke.clone());
                    }
                    break;
                }
                case "I*": {
                    int position = Integer.parseInt(tokens[1]);
                    int id = Integer.parseInt(tokens[2]);
                    Pokemon poke = buscarPokemonPorId(id, pokemons);
                    if (poke != null) {
                        lista.inserir(poke.clone(), position);
                    }
                    break;
                }
                case "IF": {
                    int id = Integer.parseInt(tokens[1]);
                    Pokemon poke = buscarPokemonPorId(id, pokemons);
                    if (poke != null) {
                        lista.inserirFim(poke.clone());
                    }
                    break;
                }
                case "RI": {
                    Pokemon removed = lista.removerInicio();
                    if (removed != null) {
                        System.out.println("(R) " + removed.getName());
                    }
                    break;
                }
                case "R*": {
                    int position = Integer.parseInt(tokens[1]);
                    Pokemon removed = lista.remover(position);
                    if (removed != null) {
                        System.out.println("(R) " + removed.getName());
                    }
                    break;
                }
                case "RF": {
                    Pokemon removed = lista.removerFim();
                    if (removed != null) {
                        System.out.println("(R) " + removed.getName());
                    }
                    break;
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}

class Lista {
    private Pokemon[] array;
    private int n;

    public Lista() {
        this(1000); 
    }

    public Lista(int maxSize) {
        array = new Pokemon[maxSize];
        n = 0;
    }

    public void inserirInicio(Pokemon pokemon) throws Exception {
        if (n >= array.length) {
            throw new Exception("Erro ao inserir no início: array cheio");
        }
        // mover elemtnos pra direita
        for (int i = n; i > 0; i--) {
            array[i] = array[i - 1];
        }
        array[0] = pokemon;
        n++;
    }

    public void inserirFim(Pokemon pokemon) throws Exception {
        if (n >= array.length) {
            throw new Exception("Erro ao inserir no fim: array cheio");
        }
        array[n] = pokemon;
        n++;
    }

    public void inserir(Pokemon pokemon, int pos) throws Exception {
        if (n >= array.length || pos < 0 || pos > n) {
            throw new Exception("Erro ao inserir na posição: posição inválida ou array cheio");
        }
        for (int i = n; i > pos; i--) {
            array[i] = array[i - 1];
        }
        array[pos] = pokemon;
        n++;
    }

    public Pokemon removerInicio() throws Exception {
        if (n == 0) {
            throw new Exception("Erro ao remover do início: array vazio");
        }
        Pokemon removed = array[0];
        for (int i = 0; i < n - 1; i++) {
            array[i] = array[i + 1];
        }
        n--;
        return removed;
    }

    public Pokemon removerFim() throws Exception {
        if (n == 0) {
            throw new Exception("Erro ao remover do fim: array vazio");
        }
        Pokemon removed = array[n - 1];
        n--;
        return removed;
    }

    public Pokemon remover(int pos) throws Exception {
        if (n == 0 || pos < 0 || pos >= n) {
            throw new Exception("Erro ao remover na posição: posição inválida ou array vazio");
        }
        Pokemon removed = array[pos];
        for (int i = pos; i < n - 1; i++) {
            array[i] = array[i + 1];
        }
        n--;
        return removed;
    }

    public void mostrar() {
        for (int i = 0; i < n; i++) {
            System.out.print("[" + i + "] ");
            array[i].imprimir();
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
