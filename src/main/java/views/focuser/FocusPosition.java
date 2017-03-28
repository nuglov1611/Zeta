package views.focuser;

public class FocusPosition implements Focusable {

    int focus_position;

    @Override
    public int getFocusPosition() {
        return focus_position;
    }

    @Override
    public void setFocusPosition(int position) {
        focus_position = position;
    }

    @Override
    public void focusThis() {
    }

    @Override
    public void setFocusable(boolean focusable) {

    }

}
