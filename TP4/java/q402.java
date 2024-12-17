import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class q402 {

    public static void main(String[] args) {
        GerenciadorPokemons gp = new GerenciadorPokemons();
        gp.lerPokemonsDeArquivo();
        Pokemon[] pokemons = gp.getPokemons();

        ArvoreArvore arvore = new ArvoreArvore();

        // Inserção dos nós na árvore principal na ordem especificada
        int ordem_insercao[] = {7, 3, 11, 1, 5, 9, 13, 0, 2, 4, 6, 8, 10, 12, 14};
        arvore.inserirKeysInOrder(ordem_insercao, ordem_insercao.length);

        Scanner sc = new Scanner(System.in);
        String linha = "";

        // Inserção dos Pokémons na árvore de árvores
        Comparacao comp_total = new Comparacao();
        while (sc.hasNextLine()) {
            linha = sc.nextLine().trim();
            if (linha.equals("FIM")) break;
            if (linha.isEmpty()) continue;
            try {
                int id = Integer.parseInt(linha);
                Pokemon poke = buscarPokemonPorId(id, pokemons);
                if (poke != null) {
                    int key = poke.getCaptureRate() % 15;
                    arvore.inserirArvore2(key, poke.getName(), comp_total);
                }
            } catch (NumberFormatException e) {
                // Ignora linhas que não são números válidos
            }
        }

        // Leitura das pesquisas
        List<String> pesquisas = new ArrayList<>();
        while (sc.hasNextLine()) {
            linha = sc.nextLine().trim();
            if (linha.equals("FIM")) break;
            if (linha.isEmpty()) continue;
            pesquisas.add(linha);
        }
        sc.close();

        long inicio = System.currentTimeMillis();

        List<String> resultados = new ArrayList<>();

        for (String pesquisa : pesquisas) {
            ResultadoPesquisa resultado = arvore.pesquisar(pesquisa, comp_total);
            String saida = "=> " + pesquisa;
            resultados.add(saida);
            if (resultado.isEncontrado()) {
                resultados.add(resultado.getCaminho() + " SIM");
            } else {
                resultados.add(resultado.getCaminho() + " NAO");
            }
        }

        long fim = System.currentTimeMillis();
        long tempoExecucao = fim - inicio;

        // Impressão dos resultados
        for (String res : resultados) {
            System.out.println(res);
        }

        // Criação do arquivo de log
        String matricula = "819886"; // Substitua pela sua matrícula
        String nomeArquivo = matricula + "_arvoreArvore.txt";

        try {
            FileWriter fw = new FileWriter(nomeArquivo);
            fw.write(matricula + "\t" + tempoExecucao + "\t" + comp_total.getContador() + "\n");
            fw.close();
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

    // Classe Pokemon
    static class Pokemon {
        private int id;
        private int captureRate;
        private String name;

        public Pokemon(int id, int captureRate, String name) {
            this.id = id;
            this.captureRate = captureRate;
            this.name = name;
        }

        public int getId() {
            return id;
        }

        public int getCaptureRate() {
            return captureRate;
        }

        public String getName() {
            return name;
        }
    }

    // Classe Comparacao
    static class Comparacao {
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

    // Classe ResultadoPesquisa
    static class ResultadoPesquisa {
        private String caminho;
        private boolean encontrado;
        private int comparacoes;

        public ResultadoPesquisa(String caminho, boolean encontrado, int comparacoes) {
            this.caminho = caminho;
            this.encontrado = encontrado;
            this.comparacoes = comparacoes;
        }

        public String getCaminho() {
            return caminho;
        }

        public boolean isEncontrado() {
            return encontrado;
        }

        public int getComparacoes() {
            return comparacoes;
        }
    }

    // Classe ArvoreArvore
    static class ArvoreArvore {
        private No1 raiz;

        public ArvoreArvore() {
            raiz = null;
        }

        // Inserir múltiplas chaves na ordem especificada
        public void inserirKeysInOrder(int[] keys, int n) {
            for (int i = 0; i < n; i++) {
                raiz = inserirArvore1Recursivo(raiz, keys[i]);
            }
        }

        // Inserir na árvore principal (No1)
        private No1 inserirArvore1Recursivo(No1 atual, int key) {
            if (atual == null) {
                return new No1(key);
            }
            if (key < atual.key) {
                atual.esq = inserirArvore1Recursivo(atual.esq, key);
            } else if (key > atual.key) {
                atual.dir = inserirArvore1Recursivo(atual.dir, key);
            }
            // Duplicates not inserted
            return atual;
        }

        // Inserir na árvore secundária (No2)
        public void inserirArvore2(int key, String name, Comparacao comp_total) {
            No1 node1 = buscarArvore1(raiz, key, comp_total);
            if (node1 != null) {
                node1.arvore2 = inserirArvore2Recursivo(node1.arvore2, name, comp_total);
            }
        }

        // Buscar na árvore principal
        private No1 buscarArvore1(No1 atual, int key, Comparacao comp) {
            if (atual == null)
                return null;
            comp.incrementar();
            if (key < atual.key) {
                return buscarArvore1(atual.esq, key, comp);
            } else if (key > atual.key) {
                return buscarArvore1(atual.dir, key, comp);
            } else {
                return atual;
            }
        }

        // Inserir na árvore secundária (No2)
        private No2 inserirArvore2Recursivo(No2 atual, String name, Comparacao comp_total) {
            if (atual == null) {
                return new No2(name);
            }
            comp_total.incrementar();
            if (name.compareTo(atual.name) < 0) {
                atual.esq = inserirArvore2Recursivo(atual.esq, name, comp_total);
            } else if (name.compareTo(atual.name) > 0) {
                atual.dir = inserirArvore2Recursivo(atual.dir, name, comp_total);
            }
            // Duplicates not inserted
            return atual;
        }

        // Pesquisar na árvore de árvores
        public ResultadoPesquisa pesquisar(String name, Comparacao comp_total) {
            ResultadoPesquisa resultado = new ResultadoPesquisa("", false, 0);
            StringBuilder caminho = new StringBuilder("raiz");
            boolean encontrado = false;
            int comparacoes = 0;

            Stack<No1> stack1 = new Stack<>();
            No1 current1 = raiz;

            while (current1 != null || !stack1.isEmpty()) {
                while (current1 != null) {
                    stack1.push(current1);
                    caminho.append(" ESQ");
                    current1 = current1.esq;
                }

                if (!stack1.isEmpty()) {
                    current1 = stack1.pop();
                    caminho.append(" dir");

                    // Pesquisar na árvore secundária
                    No2 arvore2 = current1.arvore2;
                    if (arvore2 != null) {
                        StringBuilder caminhoSecundaria = new StringBuilder(caminho.toString() + " ");
                        boolean found = pesquisarArvore2(arvore2, name, caminhoSecundaria, comp_total);
                        if (found) {
                            resultado.encontrado = true;
                            resultado.caminho = caminhoSecundaria.toString().trim();
                            resultado.comparacoes = comp_total.getContador();
                            return resultado;
                        }
                    }

                    current1 = current1.dir;
                }
            }

            // Se não encontrado em nenhuma árvore secundária
            resultado.caminho = caminho.toString().trim();
            resultado.comparacoes = comp_total.getContador();
            return resultado;
        }

        // Pesquisar na árvore secundária (No2)
        private boolean pesquisarArvore2(No2 atual, String name, StringBuilder caminhoSecundaria, Comparacao comp_total) {
            No2 current2 = atual;
            Stack<No2> stack2 = new Stack<>();
            boolean encontrado = false;

            while (current2 != null || !stack2.isEmpty()) {
                while (current2 != null) {
                    stack2.push(current2);
                    caminhoSecundaria.append(" esq");
                    current2 = current2.esq;
                }

                if (!stack2.isEmpty()) {
                    current2 = stack2.pop();
                    caminhoSecundaria.append(" dir");

                    comp_total.incrementar();
                    if (current2.name.equals(name)) {
                        caminhoSecundaria.append(" ");
                        return true;
                    }

                    current2 = current2.dir;
                }
            }

            return false;
        }
    }

    // Nó1: nó da árvore principal
    static class No1 {
        int key; // captureRate mod 15
        No1 esq, dir;
        No2 arvore2;

        public No1(int key) {
            this.key = key;
            this.esq = this.dir = null;
            this.arvore2 = null;
        }
    }

    // Nó2: nó da árvore secundária
    static class No2 {
        String name;
        No2 esq, dir;

        public No2(String name) {
            this.name = name;
            this.esq = this.dir = null;
        }
    }

    // Classe GerenciadorPokemons
    static class GerenciadorPokemons {
        private Pokemon[] pokemons = new Pokemon[1000];
        private int numPokemons = 0;

        public void lerPokemonsDeArquivo() {
            try {
                BufferedReader br = new BufferedReader(new FileReader("/tmp/pokemon.csv")); // Atualize o caminho se necessário
                String linha;
                br.readLine(); // Ignora o cabeçalho
                while ((linha = br.readLine()) != null && numPokemons < 1000) {
                    if (linha.equals("FIM")) break;
                    if (!linha.isEmpty()) {
                        String[] dados = parseCSVLine(linha);
                        if (dados.length >= 12) {
                            int id = isNumeric(dados[0]) ? Integer.parseInt(dados[0].trim()) : 0;
                            int captureRate = isNumeric(dados[9].trim()) ? Integer.parseInt(dados[9].trim()) : 0;
                            String name = dados[2].trim();
                            // Remove aspas se existirem
                            if (name.startsWith("\"") && name.endsWith("\"")) {
                                name = name.substring(1, name.length() - 1);
                            }
                            pokemons[numPokemons++] = new Pokemon(id, captureRate, name);
                        }
                    }
                }
                br.close();
            } catch (IOException e) {
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
                if (c == '\"') {
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
    }
}
