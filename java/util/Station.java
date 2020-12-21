package util;

/**
 * @author 10652
 */

public enum Station {
    zero(0, ""),
    one(1, "1"),
    two(2, "2"),
    three(3, "3"),
    four(4, "4"),
    five(5, "5"),
    six(6, "6"),
    seven(7, "7"),
    eight(8, "8"),
    mine(-1, "M"),
    unknown(-2, "N");
    private int value;
    private String text;

    Station(int value, String text) {
        this.value = value;
        this.text = text;
    }

    Station() {

    }

    public int getValue() {
        return value;
    }

    public String getText() {
        return text;
    }

    /**
     * 根据雷的数量，获取对应 枚举常量
     *
     * @param num
     * @return
     */
    public static Station getStationByValue(int num) {
        switch (num) {
            case 0:
                return Station.zero;
            case 1:
                return Station.one;
            case 2:
                return Station.two;
            case 3:
                return Station.three;
            case 4:
                return Station.four;
            case 5:
                return Station.five;
            case 6:
                return Station.six;
            case 7:
                return Station.seven;
            case 8:
                return Station.eight;
            default:
                return null;
        }
    }
}