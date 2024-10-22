import java.util.*;

public class SaloonStandoff {
    private int n; // Tamanho do tabuleiro
    private int b; // Número de Bigodudos
    private int c; // Número de Capetas
    private char[][] board;
    private List<List<String>> solutions;
    private List<List<String>>invalidBoards;

    // Listas para armazenar as posições de cada gangue
    private List<int[]> bigodudos;
    private List<int[]> capetas;

    public SaloonStandoff(int n, int b, int c) {
        this.n = n;
        this.b = b;
        this.c = c;
        this.board = new char[n][n];
        this.invalidBoards = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            Arrays.fill(board[i], '.');
        }
        this.solutions = new ArrayList<>();
        this.bigodudos = new ArrayList<>();
        this.capetas = new ArrayList<>();
    }

    public List<List<String>> solve() {
        backtrack(0, 0, 0, 0);
        return solutions;
    }

    private void backtrack(int row, int col, int placedB, int placedC) {
        // Se todas as peças foram colocadas, verificar as condições
        if (placedB == b && placedC == c ) {
            if (validateAll()) {
                solutions.add(createBoard());
            } else {
                invalidBoards.add(createBoard()); // Adiciona a configuração inválida à lista
            }
           
            return;
        }

        // Se chegou ao final do tabuleiro
        if (row == n) {
            invalidBoards.add(createBoard()); 
            return;
        }

        // Próxima posição
        int nextRow = row;
        int nextCol = col + 1;
        if (nextCol == n) {
            nextRow += 1;
            nextCol = 0;
        }

        
        // Tentar colocar um Bigodudo
        if (placedB < b && canPlace('B', row, col)) {
            board[row][col] = 'B';
            bigodudos.add(new int[]{row, col});
            backtrack(nextRow, nextCol, placedB + 1, placedC);
            bigodudos.remove(bigodudos.size() - 1);
            board[row][col] = '.';
        }

        // Tentar colocar um Capeta
        if (placedC < c && canPlace('C', row, col)) {
            board[row][col] = 'C';
            capetas.add(new int[]{row, col});
            backtrack(nextRow, nextCol, placedB, placedC + 1);
            capetas.remove(capetas.size() - 1);
            board[row][col] = '.';
        }

        // Tentar deixar a posição vazia
        backtrack(nextRow, nextCol, placedB, placedC);
    }

    // Verifica se uma peça pode ser colocada na posição (row, col)
    private boolean canPlace(char gang, int row, int col) {
        
        char gangueInimiga;
        if(gang == 'B'){
            gangueInimiga = 'C';
        }
        else{
            gangueInimiga = 'B';
        }
        // Direções: N, NE, E, SE, S, SW, W, NW
        int[] dRow = {-1, -1, 0, 1, 1, 1, 0, -1};
        int[] dCol = {0, 1, 1, 1, 0, -1, -1, -1};
        
        //para todas as direçoes do tabuleiro
        for (int d = 0; d < 8; d++) {
            int r = row + dRow[d];
            int c = col + dCol[d];

            //enquanto r e c estiverem dentro do limite do tabuleiro
            while (r >= 0 && r < n && c >= 0 && c < n) {
            //se a posiçao nao estiver vazia "." 
                if (board[r][c] != '.') {
            //se o inimigo inimiga da mesma gangue
                    if(board[r][c] == gangueInimiga){

                        break; //encontrou um inimigo, para de procurar um amigo nessa posição
                    } 
                    else{
                        return false;
                    }
                }
                r += dRow[d];
                c += dCol[d];
            }
        }   
        return true;
    }

   // Verifica se a peça na posição (row, col) vê pelo menos dois inimigos
    private boolean hasAtLeastTwoEnemies(int[] pos, char enemyGang) {
        int row = pos[0];
        int col = pos[1];
        int count = 0;

        // Direções: N, NE, E, SE, S, SW, W, NW
        int[] dRow = {-1, -1, 0, 1, 1, 1, 0, -1};
        int[] dCol = {0, 1, 1, 1, 0, -1, -1, -1};

        for (int d = 0; d < 8; d++) {
            int r = row + dRow[d];
            int c = col + dCol[d];
            while (r >= 0 && r < n && c >= 0 && c < n) {
                if (board[r][c] != '.') {
                    if (board[r][c] == enemyGang) {
                        count++;
                    }
                    break; // Encontrou uma peça, parar nessa direção
                }
                r += dRow[d];
                c += dCol[d];
            }
            if (count >= 2) {
                return true; // Assim que encontrar 2 inimigos, retorna verdadeiro
            }
        }
        return count >= 2;
    }

// Verifica todas as peças após a colocação
private boolean validateAll() {
    // Verificar para cada Bigodudo
    for (int[] bPos : bigodudos) {
        if (!hasAtLeastTwoEnemies(bPos, 'C')) {
            return false; // Bigodudo que não vê dois Capetas
        }
    }
    // Verificar para cada Capeta
    for (int[] cPos : capetas) {
        if (!hasAtLeastTwoEnemies(cPos, 'B')) {
            return false; // Capeta que não vê dois Bigodudos
        }
    }
    return true; // Todos os pistoleiros atendem às condições
}

    // Cria a representação do tabuleiro para a solução
    private List<String> createBoard() {
        List<String> solution = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            solution.add(new String(board[i]));
        }
        return solution;
    }
    public List<List<String>> getInvalidBoards() {
        return invalidBoards;
    }

    public static void main(String[] args) {
        // Usando Scanner para ler valores do terminal
        Scanner sc = new Scanner(System.in);
        
        // Ler o valor de n
        System.out.print("Digite o tamanho do tabuleiro (n): ");
        int n = sc.nextInt();

        // Ler o número de Bigodudos (b)
        System.out.print("Digite o número de Bigodudos (b): ");
        int b = sc.nextInt();

        // Ler o número de Capetas (c)
        System.out.print("Digite o número de Capetas (c): ");
        int c = sc.nextInt();

        // Criar a instância da solução com os valores fornecidos
        SaloonStandoff sol = new SaloonStandoff(n, b, c);
        List<List<String>> results = sol.solve();
        
        // Verificar se soluções foram encontradas
        if (results == null || results.isEmpty()) {
            System.out.println("Nenhuma solução encontrada.");
        } else {
            int count = 1;
            for (List<String> board : results) {
                System.out.println("Solução " + count + ":");
                for (String row : board) {
                    System.out.println(row);
                }
                System.out.println();
                count++;
            }
        }
          

        

        // Fechar o scanner
        sc.close();
    }
         

}
