package core.document;

class KeyCatcherInfo {

    int[]      keys;

    KeyCatcher com;

    boolean isEqual(int[] ar) {
        if (ar == null || keys == null || ar.length != keys.length) {
            return false;
        }
        for (int i = 0; i < ar.length; i++) {
            if (ar[i] != keys[i]) {
                return false;
            }
        }
        return true;
    }

}
