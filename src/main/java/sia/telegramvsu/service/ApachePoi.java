package sia.telegramvsu.service;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ApachePoi {
    public static List<List<String>> parseExcelWithMergedCells(File file) throws IOException {
        List<List<String>> list = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(file);
             XSSFWorkbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0);
            List<CellRangeAddress> mergedRegions = sheet.getMergedRegions();

            for (int rowNum = 0; rowNum <= sheet.getLastRowNum(); rowNum++) {
                Row row = sheet.getRow(rowNum);
                List<String> rowData = new ArrayList<>();

                if (row != null) {
                    int maxColNum = getMaxColumnCount(sheet);

                    for (int colNum = 0; colNum < maxColNum; colNum++) {
                        String cellValue = getMergedCellValue(sheet, rowNum, colNum, mergedRegions);
                        rowData.add(cellValue);
                    }
                }

                if (rowData.isEmpty()) {
                    for (int i = 0; i < getMaxColumnCount(sheet); i++) {
                        rowData.add("");
                    }
                }

                list.add(rowData);
            }
        }
        return list;
    }

    private static String getMergedCellValue(Sheet sheet, int rowNum, int colNum, List<CellRangeAddress> mergedRegions) {
        // Проверяем, является ли ячейка частью объединенной области
        for (CellRangeAddress mergedRegion : mergedRegions) {
            if (mergedRegion.isInRange(rowNum, colNum)) {
                // Возвращаем значение из первой ячейки объединенной области
                Row firstRow = sheet.getRow(mergedRegion.getFirstRow());
                if (firstRow != null) {
                    Cell firstCell = firstRow.getCell(mergedRegion.getFirstColumn());
                    if (firstCell != null) {
                        return getCellValueAsString(firstCell);
                    }
                }
                return "";
            }
        }

        // Если ячейка не объединена, возвращаем её значение
        Row row = sheet.getRow(rowNum);
        if (row != null) {
            Cell cell = row.getCell(colNum);
            if (cell != null) {
                return getCellValueAsString(cell);
            }
        }
        return "";
    }

    private static int getMaxColumnCount(Sheet sheet) {
        int maxColumns = 0;
        for (int i = 0; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row != null && row.getLastCellNum() > maxColumns) {
                maxColumns = (int) row.getLastCellNum();
            }
        }
        return maxColumns;
    }

    private static String getCellValueAsString(Cell cell) {
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> String.valueOf  (cell.getNumericCellValue());
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            case FORMULA -> cell.getCellFormula();
            case BLANK, _NONE -> "";
            default -> "UNKNOWN";
        };
    }
}
