package pcd.ass_single.part2.example;

import java.util.Arrays;

public class PixelGrid {
	private final int nRows;
	private final int nColumns;
	private final int[][] grid;
	
	public PixelGrid(final int nRows, final int nColumns) {
		this.nRows = nRows;
		this.nColumns = nColumns;
		grid = new int[nRows][nColumns];
	}

	public void clear() {
		for (int i = 0; i < nRows; i++) {
			Arrays.fill(grid[i], 0);
		}
	}
	
	public void set(final int x, final int y, final int color) {
		grid[y][x] = color;
	}
	
	public int get(int x, int y) {
		return grid[y][x];
	}
	
	public int getNumRows() {
		return this.nRows;
	}
	

	public int getNumColumns() {
		return this.nColumns;
	}
	
}
