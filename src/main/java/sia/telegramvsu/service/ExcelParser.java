package sia.telegramvsu.service;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import sia.telegramvsu.model.LessonVSU;
import sia.telegramvsu.model.WeekDay;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Data
@Component
public class ExcelParser {

    @Value("${path.excel}")
    private String filePath;

    final int ROWS_BETWEEN_LESSONS = 3;
    final int TIME_COLUMN_NUMBER = 2;
    final int GROUPS_ROW_NUMBER = 12;

    private Map<Integer, List<String>> schedules;

    public void parseExel() throws IOException {
        Map<Integer, List<String>> list = new HashMap<>();
        Workbook workbook;
        FileInputStream fis;

        try {
            fis = new FileInputStream(new File(filePath));
            workbook = new XSSFWorkbook(fis);

            Sheet sheet = workbook.getSheetAt(0); // Берём первый лист

            int i = 0;
            for (Row row : sheet) {
                List<String> rowData = new ArrayList<>();
                for (Cell cell : row) {
                    rowData.add(getCellValueAsString(cell));
                }
                list.put(i, rowData);
                i++;
            }

            log.info("Excel file parsed successfully");
        }catch (IOException e){
            log.error("Error parse exel" + e.getMessage());
        }



        this.schedules = list;
    }

    public String getWeekSubjects(String groupName) {
        StringBuilder sb = new StringBuilder();
            sb.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
        for (WeekDay weekDay : WeekDay.values()) {
            sb.append(getDaySubjects(weekDay, groupName))
            .append("\n━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");

        }
        return sb.toString();
    }

    public String getDaySubjects(WeekDay weekDay, String groupName) {
        int indexGroup = findGroupIndex(groupName);
        List<LessonVSU> lessons = new ArrayList<>();

        for (int i = 0; i < 8; i++) {
            lessons.add(new LessonVSU());
        }

        var iterator = lessons.iterator();
        for (int i = weekDay.firstRow; i < weekDay.lastRow; i+= ROWS_BETWEEN_LESSONS) {
            LessonVSU lesson = iterator.next();
            lesson.setNumber(schedules.get(i).get(TIME_COLUMN_NUMBER));
            lesson.setTime(schedules.get(i+1).get(TIME_COLUMN_NUMBER));

            lesson.setSubject(schedules.get(i).get(indexGroup));
            lesson.setLector(schedules.get(i+1).get(indexGroup));
            lesson.setAuditorium(schedules.get(i+2).get(indexGroup));
        }

        return formatLessons(lessons, weekDay);
    }

    private String formatLessons(List<LessonVSU> lessons, WeekDay weekDay) {
        StringBuilder sb = new StringBuilder();

        sb.append("""
                <u><b>%s:</b></u>
                
                """.formatted(weekDay.dayString));
        for (LessonVSU lesson : lessons) {
            if (!lesson.getSubject().isEmpty()) {
                sb.append("""
                        <b>№%s</b> %s
                          <b>%s</b>
                          %s
                          %s
                        
                        """.formatted(lesson.getNumber(), lesson.getTime(), lesson.getSubject(), lesson.getLector(), lesson.getAuditorium()));
            }
        }
        return sb.toString();
    }

    public boolean isGroup(String group) {
        return findGroupIndex(group) != -1;
    }

    private int findGroupIndex(String nameGroup) {
        List<String> groups = schedules.get(GROUPS_ROW_NUMBER);

        for (int i = 0; i < groups.size(); i++) {
            if (groups.get(i).equalsIgnoreCase(nameGroup)) {;
                return i;
            }
        }

        return -1;
    }

    private String getCellValueAsString(Cell cell) {
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> String.valueOf(cell.getNumericCellValue());
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            case FORMULA -> cell.getCellFormula();
            case BLANK, _NONE -> "";
            default -> "UNKNOWN";
        };
    }
}