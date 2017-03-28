package loader.protocols;

public class Constants {
	public static final int CONTINUE = 100; // ����������
	public static final int SWITCHING_PROTOCOLS = 101; // ������������
														// ����������
	public static final int PROCESSING = 102; // ��� ���������

	// 2xx: Success //�������).
	public static final int OK = 200; // ������
	public static final int CREATED = 201; // �������
	public static final int ACCEPTED = 202; // �������
	public static final int INFORMATION_NONAUTHORITATIVE = 203; // ���������� ��
																// �����������
	public static final int NO_CONTENT = 204; // ��� �����������
	public static final int RESET_CONTENT = 205; // �������� ����������
	public static final int PARTIAL_CONTENT = 206; // ��������� ����������
	public static final int MULTI_STATUS = 207; // ��������������
	public static final int IM_USED = 226; // IM ������������
	// 3xx: Redirection; //���������������
	public static final int MULTIPLE_CHOICES = 300; // ��������� �������
	public static final int MOVED_PERMANENTLY = 301; // ���������� ������������
	public static final int FOUND = 302; // �������
	public static final int SEE_OTHER = 303; // �������� ������
	public static final int NOT_MODIFIED = 304; // �� ����������
	public static final int USE_PROXY = 305; // ������������ ������
	public static final int RESERVED = 306; // ���������������
	public static final int TEMPORARY_REDIRECT = 307; // ���������
														// ���������������
	// 4xx: Client Error; //������ �������
	public static final int BAD_BEQUEST = 400; // ������ ������
	public static final int UNAUTHORIZED = 401; // �������������
	public static final int PAYMENT_REQUIRED = 402; // ���������� ������
	public static final int FORBIDDEN = 403; // ���������
	public static final int NOT_FOUND = 404; // �� �������
	public static final int METHOD_NOT_ALLOWED = 405; // ����� �� ��������������
	public static final int NOT_ACCEPTABLE = 406; // �� ���������
	public static final int PROXY_AUTHENTICATION_REQUIRED = 407; // ���������� �������������� ������
	public static final int REQUEST_TIMEOUT = 408; // ����� �������� �������
	public static final int CONFLICT = 409; // ��������
	public static final int GONE = 410; // �����
	public static final int LENGTH_REQUIRED = 411; // ���������� �����
	public static final int PRECONDITION_FAILED = 412; // ������� ������
	public static final int REQUEST_ENTITY_TOO_LARGE = 413; // ������ ������� ������� �����
	public static final int REQUESTURI_TOO_LONG = 414; // ������������� URI ������� �������
	public static final int UNSUPPORTED_MEDIA_TYPE = 415; // ���������������� ��� ������
	public static final int REQUESTED_RANGE_NOT_SATISFIABLE = 416; // ������������� �������� �� ��������
	public static final int EXPECTATION_FAILED = 417; // ��������� �� ���������
	public static final int UUPROCESSABLE_ENTITY = 422; // ���������������� ���������
	public static final int LOCKED = 423; // �������������
	public static final int FAILED_DEPENDENCY = 424; // ������������� �����������
	public static final int UNORDERED_COLLECTION = 425; // ��������������� �����
	public static final int UPGRADE_REQUIRED = 426; // ���������� ����������
	public static final int RETRY_WITH = 449; // ��������� �...

	// 5xx: Server Error; //������ �������
	public static final int INTERNAL_SERVER_ERROR = 500; // ���������� ������
															// �������
	public static final int NOT_IMPLEMENTED = 501; // �� �����������
	public static final int BAD_GATEWAY = 502; // ������ ����
	public static final int SERVICE_UNAVAILABLE = 503; // ������ ����������
	public static final int GATEWAY_TIMEOUT = 504; // ���� �� ��������
	public static final int HTTP_VERSION_NOT_SUPPORTED = 505; // ������ HTTP �� ��������������
	public static final int VARIANT_ALSO_NEGOTIATES = 506; // ������� ���� ����������
	public static final int INSUFFICIENT_STORAGE = 507; // ������������ ���������
	public static final int BANDWIDTH_LIMIT_EXCEEDED = 509; // ��������� ���������� ������ ������
	public static final int NOT_EXTENDED = 510; // �� ���������
	

	public static final int HTTP_RETRY = 2; // ��������� ������
	public static final int HTTP_STOP = 1; // ���������� ������� �������
	public static final int HTTP_OK = 0; // ������ ��������� �������
	
	
}