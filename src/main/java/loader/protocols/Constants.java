package loader.protocols;

public class Constants {
	public static final int CONTINUE = 100; // Продолжать
	public static final int SWITCHING_PROTOCOLS = 101; // Переключение
														// протоколов
	public static final int PROCESSING = 102; // Идёт обработка

	// 2xx: Success //Успешно).
	public static final int OK = 200; // Хорошо
	public static final int CREATED = 201; // Создано
	public static final int ACCEPTED = 202; // Принято
	public static final int INFORMATION_NONAUTHORITATIVE = 203; // Информация не
																// авторитетна
	public static final int NO_CONTENT = 204; // Нет содержимого
	public static final int RESET_CONTENT = 205; // Сбросить содержимое
	public static final int PARTIAL_CONTENT = 206; // Частичное содержимое
	public static final int MULTI_STATUS = 207; // Многостатусный
	public static final int IM_USED = 226; // IM использовано
	// 3xx: Redirection; //Перенаправление
	public static final int MULTIPLE_CHOICES = 300; // Множество выборов
	public static final int MOVED_PERMANENTLY = 301; // Перемещено окончательно
	public static final int FOUND = 302; // Найдено
	public static final int SEE_OTHER = 303; // Смотреть другое
	public static final int NOT_MODIFIED = 304; // Не изменялось
	public static final int USE_PROXY = 305; // Использовать прокси
	public static final int RESERVED = 306; // зарезервировано
	public static final int TEMPORARY_REDIRECT = 307; // Временное
														// перенаправление
	// 4xx: Client Error; //Ошибка клиента
	public static final int BAD_BEQUEST = 400; // Плохой запрос
	public static final int UNAUTHORIZED = 401; // Неавторизован
	public static final int PAYMENT_REQUIRED = 402; // Необходима оплата
	public static final int FORBIDDEN = 403; // Запрещено
	public static final int NOT_FOUND = 404; // Не найдено
	public static final int METHOD_NOT_ALLOWED = 405; // Метод не поддерживается
	public static final int NOT_ACCEPTABLE = 406; // Не приемлемо
	public static final int PROXY_AUTHENTICATION_REQUIRED = 407; // Необходима аутентификация прокси
	public static final int REQUEST_TIMEOUT = 408; // Время ожидания истекло
	public static final int CONFLICT = 409; // Конфликт
	public static final int GONE = 410; // Удалён
	public static final int LENGTH_REQUIRED = 411; // Необходима длина
	public static final int PRECONDITION_FAILED = 412; // Условие «ложно»
	public static final int REQUEST_ENTITY_TOO_LARGE = 413; // Размер запроса слишком велик
	public static final int REQUESTURI_TOO_LONG = 414; // Запрашиваемый URI слишком длинный
	public static final int UNSUPPORTED_MEDIA_TYPE = 415; // Неподдерживаемый тип данных
	public static final int REQUESTED_RANGE_NOT_SATISFIABLE = 416; // Запрашиваемый диапазон не достижим
	public static final int EXPECTATION_FAILED = 417; // Ожидаемое не приемлемо
	public static final int UUPROCESSABLE_ENTITY = 422; // Необрабатываемый экзмепляр
	public static final int LOCKED = 423; // Заблокировано
	public static final int FAILED_DEPENDENCY = 424; // Невыполненная зависимость
	public static final int UNORDERED_COLLECTION = 425; // Неупорядоченный набор
	public static final int UPGRADE_REQUIRED = 426; // Необходимо обновление
	public static final int RETRY_WITH = 449; // Повторить с...

	// 5xx: Server Error; //Ошибка сервера
	public static final int INTERNAL_SERVER_ERROR = 500; // Внутренняя ошибка
															// сервера
	public static final int NOT_IMPLEMENTED = 501; // Не реализовано
	public static final int BAD_GATEWAY = 502; // Плохой шлюз
	public static final int SERVICE_UNAVAILABLE = 503; // Сервис недоступен
	public static final int GATEWAY_TIMEOUT = 504; // Шлюз не отвечает
	public static final int HTTP_VERSION_NOT_SUPPORTED = 505; // Версия HTTP не поддерживается
	public static final int VARIANT_ALSO_NEGOTIATES = 506; // Вариант тоже согласован
	public static final int INSUFFICIENT_STORAGE = 507; // Переполнение хранилища
	public static final int BANDWIDTH_LIMIT_EXCEEDED = 509; // Исчерпана пропускная ширина канала
	public static final int NOT_EXTENDED = 510; // Не расширено
	

	public static final int HTTP_RETRY = 2; // Повторить запрос
	public static final int HTTP_STOP = 1; // Прекратить посылку запроса
	public static final int HTTP_OK = 0; // Запрос обработан успешно
	
	
}
