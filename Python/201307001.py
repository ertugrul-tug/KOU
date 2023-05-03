import tkinter as tk
import random
import sys
from tkinter import *
from tkinter import ttk

input_file = sys.argv[1]
output_file = sys.argv[2]

root = tk.Tk()
root.title("Nostaljik Windows Oyunu Vize Ödevi")

# Sabit Değerler
WIDTH = 800
HEIGHT = 600
BLOCK_SIZE = 40
BOARD_WIDTH = 20
BOARD_HEIGHT = 15
COLORS = [
    (0, "white"),
    (1, "#505050"),
    (2, "red"),
    (3, "green"),
    (4, "blue"),
    (5, "cyan"),
    (6, "yellow"),
    (7, "magenta"),
    (8, "#FFA500"),
]

# Değişkenler
board = [[None] * BOARD_HEIGHT for _ in range(BOARD_WIDTH)]
canvas = Canvas
score_label = Label(root, text="Score: 0", font=("Times", 16, "bold"), bg="white")
score_label.pack(side=BOTTOM, fill=X)
score = 0
punto = 24

def restart_game():
    global board
    global score
    # Tahtayı sıfırla
    canvas.delete("all")

    # Skoru sıfırla
    score = 0

    # Oyun tahtasını ve ekrandaki skoru güncelle
    fill_board()
    draw_board()
    score_label.config(text="Score: " + str(score))
    pass

def exit_game():
    raise SystemExit
    pass

def change_board_size(size):
    global BOARD_HEIGHT, BOARD_WIDTH, BLOCK_SIZE, board, punto
    new_width, new_height = size
    BLOCK_SIZE = 600 // new_height if new_height > 15 else 40
    if new_height > 15:
        punto = 18
    else:
        punto = 24
    new_board = [[None] * new_height for _ in range(new_width)]
    for x in range(min(new_width, BOARD_WIDTH)):
        for y in range(min(new_height, BOARD_HEIGHT)):
            new_board[x][y] = board[x][y]
    BOARD_WIDTH, BOARD_HEIGHT = new_width, new_height
    board = new_board
    restart_game()

def fill_board():
    for x in range(BOARD_WIDTH):
        for y in range(BOARD_HEIGHT):
            temp = random.choice(COLORS)
            board[x][y] = temp
    draw_board()

def draw_board():
    canvas.delete("all")
    canvas.configure(bg="#1beb00")
    for x in range(BOARD_WIDTH):
        for y in range(BOARD_HEIGHT):
            if board[x][y] is None:
                continue
            value = board[x][y][0]
            color = board[x][y][1]
            if color:
                canvas.create_rectangle(
                    x * BLOCK_SIZE,
                    y * BLOCK_SIZE,
                    (x + 1) * BLOCK_SIZE,
                    (y + 1) * BLOCK_SIZE,
                    fill="#808080",
                    outline="black",
                    width=2,
                )
                canvas.create_rectangle(
                    x * BLOCK_SIZE + 4,
                    y * BLOCK_SIZE + 4,
                    (x + 1) * BLOCK_SIZE - 4,
                    (y + 1) * BLOCK_SIZE - 4,
                    fill=color,
                    outline="black",
                    width=2,
                )
                if value == 1:
                    fill = "white"
                else:
                    fill = "black"
                canvas.create_text(
                    (x + 0.5) * BLOCK_SIZE,
                    (y + 0.5) * BLOCK_SIZE,
                    text=str(value),
                    fill=fill,
                    font=("Times", punto, "bold"),
                )

def find_group(x, y, group, visited):
    visited.add((x, y))
    value, color = board[x][y]
    group.add((x, y))
    for dx, dy in ((-1, 0), (1, 0), (0, -1), (0, 1)):
        nx, ny = x + dx, y + dy
        if (
            0 <= nx < BOARD_WIDTH
            and 0 <= ny < BOARD_HEIGHT
            and board[nx][ny] is not None
            and (nx, ny) not in visited
            and board[nx][ny][1] == color
        ):
            find_group(nx, ny, group, visited)


def remove_group(group):
    # Skoru güncelle
    global score
    if len(group) < 2:
        return
    n = len(group)
    c = sum(board[x][y][0] for x, y in group)
    score += c * fibonacci(n)
    score_label.config(text="Skor: " + str(score))  # Skoru güncelle

    # Dosyaya son durumu çıkar
    with open(output_file, "w") as f_out:
        for wy in range(len(board[0])):
            for wx in range(len(board)):
                if board[wx][wy] is not None:
                    f_out.write(str(board[wx][wy][0]))
                else:
                    f_out.write(" ")
            f_out.write("\n")
        f_out.write("\nSkor:"+str(score))

    for x, y in group:
        board[x][y] = None

    # Blokları aşağı düşür
    for x in range(BOARD_WIDTH):
        column = [board[x][y] for y in range(BOARD_HEIGHT) if board[x][y] is not None]
        column = [None] * (BOARD_HEIGHT - len(column)) + column
        for y in range(BOARD_HEIGHT):
            board[x][y] = column[y]

    # Blokları yana kaydır
    empty_columns = [
        x
        for x in range(BOARD_WIDTH)
        if all(board[x][y] is None for y in range(BOARD_HEIGHT))
    ]
    non_empty_columns = [x for x in range(BOARD_WIDTH) if x not in empty_columns]

    for i, x in enumerate(non_empty_columns):
        for y in range(BOARD_HEIGHT):
            board[i][y] = board[x][y]
            if i != x:
                board[x][y] = None

    # Blokları tekrardan aşağı kaydır
    for x in range(BOARD_WIDTH):
        column = [board[x][y] for y in range(BOARD_HEIGHT) if board[x][y] is not None]
        column = [None] * (BOARD_HEIGHT - len(column)) + column
        for y in range(BOARD_HEIGHT):
            board[x][y] = column[y]


def handle_click(event):
    x, y = event.x // BLOCK_SIZE, event.y // BLOCK_SIZE

    if not board[x][y]:
        return

    group = set()
    find_group(x, y, group, set())

    if len(group) > 1:
        remove_group(group)
        draw_board()

def fibonacci(n):
    if n == 0:
        return 0
    elif n == 1:
        return 1
    else:
        return fibonacci(n - 1) + fibonacci(n - 2)

def main():
    global canvas, score
    notebook = ttk.Notebook(root)
    notebook.pack(expand=True, fill=BOTH)
    canvas = tk.Canvas(root, width=WIDTH, height=HEIGHT)
    canvas.pack()

    with open(input_file, "r") as f_in:
        # Dosyadan girdileri al
        for line in f_in:
            temp = line.strip().split("x")
            change_board_size([int(temp[0]),(int(temp[1]))])

    # Menü barını oluştur
    menu_bar = Menu(root)
    root.config(menu=menu_bar)

    # Menu'ye Geliştirici, Tekrar Başlat ve Çıkış alt sekmelerini ekle
    general_menu = Menu(menu_bar, tearoff=False)
    menu_bar.add_cascade(label="Menü", menu=general_menu)
    general_menu.add_command(label="Tekrar Başlat", command=restart_game)
    general_menu.add_command(label="Çıkış", command=exit_game)

    options_menu = Menu(menu_bar, tearoff=False)
    options_menu.add_radiobutton(
        label="Small (10x7)", value=10, command=lambda: change_board_size([10, 7])
    )
    options_menu.add_radiobutton(
        label="Medium (15x10)", value=15, command=lambda: change_board_size([15, 10])
    )
    options_menu.add_radiobutton(
        label="Large (20x15)", value=20, command=lambda: change_board_size([20, 15])
    )
    options_menu.add_radiobutton(
        label="Huge (30x20)", value=30, command=lambda: change_board_size([30, 20])
    )

    menu_bar.add_cascade(label="Ayarlar", menu=options_menu)

    # Menu barına Yardım ekle
    help_menu = Menu(menu_bar, tearoff=False)
    help_menu.add_radiobutton(
        label="Bu oyun, amacın tahtadaki tüm renkli blokları temizlemek olduğu bir bulmaca oyunudur."
    )
    help_menu.add_radiobutton(
        label="Bloklar, aynı renkteki iki veya daha fazla bitişik bloktan oluşan gruplara tıklanarak temizlenir."
    )
    help_menu.add_radiobutton(
        label="Grupta ne kadar çok blok olursa puan o kadar yüksek olur. Oyun, daha fazla blok temizlenemediğinde sona erer."
    )

    menu_bar.add_cascade(label="Yardım", menu=help_menu)

    fill_board()
    draw_board()

    canvas.bind("<Button-1>", handle_click)

    root.mainloop()

if __name__ == "__main__":
    main()
