#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <time.h>

// Definição da estrutura Pokemon
typedef struct {
    int id;
    char name[100];
    // Outros campos podem ser adicionados conforme necessário
} Pokemon;

// Definição da estrutura para contagem de comparações
typedef struct {
    int contador;
} Comparacao;

// Inicializa a contagem de comparações
void inicializarComparacao(Comparacao *c) {
    if (c != NULL) {
        c->contador = 0;
    }
}

// Incrementa a contagem de comparações
void incrementarComparacao(Comparacao *c) {
    if (c != NULL) {
        c->contador++;
    }
}

// Retorna o número de comparações
int getContador(const Comparacao *c) {
    if (c != NULL) {
        return c->contador;
    }
    return 0;
}

// Definição da estrutura do nó da Árvore AVL
typedef struct AVLNode {
    char name[100];
    struct AVLNode *esq;
    struct AVLNode *dir;
    int altura;
} AVLNode;

// Função para obter a altura de um nó
int alturaAVL(const AVLNode *N) {
    if (N == NULL)
        return 0;
    return N->altura;
}

// Função para obter o máximo entre dois inteiros
int max(int a, int b) {
    return (a > b) ? a : b;
}

// Função para criar um novo nó
AVLNode* novoNo(const char *name) {
    AVLNode* node = (AVLNode*) malloc(sizeof(AVLNode));
    if (node == NULL) {
        fprintf(stderr, "Erro de alocação de memória para novo nó.\n");
        exit(EXIT_FAILURE);
    }
    strncpy(node->name, name, sizeof(node->name) - 1);
    node->name[sizeof(node->name) - 1] = '\0'; 
    node->esq = NULL;
    node->dir = NULL;
    node->altura = 1; // Novo nó é inicialmente adicionado na folha
    return node;
}

// Rotação à direita
AVLNode *rotacaoDir(AVLNode *y) {
    AVLNode *x = y->esq;
    AVLNode *T2 = x->dir;

    // Realiza rotação
    x->dir = y;
    y->esq = T2;

    // Atualiza alturas
    y->altura = max(alturaAVL(y->esq), alturaAVL(y->dir)) + 1;
    x->altura = max(alturaAVL(x->esq), alturaAVL(x->dir)) + 1;

    // Retorna nova raiz
    return x;
}

// Rotação à esquerda
AVLNode *rotacaoEsq(AVLNode *x) {
    AVLNode *y = x->dir;
    AVLNode *T2 = y->esq;

    // Realiza rotação
    y->esq = x;
    x->dir = T2;

    // Atualiza alturas
    x->altura = max(alturaAVL(x->esq), alturaAVL(x->dir)) + 1;
    y->altura = max(alturaAVL(y->esq), alturaAVL(y->dir)) + 1;

    // Retorna nova raiz
    return y;
}

// Obtém o fator de balanceamento de um nó (correção aqui)
int getFatorBalanceamento(const AVLNode *N) {
    if (N == NULL)
        return 0;
    return alturaAVL(N->esq) - alturaAVL(N->dir); // Correção: esquerda - direita
}

// Função para inserir um nome na Árvore AVL
AVLNode* inserirAVL(AVLNode* node, const char *name, Comparacao *c) {
    // 1. Perform the normal BST insertion
    if (node == NULL)
        return novoNo(name);

    // Comparação
    incrementarComparacao(c);
    int cmp = strcmp(name, node->name);
    if (cmp < 0)
        node->esq = inserirAVL(node->esq, name, c);
    else if (cmp > 0)
        node->dir = inserirAVL(node->dir, name, c);
    else // Não permite duplicatas
        return node;

    // 2. Atualiza a altura do ancestral
    node->altura = 1 + max(alturaAVL(node->esq), alturaAVL(node->dir));

    // 3. Obtém o fator de balanceamento
    int balance = getFatorBalanceamento(node);

    // 4. Se o nó estiver desbalanceado, então há 4 casos

    // Caso Esquerda Esquerda
    if (balance > 1 && strcmp(name, node->esq->name) < 0)
        return rotacaoDir(node);

    // Caso Direita Direita
    if (balance < -1 && strcmp(name, node->dir->name) > 0)
        return rotacaoEsq(node);

    // Caso Esquerda Direita
    if (balance > 1 && strcmp(name, node->esq->name) > 0) {
        node->esq = rotacaoEsq(node->esq);
        return rotacaoDir(node);
    }

    // Caso Direita Esquerda
    if (balance < -1 && strcmp(name, node->dir->name) < 0) {
        node->dir = rotacaoDir(node->dir);
        return rotacaoEsq(node);
    }

    // Retorna o ponteiro do nó (inalterado)
    return node;
}

// Estrutura para armazenar o resultado da pesquisa
typedef struct {
    char caminho[1000];
    int encontrado;
} ResultadoPesquisa;

// Função para realizar a pesquisa na Árvore AVL
ResultadoPesquisa pesquisarAVL(const AVLNode* root, const char *name, Comparacao *c) {
    ResultadoPesquisa result;
    result.encontrado = 0;
    result.caminho[0] = '\0';

    if (root == NULL) {
        strncpy(result.caminho, "raiz", sizeof(result.caminho) - 1);
        result.caminho[sizeof(result.caminho) - 1] = '\0';
        return result;
    }

    strcat(result.caminho, "raiz");

    AVLNode *current = (AVLNode*)root;

    while (current != NULL) {
        incrementarComparacao(c);
        int cmp = strcmp(name, current->name);
        if (cmp == 0) {
            result.encontrado = 1;
            break;
        } else if (cmp < 0) {
            current = current->esq;
            strcat(result.caminho, " esq");
        } else {
            current = current->dir;
            strcat(result.caminho, " dir");
        }
    }

    return result;
}

// Função para liberar a memória da Árvore AVL
void liberarAVL(AVLNode* node) {
    if (node == NULL)
        return;
    liberarAVL(node->esq);
    liberarAVL(node->dir);
    free(node);
}

// Função para remover a quebra de linha
void removerQuebraLinha(char *str) {
    size_t len = strlen(str);
    if(len > 0 && str[len-1] == '\n') {
        str[len-1] = '\0';
    }
}

// Função para buscar um Pokémon por ID
Pokemon* buscarPokemonPorId(int id, Pokemon *pokemons, int numPokemons) {
    for(int i = 0; i < numPokemons; i++) {
        if(pokemons[i].id == id)
            return &pokemons[i];
    }
    return NULL;
}

// Função para parsear uma linha CSV
int parseCSVLine(char *linha, Pokemon *pokemon) {
    // Supondo que os campos são separados por vírgula e o segundo campo é o nome
    // Adapte conforme o formato real do CSV
    char *token = strtok(linha, ",");
    if(token == NULL) return 0;
    pokemon->id = atoi(token);

    token = strtok(NULL, ",");
    if(token == NULL) return 0;
    strncpy(pokemon->name, token, sizeof(pokemon->name) - 1);
    pokemon->name[sizeof(pokemon->name) - 1] = '\0'; // Garantir terminação nula

    // Continue parseando outros campos se necessário

    return 1;
}

int main() {
    // Inicialização
    Pokemon pokemons[1000];
    int numPokemons = 0;

    // Leitura do arquivo CSV
    FILE *file = fopen("/tmp/pokemon.csv", "r");
    if(file == NULL) {
        fprintf(stderr, "Erro ao abrir o arquivo pokemon.csv\n");
        return EXIT_FAILURE;
    }

    char linha[1024];
    // Ignorar o cabeçalho
    if (fgets(linha, sizeof(linha), file) == NULL) {
        fprintf(stderr, "Erro ao ler o cabeçalho do arquivo CSV.\n");
        fclose(file);
        return EXIT_FAILURE;
    }

    while(fgets(linha, sizeof(linha), file)) {
        removerQuebraLinha(linha);
        if(strlen(linha) == 0)
            continue;
        if(numPokemons >= 1000) {
            fprintf(stderr, "Número máximo de Pokémons atingido (%d).\n", numPokemons);
            break;
        }
        if(parseCSVLine(linha, &pokemons[numPokemons]))
            numPokemons++;
    }
    fclose(file);

    // Criação da árvore AVL para inserções
    AVLNode *raiz = NULL;
    Comparacao compInsercao;
    inicializarComparacao(&compInsercao);

    // Leitura das inserções
    while(fgets(linha, sizeof(linha), stdin)) {
        removerQuebraLinha(linha);
        if(strcmp(linha, "FIM") == 0)
            break;
        if(strlen(linha) == 0)
            continue;
        int id = atoi(linha);
        Pokemon *poke = buscarPokemonPorId(id, pokemons, numPokemons);
        if(poke != NULL) {
            raiz = inserirAVL(raiz, poke->name, &compInsercao);
        }
    }

    // Leitura das pesquisas
    char **pesquisas = NULL;
    int numPesquisas = 0;
    while(fgets(linha, sizeof(linha), stdin)) {
        removerQuebraLinha(linha);
        if(strcmp(linha, "FIM") == 0)
            break;
        if(strlen(linha) == 0)
            continue;
        pesquisas = realloc(pesquisas, sizeof(char*) * (numPesquisas + 1));
        if(pesquisas == NULL) {
            fprintf(stderr, "Erro de realocação de memória para pesquisas.\n");
            liberarAVL(raiz);
            return EXIT_FAILURE;
        }
        pesquisas[numPesquisas] = strdup(linha);
        if(pesquisas[numPesquisas] == NULL) {
            fprintf(stderr, "Erro de alocação de memória para pesquisas.\n");
            // Liberar as pesquisas já alocadas
            for(int i = 0; i < numPesquisas; i++) {
                free(pesquisas[i]);
            }
            free(pesquisas);
            liberarAVL(raiz);
            return EXIT_FAILURE;
        }
        numPesquisas++;
    }

    // Início da medição de tempo
    clock_t inicio = clock();

    // Inicializar a contagem de comparações para pesquisas
    Comparacao compPesquisa;
    inicializarComparacao(&compPesquisa);

    // Realiza as pesquisas
    for(int i = 0; i < numPesquisas; i++) {
        ResultadoPesquisa res = pesquisarAVL(raiz, pesquisas[i], &compPesquisa);
        printf("%s %s\n", res.caminho, res.encontrado ? "SIM" : "NAO");
        free(pesquisas[i]);
    }
    free(pesquisas);

    // Fim da medição de tempo
    clock_t fim = clock();
    double tempoExecucao = ((double)(fim - inicio)) / CLOCKS_PER_SEC * 1000; // em milissegundos

    // Criação do arquivo de log
    char nomeArquivo[50];
    strcpy(nomeArquivo, "819886_avl.txt");
    FILE *logFile = fopen(nomeArquivo, "w");
    if(logFile == NULL) {
        fprintf(stderr, "Erro ao criar o arquivo de log\n");
        liberarAVL(raiz);
        return EXIT_FAILURE;
    }

    // Escrever no log: [ID] [Tempo Execução] [Comparações na Pesquisa]
    fprintf(logFile, "819886\t%.0f\t%d\n", tempoExecucao, getContador(&compPesquisa));
    fclose(logFile);

    // Libera a árvore AVL
    liberarAVL(raiz);

    return EXIT_SUCCESS;
}
