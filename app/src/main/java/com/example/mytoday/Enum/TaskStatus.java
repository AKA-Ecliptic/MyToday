package com.example.mytoday.Enum;

public enum TaskStatus {
    INCOMPLETE(0),
    IN_PROGRESS(1),
    COMPLETE(2);

    private final int value;

    TaskStatus(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }

    public static TaskStatus fromValue(int value) {
        for (TaskStatus todo : TaskStatus.values()) {
            if (todo.value == value) return todo;
        }
        return INCOMPLETE;
    }
}
