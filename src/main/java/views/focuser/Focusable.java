package views.focuser;

public interface Focusable {

    public void focusThis();

    public int getFocusPosition();

    public void setFocusPosition(int position);

    public void setFocusable(boolean focusable);

}
