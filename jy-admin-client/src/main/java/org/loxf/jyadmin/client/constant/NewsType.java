package org.loxf.jyadmin.client.constant;

public enum NewsType {
    NEWS(1, "新闻"),
    CLASS(2, "面授课程");

    public final Integer value;
    public final String desc;
    NewsType(final Integer value, final String desc) {
        this.value = value;
        this.desc = desc;
    }
}
