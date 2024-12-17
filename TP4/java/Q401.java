import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Q401 {
    

    public static void main(String[] args) {
        GerenciadorPokemons gp = new GerenciadorPokemons();
        gp.lerPokemonsDeArquivo();
        Pokemon[] pokemons = gp.getPokemons();
        //ciracao da arvore
        ArvoreBinaria arvore = new ArvoreBinaria();

        Scanner sc = new Scanner(System.in);
        String linha = "";
        int comp;

        while (sc.hasNextLine()) {
            linha = sc.nextLine().trim();
            if (linha.equals("FIM")) {
                break;
            }
            if (!linha.isEmpty()) {
                try {
                    int id = Integer.parseInt(linha);
                    Pokemon poke = buscarPokemonPorId(id, pokemons);
                    if (poke != null) {
                        arvore.inserir(poke.getName());
                    }
                } catch (NumberFormatException e) {
                    
                }
            }
        }

        List<String> pesquisas = new ArrayList<>();
        if (sc.hasNextLine()) {
            linha = sc.nextLine().trim();
            while (!linha.equals("FIM")) {
                if (!linha.isEmpty()) {
                    pesquisas.add(linha);
                }
        
                if (sc.hasNextLine()) {
                    linha = sc.nextLine().trim();
                } 
            }
        }
        sc.close();
        

        long inicio = System.currentTimeMillis();
        Comparacao comparacao = new Comparacao();

        List<String> resultados = new ArrayList<>();

        for (String pesquisa : pesquisas) {
            ResultadoPesquisa resultado = arvore.pesquisar(pesquisa, comparacao);
            String saida = resultado.getCaminho() + (resultado.isEncontrado() ? " SIM" : " NAO");
            resultados.add(saida);
        }

        long fim = System.currentTimeMillis();
        long tempoExecucao = fim - inicio;

        for (String res : resultados) {
            System.out.println(res);
        }

        // Criação do arquivo de log
        String matricula = "819886"; 
        String nomeArquivo = matricula + "_arvoreBinaria.txt";

        try (FileWriter fw = new FileWriter(nomeArquivo)) {
            fw.write(matricula + "\t" + tempoExecucao + "\t" + comp + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Pokemon buscarPokemonPorId(int id, Pokemon[] pokemons) {
        for (Pokemon p : pokemons) {
            if (p != null && p.getId() == id) {
                return p;
            }
        }
        return null;
    }
}

class ArvoreBinaria {

    class No {
        String nome;
        No esq, dir;

        No(String nome) {
            this.nome = nome;
            esq = dir = null;
        }
    }

    private No raiz;
    private int comparacoes;

    public ArvoreBinaria() {
        raiz = null;
        comparacoes = 0;
    }

    public boolean inserir(String nome) {
        if (raiz == null) {
            raiz = new No(nome);
            return true;
        }

        No atual = raiz;
        No pai = null;

        while (atual != null) {
            pai = atual;
            comparacoes++;
            if (nome.compareTo(atual.nome) < 0) {
                atual = atual.esq;
            } else if (nome.compareTo(atual.nome) > 0) {
                atual = atual.dir;
            } else {
                return false; // Não insere duplicados
            }
        }

        if (nome.compareTo(pai.nome) < 0) {
            pai.esq = new No(nome);
        } else {
            pai.dir = new No(nome);
        }

        return true;
    }

    // Pesquisa na árvore, retorna ResultadoPesquisa
    public ResultadoPesquisa pesquisar(String nome, Comparacao comparacaoObj) {
        No atual = raiz;
        StringBuilder caminho = new StringBuilder(nome);
        caminho.append("\n=>raiz");

        boolean encontrado = false;

        while (atual != null) {
            comparacaoObj.incrementar();
            if (nome.equals(atual.nome)) {
                encontrado = true;
                break;
            } else if (nome.compareTo(atual.nome) < 0) {
                atual = atual.esq;
                caminho.append(" esq");
            } else {
                atual = atual.dir;
                caminho.append(" dir");
            }
        }

        return new ResultadoPesquisa(caminho.toString(), encontrado);
    }
}

class ResultadoPesquisa {
    private String caminho;
    private boolean encontrado;

    public ResultadoPesquisa(String caminho, boolean encontrado) {
        this.caminho = caminho;
        this.encontrado = encontrado;
    }

    public String getCaminho() {
        return caminho;
    }

    public boolean isEncontrado() {
        return encontrado;
    }
}

class Comparacao {
    private int contador;

    public Comparacao() {
        contador = 0;
    }

    public void incrementar() {
        contador++;
    }

    public int getContador() {
        return contador;
    }
}

class Pokemon {
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
            String linha;
            br.readLine(); // Ignora o cabeçalho
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            dateFormat.setLenient(false);

            while ((linha = br.readLine()) != null) {
                if (linha.equals("FIM")) break;
                if (!linha.isEmpty()) {
                    String[] dados = parseCSVLine(linha);
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

    public static String[] parseCSVLine(String linha) {
        List<String> tokens = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        boolean inQuotes = false;
        int i = 0;
        while (i < linha.length()) {
            char c = linha.charAt(i);
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
