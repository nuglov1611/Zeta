package publicapi;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Hashtable;

import javax.swing.JMenu;

import proguard.annotation.Keep;
import core.parser.Proper;
import core.rml.RmlObject;

/**
 * Документ - корневой контейнер для RML-объектов. Описывается отдельным RML-файлом.
 *
 * Свойства 
 * - document_title - заголовок документа, отображается вверху окна в title bar. При пустом значении поля title bar не отображается. Значение по умолчанию - пустое
 * - layout - схема размещения визуальных RML-объектов в документе. Допустимые значения: "abs", "border", "box", "grid", "flow". Значение по умолчанию - "grid"
 * - version - версия документа. Значение по умолчанию - "..."
 * - postloadscript - скрипт выполняющийся после создания объектов (вычисляемое выражение [[calc]])
 * - preloadscript - скрипт выполняющийся до создания объектов (вычисляемое выражение [[calc]])
 * - closescript - скрипт выполняющийся перед закрытием документа (вычисляемое выражение [[calc]])
 * - hashable - значения yes/no. В случае если параметр равен yes, документ сохраняет свое состояние все время работы системы. Фактически используетсяч всегда один экземпляр документа. Чаще всего применяется для документов - подбора сущностей из справочника.
 *  - значение по-умолчанию 'no'
 * - axis - способ размещения объектов для box layout. 
 *  - X - размещение компонентов по горизонтали
 *  - Y - размещение компонентов по вертикали
 *  - Значение по-умолчанию 'Y'
 * - document_width - ширина документа
 * - document_height - высота документа
 * - grid_x - число ячеек по горизонтали для grid layout. Обязательное поле если layout = "grid"
 * - grid_y - число ячеек по вертикали для grid layout. Обязательное поле если layout = "grid"
 * 
 * 
 * Пример 
 * @code
 Cp1251
 DOC
 document_title="Справочник контрагентов"
 document_width=900
 document_height=600
 {toolbar   
   {treeview2
      return=yes
      expandall=yes
      alias=tree
      nodeAction="~($g.self@acthok)~"   
      listAction2="~($g.self@acthok)~"
      nodefont="Courier,bold|italic,12" pointfont="Courier,italic,12"  
      hilite=gray,white
      node=black point=gray
      rootname="Контрагенты"
      hiliting=on
    {groupreport     
     alias = gr
     query = "select client.id_client, client.id_parent, client.code1, name_client, lvl, client.address1, territory.id_territory, territory.name_territory
                from (
                         select client.id_client, client.id_parent, decode(level, 4, code1||' '||name_client, name_client) name_client, level lvl,
                                client.address1, client.id_terr, client.code1
                           from client
                          where client.id_client != -1
                          ~($if (($try (ARGUMENTS.0) $catch ('Any')('-999-')) != '-999-') (' and client.id_type = ' + ARGUMENTS.0) 
                            $else ('')
                           )~
               connect by prior client.id_client = client.id_parent
                     start with  ~($if (($try (ARGUMENTS.0) $catch ('Any')('-999-'))  != '-999-') ('id_client = ' + ARGUMENTS.0)
                                   $else ('id_parent is null')
                                   )~
                     ) client, territory
                 where client.id_terr = territory.id_territory (+)"
               grouping = "name_client"
                sorting = "1"
              treeparam = "lvl;client.id_client;client.id_parent;name_client"
               editable = no
      } // gp
   } // treewiew
  {button
   alias = but2
   left=400   height=20       width=100   top =10
   label="OK"
   action="($X
                ($if (($g.tree@getLevel) == 0)
                    (
                        (gr.client.id_client = -1)
                        (gr.client.name_client = 'Не известен')
                    )
                )
                ($g.self@acthok)
           )"
   shortcut = "F3"
  }
  {button
   alias = but3
   left=500   height=20       width=100   top =10
   label="Закрыть ESC"
   action = "($g.self@actcancel)" 
   shortcut = "ESCAPE"
  }
 }
@endcode
 */
public interface DocumentAPI extends RmlContainerAPI {
    
    /**
     * Возвращаеть путь до директории с файлом-описанием документа в RML-репозитории
     * @return путь
     */
    @Keep
    public String getPath();
    
    /**
     * Возвращаеть имя файла с описанием документа
     * @return имя файла
     */
    @Keep
    public String getName();
    
    /**
     * Сохранить данные из всех Datastore в базу
     * @return 
     */
    @Keep
    public int actSave();

    /**
     *Закрыть документ без сохранения
     *@return  
     */
    @Keep
    public int actCancel();
    
    /**
     * Сохранить данные из всех Datastore в базу и закрыть документ
     * @return 
     */
    @Keep
    public int actDoc();
    
    /**
     * Сохранить данные в объект STORE и закрыть документ
     * @return 
     */
    @Keep
    public int actHok();
    
    /**
     * Открыть документ в новом окне
     * @return
     */
    @Keep
    public int actNew();
    
    /**
     * Выполнить action
     * @param action скрипт для вычисления action 
     */
    @Keep
    public void doAction(String action) throws Exception;
    
    /**
     * Выполнить скрипт в блокирующем режиме
     * @param script скрипт
     * @return результат выполнения скрипта
     */
    @Keep
    public Object calculate(String script) throws Exception;
    
    
    /**
     * Выполнить скрипт в не блокирующем режиме (запуск скрипта в отдельном потоке)
     * @param script скрипт 
     */
    @Keep
    public void runInBackground(String script) throws Exception;
   
    /**
     * Получить путь к RML-репозиторию
     * @return путь к репозиторию
     */
    @Keep
    public String getServerParth();
    
    /**
     * Получить все объекты документа
     * @return таблица объектов
     */
    @Keep
    public Hashtable<String, Object> getAliases();
    
    
    /**
     * @param alias 
     * @return объект 
     */
    @Keep
    public Object getObject(String alias);
    
    /**
     * Загрузить и разобрать RML-документ
     * @param file - имя RML-файла 
     * @return объект Proper 
     */
    @Keep
    public Proper loadRml(String file) throws Exception;

    /**
     * Разобрать RML-выражение
     * @param rml - имя RML-файла 
     * @return объект Proper 
     */
    @Keep
    public Proper parse(String rml) throws Exception;

    /**
     * Дилог открытия файла
     * @return путь к файлу 
     */
    @Keep
    public File openFileDlg();


    /**
     * Прочитать содержимое файла
     * @param file путь к файлу
     * @return содердимое файла 
     */
    @Keep
    public String readFile(String file) throws FileNotFoundException, IOException;
    
    /**
     * Записать содержимое в файла
     * @param file путь к файлу
     * @param value содердимое для записи
     */
    @Keep
    public void writeToFile(String file, byte[] value) throws FileNotFoundException, IOException;
    
    /**
     * Выполнить системный вызов (командная строка) в неблокирующем режиме
     * @param cmd выражение для выполнения
     */
    @Keep
    public void shell(String cmd) throws IOException;
    
    /**
     * Выполнить системный вызов (командная строка) в блокирующем редиме
     * @param cmd выражение для выполнения
     * @return код ошибки
     */
    @Keep
    public int shellWait(String cmd) throws IOException, InterruptedException;
    
    /**
     * Зарегистрировать объект в общем списке Rml-объектов документа
     * @param rmlObject - объект
     */
    @Keep
	public void registrate(RmlObject rmlObject);

    /**
     * Добавить меню документа в рабочее пространство
     */
    @Keep
    public void initDocumentMenu();
    
    /**
     * Добавить добавить элемент в меню документа
     * Пример изменения главного меню:
     * @code
		importClass(javax.swing.JMenu);
		importClass(javax.swing.JMenuItem);
		
		i1 = new JMenuItem('Test1');
		i1.setActionCommand('~js:{THEME_TEST.setValue(11); \'\'; }~');
		i2 = new JMenu('Test2');
		i3 = new JMenu('Test3');
		m1 = new JMenu('TestMenu1');
		i11 = new JMenuItem('Test11');
		i12 = new JMenuItem('Test12');
		i13 = new JMenuItem('Test13');
		m1.add(i11);
		m1.add(i12);
		m1.add(i13);
		
		SELF.addMenu(m1);
		SELF.addMenu(i1);
		SELF.addMenu(i2);
		SELF.addMenu(i3);
		SELF.initDocumentMenu();      
       @endcode
     * @param item - элемент меню
     */
    @Keep
    public void addMenu(JMenu item);
}
