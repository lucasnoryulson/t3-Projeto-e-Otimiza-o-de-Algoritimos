#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <stdbool.h>

#define MAX_N 10

typedef struct {
    int row;
    int col;
} Position;

typedef struct {
    int n;
    int b;
    int c;
    char board[MAX_N][MAX_N];
    char** solutions;
    int solutionsCount;
    char** invalidBoards;
    int invalidBoardsCount;
    Position* bigodudos;
    int bigodudosCount;
    Position* capetas;
    int capetasCount;
} SaloonStandoff;

SaloonStandoff* createSaloonStandoff(int n, int b, int c) {
    SaloonStandoff* game = (SaloonStandoff*)malloc(sizeof(SaloonStandoff));
    game->n = n;
    game->b = b;
    game->c = c;
    game->solutionsCount = 0;
    game->invalidBoardsCount = 0;
    game->bigodudosCount = 0;
    game->capetasCount = 0;

    for (int i = 0; i < n; i++) {
        memset(game->board[i], '.', n);
    }

    game->solutions = NULL;
    game->invalidBoards = NULL;
    game->bigodudos = (Position*)malloc(b * sizeof(Position));
    game->capetas = (Position*)malloc(c * sizeof(Position));

    return game;
}

void addSolution(SaloonStandoff* game) {
    game->solutionsCount++;
    game->solutions = (char**)realloc(game->solutions, game->solutionsCount * sizeof(char*));
    game->solutions[game->solutionsCount - 1] = (char*)malloc(game->n * game->n * sizeof(char));
    
    int index = 0;
    for (int i = 0; i < game->n; i++) {
        for (int j = 0; j < game->n; j++) {
            game->solutions[game->solutionsCount - 1][index++] = game->board[i][j];
        }
    }
}

void addInvalidBoard(SaloonStandoff* game) {
    game->invalidBoardsCount++;
    game->invalidBoards = (char**)realloc(game->invalidBoards, game->invalidBoardsCount * sizeof(char*));
    game->invalidBoards[game->invalidBoardsCount - 1] = (char*)malloc(game->n * game->n * sizeof(char));
    
    int index = 0;
    for (int i = 0; i < game->n; i++) {
        for (int j = 0; j < game->n; j++) {
            game->invalidBoards[game->invalidBoardsCount - 1][index++] = game->board[i][j];
        }
    }
}

bool canPlace(SaloonStandoff* game, char gang, int row, int col) {
    char enemyGang = (gang == 'B') ? 'C' : 'B';
    int dRow[] = {-1, -1, 0, 1, 1, 1, 0, -1};
    int dCol[] = {0, 1, 1, 1, 0, -1, -1, -1};

    for (int d = 0; d < 8; d++) {
        int r = row + dRow[d];
        int c = col + dCol[d];

        while (r >= 0 && r < game->n && c >= 0 && c < game->n) {
            if (game->board[r][c] != '.') {
                if (game->board[r][c] == enemyGang) {
                    break;
                } else {
                    return false;
                }
            }
            r += dRow[d];
            c += dCol[d];
        }
    }
    return true;
}

bool hasAtLeastTwoEnemies(SaloonStandoff* game, Position pos, char enemyGang) {
    int row = pos.row;
    int col = pos.col;
    int count = 0;

    int dRow[] = {-1, -1, 0, 1, 1, 1, 0, -1};
    int dCol[] = {0, 1, 1, 1, 0, -1, -1, -1};

    for (int d = 0; d < 8; d++) {
        int r = row + dRow[d];
        int c = col + dCol[d];

        while (r >= 0 && r < game->n && c >= 0 && c < game->n) {
            if (game->board[r][c] == enemyGang) {
                count++;
                if (count >= 2) {
                    return true;
                }
                break;
            } else if (game->board[r][c] != '.') {
                break;
            }
            r += dRow[d];
            c += dCol[d];
        }
    }
    return false;
}

bool validateAll(SaloonStandoff* game) {
    for (int i = 0; i < game->bigodudosCount; i++) {
        if (!hasAtLeastTwoEnemies(game, game->bigodudos[i], 'C')) {
            return false;
        }
    }
    for (int i = 0; i < game->capetasCount; i++) {
        if (!hasAtLeastTwoEnemies(game, game->capetas[i], 'B')) {
            return false;
        }
    }
    return true;
}

void backtrack(SaloonStandoff* game, int row, int col, int placedB, int placedC) {
    if (placedB == game->b && placedC == game->c) {
        if (validateAll(game)) {
            addSolution(game);
        } else {
            addInvalidBoard(game);
        }
        return;
    }

    if (row == game->n) {
        addInvalidBoard(game);
        return;
    }

    int nextRow = row;
    int nextCol = col + 1;
    if (nextCol == game->n) {
        nextRow += 1;
        nextCol = 0;
    }

    if (placedB < game->b && canPlace(game, 'B', row, col)) {
        game->board[row][col] = 'B';
        game->bigodudos[game->bigodudosCount++] = (Position){row, col};
        backtrack(game, nextRow, nextCol, placedB + 1, placedC);
        game->bigodudosCount--;
        game->board[row][col] = '.';
    }

    if (placedC < game->c && canPlace(game, 'C', row, col)) {
        game->board[row][col] = 'C';
        game->capetas[game->capetasCount++] = (Position){row, col};
        backtrack(game, nextRow, nextCol, placedB, placedC + 1);
        game->capetasCount--;
        game->board[row][col] = '.';
    }

    backtrack(game, nextRow, nextCol, placedB, placedC);
}

void solve(SaloonStandoff* game) {
    backtrack(game, 0, 0, 0, 0);
}

void freeSaloonStandoff(SaloonStandoff* game) {
    for (int i = 0; i < game->solutionsCount; i++) {
        free(game->solutions[i]);
    }
    free(game->solutions);

    for (int i = 0; i < game->invalidBoardsCount; i++) {
        free(game->invalidBoards[i]);
    }
    free(game->invalidBoards);

    free(game->bigodudos);
    free(game->capetas);
    free(game);
}

int main() {
    int n, b, c;
    printf("Digite o valor de n: ");
    scanf("%d", &n);
    printf("Digite o valor de b: ");
    scanf("%d", &b);
    printf("Digite o valor de c: ");
    scanf("%d", &c);
    SaloonStandoff* game = createSaloonStandoff(n, b, c);
    solve(game);

    printf("Solutions found: %d\n", game->solutionsCount);

    freeSaloonStandoff(game);
    return 0;
}

