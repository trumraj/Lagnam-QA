package utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ExcelReader {

    // Read by sheet index
    public static Map<String, String> getKeyValueData(String filePath, int sheetIndex) {
        Map<String, String> data = new HashMap<>();
        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook wb = WorkbookFactory.create(fis)) {

            Sheet sheet = wb.getSheetAt(sheetIndex);

            Iterator<Row> rows = sheet.iterator();
            while (rows.hasNext()) {
                Row row = rows.next();
                Cell keyCell = row.getCell(0);
                Cell valueCell = row.getCell(1);

                if (keyCell != null && valueCell != null) {
                    if (keyCell.getCellType() != CellType.STRING) keyCell.setCellType(CellType.STRING);
                    if (valueCell.getCellType() != CellType.STRING) valueCell.setCellType(CellType.STRING);
                    data.put(keyCell.getStringCellValue().trim(),
                             valueCell.getStringCellValue().trim());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    // Read by sheet name
    public static Map<String, String> getKeyValueData(String filePath, String sheetName) {
        Map<String, String> data = new HashMap<>();
        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook wb = WorkbookFactory.create(fis)) {

            Sheet sheet = wb.getSheet(sheetName);

            if (sheet == null) {
                throw new IllegalArgumentException("Sheet '" + sheetName + "' not found in " + filePath);
            }

            Iterator<Row> rows = sheet.iterator();
            while (rows.hasNext()) {
                Row row = rows.next();
                Cell keyCell = row.getCell(0);
                Cell valueCell = row.getCell(1);

                if (keyCell != null && valueCell != null) {
                    if (keyCell.getCellType() != CellType.STRING) keyCell.setCellType(CellType.STRING);
                    if (valueCell.getCellType() != CellType.STRING) valueCell.setCellType(CellType.STRING);
                    data.put(keyCell.getStringCellValue().trim(),
                             valueCell.getStringCellValue().trim());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    //Read by column number
    public static Map<String, String> getColumnData(String filePath, String sheetName, int colIndex) {
    Map<String, String> data = new HashMap<>();

    try (FileInputStream fis = new FileInputStream(new File(filePath));
         Workbook workbook = WorkbookFactory.create(fis)) {

        Sheet sheet = workbook.getSheet(sheetName);

        for (int row = 0; row <= sheet.getLastRowNum(); row++) {
            Row r = sheet.getRow(row);
            if (r == null) continue;

            Cell keyCell = r.getCell(0);     // first column = key
            Cell valueCell = r.getCell(colIndex);  // target column

            if (keyCell == null) continue;

            String key = keyCell.getStringCellValue();
            String value = (valueCell == null) ? "" : valueCell.toString();

            data.put(key, value);
        }

    } catch (Exception e) {
        e.printStackTrace();
    }

    return data;
}
public static int getTotalColumns(String file, String sheet) {
    return getTotalColumns(file, sheet, 1);
}

// Count columns of data using a specific row (1-indexed) as the reference,
// for sheets where a sub-table starts partway down (e.g. a shared multi-section sheet).
public static int getTotalColumns(String file, String sheet, int headerRow1Based) {
    try {
        FileInputStream fis = new FileInputStream(file);
        XSSFWorkbook wb = new XSSFWorkbook(fis);
        XSSFSheet sh = wb.getSheet(sheet);
        Row row = sh.getRow(headerRow1Based - 1);
        int cols = row.getLastCellNum() - 1; // ignoring Column A (keys)
        wb.close();
        return cols;
    } catch (Exception e) {
        throw new RuntimeException(e);
    }
}

// Read a single column's key/value pairs within a specific row range (1-indexed, inclusive),
// for sheets where a sub-table starts partway down (e.g. a shared multi-section sheet).
public static Map<String, String> getColumnData(String filePath, String sheetName, int colIndex,
                                                  int startRow1Based, int endRow1Based) {
    Map<String, String> data = new HashMap<>();

    try (FileInputStream fis = new FileInputStream(new File(filePath));
         Workbook workbook = WorkbookFactory.create(fis)) {

        Sheet sheet = workbook.getSheet(sheetName);

        for (int row = startRow1Based - 1; row <= endRow1Based - 1; row++) {
            Row r = sheet.getRow(row);
            if (r == null) continue;

            Cell keyCell = r.getCell(0);     // first column = key
            Cell valueCell = r.getCell(colIndex);  // target column

            if (keyCell == null) continue;

            String key = keyCell.getStringCellValue();
            String value = (valueCell == null) ? "" : valueCell.toString();

            data.put(key, value);
        }

    } catch (Exception e) {
        e.printStackTrace();
    }

    return data;
}

}
