package views.focuser;

public interface Focusable {

    void focusThis();

    int getFocusPosition();

    void setFocusPosition(int position);

    void setFocusable(boolean focusable);

}
