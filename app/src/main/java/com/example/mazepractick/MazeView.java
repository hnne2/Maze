package com.example.mazepractick;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Stack;

public class MazeView extends View {
    private Paint wallPaint;
    private Paint solutionPaint;
    private Cell[][] grid;
    private int cols, rows;
    private int tileSize;

    private Random random = new Random();

    public MazeView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initPaint();
    }

    public MazeView(Context context, int cols, int rows) {
        super(context);
        initPaint();
        init(cols, rows);
    }

    private void initPaint() {
        wallPaint = new Paint();
        wallPaint.setColor(Color.DKGRAY);
        wallPaint.setStrokeWidth(3);

        solutionPaint = new Paint();
        solutionPaint.setColor(Color.RED);
        solutionPaint.setStrokeWidth(5);
    }

    public void setMazeSize(int cols, int rows) {
        this.cols = cols;
        this.rows = rows;
        init(cols, rows); // Инициализация лабиринта с новыми размерами
        requestLayout(); // Перезапрос разметки View
        invalidate(); // Обновление View
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (cols > 0 && rows > 0) {
            init(cols, rows); // Инициализация лабиринта после изменения размеров View
        }
    }

    private void init(int cols, int rows) {
        this.cols = cols;
        this.rows = rows;
        int viewWidth = getWidth();
        int viewHeight = getHeight();

        this.tileSize = Math.min(viewWidth / cols, viewHeight / rows);

        this.grid = new Cell[cols][rows];

        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < cols; x++) {
                grid[x][y] = new Cell(x, y);
            }
        }

        generateMaze();
    }
    private void generateMaze() {
        Stack<Cell> stack = new Stack<>();
        Cell start = grid[0][0];
        start.visited = true;
        stack.push(start);

        while (!stack.isEmpty()) {
            Cell current = stack.peek();
            Cell next = getUnvisitedNeighbor(current);

            if (next != null) {
                next.visited = true;
                stack.push(next);
                removeWalls(current, next);
            } else {
                stack.pop();
            }
        }

        // Set the solution path for drawing
        solveMaze();
    }

    private void solveMaze() {
        // Сбросим метки посещенных ячеек и previous
        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < cols; x++) {
                grid[x][y].visited = false;
                grid[x][y].previous = null;
            }
        }

        Stack<Cell> stack = new Stack<>();
        Cell start = grid[0][0];
        stack.push(start);
        start.visited = true;

        while (!stack.isEmpty()) {
            Cell current = stack.pop();

            // Если достигли конца, начинаем сохранять путь
            if (current == grid[cols - 1][rows - 1]) {
                return;
            }

            for (Cell neighbor : getUnvisitedNeighbors(current)) {
                neighbor.previous = current;
                neighbor.visited = true;
                stack.push(neighbor);
            }
        }
    }

    private List<Cell> getUnvisitedNeighbors(Cell cell) {
        List<Cell> neighbors = new ArrayList<>();
        int x = cell.x;
        int y = cell.y;

        if (y > 0 && !cell.walls[0] && !grid[x][y - 1].visited) neighbors.add(grid[x][y - 1]); // верхний сосед
        if (x < cols - 1 && !cell.walls[1] && !grid[x + 1][y].visited) neighbors.add(grid[x + 1][y]); // правый сосед
        if (y < rows - 1 && !cell.walls[2] && !grid[x][y + 1].visited) neighbors.add(grid[x][y + 1]); // нижний сосед
        if (x > 0 && !cell.walls[3] && !grid[x - 1][y].visited) neighbors.add(grid[x - 1][y]); // левый сосед

        return neighbors;
    }

    private Cell getUnvisitedNeighbor(Cell cell) {
        List<Cell> neighbors = new ArrayList<>();
        int x = cell.x;
        int y = cell.y;

        if (y > 0 && !grid[x][y - 1].visited) neighbors.add(grid[x][y - 1]);
        if (x < cols - 1 && !grid[x + 1][y].visited) neighbors.add(grid[x + 1][y]);
        if (y < rows - 1 && !grid[x][y + 1].visited) neighbors.add(grid[x][y + 1]);
        if (x > 0 && !grid[x - 1][y].visited) neighbors.add(grid[x - 1][y]);

        if (neighbors.isEmpty()) return null;
        return neighbors.get(random.nextInt(neighbors.size()));
    }

    private void removeWalls(Cell current, Cell next) {
        int dx = next.x - current.x;
        int dy = next.y - current.y;

        if (dx == 1) {
            current.walls[1] = false;
            next.walls[3] = false;
        } else if (dx == -1) {
            current.walls[3] = false;
            next.walls[1] = false;
        }

        if (dy == 1) {
            current.walls[2] = false;
            next.walls[0] = false;
        } else if (dy == -1) {
            current.walls[0] = false;
            next.walls[2] = false;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (grid == null) {
            Log.d("TAG", "onDraw: Grid is not initialized");
            return; // If grid is not initialized, skip drawing
        }

        int offsetX = (getWidth() - (cols * tileSize)) / 2;
        int offsetY = (getHeight() - (rows * tileSize)) / 2;

        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < cols; x++) {
                Cell cell = grid[x][y];
                int startX = offsetX + x * tileSize;
                int startY = offsetY + y * tileSize;

                if (cell.walls[0]) canvas.drawLine(startX, startY, startX + tileSize, startY, wallPaint);
                if (cell.walls[1]) canvas.drawLine(startX + tileSize, startY, startX + tileSize, startY + tileSize, wallPaint);
                if (cell.walls[2]) canvas.drawLine(startX + tileSize, startY + tileSize, startX, startY + tileSize, wallPaint);
                if (cell.walls[3]) canvas.drawLine(startX, startY + tileSize, startX, startY, wallPaint);
            }
        }

        // Draw the solution path
        Cell current = grid[cols - 1][rows - 1];

        while (current.previous != null) {
            Log.d("TAG", "onDraw: Drawing solution path");
            int startX = offsetX + current.x * tileSize + tileSize / 2;
            int startY = offsetY + current.y * tileSize + tileSize / 2;
            int endX = offsetX + current.previous.x * tileSize + tileSize / 2;
            int endY = offsetY + current.previous.y * tileSize + tileSize / 2;

            canvas.drawLine(startX, startY, endX, endY, solutionPaint);

            current = current.previous;
        }
    }

    private static class Cell {
        int x, y;
        boolean[] walls = {true, true, true, true}; // top, right, bottom, left
        boolean visited = false;
        Cell previous = null;

        Cell(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }
}
