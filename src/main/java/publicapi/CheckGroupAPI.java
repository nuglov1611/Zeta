package publicapi;

/**
 * Группа связанных ChackBox (в группе может быть включен только один CheckBox,
 * т.е. при включении одно элемента из группы остальные элементы переходят в
 * состояние "выключен")
 *
 *- alignment - выравнивание элементов внутри группы. Может быть 2 варианта: horizontal и vertical. Значение по умолчанию vertical.
 *- gap - расстояние по вертикали между элементами группы. Задается любым положительным числом. Имеет размерность в пикселях. Значение по умолчанию 5
 *
 *@code
   {checkgroup
      top=75
      left=0
      alignment=center
      alias=cbgroup
      gap=30
      { checkbox 
          label = "CheckBox1"
          offvalue = "off" onvalue = "on" check = yes
      }
      { checkbox 
          label = "CheckBox2"
          offvalue = "off" onvalue = "on" check = no
      }
      { checkbox 
        label = "CheckBox3"
        offvalue = "off" onvalue = "on" check = yes
      }
      { checkbox 
        label = "CheckBox4"
        offvalue = "off" onvalue = "on" check = no
      }
    }  
  @endcode
 *  
 */
public interface CheckGroupAPI extends VisualRmlObjectAPI, RmlContainerAPI {

}
