package indi.noclay.cloudhealth.util;

/**
 * Created by 82661 on 2016/11/6.
 */

public class ResultMessage {
    private int score;
    private String message;
    private boolean isDanger;

    public ResultMessage(int score, String message, boolean isDanger) {
        this.score = score;
        this.message = message;
        this.isDanger = isDanger;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isDanger() {
        return isDanger;
    }

    public void setDanger(boolean danger) {
        isDanger = danger;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}
