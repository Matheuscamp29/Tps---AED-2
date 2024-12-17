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
Pokemon* pokemons[801]; // Array de ponteiros para Pokémons
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
            if (numPokemons >= 801) break; // Limite máximo
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

//=============== Lista de Pokemons (Lista Dinâmica Simples) ================
typedef struct Nodo {
    Pokemon* p;
    struct Nodo* next;
} Nodo;

typedef struct {
    Nodo* head;
    int n;
} Lista;

// Função para criar uma nova lista
Lista* novaLista() {
    Lista* lista = (Lista*) malloc(sizeof(Lista));
    lista->head = NULL;
    lista->n = 0;
    return lista;
}

// Função para inserir no início da lista
void inserirInicioLista(Lista* lista, Pokemon* p) {
    Nodo* novo = (Nodo*) malloc(sizeof(Nodo));
    novo->p = p;
    novo->next = lista->head;
    lista->head = novo;
    lista->n++;
}

// Função para inserir no fim da lista
void inserirFimLista(Lista* lista, Pokemon* p) {
    Nodo* novo = (Nodo*) malloc(sizeof(Nodo));
    novo->p = p;
    novo->next = NULL;
    if (lista->head == NULL) {
        lista->head = novo;
    } else {
        Nodo* current = lista->head;
        while (current->next != NULL) {
            current = current->next;
        }
        current->next = novo;
    }
    lista->n++;
}

// Função para inserir em uma posição específica da lista
void inserirPosicaoLista(Lista* lista, Pokemon* p, int pos) {
    if (pos < 0 || pos > lista->n) {
        fprintf(stderr, "Erro ao inserir na posição %d: posição inválida\n", pos);
        exit(EXIT_FAILURE);
    }
    if (pos == 0) {
        inserirInicioLista(lista, p);
        return;
    }
    Nodo* novo = (Nodo*) malloc(sizeof(Nodo));
    novo->p = p;
    Nodo* current = lista->head;
    for (int i = 0; i < pos - 1; i++) {
        current = current->next;
    }
    novo->next = current->next;
    current->next = novo;
    lista->n++;
}

// Função para remover do início da lista
Pokemon* removerInicioLista(Lista* lista) {
    if (lista->head == NULL) {
        fprintf(stderr, "Erro ao remover do início: lista vazia\n");
        exit(EXIT_FAILURE);
    }
    Nodo* removido = lista->head;
    lista->head = removido->next;
    Pokemon* p = removido->p;
    free(removido);
    lista->n--;
    return p;
}

// Função para remover do fim da lista
Pokemon* removerFimLista(Lista* lista) {
    if (lista->head == NULL) {
        fprintf(stderr, "Erro ao remover do fim: lista vazia\n");
        exit(EXIT_FAILURE);
    }
    Nodo* current = lista->head;
    Nodo* anterior = NULL;
    while (current->next != NULL) {
        anterior = current;
        current = current->next;
    }
    if (anterior == NULL) { // Apenas um elemento
        lista->head = NULL;
    } else {
        anterior->next = NULL;
    }
    Pokemon* p = current->p;
    free(current);
    lista->n--;
    return p;
}

// Função para remover de uma posição específica da lista
Pokemon* removerPosicaoLista(Lista* lista, int pos) {
    if (lista->head == NULL || pos < 0 || pos >= lista->n) {
        fprintf(stderr, "Erro ao remover da posição %d: posição inválida ou lista vazia\n", pos);
        exit(EXIT_FAILURE);
    }
    Nodo* removido;
    Pokemon* p;
    if (pos == 0) {
        removido = lista->head;
        lista->head = removido->next;
        p = removido->p;
        free(removido);
    } else {
        Nodo* current = lista->head;
        for (int i = 0; i < pos -1; i++) {
            current = current->next;
        }
        removido = current->next;
        current->next = removido->next;
        p = removido->p;
        free(removido);
    }
    lista->n--;
    return p;
}

// Função para imprimir toda a lista de Pokémons
void mostrarLista(Lista* lista) {
    Nodo* current = lista->head;
    int index = 0;
    while (current != NULL) {
        printf("[%d] ", index);
        imprimirPokemon(current->p);
        current = current->next;
        index++;
    }
}

//=============== Processamento de Comandos ================
void processarComando(char* comando, Lista* lista) {
    char operacao[3];
    int pos, id;
    sscanf(comando, "%s", operacao);

    if (strcmp(operacao, "II") == 0) {
        sscanf(comando, "%*s %d", &id);
        Pokemon* p = buscarPokemonPorId(id);
        if (p != NULL) {
            inserirInicioLista(lista, p);
        }
    } else if (strcmp(operacao, "IF") == 0) {
        sscanf(comando, "%*s %d", &id);
        Pokemon* p = buscarPokemonPorId(id);
        if (p != NULL) {
            inserirFimLista(lista, p);
        }
    } else if (strcmp(operacao, "I*") == 0) {
        sscanf(comando, "%*s %d %d", &pos, &id);
        Pokemon* p = buscarPokemonPorId(id);
        if (p != NULL) {
            inserirPosicaoLista(lista, p, pos);
        }
    } else if (strcmp(operacao, "RI") == 0) {
        Pokemon* removido = removerInicioLista(lista);
        printf("(R) %s\n", removido->nome);
    } else if (strcmp(operacao, "RF") == 0) {
        Pokemon* removido = removerFimLista(lista);
        printf("(R) %s\n", removido->nome);
    } else if (strcmp(operacao, "R*") == 0) {
        sscanf(comando, "%*s %d", &pos);
        Pokemon* removido = removerPosicaoLista(lista, pos);
        printf("(R) %s\n", removido->nome);
    }
}

//=============== Função Principal ================
int main() {
    lerPokemonsDeArquivo();

    // Criação da lista
    Lista* lista = novaLista();

    char linha[MAX_STRING];
    int linhaNum;

    // Leitura dos IDs iniciais
    fgets(linha, sizeof(linha), stdin); // ID ou "FIM"
    linha[strcspn(linha, "\n")] = '\0';
    while (strcmp(linha, "FIM") != 0) { // Correção na condição de parada
        int id = atoi(linha);
        Pokemon* p = buscarPokemonPorId(id);
        if (p != NULL) {
            inserirFimLista(lista, p);
        }
        fgets(linha, sizeof(linha), stdin); // Próximo ID ou "FIM"
        linha[strcspn(linha, "\n")] = '\0';
    }

    // Leitura do número de comandos
    fgets(linha, sizeof(linha), stdin); // Número de comandos
    int numComandos = atoi(linha);

    // Processamento dos comandos
    for (int i = 0; i < numComandos; i++) {
        fgets(linha, sizeof(linha), stdin); // Comando
        linha[strcspn(linha, "\n")] = '\0';
        processarComando(linha, lista);
    }

    // Mostrar a lista final
    mostrarLista(lista);

    // Liberação de memória
    liberarPokemons();
    
    // Liberação da lista ligada
    Nodo* current = lista->head;
    while (current != NULL) {
        Nodo* temp = current;
        current = current->next;
        free(temp);
    }
    free(lista);

    return 0;
}
