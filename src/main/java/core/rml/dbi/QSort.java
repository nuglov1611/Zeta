package core.rml.dbi;

import java.util.Vector;

public class QSort {
    private static FCompare fc;

    public static void setFCompare(FCompare f) {
        fc = f;
    }

    public static void QuickSort(Vector<Object> a, int lo0, int hi0) {
        int lo = lo0;
        int hi = hi0;
        Object mid;

        if (hi0 > lo0) {
            mid = a.elementAt((lo0 + hi0) / 2);

            while (lo <= hi) {
                // while( ( lo < hi0 ) && ( a[lo] < mid ))
                while ((lo < hi0) && (fc.compareL(a.elementAt(lo), mid))) {
                    ++lo;
                }
                // while((hi > lo0) && ( a[hi] > mid ))
                while ((hi > lo0) && (fc.compareB(a.elementAt(hi), mid))) {
                    --hi;
                }
                if (lo <= hi) {
                    swap(a, lo, hi);
                    ++lo;
                    --hi;
                }
            }
            if (lo0 < hi) {
                QuickSort(a, lo0, hi);
            }
            if (lo < hi0) {
                QuickSort(a, lo, hi0);
            }
        }
        ;
    }

    private static void swap(Vector<Object> a, int i, int j) {
        Object T;
        T = a.elementAt(i);
        a.setElementAt(a.elementAt(j), i);
        a.setElementAt(T, j);
    }
}
