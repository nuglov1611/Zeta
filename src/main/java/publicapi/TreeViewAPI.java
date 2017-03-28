package publicapi;

import proguard.annotation.Keep;
import core.rml.dbi.GroupReport;

/**
 * Визуальный компонент "дерево". По сути визуальное представление GroupReport
 *
 */
public interface TreeViewAPI extends VisualRmlObjectAPI, RmlContainerAPI, RetrieveableAPI {
    /**
     * Задает текущий элемент
     * @param n номер элемента если -1, то выбирается корневой элемент
     */
    @Keep
    public void setCurrentNode(int n);

    /**
     * Задает источник данных для дерева
     * @param dataTree - источник данных
     */
    @Keep
    public void setSource(GroupReport dataTree);
 
    /**
     * Возвращает кол-во уровней до данного элемента в дереве, расстояние от корня до элемента
     * @return кол-во уровней до данного элемента
     */
    @Keep
    public int getLevel();
    
    

}
