import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

public class InExecl {
  public InExecl(String filename, Tools tools) {
    heads = tools.set();
    rows = 0;
    this.filename = filename;
    workbook = new HSSFWorkbook();
    sheet = workbook.createSheet("Employee Info");

    Row row = sheet.createRow(rows++);
    int cellid = 0;
    Cell cell = row.createCell(cellid++);
    cell.setCellValue("时间");
    for (String head : heads) {
      cell = row.createCell(cellid++);
      cell.setCellValue(head);
    }
  }

  public void inRow(Pair<LocalDate, Set<Rate>> rowItem) {
    Row row = sheet.createRow(rows++);
    int cellid = 0;
    Cell cell = row.createCell(cellid++);
    cell.setCellValue(rowItem.first.toString());
    for (Rate rate: rowItem.second) {
      cell = row.createCell(cellid++);
      cell.setCellValue(rate.unitTo());
    }
  }

  public void toFile() {
    try (FileOutputStream out = new FileOutputStream(new File(filename))) {
      workbook.write(out);
    } catch (IOException e) {
      System.out.println(e.getMessage());
      e.printStackTrace();
      throw new RuntimeException(e);
    }
  }

  private Set<String> heads;
  private String filename;
  private Workbook workbook;
  private Sheet sheet;
  private int rows;
}
