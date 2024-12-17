#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <ctype.h>
#include <math.h>

#define MAX_TAM_FILA 5
#define MAX_POKEMON 10000
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
void limparString(char* dest, const char* src) {
    int di = 0;
    for (int si = 0; src[si] != '\0'; si++) {
        if (src[si] != '\'' && src[si] != '\"') {
            dest[di++] = src[si];
        }
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
void imprimirPokemon(Pokemon* p) {
    char tipoClean[MAX_STRING];
    char habilidadeClean[MAX_STRING];
    
    printf("[#%d -> %s: %s - [", p->id, p->nome, p->descricao);
    for (int i = 0; i < p->numTipos; i++) {
        // Remove quaisquer aspas restantes no tipo
        limparString(tipoClean, p->tipos[i]);
        printf("'%s'", tipoClean);
        if (i < p->numTipos - 1) printf(", ");
    }
    printf("] - [");
    for (int i = 0; i < p->numHabilidades; i++) {
        // Remove quaisquer aspas restantes na habilidade
        limparString(habilidadeClean, p->habilidades[i]);
        printf("'%s'", habilidadeClean);
        if (i < p->numHabilidades - 1) printf(", ");
    }
    printf("] - %.1lfkg - %.1lfm - %d%% - %s - %d gen] - %s\n",
           p->peso, p->altura, p->taxaCaptura,
           p->ehLegendario ? "true" : "false", p->geracao, p->dataCaptura);
}

//=============== Gerenciamento de Pokemons ================
Pokemon* pokemons[MAX_POKEMON]; // Array de ponteiros para Pokémons
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
            if (numPokemons >= MAX_POKEMON) break; // Limite máximo
        }
        fclose(file);
    }
}

// Função para liberar a memória de todos os Pokémons
void liberarPokemons() {
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

//=============== Estrutura Fila Circular ================
typedef struct {
    int inicio;
    int fim;
    int count;
    Pokemon* array[MAX_TAM_FILA];
} FilaCircular;

// Função para criar uma nova fila circular
FilaCircular* novaFilaCircular() {
    FilaCircular* fila = (FilaCircular*) malloc(sizeof(FilaCircular));
    if (fila == NULL) {
        fprintf(stderr, "Erro ao alocar memória para a fila circular.\n");
        exit(EXIT_FAILURE);
    }
    fila->inicio = 0;
    fila->fim = 0;
    fila->count = 0;
    for(int i = 0; i < MAX_TAM_FILA; i++) {
        fila->array[i] = NULL;
    }
    return fila;
}

// Função para calcular a média arredondada dos taxaCaptura na fila
int calcularMedia(FilaCircular* fila) {
    if (fila->count == 0) return 0;
    int soma = 0;
    for(int i = 0; i < fila->count; i++) {
        int index = (fila->inicio + i) % MAX_TAM_FILA;
        soma += fila->array[index]->taxaCaptura;
    }
    double media = (double)soma / fila->count;
    return (int)round(media);
}

// Função para inserir na fila circular (enqueue)
void enqueue(FilaCircular* fila, Pokemon* p, int* contador) {
    if (fila->count == MAX_TAM_FILA) {
        // Remover o mais antigo sem imprimir
        fila->array[fila->inicio] = NULL;
        fila->inicio = (fila->inicio + 1) % MAX_TAM_FILA;
        fila->count--;
    }

    // Inserir o novo Pokémon
    fila->array[fila->fim] = p;
    fila->fim = (fila->fim + 1) % MAX_TAM_FILA;
    fila->count++;

    // Incrementar o contador e imprimir
    (*contador)++;
    printf("Média: %d\n", calcularMedia(fila));
}

// Função para remover da fila circular (dequeue)
Pokemon* dequeue(FilaCircular* fila) {
    if (fila->count == 0) {
        fprintf(stderr, "Erro ao remover: fila vazia\n");
        exit(EXIT_FAILURE);
    }
    Pokemon* removido = fila->array[fila->inicio];
    printf("(R) %s\n", removido->nome);
    fila->array[fila->inicio] = NULL;
    fila->inicio = (fila->inicio + 1) % MAX_TAM_FILA;
    fila->count--;
    return removido;
}

// Função para mostrar a fila
void mostrarFila(FilaCircular* fila) {
    for(int i = 0; i < fila->count; i++) {
        int index = (fila->inicio + i) % MAX_TAM_FILA;
        printf("[%d] ", i);
        imprimirPokemon(fila->array[index]);
    }
}

//=============== Processamento de Comandos ================
void processarComando(char* comando, FilaCircular* fila, int* contador) {
    char operacao[2];
    int id;
    sscanf(comando, "%s", operacao);

    if (strcmp(operacao, "I") == 0) {
        sscanf(comando, "%*s %d", &id);
        Pokemon* p = buscarPokemonPorId(id);
        if (p != NULL) {
            enqueue(fila, p, contador);
        }
    } else if (strcmp(operacao, "R") == 0) {
        dequeue(fila);
    }
}

//=============== Função Principal ================
int main() {
    lerPokemonsDeArquivo();

    // Criação da fila circular
    FilaCircular* fila = novaFilaCircular();

    char linha[MAX_STRING];
    int contador = 0;

    // Leitura dos IDs iniciais até "FIM"
    while (fgets(linha, sizeof(linha), stdin) != NULL) {
        linha[strcspn(linha, "\n")] = '\0';
        if (strcmp(linha, "FIM") == 0) break;

        int id = atoi(linha);
        Pokemon* p = buscarPokemonPorId(id);
        if (p != NULL) {
            enqueue(fila, p, &contador);
        }
    }

    // Leitura do número de comandos
    if (fgets(linha, sizeof(linha), stdin) == NULL) {
        fprintf(stderr, "Erro ao ler o número de comandos\n");
        exit(EXIT_FAILURE);
    }
    int numComandos = atoi(linha);

    // Processamento dos comandos
    for (int i = 0; i < numComandos; i++) {
        if (fgets(linha, sizeof(linha), stdin) == NULL) {
            fprintf(stderr, "Erro ao ler o comando %d\n", i+1);
            exit(EXIT_FAILURE);
        }
        linha[strcspn(linha, "\n")] = '\0';
        processarComando(linha, fila, &contador);
    }

    // Mostrar a fila final
    mostrarFila(fila);

    // Liberar memória
    liberarPokemons();
    free(fila);

    return 0;
}
