import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        // Inicializar o gerenciador de Pokémons e ler do arquivo
        PokemonManager manager = new PokemonManager();
        manager.lerPokemonsDeArquivo("/tmp/pokemon.csv");
        DoublyLinkedList pokemons = manager.getPokemons();

        // Lista para Pokémons selecionados
        DoublyLinkedList selectedPokemons = new DoublyLinkedList();

        Scanner sc = new Scanner(System.in);
        String input;
        
        // Leitura dos IDs para selecionar Pokémons
        while (sc.hasNextLine()) {
            input = sc.nextLine().trim();
            if (input.equals("FIM")) {
                break;
            }
            try {
                int id = Integer.parseInt(input);
                Pokemon p = pokemons.buscarPorId(id);
                if (p != null) {
                    selectedPokemons.append(p.clone());
                } else {
                    System.out.println("Pokémon com ID " + id + " não existe.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida: " + input);
            }
        }

        // Medir o tempo de execução do QuickSort
        long startTime = System.nanoTime();
        selectedPokemons.quickSort();
        long endTime = System.nanoTime();
        double executionTime = (endTime - startTime) / 1_000_000.0; // em milissegundos

        // Imprimir os Pokémons ordenados
        selectedPokemons.imprimir();

        // Preparar o arquivo de log
        String matricula = "819886"; // Substitua pela sua matrícula
        String logFileName = matricula + "_quicksort2.txt";
        try (PrintWriter logWriter = new PrintWriter(new FileWriter(logFileName))) {
            logWriter.printf("%s\t%d\t%d\t%.2f\n", matricula, DoublyLinkedList.comparisons, DoublyLinkedList.movements, executionTime);
        } catch (IOException e) {
            System.out.println("Erro ao criar o arquivo de log.");
        }

        // Fechar o Scanner
        sc.close();
    }
}

// Classe que representa um Pokémon
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
    private String captureDate;

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
        this.captureDate = "";
    }

    // Construtor com parâmetros
    public Pokemon(int id, int generation, int captureRate, String name, String description,
                   ArrayList<String> types, ArrayList<String> abilities, double weight,
                   double height, boolean isLegendary, String captureDate) {
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
    public String getCaptureDate() { return captureDate; }

    // Método para imprimir o Pokémon
    public void imprimir() {
        String formattedTypes = "";
        for (int i = 0; i < types.size(); i++) {
            formattedTypes += "'" + types.get(i) + "'";
            if (i < types.size() - 1) {
                formattedTypes += ", ";
            }
        }

        String formattedAbilities = "";
        for (int i = 0; i < abilities.size(); i++) {
            formattedAbilities += "'" + abilities.get(i) + "'";
            if (i < abilities.size() - 1) {
                formattedAbilities += ", ";
            }
        }

        System.out.println("[#" + id + " -> " + name + ": " + description + " - [" + formattedTypes + "] - ["
                + formattedAbilities + "] - " + weight + "kg - " + height + "m - " + captureRate + "% - "
                + (isLegendary ? "true" : "false") + " - " + generation + " gen] - " + captureDate);
    }

    // Método clone
    @Override
    public Pokemon clone() {
        try {
            Pokemon cloned = (Pokemon) super.clone();
            cloned.types = new ArrayList<>(this.types);
            cloned.abilities = new ArrayList<>(this.abilities);
            return cloned;
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }
}

// Classe que gerencia a lista duplamente encadeada e o QuickSort
class DoublyLinkedList {
    Node head;
    Node tail;
    static long comparisons = 0;
    static long movements = 0;

    public DoublyLinkedList() {
        this.head = null;
        this.tail = null;
    }

    // Classe interna que representa um nó
    class Node {
        Pokemon data;
        Node next;
        Node prev;

        public Node(Pokemon data) {
            this.data = data;
            this.next = null;
            this.prev = null;
        }
    }

    // Método para criar e adicionar um nó no final da lista
    public void append(Pokemon p) {
        Node newNode = new Node(p);
        movements++; // Considera a movimentação de adicionar um nó

        if (head == null) {
            head = newNode;
            tail = newNode;
        } else {
            tail.next = newNode;
            newNode.prev = tail;
            tail = newNode;
        }
    }

    // Método para buscar um Pokémon pelo ID
    public Pokemon buscarPorId(int id) {
        Node current = head;
        while (current != null) {
            comparisons++; // Conta a comparação
            if (current.data.getId() == id) {
                return current.data;
            }
            current = current.next;
        }
        return null;
    }

    // Método para imprimir toda a lista
    public void imprimir() {
        Node current = head;
        while (current != null) {
            current.data.imprimir();
            current = current.next;
        }
    }

    // Método para liberar a lista (não é necessário em Java, mas mantido para consistência)
    public void liberar() {
        head = null;
        tail = null;
    }

    // Implementação do QuickSort para lista duplamente encadeada
    public void quickSort() {
        quickSortRec(head, tail);
    }

    private void quickSortRec(Node low, Node high) {
        if (high != null && low != high && low != high.next) {
            Node p = partition(low, high);
            quickSortRec(low, p.prev);
            quickSortRec(p.next, high);
        }
    }

    private Node partition(Node low, Node high) {
        Pokemon pivot = high.data;
        movements++; // Movimentação do pivot
        Node i = low.prev;

        for (Node j = low; j != high; j = j.next) {
            if (comparePokemons(j.data, pivot) <= 0) {
                i = (i == null) ? low : i.next;
                swapNodesData(i, j);
            }
        }
        i = (i == null) ? low : i.next;
        swapNodesData(i, high);
        return i;
    }

    private void swapNodesData(Node a, Node b) {
        if (a == null || b == null) return;
        Pokemon temp = a.data;
        a.data = b.data;
        b.data = temp;
        movements += 3; // Considera as movimentações da troca
    }

    // Método de comparação conforme especificado
    private int comparePokemons(Pokemon p1, Pokemon p2) {
        comparisons++;
        if (p1.getGeneration() < p2.getGeneration()) {
            return -1;
        } else if (p1.getGeneration() > p2.getGeneration()) {
            return 1;
        } else {
            return p1.getName().compareTo(p2.getName());
        }
    }
}

// Classe que gerencia a leitura dos Pokémons do arquivo CSV
class PokemonManager {
    private DoublyLinkedList pokemons;

    public PokemonManager() {
        pokemons = new DoublyLinkedList();
    }

    public DoublyLinkedList getPokemons() {
        return pokemons;
    }

    // Método para ler Pokémons do arquivo CSV
    public void lerPokemonsDeArquivo(String filename) {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            br.readLine(); // Pular o cabeçalho

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
                            types.add(dados[4].trim().replaceAll("[\\[\\]'\"]", ""));
                        }
                        if (!dados[5].trim().isEmpty()) {
                            types.add(dados[5].trim().replaceAll("[\\[\\]'\"]", ""));
                        }

                        String abilitiesStr = dados[6].trim().replaceAll("[\\[\\]'\"]", "");
                        ArrayList<String> abilities = new ArrayList<>();
                        if (!abilitiesStr.isEmpty()) {
                            String[] abilitiesArray = abilitiesStr.split(",\\s*");
                            for (String ability : abilitiesArray) {
                                abilities.add(ability.trim());
                            }
                        }

                        double weight = isDouble(dados[7].trim()) ? Double.parseDouble(dados[7].trim()) : 0.0;
                        double height = isDouble(dados[8].trim()) ? Double.parseDouble(dados[8].trim()) : 0.0;
                        int captureRate = isNumeric(dados[9].trim()) ? Integer.parseInt(dados[9].trim()) : 0;
                        boolean isLegendary = dados[10].trim().equals("1");
                        String captureDate = dados[11].trim();

                        Pokemon pokemon = new Pokemon(id, generation, captureRate, name, description,
                                types, abilities, weight, height, isLegendary, captureDate);

                        pokemons.append(pokemon);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Erro ao ler o arquivo: " + e.getMessage());
        }
    }

    // Método para parsear uma linha CSV considerando aspas
    private String[] parseCSVLine(String line) {
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

    // Método para verificar se uma string é numérica
    private boolean isNumeric(String str) {
        if (str == null || str.isEmpty()) return false;
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // Método para verificar se uma string é um número de ponto flutuante
    private boolean isDouble(String str) {
        if (str == null || str.isEmpty()) return false;
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
