class Solution:
    def solveQueen(self,n:int) -> list[list[str]]:
        col = set()
        posDiag = set()
        negDiag = set()

        res = []
        board = [["."] * n for i in range(n)]

        def backtrack(row):
            if row == n:
                res.append(["".join(row) for row in board])
                return

            for c in range(n):
                if c in col or c + row in posDiag or c - row in negDiag:
                    continue

                col.add(c)
                posDiag.add(c + row)
                negDiag.add(c - row)

                board[row][c] = "Q"

                backtrack(row + 1)

                col.remove(c)
                posDiag.remove(c + row)
                negDiag.remove(c - row)

                board[row][c] = "."
        backtrack(0)
        return res

# Executa o código para n = 3
solution = Solution()
n = 4
result = solution.solveQueen(n)

# Imprimir o resultado
if result:
    for solution in result:
        for row in solution:
            print(row)
        print()  # Linha em branco entre soluções
else:
    print(f"Não há soluções válidas para n = {n}")