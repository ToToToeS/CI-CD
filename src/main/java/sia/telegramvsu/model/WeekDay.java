package shatilo.springtgbot.model;

public enum WeekDay {
    MONDAY(14, 38, "Понедельник"), TUESDAY(39, 63, "Вторник"), WEDNESDAY(64, 88, "Среда"),
    THURSDAY(89, 113, "Четверг"), FRIDAY(114, 138, "Пятница"), SATURDAY(139, 163, "Суббота");

    public final int firstRow;
    public final int lastRow;
    public final String dayString;

    WeekDay(int firstRow, int lastRow, String dayString) {
        this.firstRow = firstRow;
        this.lastRow = lastRow;
        this.dayString = dayString;
    }
}
