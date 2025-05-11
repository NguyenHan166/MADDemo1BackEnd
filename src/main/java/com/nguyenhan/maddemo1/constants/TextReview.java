package com.nguyenhan.maddemo1.constants;

public enum TextReview {
    EXCELLENT("Xuất sắc!"),
    GOOD("Hoàn thành tốt yêu cầu!"),
    ABSENT_WARNING("Cần tham gia các buổi học tích cực hơn!"),
    ABSENT_FAIL("Không đạt số buổi học tham gia yêu cầu!"),
    ASSIGNMENT_WARNING("Cần tích cực hoàn thành các bài tập hơn!"),
    ABSENT_ASSIGNMENT_WARNING("Cần cải thiện cả việc học và bài tập!"),
    ABSENT_ASSIGNMENT_FAIL("Không đạt cả về tham gia học và bài tập!");

    private final String displayName;

    TextReview(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}

