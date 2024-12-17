#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <ctype.h>

#define MAX_TAM_PILHA 1000
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
    p->tipos = NULL;
    p->numTipos = 0;
    p->habilidades = NULL;
    p->numHabilidades = 0;
    return p;
}

// Função para liberar a memória de um Pokémon
void liberarPokemon(Pokemon* p) {
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
            p->tipos[p->numTipos] = malloc(MAX_STRING);
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
        p->habilidades[p->numHabilidades] = malloc(MAX_STRING);
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
Pokemon* pokemons[10000]; // Array de ponteiros para Pokémons
int numPokemons = 0;

// Função para ler Pokémons de um arquivo
void lerPokemonsDeArquivo() {
    FILE *file = fopen("/tmp/pokemon.csv", "r");
    if (file == NULL) {
        fprintf(stderr, "Erro ao abrir o arquivo /tmp/pokemon.csv\n");
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

//=============== Estrutura Pilha ================
typedef struct {
    int topo;
    Pokemon* array[MAX_TAM_PILHA];
} Pilha;

// Função para criar uma nova pilha
Pilha* novaPilha() {
    Pilha* pilha = (Pilha*) malloc(sizeof(Pilha));
    pilha->topo = -1;
    return pilha;
}

// Função para empilhar um Pokémon
void push(Pilha* pilha, Pokemon* p) {
    if (pilha->topo >= MAX_TAM_PILHA - 1) {
        fprintf(stderr, "Erro ao empilhar: pilha cheia\n");
        exit(EXIT_FAILURE);
    }
    pilha->array[++pilha->topo] = p;
}

// Função para desempilhar um Pokémon
Pokemon* pop(Pilha* pilha) {
    if (pilha->topo < 0) {
        fprintf(stderr, "Erro ao desempilhar: pilha vazia\n");
        exit(EXIT_FAILURE);
    }
    return pilha->array[pilha->topo--];
}

// Função para mostrar a pilha
void mostrarPilha(Pilha* pilha) {
    for (int i = 0; i <= pilha->topo; i++) {
        printf("[%d] ", i);
        imprimirPokemon(pilha->array[i]);
    }
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
        }
    } else if (strcmp(operacao, "R") == 0) {
        if (pilha->topo >= 0) {
            Pokemon* removido = pop(pilha);
            printf("(R) %s\n", removido->nome);
        }
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
        processarComando(linha, pilha);
    }

    // Mostrar a pilha final
    mostrarPilha(pilha);

    liberarPokemons();
    free(pilha);

    return 0;
}
