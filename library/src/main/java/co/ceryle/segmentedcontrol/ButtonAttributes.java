package co.ceryle.segmentedcontrol;

/**
 * Created by EGE on 2.10.2016.
 */

public class ButtonAttributes {
    private int tintColor, textColor;

    private boolean hasTintColor ,hasTextColor;

    public int getTintColor() {
        return tintColor;
    }

    public void setTintColor(int tintColor) {
        this.tintColor = tintColor;
    }

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    public boolean hasTintColor() {
        return hasTintColor;
    }

    public void setTintColor(boolean hasTintColor) {
        this.hasTintColor = hasTintColor;
    }

    public boolean hasTextColor() {
        return hasTextColor;
    }

    public void setTextColor(boolean hasTextColor) {
        this.hasTextColor = hasTextColor;
    }
}