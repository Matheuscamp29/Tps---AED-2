#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <ctype.h>

#define MAX_POKEMON 1000
#define MAX_LINHA 1024
#define MAX_CAMPOS 20
#define MAX_STRING 100

//=============== Estrutura Pokemon ================
typedef struct {
    int id;
    int geracao;
    int taxaCaptura;
    char nome[MAX_STRING];
    char descricao[MAX_STRING];
    char **tipos;
    int numTipos;
    char **habilidades;
    int numHabilidades;
    double peso;
    double altura;
    int ehLegendario;
    char dataCaptura[MAX_STRING];
} Pokemon;

// Função para criar um novo Pokémon
Pokemon* novoPokemon() {
    Pokemon* p = (Pokemon*) malloc(sizeof(Pokemon));
    if (p == NULL) {
        fprintf(stderr, "Erro ao alocar memória para um novo Pokémon.\n");
        exit(EXIT_FAILURE);
    }
    p->tipos = NULL;
    p->numTipos = 0;
    p->habilidades = NULL;
    p->numHabilidades = 0;
    return p;
}

// Função para liberar a memória de um Pokémon
void liberarPokemon(Pokemon* p) {
    if (p == NULL) return;
    
    for (int i = 0; i < p->numTipos; i++) {
        free(p->tipos[i]);
    }
    free(p->tipos);

    for (int i = 0; i < p->numHabilidades; i++) {
        free(p->habilidades[i]);
    }
    free(p->habilidades);

    free(p);
}

// Função auxiliar para limpar aspas de uma string
// Função auxiliar para remover aspas e espaços em branco de uma string
void limparString(char* dest, const char* src) {
    int di = 0;
    int start = 0;
    int end = strlen(src) - 1;

    // Remover aspas no início
    while (src[start] == '\'' || src[start] == '\"') start++;

    // Remover aspas no final
    while (end >= start && (src[end] == '\'' || src[end] == '\"' || isspace(src[end]))) end--;

    // Copiar caracteres sem aspas e sem espaços extras
    for (int si = start; si <= end; si++) {
        dest[di++] = src[si];
    }
    dest[di] = '\0';

   
}


// Função para ler e configurar os campos do Pokémon a partir de uma linha CSV
void lerPokemon(Pokemon* p, char linha[]) {
    char *campos[MAX_CAMPOS];
    int numCampos = 0;
    int dentroAspas = 0;
    char *campoInicio = linha;
    int i = 0;

    // Parse da linha CSV
    while (linha[i] != '\0') {
        if (linha[i] == '"') {
            dentroAspas = !dentroAspas;
        } else if (linha[i] == ',' && !dentroAspas) {
            linha[i] = '\0';
            if (numCampos < MAX_CAMPOS) {
                campos[numCampos++] = campoInicio;
            }
            campoInicio = &linha[i + 1];
        }
        i++;
    }
    if (numCampos < MAX_CAMPOS) {
        campos[numCampos++] = campoInicio;
    }

    // Preenchimento dos dados do Pokémon
    p->id = atoi(campos[0]);
    p->geracao = atoi(campos[1]);
    strncpy(p->nome, campos[2], MAX_STRING);
    p->nome[MAX_STRING - 1] = '\0';
    strncpy(p->descricao, campos[3], MAX_STRING);
    p->descricao[MAX_STRING - 1] = '\0';

    // Tipos
    p->numTipos = 0;
    p->tipos = NULL;
    for (int i = 4; i <= 5; i++) {
        if (strlen(campos[i]) > 0) {
            // Remove caracteres indesejados
            char tipoTemp[MAX_STRING];
            strcpy(tipoTemp, campos[i]);
            // Remover [ ] ' "
            char *remover = "[]'\"";
            char *ptr = tipoTemp;
            int len = strlen(remover);
            for (int j = 0; j < len; j++) {
                char *pos;
                while ((pos = strchr(ptr, remover[j])) != NULL) {
                    memmove(pos, pos + 1, strlen(pos));
                }
            }

            // Alocar e copiar o tipo limpo
            p->tipos = realloc(p->tipos, (p->numTipos + 1) * sizeof(char *));
            if (p->tipos == NULL) {
                fprintf(stderr, "Erro ao realocar memória para tipos.\n");
                exit(EXIT_FAILURE);
            }
            p->tipos[p->numTipos] = malloc(MAX_STRING);
            if (p->tipos[p->numTipos] == NULL) {
                fprintf(stderr, "Erro ao alocar memória para um tipo.\n");
                exit(EXIT_FAILURE);
            }
            strncpy(p->tipos[p->numTipos], tipoTemp, MAX_STRING);
            p->tipos[p->numTipos][MAX_STRING - 1] = '\0';
            p->numTipos++;
        }
    }

    // Habilidades
    p->numHabilidades = 0;
    p->habilidades = NULL;
    char *habilidadeToken = strtok(campos[6], ",");
    while (habilidadeToken != NULL) {
        // Remove caracteres indesejados
        char habilidadeTemp[MAX_STRING];
        strcpy(habilidadeTemp, habilidadeToken);
        // Remover [ ] ' "
        char *remover = "[]'\"";
        char *ptr = habilidadeTemp;
        int len = strlen(remover);
        for (int j = 0; j < len; j++) {
            char *pos;
            while ((pos = strchr(ptr, remover[j])) != NULL) {
                memmove(pos, pos + 1, strlen(pos));
            }
        }

        // Alocar e copiar a habilidade limpa
        p->habilidades = realloc(p->habilidades, (p->numHabilidades + 1) * sizeof(char *));
        if (p->habilidades == NULL) {
            fprintf(stderr, "Erro ao realocar memória para habilidades.\n");
            exit(EXIT_FAILURE);
        }
        p->habilidades[p->numHabilidades] = malloc(MAX_STRING);
        if (p->habilidades[p->numHabilidades] == NULL) {
            fprintf(stderr, "Erro ao alocar memória para uma habilidade.\n");
            exit(EXIT_FAILURE);
        }
        strncpy(p->habilidades[p->numHabilidades], habilidadeTemp, MAX_STRING);
        p->habilidades[p->numHabilidades][MAX_STRING - 1] = '\0';
        p->numHabilidades++;
        habilidadeToken = strtok(NULL, ",");
    }

    p->peso = atof(campos[7]);
    p->altura = atof(campos[8]);
    p->taxaCaptura = atoi(campos[9]);
    p->ehLegendario = atoi(campos[10]);
    strncpy(p->dataCaptura, campos[11], MAX_STRING);
    p->dataCaptura[MAX_STRING - 1] = '\0';
}

// Função para imprimir um Pokémon no formato especificado
// Função para imprimir um Pokémon no formato especificado
void imprimirPokemon(Pokemon* p) {
    char tipoClean[MAX_STRING];
    char habilidadeClean[MAX_STRING];
    
    printf("[#%d -> %s: %s - [", p->id, p->nome, p->descricao);
    for (int i = 0; i < p->numTipos; i++) {
        // Remove quaisquer aspas e espaços extras no tipo
        limparString(tipoClean, p->tipos[i]);
        printf("'%s'", tipoClean);
        if (i < p->numTipos - 1) printf(", ");
    }
    printf("] - [");
    for (int i = 0; i < p->numHabilidades; i++) {
        // Remove quaisquer aspas e espaços extras na habilidade
        limparString(habilidadeClean, p->habilidades[i]);
        printf("'%s'", habilidadeClean);
        if (i < p->numHabilidades - 1) printf(", ");
    }
    printf("] - %.1lfkg - %.1lfm - %d%% - %s - %d gen] - %s\n",
           p->peso, p->altura, p->taxaCaptura,
           p->ehLegendario ? "true" : "false", p->geracao, p->dataCaptura);
}


//=============== Gerenciamento de Pokemons ================
Pokemon* pokemons[10000]; // Array de ponteiros para Pokémons
int numPokemons = 0;

// Função para ler Pokémons de um arquivo
void lerPokemonsDeArquivo() {
    FILE *file = fopen("/tmp/pokemon.csv", "r");
    if (file == NULL) {
        fprintf(stderr, "Erro ao abrir o arquivo /tmp/pokemon.csv\n");
        exit(EXIT_FAILURE);
    } else {
        char linha[MAX_LINHA];
        fgets(linha, sizeof(linha), file); // Pula o cabeçalho
        while (fgets(linha, sizeof(linha), file) != NULL) {
            linha[strcspn(linha, "\n")] = '\0';
            if (strcmp(linha, "FIM") == 0) break;
            Pokemon* p = novoPokemon();
            lerPokemon(p, linha);
            pokemons[numPokemons++] = p;
            if (numPokemons >= 10000) break; // Limite máximo
        }
        fclose(file);
    }
}

// Função para liberar a memória de todos os Pokémons
void liberarPokemonsFunc() { // Renomeada para evitar conflito com a função remover
    for (int i = 0; i < numPokemons; i++) {
        liberarPokemon(pokemons[i]);
    }
}

// Função para buscar um Pokémon por ID
Pokemon* buscarPokemonPorId(int id) {
    for (int i = 0; i < numPokemons; i++) {
        if (pokemons[i]->id == id) {
            return pokemons[i];
        }
    }
    return NULL;
}

//=============== Estrutura Pilha Dinâmica ================
typedef struct Nodo {
    Pokemon* p;
    struct Nodo* next;
} Nodo;

typedef struct {
    Nodo* topo;
    int tamanho;
} Pilha;

// Função para criar uma nova pilha
Pilha* novaPilha() {
    Pilha* pilha = (Pilha*) malloc(sizeof(Pilha));
    if (pilha == NULL) {
        fprintf(stderr, "Erro ao alocar memória para a pilha.\n");
        exit(EXIT_FAILURE);
    }
    pilha->topo = NULL;
    pilha->tamanho = 0;
    return pilha;
}

// Função para empilhar um Pokémon
void push(Pilha* pilha, Pokemon* p) {
    Nodo* novoNodo = (Nodo*) malloc(sizeof(Nodo));
    if (novoNodo == NULL) {
        fprintf(stderr, "Erro ao alocar memória para um novo nodo na pilha.\n");
        exit(EXIT_FAILURE);
    }
    novoNodo->p = p;
    novoNodo->next = pilha->topo;
    pilha->topo = novoNodo;
    pilha->tamanho++;
}

// Função para desempilhar um Pokémon
Pokemon* pop(Pilha* pilha) {
    if (pilha->topo == NULL) {
        fprintf(stderr, "Erro ao desempilhar: pilha vazia\n");
        exit(EXIT_FAILURE);
    }
    Nodo* removido = pilha->topo;
    pilha->topo = removido->next;
    Pokemon* p = removido->p;
    free(removido);
    pilha->tamanho--;
    return p;
}

// Função para mostrar a pilha
void mostrarPilha(Pilha* pilha) {
    if (pilha->tamanho == 0) {
        return; // Pilha vazia, nada a imprimir
    }

    // Alocar um array temporário para armazenar os Pokémons
    Pokemon** array = (Pokemon**) malloc(pilha->tamanho * sizeof(Pokemon*));
    if (array == NULL) {
        fprintf(stderr, "Erro ao alocar memória para o array temporário.\n");
        exit(EXIT_FAILURE);
    }

    // Percorrer a pilha e armazenar os Pokémons no array de forma invertida
    Nodo* current = pilha->topo;
    int index = pilha->tamanho - 1;
    while (current != NULL) {
        array[index--] = current->p;
        current = current->next;
    }

    // Imprimir os Pokémons na ordem de inserção
    for (int i = 0; i < pilha->tamanho; i++) {
        printf("[%d] ", i);
        imprimirPokemon(array[i]);
    }

    // Liberar a memória alocada para o array temporário
    free(array);
}

// Função para liberar a memória da pilha
void liberarPilha(Pilha* pilha) {
    Nodo* current = pilha->topo;
    while (current != NULL) {
        Nodo* temp = current;
        current = current->next;
        free(temp);
    }
    free(pilha);
}

//=============== Processamento de Comandos ================
void processarComando(char* comando, Pilha* pilha) {
    char operacao[2];
    int id;
    sscanf(comando, "%s", operacao);

    if (strcmp(operacao, "I") == 0) {
        sscanf(comando, "%*s %d", &id);
        Pokemon* p = buscarPokemonPorId(id);
        if (p != NULL) {
            push(pilha, p);
        } else {
            fprintf(stderr, "Pokémon com ID %d não encontrado.\n", id);
        }
    } else if (strcmp(operacao, "R") == 0) {
        if (pilha->topo != NULL) {
            Pokemon* removido = pop(pilha);
            printf("(R) %s\n", removido->nome);
        } else {
            fprintf(stderr, "Erro: Tentativa de desempilhar de uma pilha vazia.\n");
        }
    } else {
        fprintf(stderr, "Operação inválida: %s\n", operacao);
        exit(EXIT_FAILURE);
    }
}

//=============== Função Principal ================
int main() {
    lerPokemonsDeArquivo();

    // Criação da pilha
    Pilha* pilha = novaPilha();

    char linha[MAX_STRING];

    // Leitura dos IDs iniciais até "FIM"
    while (fgets(linha, sizeof(linha), stdin) != NULL) {
        linha[strcspn(linha, "\n")] = '\0';
        if (strcmp(linha, "FIM") == 0) break;
        
        int id = atoi(linha);
        Pokemon* p = buscarPokemonPorId(id);
        if (p != NULL) {
            push(pilha, p);
        } else {
            fprintf(stderr, "Pokémon com ID %d não encontrado.\n", id);
        }
    }

    // Leitura do número de comandos
    if (fgets(linha, sizeof(linha), stdin) == NULL) {
        fprintf(stderr, "Erro ao ler o número de comandos\n");
        liberarPilha(pilha);
        liberarPokemonsFunc();
        exit(EXIT_FAILURE);
    }
    int numComandos = atoi(linha);

    // Processamento dos comandos
    for (int i = 0; i < numComandos; i++) {
        if (fgets(linha, sizeof(linha), stdin) == NULL) {
            fprintf(stderr, "Erro ao ler o comando %d\n", i+1);
            liberarPilha(pilha);
            liberarPokemonsFunc();
            exit(EXIT_FAILURE);
        }
        linha[strcspn(linha, "\n")] = '\0';
        processarComando(linha, pilha);
    }

    // Mostrar a pilha final
    mostrarPilha(pilha);

    // Liberação de memória
    liberarPilha(pilha);
    liberarPokemonsFunc();

    return 0;
}
