package pcd.ass_single.part2.mom;

import java.util.Arrays;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import scala.collection.generic.BitOperations;

public class PixelGrid {
	private final int nRows;
	private final int nColumns;
	private final int[][] grid;
    private ObjectMapper mapper = new ObjectMapper();

	public PixelGrid(final int nRows, final int nColumns) {
		this.nRows = nRows;
		this.nColumns = nColumns;
		grid = new int[nRows][nColumns];
	}

    public PixelGrid(final String serializedGrid) throws JsonProcessingException {
        grid = mapper.readValue(serializedGrid, int[][].class);
        nRows = grid.length;
        nColumns = grid[0].length;
    }


    public String serializedGrid() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(grid);
    }

    public void setGrid(final String serializedGrid) throws JsonProcessingException {
        int[][] newGrid = mapper.readValue(serializedGrid, int[][].class);
        int rows = newGrid.length;
        int cols = newGrid[0].length;
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                set(c, r, newGrid[r][c]);
            }
        }
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
