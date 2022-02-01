/*
 * Copyright 2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ch.x28.inscriptis;

import ch.x28.inscriptis.models.TableCellCanvas;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A single row within a table.
 *
 * @author Sascha Wolski
 * @author Matthias Hewelt
 */
class Row {

	private final List<TableCellCanvas> cellColumns = new ArrayList<>();
	private String cellSeparator;

	public Row setCellSeparator(String cellSeparator) {
		this.cellSeparator = cellSeparator;
		return this;
	}

	private final List<TableCell> columns = new ArrayList<>();

	private static List<List<String>> zipLongest(List<List<String>> lists, String fillValue) {

		// determine longest list
		int maxListLength = 0;
		for (List<String> list : lists) {
			maxListLength = Math.max(maxListLength, list.size());
		}

		List<List<String>> resultLists = new ArrayList<>();

		for (int listElementIndex = 0; listElementIndex < maxListLength; listElementIndex++) {
			List<String> subList = new ArrayList<>();
			for (List<String> list : lists) {
				String element = list.size() > listElementIndex
					? list.get(listElementIndex)
					: fillValue;

				subList.add(element);
			}
			resultLists.add(subList);
		}
		return resultLists;
	}

	/**
	 * Computes the list of lines in the cell specified by the column_idx.
	 *
	 * @param columnIndex The column index of the cell.
	 * @return The list of lines in the cell specified by the column_idx or an empty list if the column does not exist.
	 */
	public List<String> getCellLines(int columnIndex) {

		if (columnIndex >= columns.size()) {
			return new ArrayList<String>(0);
		}

		return columns.get(columnIndex).getCellLines();
	}

	public List<TableCell> getColumns() {
		return columns;
	}

	public List<TableCellCanvas> getCellColumns() {
		return this.cellColumns;
	}

	/**
	 * @return A rendered string representation of the given row.
	 */
	public String getText() {

		List<List<String>> lines = new ArrayList<>();

		for (TableCell column : columns) {
			lines.add(column.getCellLines());
		}

		List<List<String>> longestZip = zipLongest(lines, " ");

		List<String> rowLines = new ArrayList<>();
		for (List<String> list : longestZip) {
			rowLines.add(String.join("  ", list));
		}

		return String.join("\n", rowLines);
	}

	public String getCanvasText() {

		List<List<String>> lines = new ArrayList<>();
		int minLen = Integer.MAX_VALUE;
                if (cellColumns.size() > 0) {
			for (TableCellCanvas column : cellColumns) {
				lines.add(column.blocks);
				if (column.blocks.size() < minLen) {
					minLen = column.blocks.size();
				}
			}
		} else {
			minLen = 0;
		}

		List<String> rowLines = new ArrayList<>();
		for (int i = 0; i<minLen; i++) {
			List<String> zipped = new ArrayList<>();
			for (int a = 0; a < lines.size(); a ++) {
				zipped.add(lines.get(a).get(i));
			}
			String rowLine = String.join(cellSeparator, zipped);
			rowLines.add(rowLine);
		}

		String result = "";
		if (rowLines.size() > 0) {
			result = String.join("\n", rowLines);
		}
		return result;
	}

	public int getWidth() {
		if (cellColumns.isEmpty()) {
			return 0;
		}

		List<Integer> a = cellColumns.stream().map(TableCellCanvas::getWidth).collect(Collectors.toList());
		int s = a.stream().mapToInt(Integer::intValue).sum();
		return s + cellSeparator.length() * (cellColumns.size() - 1);
	}
}
