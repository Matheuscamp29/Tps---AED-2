#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <ctype.h>
#include <time.h>

#define MAX_POKEMON 1000
#define MAX_LINE_LENGTH 1024
#define MAX_FIELDS 20
#define MAX_STRING_LENGTH 100
#define MAX_ABILITIES_LENGTH 500

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
    char abilitiesStr[MAX_ABILITIES_LENGTH];
    double weight;
    double height;
    int isLegendary;
    char captureDate[MAX_STRING_LENGTH];
} Pokemon;

long comparisons = 0;
long movements = 0;

int isNumeric(const char *str) {
    int i = 0;
    if (str[0] == '-' || str[0] == '+') i++;
    for (; str[i] != '\0'; i++) {
        if (!isdigit((unsigned char)str[i])) return 0;
    }
    return 1;
}

void trim(char *str) {
    int len = strlen(str);
    int start = 0, end = len - 1;
    while (isspace((unsigned char)str[start])) start++;
    while (end >= start && isspace((unsigned char)str[end])) end--;
    memmove(str, str + start, end - start + 1);
    str[end - start + 1] = '\0';
}

int isDouble(const char *str) {
    char *endptr;
    strtod(str, &endptr);
    return (*endptr == '\0' && endptr != str);
}

void removeChars(char *str, const char *charsToRemove) {
    int src = 0, dst = 0;
    while (str[src]) {
        if (!strchr(charsToRemove, str[src])) {
            str[dst++] = str[src];
        }
        src++;
    }
    str[dst] = '\0';
}

int parseCSVLine(char *line, char **fields, int maxFields) {
    int numFields = 0, inQuotes = 0;
    char *fieldStart = line;
    for (int i = 0; line[i]; i++) {
        if (line[i] == '"') {
            inQuotes = !inQuotes;
        } else if (line[i] == ',' && !inQuotes) {
            line[i] = '\0';
            if (numFields < maxFields) {
                fields[numFields++] = fieldStart;
            }
            fieldStart = line + i + 1;
        }
    }
    if (numFields < maxFields) {
        fields[numFields++] = fieldStart;
    }
    return numFields;
}

int lerPokemons(const char *filename, Pokemon *pokemons) {
    FILE *file = fopen(filename, "r");
    int numPokemons = 0;
    if (file) {
        char line[MAX_LINE_LENGTH];
        fgets(line, sizeof(line), file);  
        while (fgets(line, sizeof(line), file)) {
            line[strcspn(line, "\n")] = '\0';
            if (strcmp(line, "FIM") == 0) break;
            if (strlen(line) > 0) {
                char *fields[MAX_FIELDS];
                int numFields = parseCSVLine(line, fields, MAX_FIELDS);
                if (numFields >= 12) {
                    Pokemon p;
                    p.id = isNumeric(fields[0]) ? atoi(fields[0]) : 0;
                    p.generation = isNumeric(fields[1]) ? atoi(fields[1]) : 0;
                    strncpy(p.name, fields[2], MAX_STRING_LENGTH - 1);
                    p.name[MAX_STRING_LENGTH - 1] = '\0';
                    strncpy(p.description, fields[3], MAX_STRING_LENGTH - 1);
                    p.description[MAX_STRING_LENGTH - 1] = '\0';
                    p.numTypes = 0;
                    p.types = NULL;
                    for (int i = 4; i <= 5; i++) {
                        if (strlen(fields[i]) > 0) {
                            trim(fields[i]);
                            removeChars(fields[i], "[]'\"");
                            p.types = realloc(p.types, (p.numTypes + 1) * sizeof(char *));
                            p.types[p.numTypes] = malloc(MAX_STRING_LENGTH);
                            strncpy(p.types[p.numTypes], fields[i], MAX_STRING_LENGTH - 1);
                            p.types[p.numTypes][MAX_STRING_LENGTH - 1] = '\0';
                            p.numTypes++;
                        }
                    }
                    removeChars(fields[6], "[]'\"");
                    strncpy(p.abilitiesStr, fields[6], MAX_ABILITIES_LENGTH - 1);
                    p.abilitiesStr[MAX_ABILITIES_LENGTH - 1] = '\0';
                    char abilitiesCopy[MAX_ABILITIES_LENGTH];
                    strncpy(abilitiesCopy, p.abilitiesStr, MAX_ABILITIES_LENGTH - 1);
                    abilitiesCopy[MAX_ABILITIES_LENGTH - 1] = '\0';
                    char *abilityToken = strtok(abilitiesCopy, ",");
                    p.numAbilities = 0;
                    p.abilities = NULL;
                    while (abilityToken) {
                        trim(abilityToken);
                        p.abilities = realloc(p.abilities, (p.numAbilities + 1) * sizeof(char *));
                        p.abilities[p.numAbilities] = malloc(MAX_STRING_LENGTH);
                        strncpy(p.abilities[p.numAbilities], abilityToken, MAX_STRING_LENGTH - 1);
                        p.abilities[p.numAbilities][MAX_STRING_LENGTH - 1] = '\0';
                        p.numAbilities++;
                        abilityToken = strtok(NULL, ",");
                    }
                    p.weight = isDouble(fields[7]) ? atof(fields[7]) : 0.0;
                    p.height = isDouble(fields[8]) ? atof(fields[8]) : 0.0;
                    p.captureRate = isNumeric(fields[9]) ? atoi(fields[9]) : 0;
                    p.isLegendary = isNumeric(fields[10]) ? atoi(fields[10]) : 0;
                    strncpy(p.captureDate, fields[11], MAX_STRING_LENGTH - 1);
                    p.captureDate[MAX_STRING_LENGTH - 1] = '\0';
                    if (numPokemons < MAX_POKEMON) {
                        pokemons[numPokemons++] = p;
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
        if (i < p->numTypes - 1) strcat(formattedTypes, ", ");
    }
    char formattedAbilities[500] = "";
    for (int i = 0; i < p->numAbilities; i++) {
        strcat(formattedAbilities, "'");
        strcat(formattedAbilities, p->abilities[i]);
        strcat(formattedAbilities, "'");
        if (i < p->numAbilities - 1) strcat(formattedAbilities, ", ");
    }
    printf("[#%d -> %s: %s - [%s] - [%s] - %.1lfkg - %.1lfm - %d%% - %s - %d gen] - %s\n",
           p->id, p->name, p->description, formattedTypes, formattedAbilities, p->weight,
           p->height, p->captureRate, p->isLegendary ? "true" : "false", p->generation,
           p->captureDate);
}

void swap(Pokemon *a, Pokemon *b) {
    Pokemon temp = *a;
    *a = *b;
    *b = temp;
    movements += 3;
}

int getMaxStringLength(Pokemon *array, int n) {
    int max = strlen(array[0].abilitiesStr);
    for (int i = 1; i < n; i++) {
        int len = strlen(array[i].abilitiesStr);
        if (len > max) max = len;
    }
    return max;
}

void countingSortByChar(Pokemon *array, int n, int pos) {
    Pokemon *output = malloc(n * sizeof(Pokemon));
    int count[257] = {0};

    for (int i = 0; i < n; i++) {
        int index = pos < strlen(array[i].abilitiesStr) ? (unsigned char)array[i].abilitiesStr[pos] + 1 : 0;
        count[index]++;
        movements++;
    }

    for (int i = 1; i < 257; i++) {
        count[i] += count[i - 1];
    }

    for (int i = n - 1; i >= 0; i--) {
        int index = pos < strlen(array[i].abilitiesStr) ? (unsigned char)array[i].abilitiesStr[pos] + 1 : 0;
        output[count[index] - 1] = array[i];
        count[index]--;
        movements++;
    }

    for (int i = 0; i < n; i++) {
        array[i] = output[i];
        movements++;
    }

    free(output);
}

void radixSort(Pokemon *array, int n) {
    int maxLen = getMaxStringLength(array, n);
    for (int pos = maxLen - 1; pos >= 0; pos--) {
        countingSortByChar(array, n, pos);
    }

    int i = 0;
    while (i < n - 1) {
        int j = i;
        while (j < n - 1 && strcmp(array[j].abilitiesStr, array[j + 1].abilitiesStr) == 0) {
            j++;
        }
        if (j > i) {
            // Sort from index i to j by name
            for (int a = i; a <= j; a++) {
                for (int b = a + 1; b <= j; b++) {
                    comparisons++;
                    if (strcmp(array[a].name, array[b].name) > 0) {
                        swap(&array[a], &array[b]);
                    }
                }
            }
        }
        i = j + 1;
    }
}

int main() {
    Pokemon pokemons[MAX_POKEMON];
    int numPokemons = lerPokemons("/tmp/pokemon.csv", pokemons);
    Pokemon selectedPokemons[MAX_POKEMON];
    int selectedCount = 0;
    char input[MAX_STRING_LENGTH];
    while (scanf("%s", input) != EOF) {
        if (strcmp(input, "FIM") == 0) break;
        int id = atoi(input);
        int index = buscarPokemonPorId(id, pokemons, numPokemons);
        if (index != -1) {
            selectedPokemons[selectedCount++] = pokemons[index];
        } else {
            printf("Pokémon com ID %d não existe.\n", id);
        }
    }
    clock_t start = clock();
    radixSort(selectedPokemons, selectedCount);
    clock_t end = clock();
    double executionTime = ((double)(end - start)) / CLOCKS_PER_SEC * 1000.0;
    for (int i = 0; i < selectedCount; i++) {
        imprimirPokemon(&selectedPokemons[i]);
    }
    char *matricula = "819886";
    char logFileName[50];
    sprintf(logFileName, "%s_radixsort.txt", matricula);
    FILE *logFile = fopen(logFileName, "w");
    if (logFile) {
        fprintf(logFile, "%s\t%ld\t%ld\t%.2lf\n", matricula, comparisons, movements,
                executionTime);
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
