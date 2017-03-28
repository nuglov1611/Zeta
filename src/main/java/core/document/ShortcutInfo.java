package core.document;

import java.awt.event.KeyEvent;

class ShortcutInfo {

    int modifiers = 0;

    int key = 0;

    ShortcutInfo() {
    }

    ShortcutInfo(int key, int modifiers) {
        this.key = key;
        this.modifiers = modifiers;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ShortcutInfo)) {
            return false;
        }
        ShortcutInfo s = (ShortcutInfo) o;
        return s.modifiers == modifiers && s.key == key;
    }

    @Override
    public int hashCode() {
        return modifiers ^ key;
    }

    @Override
    public String toString() {
        return KeyEvent.getKeyModifiersText(modifiers) + "+"
                + KeyEvent.getKeyText(key);
    }

}
