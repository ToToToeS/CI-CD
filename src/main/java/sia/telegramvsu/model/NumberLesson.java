package sia.telegramvsu.model;

public enum NumberLesson {
    LESSON_1("№1"),
    LESSON_2("№2"),
    LESSON_3("№3"),
    LESSON_4("№4"),
    LESSON_5("№5"),
    LESSON_6("№6"),
    LESSON_7("№7"),
    LESSON_8("№8");

    private final String number;

    NumberLesson(String number) {
        this.number = number;
    }

    public String getNumber() {
        return number;
    }
}
