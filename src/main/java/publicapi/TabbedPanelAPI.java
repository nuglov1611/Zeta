package publicapi;

import proguard.annotation.Keep;

/**
 * ѕанель с закладками
 */
public interface TabbedPanelAPI extends RmlContainerAPI, RetrieveableAPI, VisualRmlObjectAPI {

    /**
     * ¬озвращает номер закладки, активной в данный момент времени.
     *
     * @return
     */
    @Keep
    int getCurrentTab();

    /**
     * ќткрыть (сделать текущей) закладку
     *
     * @param tabNumber - номер закладки (нумераци€ с 0)
     */
    @Keep
    void setCurrentTab(int tabNumber);

}
