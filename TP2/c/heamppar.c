#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <ctype.h>
#include <time.h>

#define MAX_POKEMON 1000
#define MAX_LINE_LENGTH 1024
#define MAX_FIELDS 20
#define MAX_STRING_LENGTH 100
#define K 10

typedef struct {
    int id;
    int generation;
    int captureRate;
    char name[MAX_STRING_LENGTH];
    char description[MAX_STRING_LENGTH];
    char **types;
    int numTypes;
    char **abilities;
    int numAbilities;
    double weight;
    double height;
    int isLegendary;
    char captureDate[MAX_STRING_LENGTH];
} Pokemon;

long comparisons = 0;
long movements = 0;

int isNumeric(const char *str) {
    int result = 1;
    int i = 0;
    if (str == NULL || *str == '\0') {
        result = 0;
    } else {
        if (str[0] == '-' || str[0] == '+') i++;
        while (str[i] != '\0' && result == 1) {
            if (!isdigit(str[i])) {
                result = 0;
            }
            i++;
        }
    }
    return result;
}

void trim(char *str) {
    int len = strlen(str);
    int start = 0;
    while (isspace((unsigned char)str[start])) start++;
    int end = len - 1;
    while (end >= start && isspace((unsigned char)str[end])) end--;
    int i = 0;
    while (start <= end) {
        str[i++] = str[start++];
    }
    str[i] = '\0';
}

int isDouble(const char *str) {
    char *endptr;
    strtod(str, &endptr);
    return (*endptr == '\0' && endptr != str);
}

void removeChars(char *str, const char *charsToRemove) {
    int src = 0, dst = 0;
    while (str[src] != '\0') {
        int shouldRemove = 0;
        int i = 0;
        while (charsToRemove[i] != '\0') {
            if (str[src] == charsToRemove[i]) {
                shouldRemove = 1;
                break;
            }
            i++;
        }
        if (!shouldRemove) {
            str[dst++] = str[src];
        }
        src++;
    }
    str[dst] = '\0';
}

int parseCSVLine(char *line, char **fields, int maxFields) {
    int numFields = 0;
    int inQuotes = 0;
    char *fieldStart = line;
    int i = 0;
    while (line[i] != '\0') {
        if (line[i] == '"') {
            inQuotes = !inQuotes;
        } else if (line[i] == ',' && !inQuotes) {
            line[i] = '\0';
            if (numFields < maxFields) {
                fields[numFields++] = fieldStart;
            }
            fieldStart = &line[i + 1];
        }
        i++;
    }
    if (numFields < maxFields) {
        fields[numFields++] = fieldStart;
    }
    return numFields;
}

int lerPokemons(const char *filename, Pokemon *pokemons) {
    FILE *file = fopen(filename, "r");
    int numPokemons = 0;
    if (file != NULL) {
        char line[MAX_LINE_LENGTH];
        if (fgets(line, sizeof(line), file) != NULL) {
            int fim = 0;
            while (!fim && fgets(line, sizeof(line), file) != NULL) {
                line[strcspn(line, "\n")] = '\0';
                if (strcmp(line, "FIM") == 0) {
                    fim = 1;
                } else if (strlen(line) > 0) {
                    char *fields[MAX_FIELDS];
                    int numFields = parseCSVLine(line, fields, MAX_FIELDS);
                    if (numFields >= 12) {
                        Pokemon p;
                        p.id = isNumeric(fields[0]) ? atoi(fields[0]) : 0;
                        p.generation = isNumeric(fields[1]) ? atoi(fields[1]) : 0;
                        strncpy(p.name, fields[2], MAX_STRING_LENGTH);
                        p.name[MAX_STRING_LENGTH - 1] = '\0';
                        strncpy(p.description, fields[3], MAX_STRING_LENGTH);
                        p.description[MAX_STRING_LENGTH - 1] = '\0';
                        p.numTypes = 0;
                        p.types = NULL;
                        if (strlen(fields[4]) > 0) {
                            trim(fields[4]);
                            removeChars(fields[4], "[]'\"");
                            p.types = realloc(p.types, (p.numTypes + 1) * sizeof(char *));
                            p.types[p.numTypes] = malloc(MAX_STRING_LENGTH);
                            strncpy(p.types[p.numTypes], fields[4], MAX_STRING_LENGTH);
                            p.types[p.numTypes][MAX_STRING_LENGTH - 1] = '\0';
                            p.numTypes++;
                        }
                        if (strlen(fields[5]) > 0) {
                            trim(fields[5]);
                            removeChars(fields[5], "[]'\"");
                            p.types = realloc(p.types, (p.numTypes + 1) * sizeof(char *));
                            p.types[p.numTypes] = malloc(MAX_STRING_LENGTH);
                            strncpy(p.types[p.numTypes], fields[5], MAX_STRING_LENGTH);
                            p.types[p.numTypes][MAX_STRING_LENGTH - 1] = '\0';
                            p.numTypes++;
                        }
                        removeChars(fields[6], "[]'\"");
                        char *abilityToken = strtok(fields[6], ",");
                        p.numAbilities = 0;
                        p.abilities = NULL;
                        while (abilityToken != NULL) {
                            trim(abilityToken);
                            p.abilities = realloc(p.abilities, (p.numAbilities + 1) * sizeof(char *));
                            p.abilities[p.numAbilities] = malloc(MAX_STRING_LENGTH);
                            strncpy(p.abilities[p.numAbilities], abilityToken, MAX_STRING_LENGTH);
                            p.abilities[p.numAbilities][MAX_STRING_LENGTH - 1] = '\0';
                            p.numAbilities++;
                            abilityToken = strtok(NULL, ",");
                        }
                        p.weight = isDouble(fields[7]) ? atof(fields[7]) : 0.0;
                        p.height = isDouble(fields[8]) ? atof(fields[8]) : 0.0;
                        p.captureRate = isNumeric(fields[9]) ? atoi(fields[9]) : 0;
                        p.isLegendary = isNumeric(fields[10]) ? atoi(fields[10]) : 0;
                        strncpy(p.captureDate, fields[11], MAX_STRING_LENGTH);
                        p.captureDate[MAX_STRING_LENGTH - 1] = '\0';
                        if (numPokemons < MAX_POKEMON) {
                            pokemons[numPokemons++] = p;
                        } else {
                            fim = 1;
                        }
                    }
                }
            }
        }
        fclose(file);
    }
    return numPokemons;
}

int buscarPokemonPorId(int id, Pokemon *pokemons, int numPokemons) {
    for (int i = 0; i < numPokemons; i++) {
        if (pokemons[i].id == id) {
            return i;
        }
    }
    return -1;
}

void imprimirPokemon(Pokemon *p) {
    char formattedTypes[500] = "";
    for (int i = 0; i < p->numTypes; i++) {
        strcat(formattedTypes, "'");
        strcat(formattedTypes, p->types[i]);
        strcat(formattedTypes, "'");
        if (i < p->numTypes - 1) {
            strcat(formattedTypes, ", ");
        }
    }
    char formattedAbilities[500] = "";
    for (int i = 0; i < p->numAbilities; i++) {
        strcat(formattedAbilities, "'");
        strcat(formattedAbilities, p->abilities[i]);
        strcat(formattedAbilities, "'");
        if (i < p->numAbilities - 1) {
            strcat(formattedAbilities, ", ");
        }
    }
    printf("[#%d -> %s: %s - [%s] - [%s] - %.1lfkg - %.1lfm - %d%% - %s - %d gen] - %s\n",
           p->id, p->name, p->description, formattedTypes, formattedAbilities,
           p->weight, p->height, p->captureRate,
           p->isLegendary ? "true" : "false", p->generation, p->captureDate);
}

int comparePokemons(const Pokemon *p1, const Pokemon *p2) {
    if (p1->height < p2->height) {
        return -1;
    } else if (p1->height > p2->height) {
        return 1;
    } else {
        return strcmp(p1->name, p2->name);
    }
}

void heapify(Pokemon *array, int n, int i) {
    int largest = i;
    int l = 2 * i + 1;
    int r = 2 * i + 2;

    if (l < n) {
        comparisons++;
        if (comparePokemons(&array[l], &array[largest]) > 0) {
            largest = l;
        }
    }

    if (r < n) {
        comparisons++;
        if (comparePokemons(&array[r], &array[largest]) > 0) {
            largest = r;
        }
    }

    if (largest != i) {
        Pokemon temp = array[i];
        array[i] = array[largest];
        array[largest] = temp;
        movements += 3;

        heapify(array, n, largest);
    }
}

void heapSort(Pokemon *array, int n) {
    for (int i = n / 2 - 1; i >= 0; i--) {
        heapify(array, n, i);
    }

    for (int i = n - 1; i >= 0; i--) {
        Pokemon temp = array[0];
        array[0] = array[i];
        array[i] = temp;
        movements += 3;

        heapify(array, i, 0);
    }
}

int main() {
    Pokemon pokemons[MAX_POKEMON];
    int numPokemons = lerPokemons("/tmp/pokemon.csv", pokemons);
    Pokemon selectedPokemons[MAX_POKEMON];
    int selectedCount = 0;
    char input[MAX_STRING_LENGTH];
    while (scanf("%s", input) != EOF) {
        if (strcmp(input, "FIM") == 0) {
            break;
        } else {
            int id = atoi(input);
            int index = buscarPokemonPorId(id, pokemons, numPokemons);
            if (index != -1) {
                selectedPokemons[selectedCount++] = pokemons[index];
            }
        }
    }
    clock_t start = clock();
    heapSort(selectedPokemons, selectedCount);
    clock_t end = clock();
    double executionTime = ((double)(end - start)) / CLOCKS_PER_SEC * 1000.0;
    for (int i = 0; i < selectedCount; i++) {
        imprimirPokemon(&selectedPokemons[i]);
    }
    char *matricula = "819886";
    char logFileName[50];
    sprintf(logFileName, "%s_heapsort.txt", matricula);
    FILE *logFile = fopen(logFileName, "w");
    if (logFile) {
        fprintf(logFile, "%s\t%ld\t%ld\t%.2lf\n", matricula, comparisons, movements, executionTime);
        fclose(logFile);
    }
    for (int i = 0; i < numPokemons; i++) {
        for (int j = 0; j < pokemons[i].numTypes; j++) {
            free(pokemons[i].types[j]);
        }
        free(pokemons[i].types);
        for (int j = 0; j < pokemons[i].numAbilities; j++) {
            free(pokemons[i].abilities[j]);
        }
        free(pokemons[i].abilities);
    }
    return 0;
}
